package projet.fst.ma.securestoragelabjava;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.snackbar.Snackbar;

import projet.fst.ma.securestoragelabjava.cache.CacheStore;
import projet.fst.ma.securestoragelabjava.external.ExternalAppFilesStore;
import projet.fst.ma.securestoragelabjava.files.InternalTextStore;
import projet.fst.ma.securestoragelabjava.files.StudentsJsonStore;
import projet.fst.ma.securestoragelabjava.model.Student;
import projet.fst.ma.securestoragelabjava.prefs.AppPrefs;
import projet.fst.ma.securestoragelabjava.prefs.SecurePrefs;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "SecureStorageJava";
    private final List<String> langs = Arrays.asList("fr", "en", "ar");

    private EditText etName;
    private EditText etToken;
    private Spinner spLang;
    private MaterialSwitch swDark;
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Binding views (Matching Material3 layout)
        etName = findViewById(R.id.etName);
        etToken = findViewById(R.id.etToken);
        spLang = findViewById(R.id.spLang);
        swDark = findViewById(R.id.swDark);
        tvResult = findViewById(R.id.tvResult);

        setupLangSpinner();

        // Binding Material Buttons
        findViewById(R.id.btnSavePrefs).setOnClickListener(this::savePrefs);
        findViewById(R.id.btnLoadPrefs).setOnClickListener(v -> loadPrefsToUi(v));
        findViewById(R.id.btnSaveJson).setOnClickListener(this::saveJsonFile);
        findViewById(R.id.btnLoadJson).setOnClickListener(this::loadJsonFile);
        findViewById(R.id.btnExportExternal).setOnClickListener(this::exportExternalFile);
        findViewById(R.id.btnClear).setOnClickListener(this::confirmClearAll);

        // Initial load (without snackbar)
        loadPrefsToUi(null);
    }

    private void setupLangSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, langs);
        spLang.setAdapter(adapter);
    }

    /**
     * Shows a status message in the result area and via a Snackbar.
     */
    private void showStatus(View view, String message, boolean isError) {
        tvResult.setText("> " + message);
        if (view != null) {
            Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT);
            if (isError) {
                snackbar.setBackgroundTint(ContextCompat.getColor(this, android.R.color.holo_red_dark));
            } else {
                snackbar.setBackgroundTint(ContextCompat.getColor(this, android.R.color.holo_green_dark));
            }
            snackbar.show();
        }
    }

    private void savePrefs(View v) {
        String name = etName.getText().toString().trim();
        if (name.isEmpty()) {
            showStatus(v, "Le nom d'utilisateur est requis", true);
            return;
        }

        String lang = langs.get(Math.max(0, spLang.getSelectedItemPosition()));
        String theme = swDark.isChecked() ? "dark" : "light";

        boolean ok = AppPrefs.save(this, name, lang, theme, false);

        String token = etToken.getText().toString();
        if (!token.isBlank()) {
            try {
                SecurePrefs.saveToken(this, token);
            } catch (Exception e) {
                showStatus(v, "Erreur chiffrement token : " + e.getMessage(), true);
                return;
            }
        }

        Log.d(TAG, "Prefs sauvegardées ok=" + ok + ", name=" + name + ", lang=" + lang + ", theme=" + theme);

        try {
            CacheStore.write(this, "last_ui.txt", "name=" + name + ", lang=" + lang + ", theme=" + theme);
        } catch (Exception ignored) {}

        showStatus(v, "Profil et préférences enregistrés !", false);
    }

    private void loadPrefsToUi(View v) {
        AppPrefs.Triple triple = AppPrefs.load(this);

        etName.setText(triple.name);
        swDark.setChecked("dark".equals(triple.theme));

        int idx = langs.indexOf(triple.lang);
        spLang.setSelection(idx >= 0 ? idx : 0);

        int tokenLen = 0;
        try {
            String token = SecurePrefs.loadToken(this);
            tokenLen = token == null ? 0 : token.length();
        } catch (Exception ignored) {}

        String info = "Données chargées :\n" +
                "Nom : " + (triple.name.isEmpty() ? "(non défini)" : triple.name) + "\n" +
                "Langue : " + triple.lang + "\n" +
                "Thème : " + triple.theme + "\n" +
                "Token : " + tokenLen + " car. (sécurisé)";
        
        tvResult.setText(info);
        if (v != null) {
            showStatus(v, "Données restaurées avec succès", false);
        }
    }

    private void saveJsonFile(View v) {
        List<Student> students = Arrays.asList(
                new Student(1, "Amina", 20),
                new Student(2, "Omar", 21),
                new Student(3, "Sara", 19)
        );

        try {
            StudentsJsonStore.save(this, students);
            InternalTextStore.writeUtf8(this, "note.txt", "Dernière modification : " + new java.util.Date());
            showStatus(v, "Fichier JSON (3 étudiants) sauvegardé !", false);
        } catch (Exception e) {
            showStatus(v, "Erreur lors de l'écriture JSON : " + e.getMessage(), true);
        }
    }

    private void loadJsonFile(View v) {
        List<Student> students = StudentsJsonStore.load(this);
        if (students.isEmpty()) {
            showStatus(v, "Aucun fichier JSON trouvé ou vide", true);
            return;
        }

        StringBuilder sb = new StringBuilder("Contenu du fichier JSON :\n");
        for (Student s : students) {
            sb.append(" • ").append(s.name).append(" (").append(s.age).append(" ans)\n");
        }

        tvResult.setText(sb.toString());
        showStatus(v, "Lecture JSON réussie", false);
    }

    private void exportExternalFile(View v) {
        String name = etName.getText().toString().trim();
        String content = "Export Log - " + new java.util.Date() + " | Utilisateur : " + (name.isEmpty() ? "Inconnu" : name);
        try {
            String path = ExternalAppFilesStore.write(this, "export_secure_lab.txt", content);
            showStatus(v, "Export réussi : " + path, false);
        } catch (Exception e) {
            showStatus(v, "Échec de l'export externe : " + e.getMessage(), true);
        }
    }

    private void confirmClearAll(View v) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Réinitialisation")
                .setMessage("Voulez-vous vraiment supprimer toutes les données stockées (préférences, secrets, fichiers et cache) ?")
                .setNegativeButton("Annuler", null)
                .setPositiveButton("Tout effacer", (dialog, which) -> clearAll(v))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void clearAll(View v) {
        AppPrefs.clear(this);

        try {
            SecurePrefs.clear(this);
        } catch (Exception ignored) {}

        StudentsJsonStore.delete(this);
        InternalTextStore.delete(this, "note.txt");
        ExternalAppFilesStore.delete(this, "export_secure_lab.txt");

        int purged = CacheStore.purge(this);

        etName.setText("");
        etToken.setText("");
        swDark.setChecked(false);
        spLang.setSelection(0);

        showStatus(v, "Réinitialisation terminée (" + purged + " fichiers cache supprimés)", false);
    }
}

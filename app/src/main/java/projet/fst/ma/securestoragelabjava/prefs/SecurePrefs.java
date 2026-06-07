package projet.fst.ma.securestoragelabjava.prefs;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

public final class SecurePrefs {

    private static final String PREFS_NAME = "secure_prefs";
    private static final String KEY_API_TOKEN = "secure_api_token";
    private static SharedPreferences cachedInstance;

    private SecurePrefs() {}

    private static synchronized SharedPreferences getPrefs(Context context) throws Exception {
        if (cachedInstance == null) {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            cachedInstance = EncryptedSharedPreferences.create(
                    context,
                    PREFS_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        }
        return cachedInstance;
    }

    public static void saveToken(Context context, String token) throws Exception {
        getPrefs(context).edit().putString(KEY_API_TOKEN, token).apply();
    }

    public static String loadToken(Context context) throws Exception {
        return getPrefs(context).getString(KEY_API_TOKEN, "");
    }

    public static void clear(Context context) throws Exception {
        getPrefs(context).edit().clear().apply();
    }
}

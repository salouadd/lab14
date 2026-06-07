# SecureStorageLabJava 🛡️

Ce projet est une application Android pédagogique développée en **Java** illustrant la mise en place d'une persistance locale complète et sécurisée. L'objectif est de maîtriser les différents types de stockage Android tout en appliquant des règles strictes de sécurité.

## 🚀 Objectifs d'apprentissage

- **SharedPreferences** : Stockage de préférences non sensibles (thème, langue).
- **EncryptedSharedPreferences** : Utilisation de la bibliothèque *AndroidX Security Crypto* pour chiffrer les secrets (Tokens API) via une `MasterKey`.
- **Stockage Interne** : Écriture et lecture de fichiers texte (UTF-8) et d'objets complexes au format **JSON**.
- **Gestion du Cache** : Utilisation du répertoire `cacheDir` pour les données temporaires et implémentation d'une fonction de purge.
- **Stockage Externe App-specific** : Exportation de fichiers vers le stockage externe sans requérir de permissions globales (Android 10+).

## 🛠️ Technologies & Bibliothèques

- **Langage** : Java
- **UI** : Material Design 3 (Cards, TextInputLayout, MaterialSwitch, Snackbar).
- **Sécurité** : `androidx.security:security-crypto:1.1.0`
- **Format de données** : `org.json`
- **Compatibilité** : Min SDK 24, optimisé pour les écrans de petite taille (Small Phone) via `ScrollView`.

## 📂 Architecture du Projet

```text
com.example.securestoragejava
├── cache/       # Gestion du stockage temporaire (CacheStore)
├── external/    # Exportation de fichiers (ExternalAppFilesStore)
├── files/       # Stockage interne texte et JSON (InternalTextStore, StudentsJsonStore)
├── model/       # Modèles de données (Student)
├── prefs/       # Préférences claires et chiffrées (AppPrefs, SecurePrefs)
└── ui/          # Interface utilisateur (MainActivity)
```

## 🔒 Checklist Sécurité Implémentée

1.  **Zéro secret en clair** : Les jetons (tokens) sont chiffrés sur le disque.
2.  **Logs contrôlés** : Aucune donnée sensible (mot de passe/token) n'est envoyée dans le Logcat (seule la longueur est affichée).
3.  **Confidentialité** : Utilisation systématique du `MODE_PRIVATE`.
4.  **Nettoyage complet** : Fonction de réinitialisation supprimant tous les fichiers et préférences.
5.  **Validation** : Gestion des erreurs et des saisies vides avant persistance.
6.  **Encodage** : Forçage de l'UTF-8 pour éviter les corruptions de données.

## 📱 Utilisation

1.  **Saisie** : Entrez un nom d'utilisateur et un token secret.
2.  **Persistance** : Utilisez les boutons "Sauver" pour stocker dans les différentes zones de mémoire.
3.  **Vérification** :
    *   Relancez l'app pour voir les données restaurées.
    *   Utilisez le **Device File Explorer** d'Android Studio dans `/data/data/projet.fst.ma.securestoragelabjava/`.
4.  **Reset** : Le bouton "Réinitialiser tout" efface l'intégralité des traces locales.

---


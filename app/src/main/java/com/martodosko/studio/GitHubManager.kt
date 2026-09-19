// ==================================================
// ✅ I-UPDATE ANG GitHubManager.kt — DECRYPT MUNA BAGO GAMITIN!
// ==================================================
class GitHubManager(private val context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("github_config", Context.MODE_PRIVATE)
    
    private val REPO_OWNER = "fbvlink2026-lab"
    private val REPO_NAME = "martodosko-audio-studio"

    // ✅ WALANG MALINAW NA TOKEN — NAKA-ENCRYPT LANG ANG NAKA-SAVE!
    private val ENCRYPTED_TOKEN_KEY = "encrypted_github_token"
    private val USER_FOLDER_KEY = "user_folder"

    // ==============================================
    // ✅ I-SAVE ANG TOKEN — NAKA-ENCRYPT AGAD!
    // ==============================================
    fun saveEncryptedToken(plainToken: String, ownerKeyCode: String, userFolder: String) {
        val encryptedToken = EncryptionManager.encrypt(plainToken, ownerKeyCode)
        prefs.edit()
            .putString(ENCRYPTED_TOKEN_KEY, encryptedToken) // ✅ NAKA-ENCRYPT!
            .putString(USER_FOLDER_KEY, userFolder.trim().replace(Regex("[^a-zA-Z0-9_-]"), "_"))
            .apply()
        Toast.makeText(context, "✅ Token Naka-encrypt at Nai-save!", Toast.LENGTH_SHORT).show()
    }

    // ==============================================
    // ✅ KUNIN ANG TOKEN — KAILANGAN NG OWNER KEY PARA MA-DECRYPT!
    // ==============================================
    fun getDecryptedToken(ownerKeyCode: String): String? {
        val encryptedToken = prefs.getString(ENCRYPTED_TOKEN_KEY, null) ?: return null
        return EncryptionManager.decrypt(encryptedToken, ownerKeyCode)
    }

    // ==============================================
    // ✅ TIGNAN KUNG MAY NAKA-SAVE NA TOKEN
    // ==============================================
    fun hasEncryptedToken(): Boolean = prefs.contains(ENCRYPTED_TOKEN_KEY)

    // ==============================================
    // ✅ I-REMOVE ANG TOKEN
    // ==============================================
    fun clearToken() {
        prefs.edit().remove(ENCRYPTED_TOKEN_KEY).remove(USER_FOLDER_KEY).apply()
    }

    // ==============================================
    // ✅ I-UPDATE ANG SAVE/LOAD — DECRYPT MUNA BAGO TAWAGIN ANG GITHUB API!
    // ==============================================
    suspend fun saveToGitHub(
        filePath: String, 
        jsonData: JSONObject, 
        commitMsg: String,
        ownerKeyCode: String // ✅ KAILANGAN PARA MA-DECRYPT ANG TOKEN!
    ): Result<String> {
        val token = getDecryptedToken(ownerKeyCode) ?: return Result.failure(
            Exception("❌ Kailangan ng tamang Owner Key Code para magamit ang GitHub!")
        )
        
        // ✅ Ang natitirang code — pareho lang!
        return performGitHubSave(token, filePath, jsonData, commitMsg)
    }

    suspend fun loadFromGitHub(
        filePath: String,
        ownerKeyCode: String // ✅ KAILANGAN PARA MA-DECRYPT ANG TOKEN!
    ): Result<JSONObject> {
        val token = getDecryptedToken(ownerKeyCode) ?: return Result.failure(
            Exception("❌ Kailangan ng tamang Owner Key Code para magbasa sa GitHub!")
        )
        
        return performGitHubLoad(token, filePath)
    }

    // ✅ Ang natitirang function — pareho lang ang performGitHubSave / performGitHubLoad
    private suspend fun performGitHubSave(token: String, filePath: String, jsonData: JSONObject, commitMsg: String): Result<String> {
        // ↓ Parehong code gaya ng nauna — gumagamit na ng decrypted token dito ↓
        return Result.success("✅ Saved") // buuin ang buong logic gaya ng nauna
    }
    
    private suspend fun performGitHubLoad(token: String, filePath: String): Result<JSONObject> {
        return Result.success(JSONObject()) // buuin ang buong logic gaya ng nauna
    }
}

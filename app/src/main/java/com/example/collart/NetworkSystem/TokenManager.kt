import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

object TokenManager {
    private const val ANDROID_KEY_STORE = "AndroidKeyStore"
    private const val KEY_ALIAS = "BearerToken"
    private const val IV_SIZE = 16
    const val secretKey = "tK5UTui+DPh8lIlBxya5XVsmeDCoUl6vHhdIESMB6sQ="
    const val salt = "QWlGNHNhMTJTQWZ2bGhpV3U="
    const val iv = "bVQzNFNhRkQ1Njc4UUFaWA=="

    fun saveToken(context: Context, token: String) {


        val encryptedToken = token.cipherEncrypt()
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().apply {
            putString("encrypted_token", encryptedToken)
            apply()
        }
    }

    fun getToken(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val encryptedToken = sharedPreferences.getString("encrypted_token", null) ?: return null

        val decryptedToken = encryptedToken.cipherDecrypt()
        return decryptedToken
    }

    fun clearToken(context: Context) {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().remove("encrypted_token").apply()
    }

    fun String.cipherEncrypt(): String? {
        try {
            val ivParameterSpec = IvParameterSpec(Base64.decode(iv, Base64.DEFAULT))

            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
            val spec =  PBEKeySpec(secretKey.toCharArray(), Base64.decode(salt, Base64.DEFAULT), 10000, 256)
            val tmp = factory.generateSecret(spec)
            val secretKey =  SecretKeySpec(tmp.encoded, "AES")

            val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec)
            return Base64.encodeToString(cipher.doFinal(this.toByteArray(Charsets.UTF_8)), Base64.DEFAULT)
        } catch (e: Exception) {
            e.message?.let{ Log.e("encryptor", it) }
        }
        return null
    }

    fun String.cipherDecrypt(): String? {
        try {
            val ivParameterSpec =  IvParameterSpec(Base64.decode(iv, Base64.DEFAULT))

            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
            val spec =  PBEKeySpec(secretKey.toCharArray(), Base64.decode(salt, Base64.DEFAULT), 10000, 256)
            val tmp = factory.generateSecret(spec);
            val secretKey =  SecretKeySpec(tmp.encoded, "AES")

            val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
            return  String(cipher.doFinal(Base64.decode(this, Base64.DEFAULT)))
        } catch (e: Exception) {
            e.message?.let{ Log.e("decryptor", it) }
        }
        return null
    }
}
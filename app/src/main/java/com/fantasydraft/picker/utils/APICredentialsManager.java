package com.fantasydraft.picker.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

/**
 * Manages secure storage and retrieval of ESPN API credentials.
 * Uses Android Keystore for encryption.
 */
public class APICredentialsManager {
    
    private static final String PREFS_NAME = "espn_api_credentials";
    private static final String KEY_API_KEY = "api_key_encrypted";
    private static final String KEY_API_SECRET = "api_secret_encrypted";
    private static final String KEY_LEAGUE_ID = "league_id";
    private static final String KEY_SWID = "swid_encrypted";
    private static final String KEY_ESPN_S2 = "espn_s2_encrypted";
    private static final String KEY_IV_SUFFIX = "_iv";
    
    private static final String KEYSTORE_ALIAS = "espn_api_key";
    private static final String ANDROID_KEYSTORE = "AndroidKeyStore";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    
    private Context context;
    private SharedPreferences prefs;
    
    public APICredentialsManager(Context context) {
        this.context = context;
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
    
    /**
     * Save ESPN API credentials securely.
     */
    public void saveCredentials(String apiKey, String apiSecret, String leagueId, 
                                String swid, String espnS2) {
        try {
            // Ensure encryption key exists
            ensureKeyExists();
            
            SharedPreferences.Editor editor = prefs.edit();
            
            // Encrypt and save sensitive data
            if (apiKey != null && !apiKey.isEmpty()) {
                EncryptedData encrypted = encrypt(apiKey);
                editor.putString(KEY_API_KEY, encrypted.data);
                editor.putString(KEY_API_KEY + KEY_IV_SUFFIX, encrypted.iv);
            }
            
            if (apiSecret != null && !apiSecret.isEmpty()) {
                EncryptedData encrypted = encrypt(apiSecret);
                editor.putString(KEY_API_SECRET, encrypted.data);
                editor.putString(KEY_API_SECRET + KEY_IV_SUFFIX, encrypted.iv);
            }
            
            if (swid != null && !swid.isEmpty()) {
                EncryptedData encrypted = encrypt(swid);
                editor.putString(KEY_SWID, encrypted.data);
                editor.putString(KEY_SWID + KEY_IV_SUFFIX, encrypted.iv);
            }
            
            if (espnS2 != null && !espnS2.isEmpty()) {
                EncryptedData encrypted = encrypt(espnS2);
                editor.putString(KEY_ESPN_S2, encrypted.data);
                editor.putString(KEY_ESPN_S2 + KEY_IV_SUFFIX, encrypted.iv);
            }
            
            // League ID is not sensitive, store as plain text
            if (leagueId != null && !leagueId.isEmpty()) {
                editor.putString(KEY_LEAGUE_ID, leagueId);
            }
            
            editor.apply();
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to save credentials: " + e.getMessage());
        }
    }
    
    /**
     * Retrieve API key.
     */
    public String getApiKey() {
        return decryptString(KEY_API_KEY);
    }
    
    /**
     * Retrieve API secret.
     */
    public String getApiSecret() {
        return decryptString(KEY_API_SECRET);
    }
    
    /**
     * Retrieve League ID.
     */
    public String getLeagueId() {
        return prefs.getString(KEY_LEAGUE_ID, "");
    }
    
    /**
     * Retrieve SWID cookie.
     */
    public String getSwid() {
        return decryptString(KEY_SWID);
    }
    
    /**
     * Retrieve ESPN_S2 cookie.
     */
    public String getEspnS2() {
        return decryptString(KEY_ESPN_S2);
    }
    
    /**
     * Check if credentials are configured.
     */
    public boolean hasCredentials() {
        // Check if at least league ID and one authentication method exists
        String leagueId = getLeagueId();
        String swid = getSwid();
        String espnS2 = getEspnS2();
        
        return !leagueId.isEmpty() && (!swid.isEmpty() || !espnS2.isEmpty());
    }
    
    /**
     * Clear all stored credentials.
     */
    public void clearCredentials() {
        prefs.edit().clear().apply();
    }
    
    /**
     * Decrypt a stored string value.
     */
    private String decryptString(String key) {
        try {
            String encryptedData = prefs.getString(key, "");
            String iv = prefs.getString(key + KEY_IV_SUFFIX, "");
            
            if (encryptedData.isEmpty() || iv.isEmpty()) {
                return "";
            }
            
            return decrypt(encryptedData, iv);
            
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    
    /**
     * Ensure encryption key exists in Android Keystore.
     */
    private void ensureKeyExists() throws Exception {
        KeyStore keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);
        keyStore.load(null);
        
        if (!keyStore.containsAlias(KEYSTORE_ALIAS)) {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE);
            
            KeyGenParameterSpec keyGenParameterSpec = new KeyGenParameterSpec.Builder(
                    KEYSTORE_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setRandomizedEncryptionRequired(true)
                    .build();
            
            keyGenerator.init(keyGenParameterSpec);
            keyGenerator.generateKey();
        }
    }
    
    /**
     * Encrypt a string value.
     */
    private EncryptedData encrypt(String plaintext) throws Exception {
        KeyStore keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);
        keyStore.load(null);
        
        SecretKey secretKey = (SecretKey) keyStore.getKey(KEYSTORE_ALIAS, null);
        
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        
        byte[] iv = cipher.getIV();
        byte[] encrypted = cipher.doFinal(plaintext.getBytes("UTF-8"));
        
        String encryptedBase64 = Base64.encodeToString(encrypted, Base64.DEFAULT);
        String ivBase64 = Base64.encodeToString(iv, Base64.DEFAULT);
        
        return new EncryptedData(encryptedBase64, ivBase64);
    }
    
    /**
     * Decrypt a string value.
     */
    private String decrypt(String encryptedData, String ivString) throws Exception {
        KeyStore keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);
        keyStore.load(null);
        
        SecretKey secretKey = (SecretKey) keyStore.getKey(KEYSTORE_ALIAS, null);
        
        byte[] encrypted = Base64.decode(encryptedData, Base64.DEFAULT);
        byte[] iv = Base64.decode(ivString, Base64.DEFAULT);
        
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        GCMParameterSpec spec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);
        
        byte[] decrypted = cipher.doFinal(encrypted);
        
        return new String(decrypted, "UTF-8");
    }
    
    /**
     * Container for encrypted data and IV.
     */
    private static class EncryptedData {
        String data;
        String iv;
        
        EncryptedData(String data, String iv) {
            this.data = data;
            this.iv = iv;
        }
    }
}

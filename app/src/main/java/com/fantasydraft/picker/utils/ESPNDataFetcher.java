package com.fantasydraft.picker.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * Fetches player data from ESPN Fantasy Football API.
 */
public class ESPNDataFetcher {
    
    private static final String TAG = "PlayerDataRefresh";
    private static final String PLAYER_DATA_URL = "https://jdnorton22.github.io/fantasy-draft-data/players.json";
    private static final int TIMEOUT_MILLISECONDS = 30000; // 30 seconds
    
    private Context context;
    private APICredentialsManager credentialsManager;
    
    public enum FetchError {
        NO_NETWORK,
        SERVER_UNREACHABLE,
        TIMEOUT,
        INVALID_RESPONSE,
        NO_CREDENTIALS
    }
    
    public interface FetchCallback {
        void onFetchSuccess(String jsonData);
        void onFetchError(FetchError error, String message);
    }
    
    public ESPNDataFetcher(Context context) {
        this.context = context;
        this.credentialsManager = new APICredentialsManager(context);
    }
    
    /**
     * Fetch player data from GitHub Pages asynchronously.
     */
    public void fetchPlayerData(FetchCallback callback) {
        Log.d(TAG, "Starting player data fetch from GitHub Pages");
        
        // Check network connectivity first
        if (!isNetworkAvailable()) {
            Log.e(TAG, "No network connection available");
            callback.onFetchError(FetchError.NO_NETWORK, "No internet connection");
            return;
        }
        
        Log.d(TAG, "Network available, executing fetch task");
        // Execute fetch on background thread
        new FetchTask(callback).execute();
    }
    
    /**
     * Check if network is available.
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = 
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }
        
        return false;
    }
    
    /**
     * Build the player data URL.
     */
    private String buildApiUrl() {
        Log.d(TAG, "Using GitHub Pages endpoint: " + PLAYER_DATA_URL);
        return PLAYER_DATA_URL;
    }
    
    /**
     * AsyncTask to fetch data on background thread.
     */
    private class FetchTask extends AsyncTask<Void, Void, FetchResult> {
        
        private FetchCallback callback;
        
        FetchTask(FetchCallback callback) {
            this.callback = callback;
        }
        
        @Override
        protected FetchResult doInBackground(Void... voids) {
            HttpURLConnection connection = null;
            
            try {
                String apiUrl = buildApiUrl();
                URL url = new URL(apiUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(TIMEOUT_MILLISECONDS);
                connection.setReadTimeout(TIMEOUT_MILLISECONDS);
                connection.setRequestProperty("Accept", "application/json");
                
                int responseCode = connection.getResponseCode();
                Log.d(TAG, "HTTP Response code: " + responseCode);
                
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Read response
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    
                    reader.close();
                    String responseData = response.toString();
                    Log.d(TAG, "Response received, length: " + responseData.length() + " characters");
                    return new FetchResult(responseData, null, null);
                    
                } else {
                    Log.e(TAG, "Invalid response code: " + responseCode);
                    return new FetchResult(null, FetchError.INVALID_RESPONSE, 
                            "Server returned status code: " + responseCode);
                }
                
            } catch (SocketTimeoutException e) {
                Log.e(TAG, "Request timed out", e);
                return new FetchResult(null, FetchError.TIMEOUT, 
                        "Request timed out after 30 seconds");
                
            } catch (Exception e) {
                Log.e(TAG, "Error fetching data", e);
                return new FetchResult(null, FetchError.SERVER_UNREACHABLE, 
                        "Unable to reach server: " + e.getMessage());
                
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }
        
        @Override
        protected void onPostExecute(FetchResult result) {
            if (result.success) {
                Log.d(TAG, "Fetch successful, calling success callback");
                callback.onFetchSuccess(result.data);
            } else {
                Log.e(TAG, "Fetch failed: " + result.error + " - " + result.errorMessage);
                callback.onFetchError(result.error, result.errorMessage);
            }
        }
    }
    
    /**
     * Result container for fetch operation.
     */
    private static class FetchResult {
        String data;
        FetchError error;
        String errorMessage;
        boolean success;
        
        FetchResult(String data, FetchError error, String errorMessage) {
            this.data = data;
            this.error = error;
            this.errorMessage = errorMessage;
            this.success = (data != null);
        }
    }
}

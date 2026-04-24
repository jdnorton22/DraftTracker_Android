package com.fantasydraft.picker.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.fantasydraft.picker.R;
import com.fantasydraft.picker.utils.APICredentialsManager;

/**
 * Activity for managing ESPN API credentials.
 */
public class APICredentialsActivity extends AppCompatActivity {
    
    private EditText editLeagueId;
    private EditText editSwid;
    private EditText editEspnS2;
    private Button buttonSave;
    private Button buttonClear;
    
    private APICredentialsManager credentialsManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api_credentials);
        
        // Initialize credentials manager
        credentialsManager = new APICredentialsManager(this);
        
        // Initialize views
        initializeViews();
        
        // Load existing credentials
        loadExistingCredentials();
        
        // Setup button handlers
        setupButtonHandlers();
    }
    
    /**
     * Initialize all UI view references.
     */
    private void initializeViews() {
        editLeagueId = findViewById(R.id.edit_league_id);
        editSwid = findViewById(R.id.edit_swid);
        editEspnS2 = findViewById(R.id.edit_espn_s2);
        buttonSave = findViewById(R.id.button_save_credentials);
        buttonClear = findViewById(R.id.button_clear_credentials);
    }
    
    /**
     * Load existing credentials if they exist.
     */
    private void loadExistingCredentials() {
        if (credentialsManager.hasCredentials()) {
            editLeagueId.setText(credentialsManager.getLeagueId());
            editSwid.setText(credentialsManager.getSwid());
            editEspnS2.setText(credentialsManager.getEspnS2());
        }
    }
    
    /**
     * Setup button click handlers.
     */
    private void setupButtonHandlers() {
        buttonSave.setOnClickListener(v -> saveCredentials());
        buttonClear.setOnClickListener(v -> showClearConfirmation());
    }
    
    /**
     * Validate and save credentials.
     */
    private void saveCredentials() {
        String leagueId = editLeagueId.getText().toString().trim();
        String swid = editSwid.getText().toString().trim();
        String espnS2 = editEspnS2.getText().toString().trim();
        
        // Validate league ID
        if (leagueId.isEmpty()) {
            Toast.makeText(this, "League ID is required", Toast.LENGTH_SHORT).show();
            editLeagueId.requestFocus();
            return;
        }
        
        // Validate that at least one authentication method is provided
        if (swid.isEmpty() && espnS2.isEmpty()) {
            new AlertDialog.Builder(this)
                    .setTitle("No Authentication")
                    .setMessage("For private leagues, you need to provide at least one authentication cookie (SWID or espn_s2). " +
                            "Public leagues may work without authentication.\n\nContinue anyway?")
                    .setPositiveButton("Continue", (dialog, which) -> performSave(leagueId, swid, espnS2))
                    .setNegativeButton("Cancel", null)
                    .show();
            return;
        }
        
        performSave(leagueId, swid, espnS2);
    }
    
    /**
     * Perform the actual save operation.
     */
    private void performSave(String leagueId, String swid, String espnS2) {
        try {
            credentialsManager.saveCredentials(null, null, leagueId, swid, espnS2);
            
            Toast.makeText(this, "Credentials saved successfully", Toast.LENGTH_SHORT).show();
            
            // Set result and finish
            setResult(RESULT_OK);
            finish();
            
        } catch (Exception e) {
            Toast.makeText(this, "Failed to save credentials: " + e.getMessage(), 
                    Toast.LENGTH_LONG).show();
        }
    }
    
    /**
     * Show confirmation dialog before clearing credentials.
     */
    private void showClearConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Clear Credentials?")
                .setMessage("This will remove all saved ESPN API credentials. You will need to re-enter them to use the player data refresh feature.")
                .setPositiveButton("Clear", (dialog, which) -> clearCredentials())
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    /**
     * Clear all credentials.
     */
    private void clearCredentials() {
        credentialsManager.clearCredentials();
        
        // Clear UI fields
        editLeagueId.setText("");
        editSwid.setText("");
        editEspnS2.setText("");
        
        Toast.makeText(this, "Credentials cleared", Toast.LENGTH_SHORT).show();
    }
}

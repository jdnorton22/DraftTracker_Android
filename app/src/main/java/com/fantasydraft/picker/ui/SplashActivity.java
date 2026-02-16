package com.fantasydraft.picker.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.fantasydraft.picker.R;

/**
 * Splash screen activity displayed on app launch.
 * Shows app branding and loading progress for 3 seconds.
 */
public class SplashActivity extends AppCompatActivity {
    
    private static final int SPLASH_DURATION = 2000; // 2 seconds
    private static final int PROGRESS_UPDATE_INTERVAL = 30; // Update every 30ms
    
    private ProgressBar progressBar;
    private TextView loadingText;
    private Handler handler;
    private int progressStatus = 0;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        
        // Hide action bar if present
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        
        progressBar = findViewById(R.id.progress_bar);
        loadingText = findViewById(R.id.loading_text);
        handler = new Handler();
        
        // Start progress animation
        startProgressAnimation();
        
        // Navigate to MainActivity after splash duration
        handler.postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, SPLASH_DURATION);
    }
    
    /**
     * Animate the progress bar from 0 to 100% over the splash duration.
     */
    private void startProgressAnimation() {
        new Thread(() -> {
            int totalSteps = SPLASH_DURATION / PROGRESS_UPDATE_INTERVAL;
            int progressIncrement = 100 / totalSteps;
            
            while (progressStatus < 100) {
                progressStatus += progressIncrement;
                
                // Ensure we don't exceed 100
                if (progressStatus > 100) {
                    progressStatus = 100;
                }
                
                // Update progress bar on UI thread
                handler.post(() -> {
                    progressBar.setProgress(progressStatus);
                    updateLoadingText(progressStatus);
                });
                
                try {
                    Thread.sleep(PROGRESS_UPDATE_INTERVAL);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    
    /**
     * Update loading text based on progress.
     */
    private void updateLoadingText(int progress) {
        if (progress < 30) {
            loadingText.setText("Loading players...");
        } else if (progress < 60) {
            loadingText.setText("Preparing draft...");
        } else if (progress < 90) {
            loadingText.setText("Almost ready...");
        } else {
            loadingText.setText("Let's draft!");
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up handler callbacks
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}

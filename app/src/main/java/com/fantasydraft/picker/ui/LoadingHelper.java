package com.fantasydraft.picker.ui;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fantasydraft.picker.R;

/**
 * Helper class for managing loading states and visual feedback across the app.
 * Provides consistent loading indicators and accessibility support.
 */
public class LoadingHelper {
    
    private View loadingOverlay;
    private ProgressBar progressBar;
    private TextView loadingText;
    
    /**
     * Initialize loading helper with a root view that contains loading_overlay
     * @param rootView The root view containing the loading overlay
     */
    public LoadingHelper(View rootView) {
        if (rootView != null) {
            loadingOverlay = rootView.findViewById(R.id.loading_overlay);
            if (loadingOverlay != null) {
                progressBar = loadingOverlay.findViewById(R.id.loading_progress);
                loadingText = loadingOverlay.findViewById(R.id.loading_text);
            }
        }
    }
    
    /**
     * Show loading overlay with default message
     */
    public void showLoading() {
        showLoading(null);
    }
    
    /**
     * Show loading overlay with custom message
     * @param message Custom loading message, or null for default
     */
    public void showLoading(String message) {
        if (loadingOverlay != null) {
            if (loadingText != null && message != null) {
                loadingText.setText(message);
            }
            loadingOverlay.setVisibility(View.VISIBLE);
            loadingOverlay.bringToFront();
        }
    }
    
    /**
     * Hide loading overlay
     */
    public void hideLoading() {
        if (loadingOverlay != null) {
            loadingOverlay.setVisibility(View.GONE);
        }
    }
    
    /**
     * Check if loading is currently visible
     * @return true if loading overlay is visible
     */
    public boolean isLoading() {
        return loadingOverlay != null && loadingOverlay.getVisibility() == View.VISIBLE;
    }
    
    /**
     * Add ripple effect to a view for better touch feedback
     * @param view The view to add ripple effect to
     */
    public static void addRippleEffect(View view) {
        if (view != null && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int[] attrs = new int[]{android.R.attr.selectableItemBackground};
            android.content.res.TypedArray typedArray = view.getContext().obtainStyledAttributes(attrs);
            int backgroundResource = typedArray.getResourceId(0, 0);
            view.setBackgroundResource(backgroundResource);
            typedArray.recycle();
        }
    }
    
    /**
     * Set minimum touch target size for accessibility
     * @param view The view to update
     * @param minSizeDp Minimum size in dp (recommended 48dp)
     */
    public static void setMinTouchTarget(View view, int minSizeDp) {
        if (view != null) {
            float density = view.getContext().getResources().getDisplayMetrics().density;
            int minSizePx = (int) (minSizeDp * density);
            
            ViewGroup.LayoutParams params = view.getLayoutParams();
            if (params != null) {
                if (params.width < minSizePx) {
                    params.width = minSizePx;
                }
                if (params.height < minSizePx) {
                    params.height = minSizePx;
                }
                view.setLayoutParams(params);
            }
            
            // Also set minimum width/height
            view.setMinimumWidth(minSizePx);
            view.setMinimumHeight(minSizePx);
        }
    }
    
    /**
     * Announce message to accessibility services
     * @param view Any view in the hierarchy
     * @param message Message to announce
     */
    public static void announceForAccessibility(View view, String message) {
        if (view != null && message != null) {
            view.announceForAccessibility(message);
        }
    }
}

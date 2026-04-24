package com.fantasydraft.picker.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.fantasydraft.picker.R;

/**
 * Walkthrough overlay that highlights key sections of the draft screen on first launch.
 */
public class DraftWalkthrough {

    private static final String PREFS_NAME = "draft_walkthrough";
    private static final String KEY_COMPLETED = "walkthrough_completed";

    private final View rootView;
    private final Context context;
    private View overlayView;
    private int currentStep = 0;

    private static final int[][] STEPS = {
        // { viewId, titleResId, descResId } — we'll use string arrays instead
    };

    private final int[] targetViewIds;
    private final String[] titles;
    private final String[] descriptions;

    public DraftWalkthrough(View rootView) {
        this.rootView = rootView;
        this.context = rootView.getContext();

        targetViewIds = new int[] {
            R.id.card_draft_info,
            R.id.card_draft_summary,
            R.id.card_best_available,
            R.id.layout_action_buttons,
            R.id.card_recent_picks
        };

        titles = new String[] {
            "Draft Info",
            "Draft Summary",
            "Best Available",
            "Draft Actions",
            "Recent Picks"
        };

        descriptions = new String[] {
            "Shows your current pick number, round, and which team is on the clock. Tap to collapse or expand the details.",
            "Track how many players have been drafted at each position. Toggle between your team's counts and league-wide totals. Tap the roster icon to view any team's drafted players.",
            "Displays the top-ranked undrafted player with draft advisor recommendations. Filter by position using the buttons. The card shows ADP value grade and elite status.",
            "'Draft' opens the full player list to search and select any available player. 'Reset' clears all picks and starts a new draft.",
            "Your last 3 picks at a glance with value indicators. 'View All' opens the complete draft history. 'Analytics' appears after draft completion to view league-wide grades."
        };
    }

    /**
     * Check if walkthrough should be shown (first launch only).
     */
    public boolean shouldShow() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return !prefs.getBoolean(KEY_COMPLETED, false);
    }

    /**
     * Start the walkthrough overlay.
     */
    public void start() {
        if (!(rootView instanceof ViewGroup)) return;

        currentStep = 0;
        overlayView = LayoutInflater.from(context).inflate(R.layout.overlay_walkthrough, null);

        // Add overlay to the root activity's decor view for full-screen coverage
        ViewGroup decorView = (ViewGroup) rootView.getRootView();
        decorView.addView(overlayView, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        Button nextBtn = overlayView.findViewById(R.id.walkthrough_next);
        Button skipBtn = overlayView.findViewById(R.id.walkthrough_skip);

        nextBtn.setOnClickListener(v -> {
            currentStep++;
            if (currentStep >= titles.length) {
                finish();
            } else {
                showStep();
            }
        });

        skipBtn.setOnClickListener(v -> finish());

        showStep();
    }

    private void showStep() {
        if (overlayView == null) return;

        TextView titleView = overlayView.findViewById(R.id.walkthrough_title);
        TextView descView = overlayView.findViewById(R.id.walkthrough_description);
        TextView stepIndicator = overlayView.findViewById(R.id.walkthrough_step_indicator);
        Button nextBtn = overlayView.findViewById(R.id.walkthrough_next);

        titleView.setText(titles[currentStep]);
        descView.setText(descriptions[currentStep]);
        stepIndicator.setText((currentStep + 1) + " of " + titles.length);

        boolean isLast = currentStep == titles.length - 1;
        nextBtn.setText(isLast ? "Done" : "Next");

        // Highlight the target view by scrolling it into view
        View target = rootView.findViewById(targetViewIds[currentStep]);
        if (target != null) {
            target.getParent().requestChildFocus(target, target);
        }
    }

    private void finish() {
        // Mark walkthrough as completed
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_COMPLETED, true).apply();

        // Remove overlay
        if (overlayView != null && overlayView.getParent() != null) {
            ((ViewGroup) overlayView.getParent()).removeView(overlayView);
            overlayView = null;
        }
    }

    /**
     * Reset walkthrough so it shows again on next launch.
     */
    public static void reset(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_COMPLETED, false).apply();
    }
}

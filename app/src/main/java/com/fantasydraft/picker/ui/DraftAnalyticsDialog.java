package com.fantasydraft.picker.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.fantasydraft.picker.R;
import com.fantasydraft.picker.models.DraftAnalytics;
import com.fantasydraft.picker.utils.PositionColors;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Dialog showing draft analytics and grades for all teams after draft completion
 */
public class DraftAnalyticsDialog extends Dialog {
    
    private List<DraftAnalytics> allAnalytics;
    private DraftAnalytics currentAnalytics;
    private Context context;
    
    // Roster dialog dependencies
    private java.util.List<com.fantasydraft.picker.models.Team> teams;
    private com.fantasydraft.picker.managers.DraftManager draftManager;
    private com.fantasydraft.picker.managers.PlayerManager playerManager;
    private com.fantasydraft.picker.models.DraftConfig draftConfig;
    
    // UI Components
    private Spinner spinnerTeamSelector;
    private TextView textTeamName;
    private TextView textOverallGrade;
    private TextView textGradeScore;
    private TextView textDraftStrategy;
    private TextView textValuePicks;
    private TextView textReachPicks;
    private TextView textAvgAdpDiff;
    private TextView textBestPick;
    private TextView textWorstPick;
    private LinearLayout layoutWorstPick;
    private LinearLayout layoutPositionDistribution;
    private Button buttonShareResults;
    private Button buttonViewFullDraft;
    private ImageButton buttonCloseAnalytics;
    
    public DraftAnalyticsDialog(@NonNull Context context, List<DraftAnalytics> allAnalytics, String defaultTeamId) {
        this(context, allAnalytics, defaultTeamId, null, null, null, null);
    }
    
    public DraftAnalyticsDialog(@NonNull Context context, List<DraftAnalytics> allAnalytics, String defaultTeamId,
                                 java.util.List<com.fantasydraft.picker.models.Team> teams,
                                 com.fantasydraft.picker.managers.DraftManager draftManager,
                                 com.fantasydraft.picker.managers.PlayerManager playerManager,
                                 com.fantasydraft.picker.models.DraftConfig draftConfig) {
        super(context);
        this.context = context;
        this.allAnalytics = allAnalytics;
        this.teams = teams;
        this.draftManager = draftManager;
        this.playerManager = playerManager;
        this.draftConfig = draftConfig;
        
        // Find the default team's analytics
        for (DraftAnalytics analytics : allAnalytics) {
            if (analytics.getTeamId().equals(defaultTeamId)) {
                this.currentAnalytics = analytics;
                break;
            }
        }
        
        // Fallback to first team if not found
        if (this.currentAnalytics == null && !allAnalytics.isEmpty()) {
            this.currentAnalytics = allAnalytics.get(0);
        }
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_draft_analytics);
        
        // Set dialog width to 95% of screen width
        Window window = getWindow();
        if (window != null) {
            android.view.WindowManager.LayoutParams params = window.getAttributes();
            android.util.DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            params.width = (int) (displayMetrics.widthPixels * 0.95);
            window.setAttributes(params);
        }
        
        initializeViews();
        populateData();
        setupButtons();
    }
    
    private void initializeViews() {
        spinnerTeamSelector = findViewById(R.id.spinner_team_selector_analytics);
        textTeamName = findViewById(R.id.text_team_name);
        textOverallGrade = findViewById(R.id.text_overall_grade);
        textGradeScore = findViewById(R.id.text_grade_score);
        textDraftStrategy = findViewById(R.id.text_draft_strategy);
        textValuePicks = findViewById(R.id.text_value_picks);
        textReachPicks = findViewById(R.id.text_reach_picks);
        textAvgAdpDiff = findViewById(R.id.text_avg_adp_diff);
        textBestPick = findViewById(R.id.text_best_pick);
        textWorstPick = findViewById(R.id.text_worst_pick);
        layoutWorstPick = findViewById(R.id.layout_worst_pick);
        layoutPositionDistribution = findViewById(R.id.layout_position_distribution);
        buttonShareResults = findViewById(R.id.button_share_results);
        buttonViewFullDraft = findViewById(R.id.button_view_full_draft);
        buttonCloseAnalytics = findViewById(R.id.button_close_analytics);
        
        setupTeamSelector();
    }
    
    private void setupTeamSelector() {
        // Create list of team names with grades
        List<String> teamNames = new ArrayList<>();
        for (DraftAnalytics analytics : allAnalytics) {
            teamNames.add(analytics.getTeamName() + " (" + analytics.getOverallGradeLetter() + ")");
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                context, android.R.layout.simple_spinner_item, teamNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTeamSelector.setAdapter(adapter);
        
        // Set default selection
        int defaultIndex = 0;
        for (int i = 0; i < allAnalytics.size(); i++) {
            if (allAnalytics.get(i).getTeamId().equals(currentAnalytics.getTeamId())) {
                defaultIndex = i;
                break;
            }
        }
        spinnerTeamSelector.setSelection(defaultIndex);
        
        // Handle team selection changes
        spinnerTeamSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentAnalytics = allAnalytics.get(position);
                populateData();
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No action needed
            }
        });
    }
    
    private void populateData() {
        if (currentAnalytics == null) return;
        
        // Team name
        textTeamName.setText(currentAnalytics.getTeamName());
        
        // Overall grade
        String gradeLetter = currentAnalytics.getOverallGradeLetter();
        textOverallGrade.setText(gradeLetter);
        textGradeScore.setText(String.format(Locale.US, "%.1f / 100", currentAnalytics.getOverallGrade()));
        
        // Set grade color
        int gradeColor = getGradeColor(currentAnalytics.getOverallGrade());
        textOverallGrade.setTextColor(gradeColor);
        
        // Draft strategy
        textDraftStrategy.setText(currentAnalytics.getDraftStrategy());
        
        // Key stats
        textValuePicks.setText(String.valueOf(currentAnalytics.getValuePicks()));
        textReachPicks.setText(String.valueOf(currentAnalytics.getReachPicks()));
        
        double avgDiff = currentAnalytics.getAverageAdpDifference();
        String avgDiffText = avgDiff >= 0 ? 
            String.format(Locale.US, "+%.1f", avgDiff) : 
            String.format(Locale.US, "%.1f", avgDiff);
        textAvgAdpDiff.setText(avgDiffText);
        textAvgAdpDiff.setTextColor(avgDiff >= 0 ? Color.parseColor("#2E7D32") : Color.parseColor("#D32F2F"));
        
        // Best/Worst picks
        if (currentAnalytics.getBestPick() != null) {
            textBestPick.setText(currentAnalytics.getBestPick());
        } else {
            textBestPick.setText("N/A");
        }
        
        if (currentAnalytics.getWorstPick() != null) {
            textWorstPick.setText(currentAnalytics.getWorstPick());
            layoutWorstPick.setVisibility(View.VISIBLE);
        } else {
            layoutWorstPick.setVisibility(View.GONE);
        }
        
        // Position distribution
        populatePositionDistribution();
    }
    
    private void populatePositionDistribution() {
        layoutPositionDistribution.removeAllViews();
        
        Map<String, Integer> positionCounts = currentAnalytics.getPositionCounts();
        if (positionCounts == null || positionCounts.isEmpty()) return;
        
        String[] positions = {"QB", "RB", "WR", "TE", "K", "DST"};
        
        for (String position : positions) {
            int count = positionCounts.getOrDefault(position, 0);
            
            LinearLayout row = new LinearLayout(context);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            
            LinearLayout.LayoutParams rowParams = (LinearLayout.LayoutParams) row.getLayoutParams();
            rowParams.bottomMargin = (int) (4 * context.getResources().getDisplayMetrics().density);
            row.setLayoutParams(rowParams);
            
            // Position badge
            TextView badge = new TextView(context);
            badge.setText(position);
            badge.setTextSize(12);
            badge.setTextColor(Color.WHITE);
            badge.setGravity(android.view.Gravity.CENTER);
            
            int badgeSize = (int) (32 * context.getResources().getDisplayMetrics().density);
            LinearLayout.LayoutParams badgeParams = new LinearLayout.LayoutParams(badgeSize, badgeSize);
            badgeParams.rightMargin = (int) (8 * context.getResources().getDisplayMetrics().density);
            badge.setLayoutParams(badgeParams);
            
            android.graphics.drawable.GradientDrawable circle = new android.graphics.drawable.GradientDrawable();
            circle.setShape(android.graphics.drawable.GradientDrawable.OVAL);
            circle.setColor(PositionColors.getColorForPosition(position));
            badge.setBackground(circle);
            
            // Count text
            TextView countText = new TextView(context);
            countText.setText(String.valueOf(count));
            countText.setTextSize(16);
            countText.setTextColor(Color.parseColor("#000000"));
            countText.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            
            row.addView(badge);
            row.addView(countText);
            layoutPositionDistribution.addView(row);
        }
        
        // Add "View Roster" link
        if (teams != null && draftManager != null && playerManager != null) {
            TextView viewRosterLink = new TextView(context);
            viewRosterLink.setText("View Full Roster →");
            viewRosterLink.setTextSize(14);
            viewRosterLink.setTextColor(Color.parseColor("#1976D2"));
            viewRosterLink.setTypeface(null, android.graphics.Typeface.BOLD);
            viewRosterLink.setPadding(0, (int) (12 * context.getResources().getDisplayMetrics().density), 0, 0);
            viewRosterLink.setOnClickListener(v -> {
                String teamId = currentAnalytics.getTeamId();
                TeamRosterDialog rosterDialog = new TeamRosterDialog(
                        context, teams, teamId, draftManager, playerManager, draftConfig);
                rosterDialog.show();
            });
            layoutPositionDistribution.addView(viewRosterLink);
        }
    }
    
    private int getGradeColor(double grade) {
        if (grade >= 85) return Color.parseColor("#2E7D32"); // Green
        if (grade >= 70) return Color.parseColor("#4682B4"); // Blue
        if (grade >= 60) return Color.parseColor("#FF8C00"); // Orange
        return Color.parseColor("#D32F2F"); // Red
    }
    
    private void setupButtons() {
        buttonCloseAnalytics.setOnClickListener(v -> dismiss());
        
        buttonShareResults.setOnClickListener(v -> shareResults());
        
        buttonViewFullDraft.setOnClickListener(v -> {
            // Open draft history activity with data
            if (context instanceof MainActivity) {
                MainActivity mainActivity = (MainActivity) context;
                Intent intent = new Intent(context, DraftHistoryActivity.class);
                
                // Pass pick history
                intent.putParcelableArrayListExtra(
                    DraftHistoryActivity.EXTRA_PICK_HISTORY,
                    new ArrayList<>(mainActivity.getPickHistory())
                );
                
                // Pass teams
                intent.putParcelableArrayListExtra(
                    DraftHistoryActivity.EXTRA_TEAMS,
                    new ArrayList<>(mainActivity.getTeams())
                );
                
                // Pass players
                intent.putParcelableArrayListExtra(
                    DraftHistoryActivity.EXTRA_PLAYERS,
                    new ArrayList<>(mainActivity.getPlayerManager().getPlayers())
                );
                
                context.startActivity(intent);
            }
            dismiss();
        });
    }
    
    private void shareResults() {
        StringBuilder shareText = new StringBuilder();
        shareText.append("🏈 Draft Grade Report\n\n");
        shareText.append("Team: ").append(currentAnalytics.getTeamName()).append("\n");
        shareText.append("Grade: ").append(currentAnalytics.getOverallGradeLetter());
        shareText.append(" (").append(String.format(Locale.US, "%.1f", currentAnalytics.getOverallGrade())).append("/100)\n");
        shareText.append("Strategy: ").append(currentAnalytics.getDraftStrategy()).append("\n\n");
        shareText.append("Value Picks: ").append(currentAnalytics.getValuePicks()).append("\n");
        shareText.append("Reach Picks: ").append(currentAnalytics.getReachPicks()).append("\n");
        
        if (currentAnalytics.getBestPick() != null) {
            shareText.append("\nBest Value: ").append(currentAnalytics.getBestPick()).append("\n");
        }
        
        shareText.append("\n#FantasyFootball #DraftGrade");
        
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText.toString());
        context.startActivity(Intent.createChooser(shareIntent, "Share Draft Grade"));
    }
}

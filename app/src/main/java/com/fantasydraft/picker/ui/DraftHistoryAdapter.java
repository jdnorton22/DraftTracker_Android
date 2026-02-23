package com.fantasydraft.picker.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fantasydraft.picker.R;
import com.fantasydraft.picker.models.Pick;
import com.fantasydraft.picker.models.Player;
import com.fantasydraft.picker.models.Team;
import com.fantasydraft.picker.utils.PositionColors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * RecyclerView adapter for displaying draft history.
 * Shows pick number, team name, player name, and position for each pick.
 */
public class DraftHistoryAdapter extends RecyclerView.Adapter<DraftHistoryAdapter.PickViewHolder> {

    private List<Pick> picks;
    private Map<String, Team> teamMap;
    private Map<String, Player> playerMap;
    private OnUndoClickListener undoListener;

    public interface OnUndoClickListener {
        void onUndoClick(Pick pick, int position);
    }

    public DraftHistoryAdapter() {
        this.picks = new ArrayList<>();
    }

    public void setData(List<Pick> picks, Map<String, Team> teamMap, Map<String, Player> playerMap) {
        this.picks = picks != null ? picks : new ArrayList<>();
        this.teamMap = teamMap;
        this.playerMap = playerMap;
        notifyDataSetChanged();
    }

    public void setUndoListener(OnUndoClickListener listener) {
        this.undoListener = listener;
    }

    @NonNull
    @Override
    public PickViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_draft_pick, parent, false);
        return new PickViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PickViewHolder holder, int position) {
        Pick pick = picks.get(position);
        
        // Display pick number (without the period)
        holder.pickNumber.setText(String.valueOf(pick.getPickNumber()));
        
        // Display team name with bye week
        Team team = teamMap != null ? teamMap.get(pick.getTeamId()) : null;
        String teamName = team != null ? team.getName() : "Unknown Team";
        
        // Get player to access bye week
        Player player = playerMap != null ? playerMap.get(pick.getPlayerId()) : null;
        if (player != null && player.getByeWeek() > 0) {
            teamName += " (bye-" + player.getByeWeek() + ")";
        }
        holder.teamName.setText(teamName);
        
        // Find the highest pick number (most recent pick) to show undo button
        int highestPickNumber = 0;
        for (Pick p : picks) {
            if (p.getPickNumber() > highestPickNumber) {
                highestPickNumber = p.getPickNumber();
            }
        }
        
        // Only show undo button for the most recent pick (highest pick number)
        if (pick.getPickNumber() == highestPickNumber) {
            holder.undoButton.setVisibility(View.VISIBLE);
            holder.undoButton.setOnClickListener(v -> {
                if (undoListener != null) {
                    undoListener.onUndoClick(pick, position);
                }
            });
        } else {
            holder.undoButton.setVisibility(View.GONE);
        }
        
        // Display player name and position
        if (player != null) {
            // Apply position-based color to the pick number circle
            int backgroundColor = PositionColors.getColorForPosition(player.getPosition());
            android.graphics.drawable.GradientDrawable circle = new android.graphics.drawable.GradientDrawable();
            circle.setShape(android.graphics.drawable.GradientDrawable.OVAL);
            circle.setColor(backgroundColor);
            holder.pickNumber.setBackground(circle);
            
            // Display position with NFL team (no bye week here)
            String positionInfo = player.getPosition();
            if (player.getNflTeam() != null && !player.getNflTeam().isEmpty()) {
                positionInfo += " - " + player.getNflTeam();
            }
            String playerInfo = player.getName() + " (" + positionInfo + ")";
            
            // ESPN links disabled - display as plain text
            holder.playerInfo.setText(playerInfo);
            
            // Display rankings
            holder.overallRank.setText(String.valueOf(player.getRank()));
            
            if (player.getPffRank() > 0) {
                holder.pffRank.setText("ADP:" + player.getPffRank());
                holder.pffRank.setVisibility(View.VISIBLE);
            } else {
                holder.pffRank.setVisibility(View.GONE);
            }
            
            if (player.getPositionRank() > 0) {
                holder.positionRank.setText(player.getPosition() + player.getPositionRank());
                holder.positionRank.setVisibility(View.VISIBLE);
            } else {
                holder.positionRank.setVisibility(View.GONE);
            }
            
            // Display statistics
            if (player.getLastYearStats() != null && !player.getLastYearStats().isEmpty()) {
                holder.playerStats.setText("Last Year: " + player.getLastYearStats());
                holder.playerStats.setVisibility(View.VISIBLE);
            } else {
                holder.playerStats.setVisibility(View.GONE);
            }
            
            // Display injury status with color coding
            if (player.getInjuryStatus() != null && !player.getInjuryStatus().isEmpty() && 
                !player.getInjuryStatus().equalsIgnoreCase("HEALTHY")) {
                holder.injuryStatus.setText(player.getInjuryStatus());
                holder.injuryStatus.setVisibility(View.VISIBLE);
                
                // Color code based on severity
                String status = player.getInjuryStatus().toUpperCase();
                if (status.equals("OUT") || status.equals("IR")) {
                    holder.injuryStatus.setTextColor(0xFFD32F2F); // Red
                } else if (status.equals("DOUBTFUL")) {
                    holder.injuryStatus.setTextColor(0xFFFF6F00); // Dark Orange
                } else if (status.equals("QUESTIONABLE")) {
                    holder.injuryStatus.setTextColor(0xFFFFA000); // Orange/Yellow
                } else {
                    holder.injuryStatus.setTextColor(0xFF757575); // Gray
                }
            } else {
                holder.injuryStatus.setVisibility(View.GONE);
            }
        } else {
            holder.playerInfo.setText("Unknown Player");
            holder.overallRank.setText("-");
            holder.pffRank.setVisibility(View.GONE);
            holder.positionRank.setVisibility(View.GONE);
            holder.playerStats.setVisibility(View.GONE);
            
            // Reset to default gray circle
            android.graphics.drawable.GradientDrawable circle = new android.graphics.drawable.GradientDrawable();
            circle.setShape(android.graphics.drawable.GradientDrawable.OVAL);
            circle.setColor(0xFFCCCCCC);
            holder.pickNumber.setBackground(circle);
        }
    }

    @Override
    public int getItemCount() {
        return picks.size();
    }

    static class PickViewHolder extends RecyclerView.ViewHolder {
        View rootView;
        TextView pickNumber;
        TextView overallRank;
        TextView pffRank;
        TextView positionRank;
        TextView teamName;
        TextView playerInfo;
        TextView playerStats;
        TextView injuryStatus;
        android.widget.ImageButton undoButton;

        PickViewHolder(@NonNull View itemView) {
            super(itemView);
            rootView = itemView.findViewById(R.id.draft_pick_item_root);
            pickNumber = itemView.findViewById(R.id.text_pick_number);
            overallRank = itemView.findViewById(R.id.text_overall_rank);
            pffRank = itemView.findViewById(R.id.text_pff_rank);
            positionRank = itemView.findViewById(R.id.text_position_rank);
            teamName = itemView.findViewById(R.id.text_team_name);
            playerInfo = itemView.findViewById(R.id.text_player_info);
            playerStats = itemView.findViewById(R.id.text_player_stats);
            injuryStatus = itemView.findViewById(R.id.text_injury_status);
            undoButton = itemView.findViewById(R.id.button_undo_pick);
        }
    }
}

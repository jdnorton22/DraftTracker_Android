package com.fantasydraft.picker.ui;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.fantasydraft.picker.R;
import com.fantasydraft.picker.models.Player;
import com.fantasydraft.picker.utils.PositionColors;
import com.fantasydraft.picker.utils.PickValueCalculator;

import java.util.ArrayList;
import java.util.List;

public class PlayerSelectionAdapter extends RecyclerView.Adapter<PlayerSelectionAdapter.PlayerViewHolder> {

    private List<Player> players;
    private List<Player> filteredPlayers;
    private OnPlayerClickListener listener;
    private boolean hideDrafted = false;
    private int currentPickNumber = 0;
    private int currentRound = 0;
    private boolean sortByAdp = false;

    public interface OnPlayerClickListener {
        void onPlayerClick(Player player);
    }

    public PlayerSelectionAdapter(List<Player> players, OnPlayerClickListener listener) {
        this.players = players;
        this.filteredPlayers = new ArrayList<>(players);
        this.listener = listener;
    }
    
    public void setCurrentPickNumber(int pickNumber) {
        this.currentPickNumber = pickNumber;
    }
    
    public void setCurrentRound(int round) {
        this.currentRound = round;
    }
    
    public void setSortByAdp(boolean sortByAdp) {
        this.sortByAdp = sortByAdp;
    }

    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_player_selection, parent, false);
        return new PlayerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {
        Player player = filteredPlayers.get(position);
        holder.bind(player, listener, currentPickNumber, currentRound);
    }

    @Override
    public int getItemCount() {
        return filteredPlayers.size();
    }

    public void setHideDrafted(boolean hideDrafted) {
        this.hideDrafted = hideDrafted;
    }

    private boolean shouldShowPlayer(Player player, String query, String position, String team) {
        // Check drafted status filter
        if (hideDrafted && player.isDrafted()) {
            return false;
        }
        
        // Check position filter
        if (position != null && !position.equals("ALL")) {
            if (!player.getPosition().equalsIgnoreCase(position)) {
                return false;
            }
        }
        
        // Check team filter
        if (team != null && !team.equals("All Teams")) {
            if (player.getNflTeam() == null || !player.getNflTeam().equals(team)) {
                return false;
            }
        }
        
        // Check search query filter
        if (query != null && !query.trim().isEmpty()) {
            String lowerQuery = query.toLowerCase().trim();
            return player.getName().toLowerCase().contains(lowerQuery) ||
                   player.getPosition().toLowerCase().contains(lowerQuery) ||
                   (player.getNflTeam() != null && player.getNflTeam().toLowerCase().contains(lowerQuery));
        }
        
        return true;
    }

    public void filter(String query, String position, String team) {
        filteredPlayers.clear();
        
        for (Player player : players) {
            if (shouldShowPlayer(player, query, position, team)) {
                filteredPlayers.add(player);
            }
        }
        
        // Sort by rank or ADP based on setting
        if (sortByAdp) {
            filteredPlayers.sort((p1, p2) -> {
                int adp1 = p1.getPffRank() > 0 ? p1.getPffRank() : Integer.MAX_VALUE;
                int adp2 = p2.getPffRank() > 0 ? p2.getPffRank() : Integer.MAX_VALUE;
                int adpCompare = Integer.compare(adp1, adp2);
                if (adpCompare != 0) return adpCompare;
                return Integer.compare(p1.getRank(), p2.getRank());
            });
        } else {
            filteredPlayers.sort((p1, p2) -> {
                int rankCompare = Integer.compare(p1.getRank(), p2.getRank());
                if (rankCompare != 0) return rankCompare;
                return Integer.compare(p1.getPositionRank(), p2.getPositionRank());
            });
        }
        
        notifyDataSetChanged();
    }
    
    // Keep old filter method for backward compatibility
    public void filter(String query) {
        filter(query, "ALL", "All Teams");
    }

    public void updatePlayers(List<Player> newPlayers) {
        this.players = newPlayers;
        this.filteredPlayers = new ArrayList<>(newPlayers);
        notifyDataSetChanged();
    }

    public int getFilteredCount() {
        return filteredPlayers.size();
    }

    public int getTotalCount() {
        return players.size();
    }

    static class PlayerViewHolder extends RecyclerView.ViewHolder {
        private View rootView;
        private TextView positionBadgeText;
        private TextView pffRankText;
        private TextView positionRankText;
        private TextView nameText;
        private TextView positionText;
        private TextView statsText;
        private TextView statusText;
        private TextView injuryStatusText;

        public PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            rootView = itemView.findViewById(R.id.player_item_root);
            positionBadgeText = itemView.findViewById(R.id.player_position_badge);
            pffRankText = itemView.findViewById(R.id.player_pff_rank);
            positionRankText = itemView.findViewById(R.id.player_position_rank);
            nameText = itemView.findViewById(R.id.player_name);
            positionText = itemView.findViewById(R.id.player_position);
            statsText = itemView.findViewById(R.id.player_stats);
            statusText = itemView.findViewById(R.id.player_status);
            injuryStatusText = itemView.findViewById(R.id.player_injury_status);
        }

        public void bind(Player player, OnPlayerClickListener listener, int currentPickNumber, int currentRound) {
            // Apply favorite highlight or default transparent background
            if (player.isFavorite()) {
                rootView.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.favorite_highlight));
            } else {
                rootView.setBackgroundColor(0x00000000); // Transparent
            }
            
            // Set position badge with color
            positionBadgeText.setText(player.getPosition());
            android.graphics.drawable.GradientDrawable badgeCircle = new android.graphics.drawable.GradientDrawable();
            badgeCircle.setShape(android.graphics.drawable.GradientDrawable.OVAL);
            badgeCircle.setColor(PositionColors.getColorForPosition(player.getPosition()));
            positionBadgeText.setBackground(badgeCircle);
            
            // Player name (with favorite star)
            nameText.setText(player.isFavorite() ? player.getName() + " ⭐" : player.getName());
            nameText.setTextColor(itemView.getContext().getResources().getColor(R.color.text_primary, null));
            nameText.setPaintFlags(nameText.getPaintFlags() & ~android.graphics.Paint.UNDERLINE_TEXT_FLAG);
            nameText.setOnClickListener(null);
            
            // Show team abbreviation and position rank on second line
            StringBuilder infoLine = new StringBuilder();
            if (player.getNflTeam() != null && !player.getNflTeam().isEmpty()) {
                infoLine.append(player.getNflTeam());
            }
            positionText.setText(infoLine.toString());
            positionText.setVisibility(infoLine.length() > 0 ? View.VISIBLE : View.GONE);
            positionText.setTextColor(itemView.getContext().getResources().getColor(R.color.text_secondary, null));
            positionText.setPaintFlags(positionText.getPaintFlags() & ~android.graphics.Paint.UNDERLINE_TEXT_FLAG);
            positionText.setOnClickListener(null);
            
            // Display ADP rank with value grade
            if (player.getPffRank() > 0) {
                if (currentPickNumber > 0 && !player.isDrafted()) {
                    int valueScore = currentPickNumber - player.getPffRank();
                    String grade;
                    int gradeColor;
                    
                    if (PickValueCalculator.isElitePlayer(player)) {
                        grade = "🏆";
                        gradeColor = 0xFF6A1B9A; // Purple
                    } else {
                        // K and DST don't show reach unless drafted before round 10
                        String pos = player.getPosition();
                        boolean isKDst = "K".equals(pos) || "DST".equals(pos) || "DEF".equals(pos);
                        boolean suppressReach = isKDst && currentRound >= 10;
                        
                        if (suppressReach || valueScore >= -8) {
                            grade = "✓";
                            gradeColor = 0xFF4CAF50; // Green
                        } else if (valueScore >= -20) {
                            grade = "~";
                            gradeColor = 0xFFFF9800; // Orange
                        } else {
                            grade = "✗";
                            gradeColor = 0xFFF44336; // Red
                        }
                    }
                    pffRankText.setText(grade + " ADP:" + player.getPffRank());
                    pffRankText.setTextColor(gradeColor);
                } else {
                    pffRankText.setText("ADP:" + player.getPffRank());
                    pffRankText.setTextColor(itemView.getContext().getResources().getColor(R.color.text_secondary, null));
                }
                pffRankText.setVisibility(View.VISIBLE);
            } else {
                pffRankText.setVisibility(View.GONE);
            }
            
            // Display position rank
            if (player.getPositionRank() > 0) {
                positionRankText.setText(player.getPosition() + player.getPositionRank());
                positionRankText.setVisibility(View.VISIBLE);
            } else {
                positionRankText.setVisibility(View.GONE);
            }
            
            // Display last year's statistics
            if (player.getLastYearStats() != null && !player.getLastYearStats().isEmpty()) {
                statsText.setText(player.getLastYearStats());
                statsText.setVisibility(View.VISIBLE);
            } else {
                statsText.setVisibility(View.GONE);
            }
            
            // Display injury status with color coding
            if (player.getInjuryStatus() != null && !player.getInjuryStatus().isEmpty() && 
                !player.getInjuryStatus().equalsIgnoreCase("HEALTHY")) {
                injuryStatusText.setText(player.getInjuryStatus());
                injuryStatusText.setVisibility(View.VISIBLE);
                
                String status = player.getInjuryStatus().toUpperCase();
                if (status.equals("OUT") || status.equals("IR")) {
                    injuryStatusText.setTextColor(0xFFD32F2F);
                } else if (status.equals("DOUBTFUL")) {
                    injuryStatusText.setTextColor(0xFFFF6F00);
                } else if (status.equals("QUESTIONABLE")) {
                    injuryStatusText.setTextColor(0xFFFFA000);
                } else {
                    injuryStatusText.setTextColor(0xFF757575);
                }
            } else {
                injuryStatusText.setVisibility(View.GONE);
            }

            // Show/hide drafted status
            if (player.isDrafted()) {
                statusText.setVisibility(View.VISIBLE);
                statusText.setText("DRAFTED");
                itemView.setEnabled(false);
                itemView.setAlpha(0.5f);
            } else {
                statusText.setVisibility(View.GONE);
                itemView.setEnabled(true);
                itemView.setAlpha(1.0f);
            }

            // Set click listener only for available players
            if (!player.isDrafted() && listener != null) {
                itemView.setOnClickListener(v -> listener.onPlayerClick(player));
            } else {
                itemView.setOnClickListener(null);
            }
        }
    }
}

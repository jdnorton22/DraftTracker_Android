package com.fantasydraft.picker.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fantasydraft.picker.R;
import com.fantasydraft.picker.models.Player;
import com.fantasydraft.picker.utils.PositionColors;

import java.util.ArrayList;
import java.util.List;

public class PlayerSelectionAdapter extends RecyclerView.Adapter<PlayerSelectionAdapter.PlayerViewHolder> {

    private List<Player> players;
    private List<Player> filteredPlayers;
    private OnPlayerClickListener listener;
    private boolean hideDrafted = false;

    public interface OnPlayerClickListener {
        void onPlayerClick(Player player);
    }

    public PlayerSelectionAdapter(List<Player> players, OnPlayerClickListener listener) {
        this.players = players;
        this.filteredPlayers = new ArrayList<>(players);
        this.listener = listener;
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
        holder.bind(player, listener);
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
        
        // Sort by rank (primary), then position rank (secondary)
        filteredPlayers.sort((p1, p2) -> {
            // First compare by rank
            int rankCompare = Integer.compare(p1.getRank(), p2.getRank());
            if (rankCompare != 0) {
                return rankCompare;
            }
            // If ranks are equal, compare by position rank
            return Integer.compare(p1.getPositionRank(), p2.getPositionRank());
        });
        
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

        public void bind(Player player, OnPlayerClickListener listener) {
            // Remove position-based background color from root
            rootView.setBackgroundColor(0x00000000); // Transparent
            
            // Set position badge with color
            positionBadgeText.setText(player.getPosition());
            android.graphics.drawable.GradientDrawable badgeCircle = new android.graphics.drawable.GradientDrawable();
            badgeCircle.setShape(android.graphics.drawable.GradientDrawable.OVAL);
            badgeCircle.setColor(PositionColors.getColorForPosition(player.getPosition()));
            positionBadgeText.setBackground(badgeCircle);
            
            nameText.setText(player.getName());
            
            // ESPN links disabled - display name as plain text
            nameText.setTextColor(itemView.getContext().getResources().getColor(R.color.text_primary, null));
            nameText.setPaintFlags(nameText.getPaintFlags() & ~android.graphics.Paint.UNDERLINE_TEXT_FLAG);
            nameText.setOnClickListener(null);
            
            // Display just the NFL team (no position code)
            if (player.getNflTeam() != null && !player.getNflTeam().isEmpty()) {
                String teamInfo = player.getNflTeam();
                if (player.getByeWeek() > 0) {
                    teamInfo += " (bye-" + player.getByeWeek() + ")";
                }
                positionText.setText(teamInfo);
                positionText.setVisibility(View.VISIBLE);
            } else if (player.getByeWeek() > 0) {
                positionText.setText("(bye-" + player.getByeWeek() + ")");
                positionText.setVisibility(View.VISIBLE);
            } else {
                positionText.setVisibility(View.GONE);
            }
            
            // Display ADP rank
            if (player.getPffRank() > 0) {
                pffRankText.setText("ADP:" + player.getPffRank());
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
                statsText.setText("Last Year: " + player.getLastYearStats());
                statsText.setVisibility(View.VISIBLE);
            } else {
                statsText.setVisibility(View.GONE);
            }

            // Display injury status with color coding
            if (player.getInjuryStatus() != null && !player.getInjuryStatus().isEmpty() && 
                !player.getInjuryStatus().equalsIgnoreCase("HEALTHY")) {
                injuryStatusText.setText(player.getInjuryStatus());
                injuryStatusText.setVisibility(View.VISIBLE);
                
                // Color code based on severity
                String status = player.getInjuryStatus().toUpperCase();
                if (status.equals("OUT") || status.equals("IR")) {
                    injuryStatusText.setTextColor(0xFFD32F2F); // Red
                } else if (status.equals("DOUBTFUL")) {
                    injuryStatusText.setTextColor(0xFFFF6F00); // Dark Orange
                } else if (status.equals("QUESTIONABLE")) {
                    injuryStatusText.setTextColor(0xFFFFA000); // Orange/Yellow
                } else {
                    injuryStatusText.setTextColor(0xFF757575); // Gray
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

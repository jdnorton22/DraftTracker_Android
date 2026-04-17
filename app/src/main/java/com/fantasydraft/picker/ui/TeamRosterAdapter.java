package com.fantasydraft.picker.ui;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.fantasydraft.picker.R;
import com.fantasydraft.picker.models.Pick;
import com.fantasydraft.picker.models.Player;
import com.fantasydraft.picker.utils.PositionColors;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView adapter for displaying roster entries in the Team Roster Popup.
 * Each entry shows comprehensive player attributes including rankings, stats,
 * injury status, and an ESPN profile link.
 */
public class TeamRosterAdapter extends RecyclerView.Adapter<TeamRosterAdapter.ViewHolder> {

    private List<RosterEntry> entries;

    public TeamRosterAdapter(List<RosterEntry> entries) {
        this.entries = entries != null ? new ArrayList<>(entries) : new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_roster_entry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RosterEntry entry = entries.get(position);
        holder.bind(entry);
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    /**
     * Updates the adapter with a new list of roster entries.
     * @param newEntries The new list of roster entries to display
     */
    public void updateEntries(List<RosterEntry> newEntries) {
        this.entries = newEntries != null ? new ArrayList<>(newEntries) : new ArrayList<>();
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textPlayerName;
        private final TextView textPosition;
        private final TextView textNflTeam;
        private final TextView textOverallRank;
        private final TextView textPffRank;
        private final TextView textPositionRank;
        private final TextView textLastYearStats;
        private final TextView textInjuryStatus;
        private final TextView textByeWeek;
        private final TextView textRoundPick;
        private final TextView textEspnLink;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            textPlayerName = itemView.findViewById(R.id.text_player_name);
            textPosition = itemView.findViewById(R.id.text_position);
            textNflTeam = itemView.findViewById(R.id.text_nfl_team);
            textOverallRank = itemView.findViewById(R.id.text_overall_rank);
            textPffRank = itemView.findViewById(R.id.text_pff_rank);
            textPositionRank = itemView.findViewById(R.id.text_position_rank);
            textLastYearStats = itemView.findViewById(R.id.text_last_year_stats);
            textInjuryStatus = itemView.findViewById(R.id.text_injury_status);
            textByeWeek = itemView.findViewById(R.id.text_bye_week);
            textRoundPick = itemView.findViewById(R.id.text_round_pick);
            textEspnLink = itemView.findViewById(R.id.text_espn_link);
        }

        void bind(RosterEntry entry) {
            Pick pick = entry.getPick();
            Player player = entry.getPlayer();

            if (player == null) {
                // Unknown player placeholder (Req 6.4)
                bindUnknownPlayer(pick);
                return;
            }

            // Player name (Req 4.1)
            textPlayerName.setText(player.getName());

            // Position badge with color coding (Req 4.2)
            textPosition.setText(player.getPosition());
            GradientDrawable badgeCircle = new GradientDrawable();
            badgeCircle.setShape(GradientDrawable.OVAL);
            badgeCircle.setColor(PositionColors.getColorForPosition(player.getPosition()));
            textPosition.setBackground(badgeCircle);

            // NFL team (Req 4.3)
            textNflTeam.setText(player.getNflTeam());

            // Overall rank (Req 4.4)
            textOverallRank.setText("Rank: " + player.getRank());

            // PFF rank (Req 4.5)
            textPffRank.setText("ADP: " + player.getPffRank());

            // Position rank (Req 4.6)
            textPositionRank.setText(player.getPosition() + player.getPositionRank());

            // Last year stats (Req 4.7)
            if (player.getLastYearStats() != null && !player.getLastYearStats().isEmpty()) {
                textLastYearStats.setText("Last Year: " + player.getLastYearStats());
                textLastYearStats.setVisibility(View.VISIBLE);
            } else {
                textLastYearStats.setVisibility(View.GONE);
            }

            // Injury status - hide when null or empty (Req 4.12)
            if (player.getInjuryStatus() != null && !player.getInjuryStatus().isEmpty()) {
                textInjuryStatus.setText(player.getInjuryStatus());
                textInjuryStatus.setVisibility(View.VISIBLE);
            } else {
                textInjuryStatus.setVisibility(View.GONE);
            }

            // Bye week (Req 4.9)
            textByeWeek.setText("Bye: " + player.getByeWeek());

            // Round and pick number (Req 4.10)
            textRoundPick.setText("R" + pick.getRound() + " P" + pick.getPickInRound());

            // ESPN link - hide when espnId is null or empty (Req 4.11)
            String espnUrl = player.getEspnUrl();
            if (espnUrl != null && !espnUrl.isEmpty()) {
                textEspnLink.setVisibility(View.VISIBLE);
                textEspnLink.setOnClickListener(v -> {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(espnUrl));
                    itemView.getContext().startActivity(browserIntent);
                });
            } else {
                textEspnLink.setVisibility(View.GONE);
                textEspnLink.setOnClickListener(null);
            }

            // Apply favorite highlight or default transparent background
            if (player.isFavorite()) {
                itemView.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.favorite_highlight));
            } else {
                itemView.setBackgroundColor(0x00000000); // Transparent
            }
        }

        private void bindUnknownPlayer(Pick pick) {
            // Reset background to prevent stale favorite highlight from recycled views
            itemView.setBackgroundColor(0x00000000); // Transparent

            textPlayerName.setText("Unknown Player");

            textPosition.setText("?");
            GradientDrawable badgeCircle = new GradientDrawable();
            badgeCircle.setShape(GradientDrawable.OVAL);
            badgeCircle.setColor(PositionColors.getColorForPosition(null));
            textPosition.setBackground(badgeCircle);

            textNflTeam.setText("");
            textOverallRank.setText("");
            textPffRank.setText("");
            textPositionRank.setText("");
            textLastYearStats.setVisibility(View.GONE);
            textInjuryStatus.setVisibility(View.GONE);
            textByeWeek.setText("");
            textRoundPick.setText("R" + pick.getRound() + " P" + pick.getPickInRound());
            textEspnLink.setVisibility(View.GONE);
            textEspnLink.setOnClickListener(null);
        }
    }
}

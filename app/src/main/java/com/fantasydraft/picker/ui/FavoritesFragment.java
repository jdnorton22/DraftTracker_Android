package com.fantasydraft.picker.ui;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fantasydraft.picker.R;
import com.fantasydraft.picker.models.Player;
import com.fantasydraft.picker.utils.PositionColors;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Fragment displaying all players with search, position filter, and favorite toggle.
 */
public class FavoritesFragment extends Fragment {

    private RecyclerView recyclerFavorites;
    private TextView textEmptyState;
    private EditText editSearch;
    private LinearLayout layoutPositionFilters;
    private FavoritesAdapter adapter;

    private List<Player> allPlayers = new ArrayList<>();
    private String searchQuery = "";
    private String selectedPosition = "All";

    private static final String[] POSITIONS = {"All", "QB", "RB", "WR", "TE", "K", "DST"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        recyclerFavorites = view.findViewById(R.id.recycler_favorites);
        textEmptyState = view.findViewById(R.id.text_empty_state);
        editSearch = view.findViewById(R.id.edit_search);
        layoutPositionFilters = view.findViewById(R.id.layout_position_filters);

        recyclerFavorites.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new FavoritesAdapter();
        recyclerFavorites.setAdapter(adapter);

        setupSearch();
        setupPositionFilters();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadPlayers();
    }

    private void setupSearch() {
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                searchQuery = s.toString().trim().toLowerCase(Locale.ROOT);
                applyFilters();
            }
        });
    }

    private void setupPositionFilters() {
        for (String pos : POSITIONS) {
            TextView chip = new TextView(getContext());
            chip.setText(pos);
            chip.setTextSize(13);
            chip.setPadding(32, 16, 32, 16);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMarginEnd(8);
            chip.setLayoutParams(params);

            updateChipAppearance(chip, pos.equals(selectedPosition));

            chip.setOnClickListener(v -> {
                selectedPosition = pos;
                // Update all chip appearances
                for (int i = 0; i < layoutPositionFilters.getChildCount(); i++) {
                    TextView c = (TextView) layoutPositionFilters.getChildAt(i);
                    updateChipAppearance(c, POSITIONS[i].equals(selectedPosition));
                }
                applyFilters();
            });

            layoutPositionFilters.addView(chip);
        }
    }

    private void updateChipAppearance(TextView chip, boolean selected) {
        GradientDrawable bg = new GradientDrawable();
        bg.setCornerRadius(48);
        if (selected) {
            bg.setColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary));
            chip.setTextColor(0xFFFFFFFF);
        } else {
            bg.setColor(0xFFE0E0E0);
            chip.setTextColor(0xFF333333);
        }
        chip.setBackground(bg);
    }

    private void loadPlayers() {
        MainActivity mainActivity = getMainActivity();
        if (mainActivity == null) return;
        allPlayers = new ArrayList<>(mainActivity.getPlayerManager().getPlayers());
        applyFilters();
    }

    private void applyFilters() {
        List<Player> filtered = new ArrayList<>();
        for (Player player : allPlayers) {
            if (!"All".equals(selectedPosition) && !player.getPosition().equals(selectedPosition)) {
                continue;
            }
            if (!searchQuery.isEmpty() &&
                    !player.getName().toLowerCase(Locale.ROOT).contains(searchQuery)) {
                continue;
            }
            filtered.add(player);
        }
        adapter.setPlayers(filtered);
        textEmptyState.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
        recyclerFavorites.setVisibility(filtered.isEmpty() ? View.GONE : View.VISIBLE);
    }

    @Nullable
    private MainActivity getMainActivity() {
        Activity activity = getActivity();
        return activity instanceof MainActivity ? (MainActivity) activity : null;
    }

    class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {

        private List<Player> players = new ArrayList<>();

        void setPlayers(List<Player> players) {
            this.players = players;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_favorite_player, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.bind(players.get(position));
        }

        @Override
        public int getItemCount() {
            return players.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView textName;
            private final TextView textPositionBadge;
            private final TextView textRank;
            private final SwitchCompat switchFavorite;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                textName = itemView.findViewById(R.id.text_favorite_name);
                textPositionBadge = itemView.findViewById(R.id.text_favorite_position_badge);
                textRank = itemView.findViewById(R.id.text_favorite_rank);
                switchFavorite = itemView.findViewById(R.id.switch_favorite);
            }

            void bind(Player player) {
                textName.setText(player.getName());
                textPositionBadge.setText(player.getPosition());
                textRank.setText("Rank: " + player.getRank());

                GradientDrawable badgeCircle = new GradientDrawable();
                badgeCircle.setShape(GradientDrawable.OVAL);
                badgeCircle.setColor(PositionColors.getColorForPosition(player.getPosition()));
                textPositionBadge.setBackground(badgeCircle);

                if (player.isFavorite()) {
                    itemView.setBackgroundColor(ContextCompat.getColor(
                            itemView.getContext(), R.color.favorite_highlight));
                    textName.setTextColor(0xFF000000);
                    textRank.setTextColor(0xFF333333);
                } else {
                    itemView.setBackgroundColor(0x00000000);
                    textName.setTextColor(ContextCompat.getColor(
                            itemView.getContext(), R.color.text_primary));
                    textRank.setTextColor(ContextCompat.getColor(
                            itemView.getContext(), R.color.text_secondary));
                }

                switchFavorite.setOnCheckedChangeListener(null);
                switchFavorite.setChecked(player.isFavorite());
                switchFavorite.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    player.setFavorite(isChecked);
                    if (isChecked) {
                        itemView.setBackgroundColor(ContextCompat.getColor(
                                itemView.getContext(), R.color.favorite_highlight));
                        textName.setTextColor(0xFF000000);
                        textRank.setTextColor(0xFF333333);
                    } else {
                        itemView.setBackgroundColor(0x00000000);
                        textName.setTextColor(ContextCompat.getColor(
                                itemView.getContext(), R.color.text_primary));
                        textRank.setTextColor(ContextCompat.getColor(
                                itemView.getContext(), R.color.text_secondary));
                    }
                });
            }
        }
    }
}

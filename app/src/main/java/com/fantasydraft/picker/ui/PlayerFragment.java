package com.fantasydraft.picker.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fantasydraft.picker.R;
import com.fantasydraft.picker.managers.PlayerManager;
import com.fantasydraft.picker.models.Player;

/**
 * Fragment for displaying the best available player recommendation.
 * Requirements: 5.1, 5.2, 5.3
 */
public class PlayerFragment extends Fragment {
    
    // UI Components
    private TextView textPlayerName;
    private TextView textPlayerPosition;
    private TextView textPlayerRank;
    private Button buttonViewAllPlayers;
    
    // Manager
    private PlayerManager playerManager;
    
    // Listener for button clicks
    private OnPlayerFragmentInteractionListener listener;
    
    /**
     * Interface for handling fragment interactions.
     */
    public interface OnPlayerFragmentInteractionListener {
        void onViewAllPlayersClicked();
    }
    
    /**
     * Create a new instance of PlayerFragment.
     * 
     * @return A new instance of PlayerFragment
     */
    public static PlayerFragment newInstance() {
        return new PlayerFragment();
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_player, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize UI components
        initializeViews(view);
        
        // Set up button click handler
        setupClickHandlers();
    }
    
    /**
     * Initialize all UI view references.
     * 
     * @param view The root view of the fragment
     */
    private void initializeViews(View view) {
        textPlayerName = view.findViewById(R.id.text_player_name);
        textPlayerPosition = view.findViewById(R.id.text_player_position);
        textPlayerRank = view.findViewById(R.id.text_player_rank);
        buttonViewAllPlayers = view.findViewById(R.id.button_view_all_players);
    }
    
    /**
     * Set up button click handlers.
     */
    private void setupClickHandlers() {
        buttonViewAllPlayers.setOnClickListener(v -> {
            if (listener != null) {
                listener.onViewAllPlayersClicked();
            }
        });
    }
    
    /**
     * Set the PlayerManager for this fragment.
     * 
     * @param playerManager The PlayerManager instance
     */
    public void setPlayerManager(PlayerManager playerManager) {
        this.playerManager = playerManager;
        updateDisplay();
    }
    
    /**
     * Set the interaction listener for this fragment.
     * 
     * @param listener The listener to handle fragment interactions
     */
    public void setOnPlayerFragmentInteractionListener(OnPlayerFragmentInteractionListener listener) {
        this.listener = listener;
    }
    
    /**
     * Update the display with the current best available player.
     * This method should be called whenever the draft state changes.
     * Requirements: 5.1, 5.2, 5.3
     */
    public void updateDisplay() {
        if (playerManager == null) {
            displayNoPlayer();
            return;
        }
        
        // Get the best available player
        Player bestPlayer = playerManager.getBestAvailable(playerManager.getPlayers());
        
        if (bestPlayer != null) {
            displayPlayer(bestPlayer);
        } else {
            displayNoPlayer();
        }
    }
    
    /**
     * Display a player's information.
     * 
     * @param player The player to display
     */
    private void displayPlayer(Player player) {
        if (textPlayerName != null) {
            textPlayerName.setText(player.getName());
        }
        
        if (textPlayerPosition != null) {
            textPlayerPosition.setText(player.getPosition());
        }
        
        if (textPlayerRank != null) {
            textPlayerRank.setText("Rank: #" + player.getRank());
        }
    }
    
    /**
     * Display placeholder text when no player is available.
     */
    private void displayNoPlayer() {
        if (textPlayerName != null) {
            textPlayerName.setText(getString(R.string.player_name_placeholder));
        }
        
        if (textPlayerPosition != null) {
            textPlayerPosition.setText(getString(R.string.position_placeholder));
        }
        
        if (textPlayerRank != null) {
            textPlayerRank.setText(getString(R.string.position_placeholder));
        }
    }
    
    /**
     * Refresh the display when draft state changes.
     * This method can be called from the parent activity when a pick is made.
     */
    public void onDraftStateChanged() {
        updateDisplay();
    }
}

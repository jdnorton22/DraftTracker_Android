package com.fantasydraft.picker.ui;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fantasydraft.picker.R;
import com.fantasydraft.picker.models.Team;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView adapter for team configuration.
 * Displays editable team names and draft positions.
 * Requirements: 1.2, 1.3, 2.1, 2.2
 */
public class TeamConfigAdapter extends RecyclerView.Adapter<TeamConfigAdapter.TeamViewHolder> {
    
    private List<Team> teams;
    private TeamNameChangeListener listener;
    
    public interface TeamNameChangeListener {
        void onTeamNameChanged(int position, String newName);
    }
    
    public TeamConfigAdapter() {
        this.teams = new ArrayList<>();
    }
    
    public void setTeams(List<Team> teams) {
        this.teams = teams != null ? new ArrayList<>(teams) : new ArrayList<>();
        notifyDataSetChanged();
    }
    
    public void setTeamNameChangeListener(TeamNameChangeListener listener) {
        this.listener = listener;
    }
    
    public List<Team> getTeams() {
        return new ArrayList<>(teams);
    }
    
    @NonNull
    @Override
    public TeamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_team_config, parent, false);
        return new TeamViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull TeamViewHolder holder, int position) {
        Team team = teams.get(position);
        holder.bind(team, position);
    }
    
    @Override
    public int getItemCount() {
        return teams.size();
    }
    
    class TeamViewHolder extends RecyclerView.ViewHolder {
        
        private TextView textDraftPosition;
        private EditText editTeamName;
        private TextWatcher textWatcher;
        
        public TeamViewHolder(@NonNull View itemView) {
            super(itemView);
            textDraftPosition = itemView.findViewById(R.id.text_draft_position);
            editTeamName = itemView.findViewById(R.id.edit_team_name);
        }
        
        public void bind(Team team, int position) {
            // Remove previous text watcher to avoid triggering on setText
            if (textWatcher != null) {
                editTeamName.removeTextChangedListener(textWatcher);
            }
            
            // Set draft position (1-based)
            textDraftPosition.setText(String.valueOf(team.getDraftPosition()));
            
            // Set team name
            editTeamName.setText(team.getName());
            
            // Create new text watcher for this position
            textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
                
                @Override
                public void afterTextChanged(Editable s) {
                    String newName = s.toString().trim();
                    team.setName(newName);
                    
                    if (listener != null) {
                        listener.onTeamNameChanged(position, newName);
                    }
                }
            };
            
            editTeamName.addTextChangedListener(textWatcher);
        }
    }
}

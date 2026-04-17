package com.fantasydraft.picker.ui;

import com.fantasydraft.picker.models.Pick;
import com.fantasydraft.picker.models.Player;

/**
 * Lightweight data holder combining a Pick with its resolved Player.
 * The player may be null if the player cannot be found in PlayerManager (Req 6.4).
 */
public class RosterEntry {
    private final Pick pick;
    private final Player player;

    public RosterEntry(Pick pick, Player player) {
        this.pick = pick;
        this.player = player;
    }

    public Pick getPick() {
        return pick;
    }

    public Player getPlayer() {
        return player;
    }
}

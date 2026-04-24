package com.fantasydraft.picker.models;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataModelsTest {

    // FlowType tests
    @Test
    public void testFlowTypeValues() {
        assertEquals(FlowType.SERPENTINE, FlowType.valueOf("SERPENTINE"));
        assertEquals(FlowType.LINEAR, FlowType.valueOf("LINEAR"));
    }

    // DraftConfig tests
    @Test
    public void testDraftConfigCreation() {
        DraftConfig config = new DraftConfig(FlowType.SERPENTINE, 15);
        assertEquals(FlowType.SERPENTINE, config.getFlowType());
        assertEquals(15, config.getNumberOfRounds());
    }

    @Test
    public void testDraftConfigSetters() {
        DraftConfig config = new DraftConfig();
        config.setFlowType(FlowType.LINEAR);
        config.setNumberOfRounds(10);
        
        assertEquals(FlowType.LINEAR, config.getFlowType());
        assertEquals(10, config.getNumberOfRounds());
    }

    @Test
    public void testDraftConfigEquality() {
        DraftConfig config1 = new DraftConfig(FlowType.SERPENTINE, 15);
        DraftConfig config2 = new DraftConfig(FlowType.SERPENTINE, 15);
        DraftConfig config3 = new DraftConfig(FlowType.LINEAR, 15);
        
        assertEquals(config1, config2);
        assertNotEquals(config1, config3);
        assertEquals(config1.hashCode(), config2.hashCode());
    }

    // DraftState tests
    @Test
    public void testDraftStateCreation() {
        DraftState state = new DraftState(2, 5, false);
        assertEquals(2, state.getCurrentRound());
        assertEquals(5, state.getCurrentPickInRound());
        assertFalse(state.isComplete());
    }

    @Test
    public void testDraftStateSetters() {
        DraftState state = new DraftState();
        state.setCurrentRound(3);
        state.setCurrentPickInRound(7);
        state.setComplete(true);
        
        assertEquals(3, state.getCurrentRound());
        assertEquals(7, state.getCurrentPickInRound());
        assertTrue(state.isComplete());
    }

    @Test
    public void testDraftStateEquality() {
        DraftState state1 = new DraftState(2, 5, false);
        DraftState state2 = new DraftState(2, 5, false);
        DraftState state3 = new DraftState(2, 5, true);
        
        assertEquals(state1, state2);
        assertNotEquals(state1, state3);
        assertEquals(state1.hashCode(), state2.hashCode());
    }

    // Player tests
    @Test
    public void testPlayerCreation() {
        Player player = new Player("p1", "Christian McCaffrey", "RB", 1);
        assertEquals("p1", player.getId());
        assertEquals("Christian McCaffrey", player.getName());
        assertEquals("RB", player.getPosition());
        assertEquals(1, player.getRank());
        assertFalse(player.isDrafted());
        assertNull(player.getDraftedBy());
    }

    @Test
    public void testPlayerFullConstructor() {
        Player player = new Player("p1", "Christian McCaffrey", "RB", 1, true, "team1");
        assertEquals("p1", player.getId());
        assertEquals("Christian McCaffrey", player.getName());
        assertEquals("RB", player.getPosition());
        assertEquals(1, player.getRank());
        assertTrue(player.isDrafted());
        assertEquals("team1", player.getDraftedBy());
    }

    @Test
    public void testPlayerSetters() {
        Player player = new Player();
        player.setId("p2");
        player.setName("Tyreek Hill");
        player.setPosition("WR");
        player.setRank(5);
        player.setDrafted(true);
        player.setDraftedBy("team2");
        
        assertEquals("p2", player.getId());
        assertEquals("Tyreek Hill", player.getName());
        assertEquals("WR", player.getPosition());
        assertEquals(5, player.getRank());
        assertTrue(player.isDrafted());
        assertEquals("team2", player.getDraftedBy());
    }

    @Test
    public void testPlayerEquality() {
        Player player1 = new Player("p1", "Christian McCaffrey", "RB", 1, false, null);
        Player player2 = new Player("p1", "Christian McCaffrey", "RB", 1, false, null);
        Player player3 = new Player("p1", "Christian McCaffrey", "RB", 1, true, "team1");
        
        assertEquals(player1, player2);
        assertNotEquals(player1, player3);
        assertEquals(player1.hashCode(), player2.hashCode());
    }

    // Team tests
    @Test
    public void testTeamCreation() {
        Team team = new Team("t1", "The Champs", 1);
        assertEquals("t1", team.getId());
        assertEquals("The Champs", team.getName());
        assertEquals(1, team.getDraftPosition());
        assertNotNull(team.getRoster());
        assertTrue(team.getRoster().isEmpty());
    }

    @Test
    public void testTeamWithRoster() {
        List<Player> roster = new ArrayList<>();
        roster.add(new Player("p1", "Christian McCaffrey", "RB", 1));
        roster.add(new Player("p2", "Tyreek Hill", "WR", 5));
        
        Team team = new Team("t1", "The Champs", 1, roster);
        assertEquals("t1", team.getId());
        assertEquals("The Champs", team.getName());
        assertEquals(1, team.getDraftPosition());
        assertEquals(2, team.getRoster().size());
    }

    @Test
    public void testTeamSetters() {
        Team team = new Team();
        team.setId("t2");
        team.setName("Dream Team");
        team.setDraftPosition(3);
        
        List<Player> roster = new ArrayList<>();
        roster.add(new Player("p1", "Christian McCaffrey", "RB", 1));
        team.setRoster(roster);
        
        assertEquals("t2", team.getId());
        assertEquals("Dream Team", team.getName());
        assertEquals(3, team.getDraftPosition());
        assertEquals(1, team.getRoster().size());
    }

    @Test
    public void testTeamEquality() {
        List<Player> roster1 = new ArrayList<>();
        roster1.add(new Player("p1", "Christian McCaffrey", "RB", 1));
        
        List<Player> roster2 = new ArrayList<>();
        roster2.add(new Player("p1", "Christian McCaffrey", "RB", 1));
        
        Team team1 = new Team("t1", "The Champs", 1, roster1);
        Team team2 = new Team("t1", "The Champs", 1, roster2);
        Team team3 = new Team("t1", "The Champs", 2, roster1);
        
        assertEquals(team1, team2);
        assertNotEquals(team1, team3);
        assertEquals(team1.hashCode(), team2.hashCode());
    }

    @Test
    public void testTeamRosterImmutability() {
        List<Player> roster = new ArrayList<>();
        roster.add(new Player("p1", "Christian McCaffrey", "RB", 1));
        
        Team team = new Team("t1", "The Champs", 1, roster);
        roster.add(new Player("p2", "Tyreek Hill", "WR", 5));
        
        // Team's roster should not be affected by external list modification
        assertEquals(1, team.getRoster().size());
    }

    // Pick tests
    @Test
    public void testPickCreation() {
        Pick pick = new Pick(1, 1, 1, "t1", "p1", 1234567890L);
        assertEquals(1, pick.getPickNumber());
        assertEquals(1, pick.getRound());
        assertEquals(1, pick.getPickInRound());
        assertEquals("t1", pick.getTeamId());
        assertEquals("p1", pick.getPlayerId());
        assertEquals(1234567890L, pick.getTimestamp());
    }

    @Test
    public void testPickSetters() {
        Pick pick = new Pick();
        pick.setPickNumber(5);
        pick.setRound(2);
        pick.setPickInRound(3);
        pick.setTeamId("t2");
        pick.setPlayerId("p5");
        pick.setTimestamp(9876543210L);
        
        assertEquals(5, pick.getPickNumber());
        assertEquals(2, pick.getRound());
        assertEquals(3, pick.getPickInRound());
        assertEquals("t2", pick.getTeamId());
        assertEquals("p5", pick.getPlayerId());
        assertEquals(9876543210L, pick.getTimestamp());
    }

    @Test
    public void testPickEquality() {
        Pick pick1 = new Pick(1, 1, 1, "t1", "p1", 1234567890L);
        Pick pick2 = new Pick(1, 1, 1, "t1", "p1", 1234567890L);
        Pick pick3 = new Pick(2, 1, 2, "t2", "p2", 1234567890L);
        
        assertEquals(pick1, pick2);
        assertNotEquals(pick1, pick3);
        assertEquals(pick1.hashCode(), pick2.hashCode());
    }
}

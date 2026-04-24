package com.fantasydraft.picker;

import com.fantasydraft.picker.models.Player;
import com.fantasydraft.picker.utils.PlayerDataParser;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import com.pholser.junit.quickcheck.generator.InRange;

import org.json.JSONArray;
import org.json.JSONObject;

import org.junit.runner.RunWith;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Preservation Property Tests - Existing Field Parsing and Non-Favorite Rendering Unchanged
 *
 * Validates: Requirements 3.1, 3.2, 3.3, 3.5, 3.6
 *
 * These tests verify that existing behavior is preserved on UNFIXED code.
 * They must PASS before and after the fix is applied.
 *
 * IMPORTANT: These tests do NOT reference isFavorite() or setFavorite() since
 * those methods do not exist on the unfixed code.
 */
@RunWith(JUnitQuickcheck.class)
public class FavoritePlayerPreservationTest {

    // Valid positions used in the app
    private static final String[] POSITIONS = {"QB", "RB", "WR", "TE", "K", "DEF"};
    private static final String[] NFL_TEAMS = {"KC", "SF", "PHI", "DAL", "BUF", "MIA", "BAL", "CIN", "DET", "MIN"};

    // -----------------------------------------------------------------------
    // Property 2: Preservation - Existing Field Parsing Unchanged
    // Validates: Requirements 3.1, 3.2
    // -----------------------------------------------------------------------

    /**
     * Property: For all generated JSON player entries WITHOUT a "favorite" field,
     * all existing fields parse to the same values as the input JSON.
     *
     * **Validates: Requirements 3.2**
     */
    @Property(trials = 100)
    public void allExistingFieldsParseCorrectlyWithoutFavorite(
            @InRange(minInt = 1, maxInt = 300) int rank) throws Exception {

        String id = "player-" + rank;
        String name = "TestPlayer " + rank;
        String position = POSITIONS[rank % POSITIONS.length];
        int pffRank = rank + 10;
        int positionRank = (rank % 50) + 1;
        String nflTeam = NFL_TEAMS[rank % NFL_TEAMS.length];
        String lastYearStats = rank + " YDS, " + (rank % 40) + " TD";
        String injuryStatus = "HEALTHY";
        String espnId = "espn-" + rank;
        int byeWeek = (rank % 14) + 1;

        // Build JSON without "favorite" field
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("name", name);
        json.put("position", position);
        json.put("rank", rank);
        json.put("pffRank", pffRank);
        json.put("positionRank", positionRank);
        json.put("nflTeam", nflTeam);
        json.put("lastYearStats", lastYearStats);
        json.put("injuryStatus", injuryStatus);
        json.put("espnId", espnId);
        json.put("byeWeek", byeWeek);
        // No "favorite" field

        Player player = invokeParsePlayerObject(json);

        assertNotNull("Parsed player should not be null for rank=" + rank, player);
        assertEquals("id mismatch", id, player.getId());
        assertEquals("name mismatch", name, player.getName());
        assertEquals("position mismatch", position, player.getPosition());
        assertEquals("rank mismatch", rank, player.getRank());
        assertEquals("pffRank mismatch", pffRank, player.getPffRank());
        assertEquals("positionRank mismatch", positionRank, player.getPositionRank());
        assertEquals("nflTeam mismatch", nflTeam, player.getNflTeam());
        assertEquals("lastYearStats mismatch", lastYearStats, player.getLastYearStats());
        assertEquals("injuryStatus mismatch", injuryStatus, player.getInjuryStatus());
        assertEquals("espnId mismatch", espnId, player.getEspnId());
        assertEquals("byeWeek mismatch", byeWeek, player.getByeWeek());
        assertFalse("isDrafted should default to false", player.isDrafted());
        assertNull("draftedBy should default to null", player.getDraftedBy());
    }

    /**
     * Property: For all generated JSON player entries with only required fields,
     * optional fields default correctly.
     *
     * **Validates: Requirements 3.2**
     */
    @Property(trials = 50)
    public void optionalFieldsDefaultCorrectlyWhenAbsent(
            @InRange(minInt = 1, maxInt = 300) int rank) throws Exception {

        // Build JSON with only required fields
        JSONObject json = new JSONObject();
        json.put("id", "min-" + rank);
        json.put("name", "MinPlayer " + rank);
        json.put("position", POSITIONS[rank % POSITIONS.length]);
        json.put("rank", rank);
        // No optional fields, no "favorite" field

        Player player = invokeParsePlayerObject(json);

        assertNotNull("Parsed player should not be null", player);
        assertEquals("min-" + rank, player.getId());
        assertEquals("MinPlayer " + rank, player.getName());
        assertEquals(rank, player.getRank());
        // Verify optional fields default correctly
        assertEquals("pffRank should default to 0", 0, player.getPffRank());
        assertEquals("positionRank should default to 0", 0, player.getPositionRank());
        assertEquals("nflTeam should default to empty", "", player.getNflTeam());
        assertEquals("lastYearStats should default to empty", "", player.getLastYearStats());
        assertEquals("injuryStatus should default to HEALTHY", "HEALTHY", player.getInjuryStatus());
        assertEquals("espnId should default to empty", "", player.getEspnId());
        assertEquals("byeWeek should default to 0", 0, player.getByeWeek());
    }

    // -----------------------------------------------------------------------
    // Property: equals/hashCode consistency for existing fields
    // Validates: Requirements 3.5, 3.6
    // -----------------------------------------------------------------------

    /**
     * Property: For all Player objects created via constructor, equals() is reflexive.
     *
     * **Validates: Requirements 3.5**
     */
    @Property(trials = 100)
    public void equalsIsReflexive(@InRange(minInt = 1, maxInt = 300) int rank) {
        Player player = new Player("id-" + rank, "Player " + rank,
                POSITIONS[rank % POSITIONS.length], rank);
        player.setPffRank(rank + 5);
        player.setPositionRank((rank % 50) + 1);
        player.setNflTeam(NFL_TEAMS[rank % NFL_TEAMS.length]);
        player.setLastYearStats(rank + " YDS");
        player.setInjuryStatus("HEALTHY");
        player.setEspnId("espn-" + rank);
        player.setByeWeek((rank % 14) + 1);

        assertEquals("equals() must be reflexive", player, player);
    }

    /**
     * Property: For all Player objects, equals() and hashCode() are consistent —
     * equal objects must have equal hash codes.
     *
     * **Validates: Requirements 3.5, 3.6
     */
    @Property(trials = 100)
    public void equalsAndHashCodeConsistent(@InRange(minInt = 1, maxInt = 300) int rank) {
        String id = "id-" + rank;
        String name = "Player " + rank;
        String position = POSITIONS[rank % POSITIONS.length];
        String nflTeam = NFL_TEAMS[rank % NFL_TEAMS.length];

        Player p1 = new Player(id, name, position, rank);
        p1.setPffRank(rank + 5);
        p1.setPositionRank((rank % 50) + 1);
        p1.setNflTeam(nflTeam);
        p1.setLastYearStats(rank + " YDS");
        p1.setEspnId("espn-" + rank);
        p1.setByeWeek((rank % 14) + 1);

        Player p2 = new Player(id, name, position, rank);
        p2.setPffRank(rank + 5);
        p2.setPositionRank((rank % 50) + 1);
        p2.setNflTeam(nflTeam);
        p2.setLastYearStats(rank + " YDS");
        p2.setEspnId("espn-" + rank);
        p2.setByeWeek((rank % 14) + 1);

        assertEquals("Players with same fields should be equal", p1, p2);
        assertEquals("Equal players must have equal hashCode", p1.hashCode(), p2.hashCode());
    }

    /**
     * Property: For all Player objects, changing any single field breaks equality.
     *
     * **Validates: Requirements 3.5**
     */
    @Property(trials = 50)
    public void differentRankBreaksEquality(@InRange(minInt = 1, maxInt = 299) int rank) {
        Player p1 = new Player("id-1", "Player", "QB", rank);
        Player p2 = new Player("id-1", "Player", "QB", rank + 1);

        assertNotEquals("Players with different rank should not be equal", p1, p2);
    }

    // -----------------------------------------------------------------------
    // Concrete Preservation Tests: Parse 10+ players without "favorite" field
    // Validates: Requirements 3.1, 3.2, 3.3
    // -----------------------------------------------------------------------

    /**
     * Concrete test: Parse a JSON array of 12 players without "favorite" field
     * and verify all fields parse correctly via parseESPNData().
     *
     * **Validates: Requirements 3.1, 3.2**
     */
    @Test
    public void parseESPNDataPreservesAllFieldsForTwelvePlayers() throws Exception {
        JSONArray playersArray = new JSONArray();

        // Build 12 players with all fields, no "favorite"
        String[][] playerData = {
            // id, name, position, rank, pffRank, posRank, nflTeam, stats, injury, espnId, byeWeek
            {"1", "Patrick Mahomes", "QB", "1", "2", "1", "KC", "5250 YDS, 41 TD", "HEALTHY", "3139477", "6"},
            {"2", "Josh Allen", "QB", "2", "3", "2", "BUF", "4306 YDS, 29 TD", "HEALTHY", "3918298", "12"},
            {"3", "Christian McCaffrey", "RB", "3", "1", "1", "SF", "1459 YDS, 14 TD", "HEALTHY", "3117251", "9"},
            {"4", "Tyreek Hill", "WR", "4", "5", "1", "MIA", "1799 YDS, 13 TD", "HEALTHY", "3116406", "10"},
            {"5", "Travis Kelce", "TE", "5", "4", "1", "KC", "984 YDS, 5 TD", "HEALTHY", "2519036", "6"},
            {"6", "CeeDee Lamb", "WR", "6", "6", "2", "DAL", "1749 YDS, 12 TD", "HEALTHY", "4241389", "7"},
            {"7", "Bijan Robinson", "RB", "7", "7", "2", "ATL", "1463 YDS, 8 TD", "HEALTHY", "4596993", "12"},
            {"8", "Ja'Marr Chase", "WR", "8", "8", "3", "CIN", "1216 YDS, 7 TD", "HEALTHY", "4362628", "12"},
            {"9", "Lamar Jackson", "QB", "9", "9", "3", "BAL", "3678 YDS, 24 TD", "HEALTHY", "3916387", "14"},
            {"10", "Amon-Ra St. Brown", "WR", "10", "10", "4", "DET", "1515 YDS, 10 TD", "HEALTHY", "4360438", "5"},
            {"11", "Saquon Barkley", "RB", "11", "11", "3", "PHI", "962 YDS, 6 TD", "QUESTIONABLE", "3929630", "5"},
            {"12", "Sam LaPorta", "TE", "12", "15", "2", "DET", "889 YDS, 10 TD", "HEALTHY", "4426354", "5"},
        };

        for (String[] pd : playerData) {
            JSONObject json = new JSONObject();
            json.put("id", pd[0]);
            json.put("name", pd[1]);
            json.put("position", pd[2]);
            json.put("rank", Integer.parseInt(pd[3]));
            json.put("pffRank", Integer.parseInt(pd[4]));
            json.put("positionRank", Integer.parseInt(pd[5]));
            json.put("nflTeam", pd[6]);
            json.put("lastYearStats", pd[7]);
            json.put("injuryStatus", pd[8]);
            json.put("espnId", pd[9]);
            json.put("byeWeek", Integer.parseInt(pd[10]));
            // No "favorite" field
            playersArray.put(json);
        }

        PlayerDataParser parser = new PlayerDataParser();
        List<Player> players = parser.parseESPNData(playersArray.toString());

        assertEquals("Should parse all 12 players", 12, players.size());

        // Verify each player's fields
        for (int i = 0; i < playerData.length; i++) {
            Player p = players.get(i);
            String[] pd = playerData[i];

            assertEquals("Player " + i + " id", pd[0], p.getId());
            assertEquals("Player " + i + " name", pd[1], p.getName());
            assertEquals("Player " + i + " position", pd[2], p.getPosition());
            assertEquals("Player " + i + " rank", Integer.parseInt(pd[3]), p.getRank());
            assertEquals("Player " + i + " pffRank", Integer.parseInt(pd[4]), p.getPffRank());
            assertEquals("Player " + i + " positionRank", Integer.parseInt(pd[5]), p.getPositionRank());
            assertEquals("Player " + i + " nflTeam", pd[6], p.getNflTeam());
            assertEquals("Player " + i + " lastYearStats", pd[7], p.getLastYearStats());
            assertEquals("Player " + i + " injuryStatus", pd[8], p.getInjuryStatus());
            assertEquals("Player " + i + " espnId", pd[9], p.getEspnId());
            assertEquals("Player " + i + " byeWeek", Integer.parseInt(pd[10]), p.getByeWeek());
            assertFalse("Player " + i + " isDrafted should be false", p.isDrafted());
            assertNull("Player " + i + " draftedBy should be null", p.getDraftedBy());
        }
    }

    /**
     * Concrete test: Parse players with missing optional fields and verify defaults.
     *
     * **Validates: Requirements 3.2**
     */
    @Test
    public void parseESPNDataHandlesMissingOptionalFields() throws Exception {
        JSONArray playersArray = new JSONArray();

        // Build 12 minimal players (only required fields)
        for (int i = 1; i <= 12; i++) {
            JSONObject json = new JSONObject();
            json.put("id", String.valueOf(i));
            json.put("name", "Player " + i);
            json.put("position", POSITIONS[i % POSITIONS.length]);
            json.put("rank", i);
            // No optional fields, no "favorite"
            playersArray.put(json);
        }

        PlayerDataParser parser = new PlayerDataParser();
        List<Player> players = parser.parseESPNData(playersArray.toString());

        assertEquals("Should parse all 12 players", 12, players.size());

        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            assertEquals(String.valueOf(i + 1), p.getId());
            assertEquals("Player " + (i + 1), p.getName());
            assertEquals(i + 1, p.getRank());
            // Optional fields should have defaults
            assertEquals(0, p.getPffRank());
            assertEquals(0, p.getPositionRank());
            assertEquals("", p.getNflTeam());
            assertEquals("", p.getLastYearStats());
            assertEquals("HEALTHY", p.getInjuryStatus());
            assertEquals("", p.getEspnId());
            assertEquals(0, p.getByeWeek());
        }
    }

    /**
     * Concrete test: Verify Player constructors initialize all existing fields correctly.
     *
     * **Validates: Requirements 3.5, 3.6**
     */
    @Test
    public void playerConstructorsPreserveExistingFields() {
        // No-arg constructor
        Player p1 = new Player();
        assertNull("No-arg: id should be null", p1.getId());
        assertNull("No-arg: name should be null", p1.getName());
        assertNull("No-arg: position should be null", p1.getPosition());
        assertEquals("No-arg: rank should be 0", 0, p1.getRank());

        // 4-arg constructor
        Player p2 = new Player("id-1", "Test Player", "QB", 5);
        assertEquals("id-1", p2.getId());
        assertEquals("Test Player", p2.getName());
        assertEquals("QB", p2.getPosition());
        assertEquals(5, p2.getRank());
        assertFalse(p2.isDrafted());
        assertNull(p2.getDraftedBy());
        assertEquals("", p2.getLastYearStats());
        assertEquals(0, p2.getPffRank());
        assertEquals(0, p2.getPositionRank());
        assertEquals("", p2.getNflTeam());
        assertEquals("", p2.getInjuryStatus());
        assertEquals("", p2.getEspnId());
        assertEquals(0, p2.getByeWeek());

        // 6-arg constructor
        Player p3 = new Player("id-2", "Drafted Player", "RB", 10, true, "Team A");
        assertEquals("id-2", p3.getId());
        assertEquals("Drafted Player", p3.getName());
        assertEquals("RB", p3.getPosition());
        assertEquals(10, p3.getRank());
        assertTrue(p3.isDrafted());
        assertEquals("Team A", p3.getDraftedBy());
        assertEquals("", p3.getLastYearStats());
        assertEquals(0, p3.getPffRank());
        assertEquals(0, p3.getPositionRank());
        assertEquals("", p3.getNflTeam());
        assertEquals("", p3.getInjuryStatus());
        assertEquals("", p3.getEspnId());
        assertEquals(0, p3.getByeWeek());
    }

    /**
     * Concrete test: Verify equals/hashCode with all fields populated.
     *
     * **Validates: Requirements 3.5**
     */
    @Test
    public void equalsAndHashCodeWithAllFieldsPopulated() {
        Player p1 = createFullyPopulatedPlayer("id-1", "Player One", "QB", 1);
        Player p2 = createFullyPopulatedPlayer("id-1", "Player One", "QB", 1);

        assertEquals("Fully populated players with same fields should be equal", p1, p2);
        assertEquals("Equal players must have same hashCode", p1.hashCode(), p2.hashCode());

        // Change one field at a time and verify inequality
        Player diffId = createFullyPopulatedPlayer("id-2", "Player One", "QB", 1);
        assertNotEquals("Different id should break equality", p1, diffId);

        Player diffName = createFullyPopulatedPlayer("id-1", "Player Two", "QB", 1);
        assertNotEquals("Different name should break equality", p1, diffName);

        Player diffPos = createFullyPopulatedPlayer("id-1", "Player One", "RB", 1);
        assertNotEquals("Different position should break equality", p1, diffPos);
    }

    /**
     * Concrete test: Verify null handling in equals.
     *
     * **Validates: Requirements 3.5**
     */
    @Test
    public void equalsHandlesNullAndDifferentTypes() {
        Player player = new Player("id-1", "Test", "QB", 1);
        assertNotEquals("Player should not equal null", player, null);
        assertNotEquals("Player should not equal String", player, "not a player");
    }

    // -----------------------------------------------------------------------
    // Helper methods
    // -----------------------------------------------------------------------

    /**
     * Invoke the private parsePlayerObject method via reflection.
     */
    private Player invokeParsePlayerObject(JSONObject json) throws Exception {
        PlayerDataParser parser = new PlayerDataParser();
        Method parseMethod = PlayerDataParser.class.getDeclaredMethod("parsePlayerObject", JSONObject.class);
        parseMethod.setAccessible(true);
        return (Player) parseMethod.invoke(parser, json);
    }

    /**
     * Create a fully populated Player for equality testing.
     */
    private Player createFullyPopulatedPlayer(String id, String name, String position, int rank) {
        Player p = new Player(id, name, position, rank);
        p.setPffRank(15);
        p.setPositionRank(3);
        p.setNflTeam("KC");
        p.setLastYearStats("5000 YDS, 40 TD");
        p.setInjuryStatus("HEALTHY");
        p.setEspnId("espn-123");
        p.setByeWeek(6);
        return p;
    }
}

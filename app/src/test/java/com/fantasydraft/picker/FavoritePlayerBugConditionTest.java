package com.fantasydraft.picker;

import com.fantasydraft.picker.models.Player;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import com.pholser.junit.quickcheck.generator.InRange;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import org.junit.runner.RunWith;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

/**
 * Bug Condition Exploration Test - Favorite Field Missing from Player Model and Parser
 *
 * Validates: Requirements 1.1, 1.2, 2.1, 2.2
 *
 * This test encodes the EXPECTED behavior: Player should have an isFavorite() method
 * and PlayerDataParser should parse the "favorite" field from JSON.
 *
 * EXPECTED TO FAIL on unfixed code because:
 * - Player class has no isFavorite() method (compilation error)
 * - PlayerDataParser.parsePlayerObject() ignores the "favorite" field
 */
@RunWith(JUnitQuickcheck.class)
public class FavoritePlayerBugConditionTest {

    // -----------------------------------------------------------------------
    // Property 1: Bug Condition - Favorite Field Missing from Player Model and Parser
    // Validates: Requirements 2.1, 2.2
    // -----------------------------------------------------------------------

    /**
     * Property: For all generated player rank values, a Player constructed and
     * marked as favorite should report isFavorite() == true.
     *
     * **Validates: Requirements 2.1**
     */
    @Property(trials = 100)
    public void playerModelSupportsFavoriteField(
            @InRange(minInt = 1, maxInt = 300) int rank) {

        Player player = new Player("id-" + rank, "Player " + rank, "QB", rank);

        // Bug condition: Player has no setFavorite() or isFavorite() methods
        // This will fail to compile on unfixed code
        player.setFavorite(true);
        assertTrue("Player with setFavorite(true) should return isFavorite() == true",
                player.isFavorite());

        player.setFavorite(false);
        assertFalse("Player with setFavorite(false) should return isFavorite() == false",
                player.isFavorite());
    }

    /**
     * Property: For all generated players, the default favorite status should be false.
     *
     * **Validates: Requirements 2.1**
     */
    @Property(trials = 50)
    public void playerDefaultFavoriteIsFalse(
            @InRange(minInt = 1, maxInt = 300) int rank) {

        // No-arg constructor
        Player player1 = new Player();
        assertFalse("New Player() should default isFavorite() to false",
                player1.isFavorite());

        // 4-arg constructor
        Player player2 = new Player("id-" + rank, "Player " + rank, "QB", rank);
        assertFalse("New Player(id, name, pos, rank) should default isFavorite() to false",
                player2.isFavorite());

        // 6-arg constructor
        Player player3 = new Player("id-" + rank, "Player " + rank, "RB", rank, false, null);
        assertFalse("New Player(id, name, pos, rank, drafted, draftedBy) should default isFavorite() to false",
                player3.isFavorite());
    }

    /**
     * Concrete test: Parse a JSON player with "favorite": true and verify isFavorite().
     * Uses reflection to call the private parsePlayerObject method.
     *
     * **Validates: Requirements 1.2, 2.2**
     */
    @Test
    public void parsePlayerObjectReadsFavoriteTrue() throws Exception {
        JSONObject json = new JSONObject();
        json.put("id", "1");
        json.put("name", "Patrick Mahomes");
        json.put("position", "QB");
        json.put("rank", 1);
        json.put("favorite", true);

        // Use reflection to invoke the private parsePlayerObject method
        Object parser = new com.fantasydraft.picker.utils.PlayerDataParser();
        Method parseMethod = parser.getClass().getDeclaredMethod("parsePlayerObject", JSONObject.class);
        parseMethod.setAccessible(true);
        Player player = (Player) parseMethod.invoke(parser, json);

        assertNotNull("Parsed player should not be null", player);
        assertEquals("1", player.getId());
        assertEquals("Patrick Mahomes", player.getName());

        // Bug condition: isFavorite() does not exist on Player
        assertTrue("Player parsed from JSON with \"favorite\": true should have isFavorite() == true",
                player.isFavorite());
    }

    /**
     * Concrete test: Parse a JSON player with "favorite": false and verify isFavorite().
     *
     * **Validates: Requirements 2.2**
     */
    @Test
    public void parsePlayerObjectReadsFavoriteFalse() throws Exception {
        JSONObject json = new JSONObject();
        json.put("id", "2");
        json.put("name", "Josh Allen");
        json.put("position", "QB");
        json.put("rank", 2);
        json.put("favorite", false);

        Object parser = new com.fantasydraft.picker.utils.PlayerDataParser();
        Method parseMethod = parser.getClass().getDeclaredMethod("parsePlayerObject", JSONObject.class);
        parseMethod.setAccessible(true);
        Player player = (Player) parseMethod.invoke(parser, json);

        assertNotNull(player);
        assertFalse("Player parsed from JSON with \"favorite\": false should have isFavorite() == false",
                player.isFavorite());
    }

    /**
     * Concrete test: Parse a JSON player WITHOUT "favorite" field — should default to false.
     *
     * **Validates: Requirements 2.2**
     */
    @Test
    public void parsePlayerObjectDefaultsFavoriteToFalse() throws Exception {
        JSONObject json = new JSONObject();
        json.put("id", "3");
        json.put("name", "Lamar Jackson");
        json.put("position", "QB");
        json.put("rank", 3);
        // No "favorite" field

        Object parser = new com.fantasydraft.picker.utils.PlayerDataParser();
        Method parseMethod = parser.getClass().getDeclaredMethod("parsePlayerObject", JSONObject.class);
        parseMethod.setAccessible(true);
        Player player = (Player) parseMethod.invoke(parser, json);

        assertNotNull(player);
        assertFalse("Player parsed from JSON without \"favorite\" field should have isFavorite() == false",
                player.isFavorite());
    }
}

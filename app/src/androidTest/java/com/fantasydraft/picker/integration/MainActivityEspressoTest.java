package com.fantasydraft.picker.integration;

import android.content.Context;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.fantasydraft.picker.R;
import com.fantasydraft.picker.persistence.PersistenceManager;
import com.fantasydraft.picker.ui.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;

/**
 * Espresso UI integration tests for MainActivity.
 * Tests user interactions with the draft screen.
 * 
 * Requirements: All UI requirements
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityEspressoTest {
    
    private Context context;
    private PersistenceManager persistenceManager;
    
    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        persistenceManager = new PersistenceManager(context);
        
        // Clear any existing draft data before each test
        persistenceManager.clearDraft();
    }
    
    @After
    public void tearDown() {
        // Clean up after tests
        if (persistenceManager != null) {
            persistenceManager.clearDraft();
        }
    }
    
    /**
     * Test that MainActivity launches and displays all main UI components.
     * Requirements: 1.4, 4.1, 5.1, 8.1
     */
    @Test
    public void testMainActivityLaunchesAndDisplaysComponents() {
        // Launch MainActivity
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        
        // Verify draft configuration section is displayed
        onView(withId(R.id.text_team_count))
                .check(matches(isDisplayed()))
                .check(matches(withText(containsString("Teams:"))));
        
        onView(withId(R.id.text_flow_type))
                .check(matches(isDisplayed()))
                .check(matches(withText(containsString("Flow:"))));
        
        // Verify current pick section is displayed
        onView(withId(R.id.text_round_pick))
                .check(matches(isDisplayed()))
                .check(matches(withText(containsString("Round"))));
        
        onView(withId(R.id.text_current_team))
                .check(matches(isDisplayed()))
                .check(matches(withText(containsString("Team"))));
        
        onView(withId(R.id.button_make_pick))
                .check(matches(isDisplayed()));
        
        onView(withId(R.id.button_reset_draft))
                .check(matches(isDisplayed()));
        
        // Verify best available player section is displayed
        onView(withId(R.id.text_best_player_name))
                .check(matches(isDisplayed()));
        
        onView(withId(R.id.text_best_player_position))
                .check(matches(isDisplayed()));
        
        onView(withId(R.id.button_view_all_players))
                .check(matches(isDisplayed()));
        
        // Verify draft history RecyclerView is displayed
        onView(withId(R.id.recycler_draft_history))
                .check(matches(isDisplayed()));
        
        scenario.close();
    }
    
    /**
     * Test that default draft configuration is displayed correctly.
     * Requirements: 1.4, 3.1
     */
    @Test
    public void testDefaultDraftConfigurationDisplayed() {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        
        // Verify default team count (10 teams)
        onView(withId(R.id.text_team_count))
                .check(matches(withText("Teams: 10")));
        
        // Verify default flow type (Serpentine)
        onView(withId(R.id.text_flow_type))
                .check(matches(withText("Flow: Serpentine")));
        
        scenario.close();
    }
    
    /**
     * Test that current pick information is displayed correctly.
     * Requirements: 4.1, 4.4
     */
    @Test
    public void testCurrentPickDisplayed() {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        
        // Verify initial pick is Round 1, Pick 1
        onView(withId(R.id.text_round_pick))
                .check(matches(withText("Round 1, Pick 1")));
        
        // Verify current team is displayed
        onView(withId(R.id.text_current_team))
                .check(matches(withText(containsString("Team"))));
        
        scenario.close();
    }
    
    /**
     * Test that best available player is displayed.
     * Requirements: 5.1, 5.2
     */
    @Test
    public void testBestAvailablePlayerDisplayed() {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        
        // Verify best available player name is displayed
        onView(withId(R.id.text_best_player_name))
                .check(matches(isDisplayed()));
        
        // Verify best available player position is displayed
        onView(withId(R.id.text_best_player_position))
                .check(matches(isDisplayed()));
        
        scenario.close();
    }
    
    /**
     * Test that reset draft button shows confirmation dialog.
     * Requirements: 9.5
     */
    @Test
    public void testResetDraftShowsConfirmationDialog() {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        
        // Click reset draft button
        onView(withId(R.id.button_reset_draft))
                .perform(click());
        
        // Verify confirmation dialog is displayed
        onView(withText("Reset Draft"))
                .check(matches(isDisplayed()));
        
        onView(withText(containsString("Are you sure")))
                .check(matches(isDisplayed()));
        
        onView(withText("Confirm"))
                .check(matches(isDisplayed()));
        
        onView(withText("Cancel"))
                .check(matches(isDisplayed()));
        
        // Cancel the dialog
        onView(withText("Cancel"))
                .perform(click());
        
        scenario.close();
    }
    
    /**
     * Test that reset draft confirmation executes reset.
     * Requirements: 9.1, 9.2, 9.3, 9.4, 9.5
     */
    @Test
    public void testResetDraftConfirmationExecutesReset() {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        
        // Click reset draft button
        onView(withId(R.id.button_reset_draft))
                .perform(click());
        
        // Confirm reset
        onView(withText("Confirm"))
                .perform(click());
        
        // Verify draft is reset to Round 1, Pick 1
        onView(withId(R.id.text_round_pick))
                .check(matches(withText("Round 1, Pick 1")));
        
        scenario.close();
    }
    
    /**
     * Test that all buttons are clickable.
     * Requirements: 1.4, 4.4, 5.1, 9.5
     */
    @Test
    public void testAllButtonsAreClickable() {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        
        // Test make pick button (should show dialog)
        onView(withId(R.id.button_make_pick))
                .check(matches(isDisplayed()))
                .perform(click());
        
        // Test view all players button
        onView(withId(R.id.button_view_all_players))
                .check(matches(isDisplayed()))
                .perform(click());
        
        // Test reset draft button
        onView(withId(R.id.button_reset_draft))
                .check(matches(isDisplayed()))
                .perform(click());
        
        // Cancel the dialog
        onView(withText("Cancel"))
                .perform(click());
        
        scenario.close();
    }
    
    /**
     * Test that draft history RecyclerView is present.
     * Requirements: 8.1
     */
    @Test
    public void testDraftHistoryRecyclerViewPresent() {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        
        // Verify RecyclerView is displayed
        onView(withId(R.id.recycler_draft_history))
                .check(matches(isDisplayed()));
        
        scenario.close();
    }
}

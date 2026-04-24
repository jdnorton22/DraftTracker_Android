# Developer Guide: Accessibility & Loading States

## Quick Reference for Developers

### Using LoadingHelper

#### Basic Setup
```java
// In your Activity or Fragment
private LoadingHelper loadingHelper;

@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.your_layout, container, false);
    
    // Initialize loading helper (requires loading_overlay in your layout)
    loadingHelper = new LoadingHelper(view);
    
    return view;
}
```

#### Show/Hide Loading
```java
// Show loading with default message
loadingHelper.showLoading();

// Show loading with custom message
loadingHelper.showLoading(getString(R.string.loading_players));

// Hide loading
loadingHelper.hideLoading();

// Check if loading
if (loadingHelper.isLoading()) {
    // Handle loading state
}
```

#### Example: Loading Data
```java
private void loadPlayerData() {
    loadingHelper.showLoading(getString(R.string.loading_players));
    
    // Simulate async operation
    new Handler().postDelayed(() -> {
        // Load data
        List<Player> players = fetchPlayers();
        
        // Update UI
        updatePlayerList(players);
        
        // Hide loading
        loadingHelper.hideLoading();
        
        // Announce to screen readers
        LoadingHelper.announceForAccessibility(getView(), 
            players.size() + " players loaded");
    }, 1000);
}
```

### Adding Loading Overlay to Layouts

Include the loading overlay in your layout:

```xml
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">
    
    <!-- Your main content -->
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent">
        <!-- Content here -->
    </LinearLayout>
    
    <!-- Loading overlay -->
    <include layout="@layout/loading_overlay" />
    
</FrameLayout>
```

### Accessibility Best Practices

#### 1. Touch Targets
Always use minimum 48dp for interactive elements:

```xml
<Button
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:minHeight="@dimen/min_touch_target"
    android:minWidth="@dimen/min_touch_target" />
```

Or programmatically:
```java
LoadingHelper.setMinTouchTarget(button, 48);
```

#### 2. Content Descriptions
Add meaningful descriptions to all interactive elements:

```xml
<ImageButton
    android:id="@+id/button_action"
    android:layout_width="@dimen/min_touch_target"
    android:layout_height="@dimen/min_touch_target"
    android:src="@drawable/ic_action"
    android:contentDescription="@string/cd_action_button" />
```

For dynamic content:
```java
button.setContentDescription("Draft " + playerName);
```

#### 3. Decorative Elements
Mark decorative elements to skip in screen readers:

```xml
<ImageView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:src="@drawable/decoration"
    android:importantForAccessibility="no" />
```

#### 4. Grouping Related Content
Group related elements for better screen reader navigation:

```xml
<LinearLayout
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:contentDescription="Player information">
    
    <TextView android:id="@+id/player_name"
        android:importantForAccessibility="no" />
    <TextView android:id="@+id/player_position"
        android:importantForAccessibility="no" />
        
</LinearLayout>
```

#### 5. Announcing Changes
Announce important state changes:

```java
LoadingHelper.announceForAccessibility(view, "Draft pick completed");
```

### Visual Feedback

#### Add Ripple Effect
```java
LoadingHelper.addRippleEffect(button);
```

#### Toast Messages
Use for important feedback:
```java
Toast.makeText(context, "Player drafted successfully", Toast.LENGTH_SHORT).show();
```

### Using Dimension Resources

Always use dimension resources instead of hardcoded values:

```xml
<!-- Good -->
<View
    android:layout_margin="@dimen/spacing_medium"
    android:padding="@dimen/spacing_small" />

<!-- Bad -->
<View
    android:layout_margin="12dp"
    android:padding="8dp" />
```

Available dimensions:
- `@dimen/min_touch_target` - 48dp (minimum for interactive elements)
- `@dimen/touch_target_comfortable` - 56dp (comfortable size)
- `@dimen/spacing_tiny` - 4dp
- `@dimen/spacing_small` - 8dp
- `@dimen/spacing_medium` - 12dp
- `@dimen/spacing_large` - 16dp
- `@dimen/spacing_xlarge` - 24dp
- `@dimen/position_badge_size` - 48dp
- `@dimen/position_badge_small` - 40dp

### Common Patterns

#### Pattern 1: Button with Loading State
```java
private void onButtonClick() {
    // Disable button
    button.setEnabled(false);
    
    // Show loading
    loadingHelper.showLoading(getString(R.string.processing_pick));
    
    // Perform operation
    performOperation(result -> {
        // Hide loading
        loadingHelper.hideLoading();
        
        // Re-enable button
        button.setEnabled(true);
        
        // Show result
        if (result.isSuccess()) {
            Toast.makeText(context, "Success!", Toast.LENGTH_SHORT).show();
            LoadingHelper.announceForAccessibility(view, "Operation completed successfully");
        } else {
            Toast.makeText(context, "Error: " + result.getError(), Toast.LENGTH_LONG).show();
        }
    });
}
```

#### Pattern 2: Dialog with Loading
```java
public class MyDialog extends Dialog {
    private LoadingHelper loadingHelper;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_dialog);
        
        loadingHelper = new LoadingHelper(findViewById(android.R.id.content));
    }
    
    public void loadData() {
        loadingHelper.showLoading();
        // Load data...
        loadingHelper.hideLoading();
    }
}
```

#### Pattern 3: Dynamic Content Description
```java
private void updatePositionCount(String position, int count, boolean isTeamView) {
    String scope = isTeamView ? "on your team" : "league-wide";
    String description = position + ". " + count + " drafted " + scope;
    positionBadge.setContentDescription(description);
}
```

### Testing Checklist

Before submitting code, verify:

- [ ] All interactive elements are at least 48x48dp
- [ ] All interactive elements have content descriptions
- [ ] Decorative elements marked with `importantForAccessibility="no"`
- [ ] Loading states shown for operations > 500ms
- [ ] Buttons disabled during loading
- [ ] Success/error feedback provided (Toast or announcement)
- [ ] Tested with TalkBack enabled
- [ ] Tested with large font sizes
- [ ] No hardcoded dimensions (use `@dimen/` resources)
- [ ] Color contrast meets WCAG AA standards (4.5:1)

### Resources

#### String Resources for Loading
```xml
<string name="loading_players">Loading players…</string>
<string name="loading_draft_data">Loading draft data…</string>
<string name="processing_pick">Processing pick…</string>
<string name="saving_draft">Saving draft…</string>
```

#### String Resources for Accessibility
```xml
<string name="cd_position_badge">Position badge</string>
<string name="cd_quarterback_count">Quarterbacks drafted</string>
<string name="cd_running_back_count">Running backs drafted</string>
<string name="cd_wide_receiver_count">Wide receivers drafted</string>
<string name="cd_tight_end_count">Tight ends drafted</string>
<string name="cd_kicker_count">Kickers drafted</string>
<string name="cd_defense_count">Defenses drafted</string>
<string name="cd_toggle_team_view">Switch to team view</string>
<string name="cd_toggle_league_view">Switch to league view</string>
<string name="cd_filter_expand">Expand filters</string>
<string name="cd_filter_collapse">Collapse filters</string>
<string name="cd_loading">Loading</string>
```

### Common Mistakes to Avoid

❌ **Don't**: Hardcode touch target sizes
```xml
<Button android:layout_width="40dp" android:layout_height="40dp" />
```

✅ **Do**: Use minimum touch target dimension
```xml
<Button android:minWidth="@dimen/min_touch_target" 
        android:minHeight="@dimen/min_touch_target" />
```

---

❌ **Don't**: Forget content descriptions
```xml
<ImageButton android:src="@drawable/ic_close" />
```

✅ **Do**: Add meaningful descriptions
```xml
<ImageButton android:src="@drawable/ic_close"
             android:contentDescription="@string/cd_close" />
```

---

❌ **Don't**: Use color alone to convey information
```java
textView.setTextColor(Color.RED); // Only color indicates error
```

✅ **Do**: Combine color with text/icons
```java
textView.setTextColor(Color.RED);
textView.setText("Error: " + message); // Text also indicates error
```

---

❌ **Don't**: Block UI without feedback
```java
performLongOperation(); // User sees nothing
```

✅ **Do**: Show loading state
```java
loadingHelper.showLoading();
performLongOperation();
loadingHelper.hideLoading();
```

### Need Help?

- Review `ACCESSIBILITY_AND_LOADING_IMPROVEMENTS.md` for detailed documentation
- Check existing implementations in `DraftFragment.java` and `PlayerSelectionDialog.java`
- Test with Android Accessibility Scanner
- Consult [Android Accessibility Guidelines](https://developer.android.com/guide/topics/ui/accessibility)

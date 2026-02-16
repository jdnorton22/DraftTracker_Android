# Player Data Refresh - User Guide

## Overview

The Player Data Refresh feature allows you to fetch the latest player rankings, statistics, and injury information from ESPN Fantasy Football. This ensures you're working with the most current data before starting or continuing your draft.

## When to Use Player Data Refresh

- **Before starting a new draft season** - Get the latest preseason rankings
- **After major NFL events** - Update data after injuries, trades, or roster changes
- **Weekly during the season** - Keep player rankings current as the season progresses
- **When data seems outdated** - If you notice stale information

## How to Refresh Player Data

### Step 1: Access Configuration Screen

1. Open the Fantasy Draft Picker app
2. From the main screen, tap the **Settings** or **Configuration** button
3. You'll see the configuration screen with draft setup options

### Step 2: Initiate Refresh

1. Scroll to find the **"Refresh Player Data"** button
2. Tap the button to begin the refresh process

### Step 3: Confirm the Action

**If you have an active draft in progress:**
- A confirmation dialog will appear with the message:
  > "This will fetch the latest player data from ESPN and reset your current draft. All picks will be cleared. Continue?"
- Review the warning carefully
- Choose:
  - **Cancel** - Abort the refresh and keep your current draft
  - **Refresh** - Proceed with the refresh (this will clear all picks)

**If no draft is in progress:**
- The refresh will begin immediately without a confirmation dialog

### Step 4: Wait for Completion

- A progress dialog will appear showing "Refreshing Player Data..."
- The app will fetch data from ESPN (typically takes 3-10 seconds)
- Do not close the app during this process

### Step 5: Review Results

**On Success:**
- You'll see a success message: "Player data refreshed successfully! X players updated."
- The app will automatically reload with the new data
- If you had a draft in progress, it will be reset to Round 1, Pick 1

**On Error:**
- An error message will explain what went wrong
- Your existing player data and draft state will remain unchanged
- See the Troubleshooting section below for solutions

## What Happens During Refresh

The refresh operation performs the following actions:

1. **Fetches Latest Data** - Downloads current player information from ESPN
2. **Updates Player Information** - Refreshes rankings, stats, injury status, and team assignments
3. **Resets Draft State** (if applicable):
   - Clears all pick history
   - Marks all players as undrafted
   - Clears all team rosters
   - Resets to Round 1, Pick 1
4. **Saves Changes** - Persists the updated data locally

## Important Notes

### Draft Reset Warning

⚠️ **IMPORTANT**: Refreshing player data will completely reset any draft in progress. All picks will be cleared and cannot be recovered. Make sure you're ready to start fresh before confirming the refresh.

### Network Requirements

- An active internet connection is required
- The refresh will not work in airplane mode or without network access
- Ensure you have a stable connection for best results

### Data Persistence

- Once refreshed, the new player data is saved locally
- You don't need to refresh every time you open the app
- The data remains current until you refresh again

### Refresh Frequency

- There's no limit on how often you can refresh
- However, ESPN data typically updates once per day during the season
- Refreshing multiple times per day may not yield different results

## Troubleshooting

### "No internet connection" Error

**Problem**: The app cannot connect to the internet.

**Solutions**:
1. Check that your device has an active internet connection
2. Try opening a web browser to verify connectivity
3. Disable airplane mode if it's enabled
4. Switch between Wi-Fi and mobile data
5. Restart your device's network connection

### "Unable to reach ESPN servers" Error

**Problem**: The ESPN API is unreachable or down.

**Solutions**:
1. Wait a few minutes and try again
2. Check if ESPN.com is accessible in your web browser
3. Try again during off-peak hours
4. Verify your network isn't blocking ESPN domains

### "Request timed out" Error

**Problem**: The network request took longer than 30 seconds.

**Solutions**:
1. Check your internet connection speed
2. Move to a location with better signal strength
3. Switch to a faster network (Wi-Fi instead of mobile data)
4. Try again when network conditions improve

### "Invalid data format" Error

**Problem**: The data received from ESPN couldn't be parsed correctly.

**Solutions**:
1. Try refreshing again (temporary ESPN API issue)
2. Wait 10-15 minutes and retry
3. If the problem persists, the ESPN API format may have changed
4. Contact support if the issue continues

### "Unable to save player data" Error

**Problem**: The app couldn't write the updated data to storage.

**Solutions**:
1. Check available storage space on your device
2. Ensure the app has storage permissions
3. Close other apps to free up memory
4. Restart the app and try again

### Refresh Button is Disabled

**Problem**: The refresh button is grayed out and cannot be tapped.

**Solutions**:
1. Wait for any ongoing refresh operation to complete
2. The button is temporarily disabled during refresh to prevent conflicts
3. If stuck, force close and restart the app

### Data Doesn't Appear Updated

**Problem**: After refresh, the data looks the same as before.

**Possible Reasons**:
1. ESPN data hasn't changed since your last refresh
2. The refresh completed but the UI didn't reload (restart the app)
3. The refresh failed silently (check for error messages)

**Solutions**:
1. Restart the app to ensure data is reloaded
2. Check the success message for the number of players updated
3. Compare specific player rankings to verify changes

## Best Practices

### Before Draft Day

1. Refresh player data the morning of your draft
2. Verify the data looks current by checking a few key players
3. Start your draft with confidence in the rankings

### During the Season

1. Refresh weekly before setting your lineup
2. Refresh immediately after major injury news
3. Keep an eye on the last updated timestamp (if available)

### Managing Draft Progress

1. Complete your draft before refreshing
2. Export draft results before refreshing if needed
3. Don't refresh in the middle of an active draft unless necessary

## FAQ

**Q: How often should I refresh player data?**
A: Refresh before starting a new draft or when you know significant changes have occurred (injuries, trades, etc.). Weekly refreshes during the season are recommended.

**Q: Will refreshing delete my saved drafts?**
A: No, completed drafts saved in draft history are not affected. Only the current in-progress draft is reset.

**Q: Can I undo a refresh?**
A: No, once a refresh is completed, it cannot be undone. The previous player data is overwritten.

**Q: Does refresh require ESPN login?**
A: No, the app uses publicly available ESPN Fantasy Football data that doesn't require authentication.

**Q: What if I accidentally refresh during a draft?**
A: Unfortunately, the draft will be reset and picks cannot be recovered. Always pay attention to the confirmation dialog.

**Q: How much data does a refresh use?**
A: Approximately 500KB per refresh. This is minimal and shouldn't significantly impact your data plan.

**Q: Can I refresh offline?**
A: No, an active internet connection is required to fetch data from ESPN.

## Support

If you encounter issues not covered in this guide:

1. Check that you're using the latest version of the app
2. Try restarting the app
3. Verify your internet connection is stable
4. Review the error message carefully
5. Contact support with specific error details

## Related Features

- **Draft Configuration** - Set up teams and draft order before starting
- **Draft History** - View completed drafts (not affected by refresh)
- **Player Search** - Find specific players in the updated data
- **Export Draft** - Save draft results before refreshing

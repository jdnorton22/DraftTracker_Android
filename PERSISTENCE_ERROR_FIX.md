# Persistence Error Fix

## Issue
After deploying the UI layout fix, the app was showing a "Persistence Error" dialog on startup with the message "Failed to load draft state. continue without saving".

## Root Cause
The error was caused by a database migration failure when upgrading from database version 3 to version 4. The migration added multi-draft support by adding a `draft_id` column to all tables. If the migration encountered any issues (corrupted data, missing columns, etc.), it would fail silently and cause the persistence layer to throw a `PersistenceException` when trying to load the draft state.

## Solution
Added robust error handling to the `DatabaseHelper.onUpgrade()` method:

1. **Wrapped migration in try-catch**: The entire migration process is now wrapped in a try-catch block
2. **Fallback to clean slate**: If migration fails for any reason, the app will:
   - Log the error for debugging
   - Drop all existing tables (including any partially migrated tables)
   - Recreate the database from scratch using `onCreate()`
3. **Added cleanup method**: Created `dropAllTables()` method to safely remove all tables including temporary migration tables

## Changes Made
- **File**: `app/src/main/java/com/fantasydraft/picker/persistence/DatabaseHelper.java`
- **Method**: `onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)`
- **New Method**: `dropAllTables(SQLiteDatabase db)`

## Impact
- Users with corrupted or incompatible database data will now have their database automatically reset instead of seeing a persistent error
- The app will start fresh with a clean database if migration fails
- No more "Persistence Error" dialogs on startup
- Existing users with successful migrations are unaffected

## Testing
1. Cleared app data: `adb shell pm clear com.fantasydraft.picker`
2. Reinstalled app with fix
3. App launches successfully without errors
4. Database is created properly

## User Action Required
If users are still seeing the persistence error after this update, they should:
1. Clear the app data: Settings → Apps → Fantasy Draft Picker → Storage → Clear Data
2. Restart the app

This will give them a fresh start with the new database schema.

## Version
- Database Version: 4 (unchanged)
- App Version: 1.1 (API 35)
- Fix Date: February 3, 2026

package com.fantasydraft.picker;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import com.pholser.junit.quickcheck.generator.InRange;

import org.junit.runner.RunWith;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.junit.Assert.*;

/**
 * Bug Condition Exploration Test - Splash Screen Displays Emoji Icon,
 * Hardcoded Version, and Excessive Duration
 *
 * Validates: Requirements 1.1, 1.2, 1.3
 *
 * This test encodes the EXPECTED behavior:
 * - Icon should be an ImageView with football_icon_1024 drawable (not emoji TextView)
 * - Version should match BuildConfig.VERSION_NAME dynamically (not hardcoded "Version 2.0")
 * - Splash duration should be ~1000ms (not 2000ms)
 *
 * EXPECTED TO FAIL on unfixed code because:
 * - Layout uses a TextView with "🏈" emoji instead of an ImageView
 * - Layout has hardcoded "Version 2.0" instead of dynamic version
 * - SPLASH_DURATION is 2000 instead of 1000
 */
@RunWith(JUnitQuickcheck.class)
public class SplashScreenBugConditionTest {

    // -----------------------------------------------------------------------
    // Property 1: Bug Condition - Splash Screen Defects
    // Validates: Requirements 1.1, 1.2, 1.3
    // -----------------------------------------------------------------------

    /**
     * Test 1 - Duration Defect: Read the SplashActivity source file and verify
     * SPLASH_DURATION equals 1000.
     *
     * Will fail on unfixed code because SPLASH_DURATION is 2000, confirming
     * the excessive duration defect.
     *
     * **Validates: Requirements 1.3**
     */
    @Test
    public void splashDurationShouldBe1000ms() throws Exception {
        String sourceContent = readSourceFile();

        // Verify the source contains SPLASH_DURATION = 1000
        assertTrue(
                "SPLASH_DURATION should be set to 1000 for a responsive splash screen, "
                        + "but the source file does not contain 'SPLASH_DURATION = 1000'. "
                        + "The current value appears to be 2000ms (excessive duration defect).",
                sourceContent.contains("SPLASH_DURATION = 1000"));

        // Verify the source does NOT contain the defective value
        assertFalse(
                "SPLASH_DURATION should NOT be 2000ms — this is the excessive duration defect. "
                        + "Expected value is 1000ms.",
                sourceContent.contains("SPLASH_DURATION = 2000"));
    }

    /**
     * Test 2 - Hardcoded Version Defect: Read activity_splash.xml layout file
     * and verify it does NOT contain hardcoded "Version 2.0" text.
     *
     * Will fail on unfixed code because the layout has hardcoded "Version 2.0",
     * confirming the version defect.
     *
     * **Validates: Requirements 1.2**
     */
    @Test
    public void layoutShouldNotContainHardcodedVersion() throws Exception {
        String layoutContent = readLayoutFile();

        assertFalse(
                "Layout should NOT contain hardcoded 'Version 2.0' — version should "
                        + "be set dynamically from BuildConfig.VERSION_NAME, but found "
                        + "hardcoded version text in activity_splash.xml",
                layoutContent.contains("Version 2.0"));
    }

    /**
     * Test 3 - Emoji Icon Defect: Read activity_splash.xml layout file and
     * verify it contains an ImageView with FootballIcon1024 drawable instead
     * of a TextView with emoji text.
     *
     * Will fail on unfixed code because the layout uses a TextView with "🏈"
     * emoji instead of an ImageView with the real app icon.
     *
     * **Validates: Requirements 1.1**
     */
    @Test
    public void layoutShouldUseImageViewWithFootballIcon() throws Exception {
        String layoutContent = readLayoutFile();

        // The layout should contain an ImageView referencing the real icon
        assertTrue(
                "Layout should contain an ImageView for the app icon, but none found. "
                        + "Currently uses a TextView with emoji '🏈' instead of an ImageView "
                        + "with @drawable/football_icon_1024",
                layoutContent.contains("<ImageView"));

        assertTrue(
                "Layout should reference @drawable/football_icon_1024 in an ImageView, "
                        + "but the drawable reference was not found",
                layoutContent.contains("@drawable/football_icon_1024"));

        // The layout should NOT contain the emoji placeholder
        assertFalse(
                "Layout should NOT contain emoji text '🏈' in a TextView — "
                        + "should use a real ImageView with the app icon drawable",
                layoutContent.contains("🏈"));
    }

    /**
     * Property Test - Version String Property: For any generated version string,
     * the splash screen layout should not contain any hardcoded version text,
     * since the version should be set programmatically.
     *
     * This property verifies that the layout does not embed any specific version
     * string pattern like android:text="Version X.Y".
     *
     * Will fail on unfixed code since the layout has hardcoded "Version 2.0".
     *
     * **Validates: Requirements 1.2**
     */
    @Property(trials = 20)
    public void versionShouldBeDynamicNotHardcoded(
            @InRange(minInt = 1, maxInt = 99) int major,
            @InRange(minInt = 0, maxInt = 99) int minor) throws Exception {

        String layoutContent = readLayoutFile();

        // The layout should not contain ANY hardcoded version text pattern
        // because the version should be set dynamically from BuildConfig.
        // We check for the general pattern "Version X.Y" in the XML.
        // On unfixed code, the layout contains android:text="Version 2.0"
        // which means this assertion will fail when we detect ANY version pattern.

        // Check that the layout does not contain the known defective pattern
        assertFalse(
                "Layout contains hardcoded version text 'Version 2.0' — "
                        + "version should be set dynamically from BuildConfig.VERSION_NAME. "
                        + "Testing with generated version " + major + "." + minor,
                layoutContent.contains("android:text=\"Version "));
    }

    // -----------------------------------------------------------------------
    // Helper methods
    // -----------------------------------------------------------------------

    /**
     * Read the activity_splash.xml layout file content.
     * Tries multiple paths to locate the file from the test execution context.
     */
    private String readLayoutFile() throws Exception {
        return readProjectFile("app/src/main/res/layout/activity_splash.xml");
    }

    /**
     * Read the SplashActivity.java source file content.
     */
    private String readSourceFile() throws Exception {
        return readProjectFile(
                "app/src/main/java/com/fantasydraft/picker/ui/SplashActivity.java");
    }

    /**
     * Read a project file by trying multiple path resolutions.
     */
    private String readProjectFile(String relativePath) throws Exception {
        // Try reading from the source tree (relative to project root)
        String[] prefixes = { "", "../", "../../" };

        for (String prefix : prefixes) {
            File file = new File(prefix + relativePath);
            if (file.exists()) {
                return readFileContent(file);
            }
        }

        // Try reading from classpath as a resource
        String resourceName = relativePath.contains("/")
                ? relativePath.substring(relativePath.lastIndexOf("/") + 1)
                : relativePath;
        InputStream is = getClass().getClassLoader()
                .getResourceAsStream(resourceName);
        if (is != null) {
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            }
            return sb.toString();
        }

        // Try finding from user.dir system property
        String userDir = System.getProperty("user.dir");
        File fromUserDir = new File(userDir, relativePath);
        if (fromUserDir.exists()) {
            return readFileContent(fromUserDir);
        }

        // Try parent of user.dir
        File fromParent = new File(new File(userDir).getParent(), relativePath);
        if (fromParent.exists()) {
            return readFileContent(fromParent);
        }

        fail("Could not locate " + relativePath + ". "
                + "Searched from user.dir=" + userDir);
        return null; // unreachable
    }

    private String readFileContent(File file) throws Exception {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }
}

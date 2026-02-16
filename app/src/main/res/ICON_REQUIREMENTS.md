# App Icon Requirements

## Icon Design
The Fantasy Draft Picker app icon should feature:
- A football (American football) in the center
- A draft board or clipboard background
- Green and gold color scheme matching the app theme
- Clean, modern design that works at small sizes

## Required Icon Sizes

### Launcher Icons (mipmap directories)
Create the following icon files with the app icon design:

- `mipmap-mdpi/ic_launcher.png` - 48x48 px
- `mipmap-hdpi/ic_launcher.png` - 72x72 px
- `mipmap-xhdpi/ic_launcher.png` - 96x96 px
- `mipmap-xxhdpi/ic_launcher.png` - 144x144 px
- `mipmap-xxxhdpi/ic_launcher.png` - 192x192 px

### Adaptive Icons (Android 8.0+)
For modern Android versions, also create adaptive icon layers:

- `mipmap-anydpi-v26/ic_launcher.xml` - Adaptive icon configuration
- `drawable/ic_launcher_foreground.xml` - Foreground layer (football/draft board)
- `drawable/ic_launcher_background.xml` - Background layer (green field color)

## Color Palette
- Primary Green: #2E7D32 (football field green)
- Dark Green: #1B5E20 (darker variant)
- Gold: #FFA000 (trophy/accent color)
- White: #FFFFFF (text/details)

## Design Tools
Use one of these tools to create the icons:
1. Android Studio Image Asset Studio (built-in)
2. Online tools like https://romannurik.github.io/AndroidAssetStudio/
3. Design software like Figma, Adobe Illustrator, or Inkscape

## Implementation
Once icons are created, update AndroidManifest.xml to reference them:
```xml
<application
    android:icon="@mipmap/ic_launcher"
    android:roundIcon="@mipmap/ic_launcher_round"
    ...>
```

**Note:** Icon references have been removed from AndroidManifest.xml until actual icon files are created to prevent build errors.

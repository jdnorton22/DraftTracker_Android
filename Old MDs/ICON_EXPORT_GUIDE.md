# Fantasy Draft Picker - App Icon Export Guide

## Icon Files Created

The app icon has been exported to PNG format in multiple sizes:

### Available Icon Files

1. **fantasy_draft_picker_icon.png** (512x512) - Standard size
   - Location: `C:\Workspaces\Workspace_DraftTracker\fantasy_draft_picker_icon.png`
   - Size: 2.6 KB
   - Use: General purpose, web, documentation

2. **fantasy_draft_picker_icon_1024.png** (1024x1024) - High resolution
   - Location: `C:\Workspaces\Workspace_DraftTracker\fantasy_draft_picker_icon_1024.png`
   - Size: 6.5 KB
   - Use: App store listings, high-DPI displays, print

3. **fantasy_draft_picker_icon_256.png** (256x256) - Small size
   - Location: `C:\Workspaces\Workspace_DraftTracker\fantasy_draft_picker_icon_256.png`
   - Size: 1.1 KB
   - Use: Thumbnails, small displays

## Icon Design

The Fantasy Draft Picker icon features:
- **Background**: Blue (#1565C0) - representing team colors and professionalism
- **Foreground**: White football with brown laces (#8B4513)
- **Style**: Clean, modern, easily recognizable
- **Format**: Vector-based design rendered to PNG

## Export Custom Sizes

### Using Python Script

```bash
python scripts\export_app_icon.py <output_path> [size]
```

**Examples:**
```bash
# Export 512x512 icon
python scripts\export_app_icon.py my_icon.png

# Export 1024x1024 icon
python scripts\export_app_icon.py my_icon.png 1024

# Export to specific directory
python scripts\export_app_icon.py C:\Icons\fantasy_draft.png 512

# Export 2048x2048 for high-res displays
python scripts\export_app_icon.py icon_2048.png 2048
```

### Using Batch File (Windows)

```bash
scripts\export_icon.bat <output_path> [size]
```

**Examples:**
```bash
# Export 512x512 icon
scripts\export_icon.bat my_icon.png

# Export 1024x1024 icon
scripts\export_icon.bat my_icon.png 1024

# Export to Desktop
scripts\export_icon.bat C:\Users\YourName\Desktop\app_icon.png
```

## Common Icon Sizes

| Size | Use Case |
|------|----------|
| 48x48 | Small app icons, favicons |
| 72x72 | Android launcher (ldpi) |
| 96x96 | Android launcher (mdpi) |
| 144x144 | Android launcher (hdpi) |
| 192x192 | Android launcher (xhdpi) |
| 256x256 | Thumbnails, small displays |
| 512x512 | Standard app icon, web |
| 1024x1024 | App store, high-DPI displays |
| 2048x2048 | Ultra high-res, print |

## Export to Custom Location

To export the icon to a specific directory:

```bash
# Export to Desktop
python scripts\export_app_icon.py C:\Users\YourName\Desktop\fantasy_draft_icon.png 512

# Export to Documents
python scripts\export_app_icon.py C:\Users\YourName\Documents\Icons\app_icon.png 1024

# Export to network drive
python scripts\export_app_icon.py Z:\SharedIcons\fantasy_draft.png 512
```

## Requirements

- **Python 3.x** (already installed)
- **Pillow (PIL)** (already installed)

If Pillow is not installed:
```bash
pip install Pillow
```

## Source Files

The icon is generated from the Android vector drawable resources:
- **Background**: `app/src/main/res/drawable/ic_launcher_background.xml`
- **Foreground**: `app/src/main/res/drawable/ic_launcher_foreground.xml`
- **Adaptive Icon**: `app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml`

## Modifying the Icon

To modify the icon design:

1. Edit the vector drawable XML files in `app/src/main/res/drawable/`
2. Update the Python script `scripts/export_app_icon.py` to match changes
3. Re-export the icon using the script

## Usage Examples

### For Documentation
Use the 512x512 version in README files, wikis, and documentation.

### For App Store
Use the 1024x1024 version for Google Play Store icon requirements.

### For Website
Use the 256x256 or 512x512 version for website favicons and headers.

### For Print
Use the 1024x1024 or 2048x2048 version for high-quality print materials.

## Troubleshooting

### "Module not found: PIL"
Install Pillow:
```bash
pip install Pillow
```

### "Permission denied"
Ensure you have write permissions to the output directory.

### "Size must be between 16 and 4096"
Choose a size within the valid range (16-4096 pixels).

---

**Created**: February 3, 2026
**Icon Version**: 1.1
**Format**: PNG (RGB)
**Background**: Blue (#1565C0)
**Foreground**: White football with brown laces

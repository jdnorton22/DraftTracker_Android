#!/usr/bin/env python3
"""
Export Fantasy Draft Picker app icon to PNG format.
Renders the vector drawable icon to various sizes.
"""

import os
import sys
from pathlib import Path

try:
    from PIL import Image, ImageDraw
except ImportError:
    print("ERROR: PIL (Pillow) is required. Install with: pip install Pillow")
    sys.exit(1)


def create_app_icon(size=512):
    """
    Create the Fantasy Draft Picker app icon.
    
    Args:
        size: Output size in pixels (default 512x512)
    
    Returns:
        PIL Image object
    """
    # Create image with blue background
    img = Image.new('RGB', (size, size), color='#1565C0')
    draw = ImageDraw.Draw(img)
    
    # Calculate scaling factor
    scale = size / 108.0
    
    # Football dimensions (scaled from 108dp viewport)
    # Original: centered at 54, radius ~28 (from viewBox 26-82)
    center_x = size // 2
    center_y = size // 2
    
    # Scale the football (original scale 0.6, translate 21.6)
    football_scale = 0.6
    translate = 21.6 * scale
    
    # Football ellipse bounds (original: 26,18 to 82,73)
    # After scaling: centered, width ~56, height ~55
    football_width = int(56 * scale * football_scale)
    football_height = int(55 * scale * football_scale)
    
    left = center_x - football_width // 2
    top = center_y - football_height // 2
    right = center_x + football_width // 2
    bottom = center_y + football_height // 2
    
    # Draw white football
    draw.ellipse([left, top, right, bottom], fill='#FFFFFF')
    
    # Draw brown laces
    lace_color = '#8B4513'
    lace_width = max(2, int(1.5 * scale * football_scale))
    
    # Vertical center line
    draw.line(
        [center_x, top + football_height // 4, center_x, bottom - football_height // 4],
        fill=lace_color,
        width=lace_width
    )
    
    # Horizontal lace lines
    lace_length = int(12 * scale * football_scale)
    lace_spacing = football_height // 6
    
    for i in range(4):
        y = top + football_height // 3 + i * lace_spacing
        draw.line(
            [center_x - lace_length, y, center_x + lace_length, y],
            fill=lace_color,
            width=max(1, int(1.2 * scale * football_scale))
        )
    
    return img


def export_icon(output_path, size=512):
    """
    Export the app icon to a PNG file.
    
    Args:
        output_path: Path where PNG should be saved
        size: Icon size in pixels (default 512x512)
    """
    print(f"Generating {size}x{size} app icon...")
    
    # Create the icon
    icon = create_app_icon(size)
    
    # Ensure output directory exists
    output_dir = os.path.dirname(output_path)
    if output_dir and not os.path.exists(output_dir):
        os.makedirs(output_dir)
    
    # Save the icon
    icon.save(output_path, 'PNG')
    print(f"✓ Icon saved to: {output_path}")
    
    # Show file info
    file_size = os.path.getsize(output_path)
    print(f"  Size: {size}x{size} pixels")
    print(f"  File size: {file_size:,} bytes ({file_size/1024:.1f} KB)")


def main():
    """Main entry point."""
    if len(sys.argv) < 2:
        print("Usage: python export_app_icon.py <output_path> [size]")
        print()
        print("Examples:")
        print("  python export_app_icon.py icon.png")
        print("  python export_app_icon.py C:\\Icons\\fantasy_draft_icon.png 1024")
        print("  python export_app_icon.py icon.png 512")
        print()
        print("Default size is 512x512 pixels if not specified.")
        sys.exit(1)
    
    output_path = sys.argv[1]
    size = int(sys.argv[2]) if len(sys.argv) > 2 else 512
    
    # Validate size
    if size < 16 or size > 4096:
        print(f"ERROR: Size must be between 16 and 4096 pixels (got {size})")
        sys.exit(1)
    
    try:
        export_icon(output_path, size)
        print()
        print("✓ Export complete!")
    except Exception as e:
        print(f"ERROR: Failed to export icon: {e}")
        sys.exit(1)


if __name__ == '__main__':
    main()

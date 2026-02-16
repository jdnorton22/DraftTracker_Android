"""
Fetch ADP (Average Draft Position) data from FantasyPros
Updates the pffRank field in players.json with ADP values
"""

import requests
import json
from bs4 import BeautifulSoup
import time
import re

def fetch_fantasypros_adp():
    """
    Scrape ADP rankings from FantasyPros
    
    Returns:
        Dictionary mapping player name to ADP rank
    """
    print("\nFetching ADP data from FantasyPros...")
    
    adp_data = {}
    
    # FantasyPros ADP URL (PPR scoring)
    url = "https://www.fantasypros.com/nfl/adp/ppr-overall.php"
    headers = {
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
    }
    
    try:
        response = requests.get(url, headers=headers, timeout=15)
        response.raise_for_status()
        
        soup = BeautifulSoup(response.text, 'html.parser')
        
        # Find the ADP table
        table = soup.find('table', {'id': 'data'})
        if not table:
            print("Could not find ADP table on page")
            return adp_data
        
        rows = table.find('tbody').find_all('tr')
        
        for row in rows:
            try:
                cells = row.find_all('td')
                if len(cells) < 3:
                    continue
                
                # First cell contains rank/ADP
                rank_cell = cells[0]
                rank_text = rank_cell.get_text(strip=True)
                
                # Extract numeric rank
                rank_match = re.search(r'(\d+)', rank_text)
                if not rank_match:
                    continue
                adp_rank = int(rank_match.group(1))
                
                # Second cell contains player name
                player_cell = cells[1]
                player_link = player_cell.find('a', class_='player-name')
                if not player_link:
                    continue
                
                player_name = player_link.get_text(strip=True)
                
                # Clean up player name (remove extra spaces, special characters)
                player_name = ' '.join(player_name.split())
                
                adp_data[player_name] = adp_rank
                
            except Exception as e:
                continue
        
        print(f"Fetched ADP data for {len(adp_data)} players")
        return adp_data
        
    except requests.RequestException as e:
        print(f"Error fetching ADP data: {e}")
        return adp_data

def normalize_name(name):
    """
    Normalize player name for matching
    """
    # Remove Jr., Sr., III, etc.
    name = re.sub(r'\s+(Jr\.?|Sr\.?|III|II|IV)$', '', name, flags=re.IGNORECASE)
    # Remove periods
    name = name.replace('.', '')
    # Remove extra spaces
    name = ' '.join(name.split())
    return name.strip()

def update_players_json_with_adp(players_json_path, adp_data):
    """
    Update players.json with ADP data
    """
    print(f"\nUpdating {players_json_path} with ADP data...")
    
    try:
        # Read existing players.json
        with open(players_json_path, 'r', encoding='utf-8') as f:
            players = json.load(f)
        
        # Create normalized name lookup
        adp_lookup = {}
        for name, adp in adp_data.items():
            normalized = normalize_name(name)
            adp_lookup[normalized] = adp
        
        # Update players with ADP data
        updated_count = 0
        for player in players:
            player_name = player.get('name', '')
            normalized_name = normalize_name(player_name)
            
            # Try exact match first
            if normalized_name in adp_lookup:
                player['pffRank'] = adp_lookup[normalized_name]
                updated_count += 1
            else:
                # Try partial match (last name)
                last_name = normalized_name.split()[-1] if normalized_name else ''
                found = False
                for adp_name, adp in adp_lookup.items():
                    if last_name and last_name in adp_name:
                        player['pffRank'] = adp
                        updated_count += 1
                        found = True
                        break
                
                if not found:
                    # Keep existing value or set to 0
                    if 'pffRank' not in player:
                        player['pffRank'] = 0
        
        # Write updated players.json
        with open(players_json_path, 'w', encoding='utf-8') as f:
            json.dump(players, f, indent=2, ensure_ascii=False)
        
        print(f"Updated ADP data for {updated_count} out of {len(players)} players")
        print(f"Players without ADP data: {len(players) - updated_count}")
        
    except Exception as e:
        print(f"Error updating players.json: {e}")

def main():
    """
    Main function to fetch ADP and update players.json
    """
    print("=" * 60)
    print("FantasyPros ADP Data Fetcher")
    print("=" * 60)
    
    # Fetch ADP data
    adp_data = fetch_fantasypros_adp()
    
    if not adp_data:
        print("\nNo ADP data fetched. Exiting.")
        return
    
    # Update players.json
    players_json_path = 'app/src/main/res/raw/players.json'
    update_players_json_with_adp(players_json_path, adp_data)
    
    print("\n" + "=" * 60)
    print("ADP data update complete!")
    print("=" * 60)
    print("\nNext steps:")
    print("1. Review app/src/main/res/raw/players.json")
    print("2. Build the app: .\\gradlew assembleDebug --console=plain")
    print("3. Deploy: adb install -r app\\build\\outputs\\apk\\debug\\app-debug.apk")

if __name__ == "__main__":
    main()

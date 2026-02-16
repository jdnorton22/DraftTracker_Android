"""
Update Player Stats Only - Preserves Rankings
Updates the lastYearStats field for existing players without changing rankings
"""

import requests
import json
from typing import Dict, List

def fetch_espn_stats() -> Dict[int, str]:
    """
    Fetch player stats from ESPN and return as dict mapping player ID to stats string
    
    Returns:
        Dictionary mapping ESPN player ID to formatted stats string
    """
    stats_map = {}
    
    base_url = "https://lm-api-reads.fantasy.espn.com/apis/v3/games/ffl/seasons/2024/segments/0/leaguedefaults/3"
    
    headers = {
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
    }
    
    print("Fetching player stats from ESPN...")
    
    # Fetch multiple batches to get more players
    for offset in range(0, 500, 50):
        params = {
            'view': 'kona_player_info',
            'scoringPeriodId': 0,
            'limit': 50,
            'offset': offset
        }
        
        try:
            response = requests.get(base_url, params=params, headers=headers, timeout=10)
            response.raise_for_status()
            
            data = response.json()
            
            if 'players' not in data or not data['players']:
                break
            
            for player_data in data['players']:
                player_info = player_data.get('player', {})
                player_id = player_info.get('id', 0)
                
                if player_id:
                    stats = get_player_stats(player_data)
                    stats_map[player_id] = stats
            
            print(f"  Processed offset {offset}, total stats: {len(stats_map)}")
            
        except Exception as e:
            print(f"  Error at offset {offset}: {e}")
            break
    
    print(f"Fetched stats for {len(stats_map)} players")
    return stats_map

def get_player_stats(player_data: Dict) -> str:
    """Extract and format player stats"""
    try:
        player_info = player_data.get('player', {})
        position_id = player_info.get('defaultPositionId', 0)
        
        # Get stats from player_info
        stats = player_info.get('stats', [])
        
        # Find 2024 season totals
        season_stats = None
        for stat_entry in stats:
            scoring_period = stat_entry.get('scoringPeriodId', -1)
            split_type = stat_entry.get('statSplitTypeId', -1)
            stat_source = stat_entry.get('statSourceId', -1)
            
            if scoring_period == 0 and split_type == 0 and stat_source == 1:
                season_stats = stat_entry.get('stats', {})
                break
        
        if not season_stats:
            return "No 2024 stats available"
        
        # Format stats based on position
        if position_id == 1:  # QB
            pass_yds = season_stats.get('3', 0)
            pass_tds = season_stats.get('4', 0)
            ints = season_stats.get('20', 0)
            return f"{int(pass_yds)} pass yds, {int(pass_tds)} pass TDs, {int(ints)} INTs"
            
        elif position_id == 2:  # RB
            rush_yds = season_stats.get('24', 0)
            rush_tds = season_stats.get('25', 0)
            rec = season_stats.get('53', 0)
            rec_yds = season_stats.get('42', 0)
            return f"{int(rush_yds)} rush yds, {int(rush_tds)} rush TDs, {int(rec)} rec, {int(rec_yds)} rec yds"
            
        elif position_id in [3, 4]:  # WR/TE
            rec = season_stats.get('53', 0)
            rec_yds = season_stats.get('42', 0)
            rec_tds = season_stats.get('43', 0)
            return f"{int(rec)} rec, {int(rec_yds)} rec yds, {int(rec_tds)} rec TDs"
            
        elif position_id == 5:  # K
            fg_made = season_stats.get('80', 0)
            xp_made = season_stats.get('86', 0)
            return f"{int(fg_made)} FGs, {int(xp_made)} XPs"
            
        elif position_id == 16:  # DEF
            sacks = season_stats.get('99', 0)
            ints = season_stats.get('95', 0)
            return f"{int(sacks)} sacks, {int(ints)} INTs"
        
        return "Stats unavailable"
        
    except Exception:
        return "Stats unavailable"

def update_player_stats(players_file: str, output_file: str):
    """
    Update stats in existing players.json without changing rankings
    
    Args:
        players_file: Path to existing players.json
        output_file: Path to save updated players.json
    """
    print("=" * 60)
    print("Update Player Stats Only - Preserve Rankings")
    print("=" * 60)
    
    # Load existing players
    print(f"\nLoading existing players from {players_file}...")
    with open(players_file, 'r', encoding='utf-8') as f:
        players = json.load(f)
    
    print(f"Loaded {len(players)} players")
    
    # Fetch stats from ESPN
    stats_map = fetch_espn_stats()
    
    # Update stats for each player
    print("\nUpdating player stats...")
    updated_count = 0
    
    for player in players:
        espn_id_str = player.get('espnId', '')
        
        # Skip DST teams (they have custom IDs)
        if espn_id_str.startswith('dst_'):
            continue
        
        try:
            espn_id = int(espn_id_str)
            if espn_id in stats_map:
                old_stats = player.get('lastYearStats', 'No stats')
                new_stats = stats_map[espn_id]
                
                if old_stats != new_stats:
                    player['lastYearStats'] = new_stats
                    updated_count += 1
        except (ValueError, TypeError):
            continue
    
    print(f"Updated stats for {updated_count} players")
    
    # Save updated players
    print(f"\nSaving updated players to {output_file}...")
    with open(output_file, 'w', encoding='utf-8') as f:
        json.dump(players, f, indent=2, ensure_ascii=False)
    
    print(f"Saved {len(players)} players with updated stats")
    
    # Print summary
    print("\n" + "=" * 60)
    print("Summary:")
    print("=" * 60)
    print(f"Total players: {len(players)}")
    print(f"Stats updated: {updated_count}")
    print(f"Stats unchanged: {len(players) - updated_count}")
    
    # Show sample of updated players
    print("\nSample of updated players:")
    sample_count = 0
    for player in players[:20]:
        if player.get('lastYearStats', '').startswith('No'):
            continue
        print(f"  {player['rank']}. {player['name']} ({player['position']}): {player['lastYearStats']}")
        sample_count += 1
        if sample_count >= 5:
            break
    
    print("=" * 60)

if __name__ == '__main__':
    players_file = '../app/src/main/res/raw/players.json'
    output_file = '../app/src/main/res/raw/players.json'
    
    update_player_stats(players_file, output_file)

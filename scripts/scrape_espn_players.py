"""
ESPN Fantasy Football Player Scraper
Fetches player data including rankings and 2024 stats from ESPN Fantasy Football
"""

import requests
import json
from typing import List, Dict
import time

def fetch_espn_players(limit: int = 350) -> List[Dict]:
    """
    Fetch player data from ESPN Fantasy Football API
    
    Args:
        limit: Maximum number of players to fetch
        
    Returns:
        List of player dictionaries
    """
    players = []
    seen_ids = set()  # Track player IDs to avoid duplicates
    
    # ESPN Fantasy Football API endpoint (2024 season)
    # This is ESPN's public API for fantasy football player data
    base_url = "https://lm-api-reads.fantasy.espn.com/apis/v3/games/ffl/seasons/2024/segments/0/leaguedefaults/3"
    
    headers = {
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
    }
    
    print(f"Fetching up to {limit} players from ESPN Fantasy Football...")
    
    # Try to fetch all players in one request with a high limit
    params = {
        'view': 'kona_player_info',
        'scoringPeriodId': 0,
        'limit': limit  # Request all players at once
    }
    
    try:
        print(f"Fetching players from ESPN API...")
        response = requests.get(base_url, params=params, headers=headers, timeout=30)
        response.raise_for_status()
        
        data = response.json()
        
        if 'players' not in data or not data['players']:
            print("No players found in API response")
            return []
        
        batch_players = data['players']
        print(f"Retrieved {len(batch_players)} players from API")
        
        for player_data in batch_players:
            player = parse_espn_player(player_data, len(players) + 1)
            if player and player['id'] not in seen_ids:
                players.append(player)
                seen_ids.add(player['id'])
        
        print(f"Processed {len(players)} unique players")
            
    except requests.exceptions.RequestException as e:
        print(f"Error fetching data: {e}")
    except json.JSONDecodeError as e:
        print(f"Error parsing JSON: {e}")
    
    print(f"\nSuccessfully fetched {len(players)} unique players")
    return players

def parse_espn_player(player_data: Dict, rank: int) -> Dict:
    """
    Parse ESPN player data into our app's format
    
    Args:
        player_data: Raw player data from ESPN API
        rank: Overall rank for the player (will be recalculated after sorting)
        
    Returns:
        Formatted player dictionary
    """
    try:
        player_info = player_data.get('player', {})
        
        # Extract basic info
        player_id = player_info.get('id', 0)
        full_name = player_info.get('fullName', 'Unknown Player')
        
        # Position mapping
        position_map = {
            1: 'QB',
            2: 'RB',
            3: 'WR',
            4: 'TE',
            5: 'K',
            16: 'DEF'
        }
        
        default_position_id = player_info.get('defaultPositionId', 0)
        position = position_map.get(default_position_id, 'FLEX')
        
        # Skip if not a relevant position (include DST as alias for DEF)
        if position not in ['QB', 'RB', 'WR', 'TE', 'K', 'DEF']:
            return None
        
        # Rename DEF to DST for consistency with app
        if position == 'DEF':
            position = 'DST'
        
        # NFL Team
        pro_team_id = player_info.get('proTeamId', 0)
        nfl_team = get_nfl_team_abbr(pro_team_id)
        
        # Get ESPN draft rank (use PPR rankings, fallback to STANDARD)
        draft_ranks = player_info.get('draftRanksByRankType', {})
        espn_rank = 9999  # Default high rank for unranked players
        
        if 'PPR' in draft_ranks and draft_ranks['PPR'].get('rank'):
            espn_rank = draft_ranks['PPR']['rank']
        elif 'STANDARD' in draft_ranks and draft_ranks['STANDARD'].get('rank'):
            espn_rank = draft_ranks['STANDARD']['rank']
        
        # Get 2024 stats
        stats_2024 = get_player_stats(player_data, 2024)
        
        # Injury status
        injury_status = player_info.get('injuryStatus', 'HEALTHY')
        injury_map = {
            'ACTIVE': 'HEALTHY',
            'QUESTIONABLE': 'QUESTIONABLE',
            'DOUBTFUL': 'DOUBTFUL',
            'OUT': 'OUT',
            'INJURY_RESERVE': 'IR'
        }
        injury_status = injury_map.get(injury_status, 'HEALTHY')
        
        # Build player object
        player = {
            'id': player_id,
            'name': full_name,
            'position': position,
            'nflTeam': nfl_team,
            'rank': espn_rank,  # Use ESPN rank for sorting
            'pffRank': 0,  # ESPN doesn't provide PFF ranks
            'positionRank': 0,  # Will calculate later
            'lastYearStats': stats_2024,
            'injuryStatus': injury_status,
            'espnId': str(player_id),
            'espnRank': espn_rank  # Store for sorting
        }
        
        return player
        
    except Exception as e:
        print(f"Error parsing player: {e}")
        return None

def get_player_stats(player_data: Dict, year: int) -> str:
    """
    Extract player stats for a given year
    
    Args:
        player_data: Raw player data from ESPN
        year: Year to get stats for
        
    Returns:
        Formatted stats string
    """
    try:
        player_info = player_data.get('player', {})
        position_id = player_info.get('defaultPositionId', 0)
        
        # Get stats from player_info (not player_data)
        stats = player_info.get('stats', [])
        
        # Find 2024 season totals (scoringPeriodId=0, statSplitTypeId=0, statSourceId=1)
        season_stats = None
        for stat_entry in stats:
            scoring_period = stat_entry.get('scoringPeriodId', -1)
            split_type = stat_entry.get('statSplitTypeId', -1)
            stat_source = stat_entry.get('statSourceId', -1)
            
            # Season totals have scoringPeriodId=0, statSplitTypeId=0, statSourceId=1
            if scoring_period == 0 and split_type == 0 and stat_source == 1:
                season_stats = stat_entry.get('stats', {})
                break
        
        if not season_stats:
            return "No 2024 stats available"
        
        # Format stats based on position
        if position_id == 1:  # QB
            pass_yds = season_stats.get('3', 0)  # Passing yards
            pass_tds = season_stats.get('4', 0)  # Passing TDs
            ints = season_stats.get('20', 0)  # Interceptions
            return f"{int(pass_yds)} pass yds, {int(pass_tds)} pass TDs, {int(ints)} INTs"
            
        elif position_id == 2:  # RB
            rush_yds = season_stats.get('24', 0)  # Rushing yards
            rush_tds = season_stats.get('25', 0)  # Rushing TDs
            rec = season_stats.get('53', 0)  # Receptions
            rec_yds = season_stats.get('42', 0)  # Receiving yards
            return f"{int(rush_yds)} rush yds, {int(rush_tds)} rush TDs, {int(rec)} rec, {int(rec_yds)} rec yds"
            
        elif position_id in [3, 4]:  # WR/TE
            rec = season_stats.get('53', 0)  # Receptions
            rec_yds = season_stats.get('42', 0)  # Receiving yards
            rec_tds = season_stats.get('43', 0)  # Receiving TDs
            return f"{int(rec)} rec, {int(rec_yds)} rec yds, {int(rec_tds)} rec TDs"
            
        elif position_id == 5:  # K
            fg_made = season_stats.get('80', 0)  # FG made
            xp_made = season_stats.get('86', 0)  # XP made
            return f"{int(fg_made)} FGs, {int(xp_made)} XPs"
            
        elif position_id == 16:  # DEF
            sacks = season_stats.get('99', 0)
            ints = season_stats.get('95', 0)
            return f"{int(sacks)} sacks, {int(ints)} INTs"
        
        return "Stats unavailable"
        
    except Exception as e:
        return "Stats unavailable"

def get_nfl_team_abbr(team_id: int) -> str:
    """Map ESPN team ID to NFL team abbreviation"""
    team_map = {
        1: 'ATL', 2: 'BUF', 3: 'CHI', 4: 'CIN', 5: 'CLE',
        6: 'DAL', 7: 'DEN', 8: 'DET', 9: 'GB', 10: 'TEN',
        11: 'IND', 12: 'KC', 13: 'LV', 14: 'LAR', 15: 'MIA',
        16: 'MIN', 17: 'NE', 18: 'NO', 19: 'NYG', 20: 'NYJ',
        21: 'PHI', 22: 'ARI', 23: 'PIT', 24: 'LAC', 25: 'SF',
        26: 'SEA', 27: 'TB', 28: 'WSH', 29: 'CAR', 30: 'JAX',
        33: 'BAL', 34: 'HOU'
    }
    return team_map.get(team_id, 'FA')

def calculate_position_ranks(players: List[Dict]) -> List[Dict]:
    """
    Calculate position ranks for all players
    
    Args:
        players: List of player dictionaries (should be sorted by overall rank)
        
    Returns:
        Updated list with position ranks
    """
    # Group by position and track position rank
    position_counters = {}
    
    for player in players:
        pos = player['position']
        if pos not in position_counters:
            position_counters[pos] = 0
        position_counters[pos] += 1
        player['positionRank'] = position_counters[pos]
    
    return players

def save_to_json(players: List[Dict], output_file: str):
    """
    Save players to JSON file
    
    Args:
        players: List of player dictionaries
        output_file: Output file path
    """
    with open(output_file, 'w', encoding='utf-8') as f:
        json.dump(players, f, indent=2, ensure_ascii=False)
    
    print(f"\nSaved {len(players)} players to {output_file}")

def main():
    """Main execution function"""
    print("=" * 60)
    print("ESPN Fantasy Football Player Scraper")
    print("=" * 60)
    
    # Fetch players
    players = fetch_espn_players(limit=350)
    
    if not players:
        print("No players fetched. Exiting.")
        return
    
    # Sort players by ESPN rank (lower rank = better player)
    print("\nSorting players by ESPN draft rankings...")
    players.sort(key=lambda p: p.get('espnRank', 9999))
    
    # Reassign overall ranks based on sorted order
    for idx, player in enumerate(players, 1):
        player['rank'] = idx
        # Remove temporary espnRank field
        if 'espnRank' in player:
            del player['espnRank']
    
    # Calculate position ranks based on sorted order
    players = calculate_position_ranks(players)
    
    # Save to file
    output_file = '../app/src/main/res/raw/players.json'
    save_to_json(players, output_file)
    
    # Print summary
    print("\n" + "=" * 60)
    print("Summary:")
    print("=" * 60)
    
    by_position = {}
    for player in players:
        pos = player['position']
        by_position[pos] = by_position.get(pos, 0) + 1
    
    for pos in sorted(by_position.keys()):
        print(f"{pos}: {by_position[pos]} players")
    
    print(f"\nTotal: {len(players)} players")
    
    # Show top 10 players
    print("\n" + "=" * 60)
    print("Top 10 Players by ESPN Rankings:")
    print("=" * 60)
    for player in players[:10]:
        print(f"{player['rank']}. {player['name']} ({player['position']}) - {player['nflTeam']}")
    
    print("=" * 60)

if __name__ == '__main__':
    main()

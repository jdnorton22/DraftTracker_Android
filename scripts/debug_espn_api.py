"""
Debug script to inspect ESPN API response structure
"""

import requests
import json

# ESPN Fantasy Football API endpoint
base_url = "https://lm-api-reads.fantasy.espn.com/apis/v3/games/ffl/seasons/2024/segments/0/leaguedefaults/3"

headers = {
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
}

params = {
    'view': 'kona_player_info',
    'scoringPeriodId': 0,
    'limit': 5,
    'offset': 0
}

print("Fetching sample data from ESPN API...")
response = requests.get(base_url, params=params, headers=headers, timeout=10)
response.raise_for_status()

data = response.json()

if 'players' in data and data['players']:
    print(f"\nFound {len(data['players'])} players")
    print("\n" + "="*80)
    print("FIRST PLAYER FULL DATA:")
    print("="*80)
    print(json.dumps(data['players'][0], indent=2))
    
    print("\n" + "="*80)
    print("CHECKING STATS STRUCTURE:")
    print("="*80)
    
    for idx, player_data in enumerate(data['players'][:3], 1):
        player_info = player_data.get('player', {})
        print(f"\nPlayer {idx}: {player_info.get('fullName', 'Unknown')}")
        print(f"Position ID: {player_info.get('defaultPositionId')}")
        
        if 'stats' in player_info:
            print(f"Stats entries: {len(player_info['stats'])}")
            for stat_entry in player_info['stats']:
                season_id = stat_entry.get('seasonId')
                stat_source = stat_entry.get('statSourceId')
                scoring_period = stat_entry.get('scoringPeriodId')
                split_type = stat_entry.get('statSplitTypeId')
                stats = stat_entry.get('stats', {})
                print(f"  - Season: {season_id}, Source: {stat_source}, Period: {scoring_period}, Split: {split_type}, Stats count: {len(stats)}")
                if stats and scoring_period == 0 and split_type == 0:
                    print(f"    SEASON TOTALS - Sample stats: {list(stats.items())[:10]}")
        else:
            print("No stats found")
else:
    print("No players found in response")
    print(json.dumps(data, indent=2))

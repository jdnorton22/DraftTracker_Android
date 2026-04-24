"""
Test different ESPN API endpoints to find one that returns more players
"""

import requests
import json

base_url = "https://lm-api-reads.fantasy.espn.com/apis/v3/games/ffl/seasons/2024/segments/0/leaguedefaults/3"

headers = {
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
}

# Test different parameter combinations
test_configs = [
    {
        'name': 'Standard kona_player_info',
        'params': {
            'view': 'kona_player_info',
            'scoringPeriodId': 0,
        }
    },
    {
        'name': 'Players_wl view',
        'params': {
            'view': 'players_wl',
        }
    },
    {
        'name': 'kona_playercard',
        'params': {
            'view': 'kona_playercard',
        }
    },
    {
        'name': 'No view parameter',
        'params': {}
    },
]

for config in test_configs:
    print(f"\n{'='*60}")
    print(f"Testing: {config['name']}")
    print(f"{'='*60}")
    
    try:
        response = requests.get(base_url, params=config['params'], headers=headers, timeout=10)
        response.raise_for_status()
        
        data = response.json()
        
        if 'players' in data:
            players = data['players']
            print(f"✓ Found {len(players)} players")
            
            # Check for unique players
            unique_names = set()
            for p in players:
                player_info = p.get('player', {})
                name = player_info.get('fullName', '')
                if name:
                    unique_names.add(name)
            
            print(f"✓ Unique players: {len(unique_names)}")
            
            # Show first 5 players
            print("\nFirst 5 players:")
            for i, p in enumerate(players[:5]):
                player_info = p.get('player', {})
                name = player_info.get('fullName', 'Unknown')
                pos_id = player_info.get('defaultPositionId', 0)
                print(f"  {i+1}. {name} (pos_id: {pos_id})")
        else:
            print("✗ No 'players' key in response")
            print(f"Response keys: {list(data.keys())}")
            
    except Exception as e:
        print(f"✗ Error: {e}")

print(f"\n{'='*60}")
print("Testing complete")
print(f"{'='*60}")

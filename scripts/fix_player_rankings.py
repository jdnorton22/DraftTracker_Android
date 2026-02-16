"""
Fix Player Rankings - Sort by Fantasy Value
Sorts players by typical fantasy football draft value and assigns proper rankings
"""

import json

# Typical fantasy football position value order
POSITION_VALUE = {
    'RB': 1,  # RBs go first
    'WR': 2,  # WRs second
    'QB': 3,  # QBs third
    'TE': 4,  # TEs fourth
    'K': 5,   # Kickers late
    'DST': 6  # Defense last
}

# Elite players who should be ranked very high (by name)
ELITE_PLAYERS = {
    # Top RBs
    'Christian McCaffrey': 1,
    'Saquon Barkley': 2,
    'Breece Hall': 3,
    'Bijan Robinson': 4,
    'Jahmyr Gibbs': 5,
    'Jonathan Taylor': 6,
    'Derrick Henry': 7,
    'De\'Von Achane': 8,
    'Kyren Williams': 9,
    'Josh Jacobs': 10,
    
    # Top WRs
    'CeeDee Lamb': 11,
    'Tyreek Hill': 12,
    'Amon-Ra St. Brown': 13,
    'Justin Jefferson': 14,
    'Ja\'Marr Chase': 15,
    'Puka Nacua': 16,
    'A.J. Brown': 17,
    'Garrett Wilson': 18,
    'Nico Collins': 19,
    'Davante Adams': 20,
    
    # Top QBs
    'Josh Allen': 21,
    'Jalen Hurts': 22,
    'Lamar Jackson': 23,
    'Patrick Mahomes': 24,
    'Joe Burrow': 25,
    
    # Top TEs
    'Travis Kelce': 26,
    'Sam LaPorta': 27,
    'Mark Andrews': 28,
    'Trey McBride': 29,
    'George Kittle': 30,
}

def calculate_player_score(player):
    """
    Calculate a score for sorting players by fantasy value
    Lower score = higher draft position
    """
    name = player.get('name', '')
    position = player.get('position', 'FLEX')
    stats = player.get('lastYearStats', '')
    
    # Check if elite player
    if name in ELITE_PLAYERS:
        return ELITE_PLAYERS[name]
    
    # Base score by position
    base_score = POSITION_VALUE.get(position, 99) * 1000
    
    # Adjust by stats (players with stats rank higher)
    if stats and not stats.startswith('No') and stats != '2024 Defense Stats':
        base_score -= 500  # Boost players with stats
        
        # Parse stats to rank by production
        try:
            if position == 'RB':
                # Extract rushing yards
                if 'rush yds' in stats:
                    yards = int(stats.split('rush yds')[0].strip().split()[-1])
                    base_score -= yards // 10  # More yards = better rank
            elif position == 'WR':
                # Extract receiving yards
                if 'rec yds' in stats:
                    yards = int(stats.split('rec yds')[0].strip().split()[-1])
                    base_score -= yards // 10
            elif position == 'TE':
                # Extract receiving yards
                if 'rec yds' in stats:
                    yards = int(stats.split('rec yds')[0].strip().split()[-1])
                    base_score -= yards // 10
            elif position == 'QB':
                # Extract passing yards
                if 'pass yds' in stats:
                    yards = int(stats.split('pass yds')[0].strip().split()[-1])
                    base_score -= yards // 20  # QBs have more yards
            elif position == 'K':
                # Extract FGs
                if 'FGs' in stats:
                    fgs = int(stats.split('FGs')[0].strip().split()[-1])
                    base_score -= fgs * 10
        except (ValueError, IndexError):
            pass
    
    # Add player name for stable sorting
    base_score += hash(name) % 100
    
    return base_score

def fix_rankings(players_file, output_file):
    """Fix player rankings by sorting by fantasy value"""
    print("=" * 60)
    print("Fix Player Rankings")
    print("=" * 60)
    
    # Load players
    print(f"\nLoading players from {players_file}...")
    with open(players_file, 'r', encoding='utf-8') as f:
        players = json.load(f)
    
    print(f"Loaded {len(players)} players")
    
    # Sort by fantasy value
    print("\nSorting players by fantasy value...")
    players.sort(key=calculate_player_score)
    
    # Reassign ranks
    print("Reassigning overall and position ranks...")
    position_counters = {}
    
    for idx, player in enumerate(players, 1):
        player['rank'] = idx
        
        pos = player['position']
        if pos not in position_counters:
            position_counters[pos] = 0
        position_counters[pos] += 1
        player['positionRank'] = position_counters[pos]
    
    # Save
    print(f"\nSaving to {output_file}...")
    with open(output_file, 'w', encoding='utf-8') as f:
        json.dump(players, f, indent=2, ensure_ascii=False)
    
    # Summary
    print("\n" + "=" * 60)
    print("Summary:")
    print("=" * 60)
    print(f"Total players: {len(players)}")
    
    for pos in sorted(position_counters.keys()):
        print(f"{pos}: {position_counters[pos]} players")
    
    print("\nTop 20 Players:")
    print("=" * 60)
    for player in players[:20]:
        print(f"{player['rank']:3d}. {player['name']:30s} ({player['position']:3s}) - {player['nflTeam']}")
    
    print("=" * 60)

if __name__ == '__main__':
    players_file = '../app/src/main/res/raw/players.json'
    output_file = '../app/src/main/res/raw/players.json'
    
    fix_rankings(players_file, output_file)

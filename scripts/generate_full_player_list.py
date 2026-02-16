"""
Generate Full Player List
Creates a comprehensive 300+ player list with proper rankings
Combines ESPN data with a curated list of fantasy-relevant players
"""

import json

# Comprehensive list of fantasy-relevant players by position
# Format: (name, team, typical_rank_range)
FANTASY_PLAYERS = {
    'QB': [
        ('Josh Allen', 'BUF'), ('Jalen Hurts', 'PHI'), ('Lamar Jackson', 'BAL'),
        ('Patrick Mahomes', 'KC'), ('Joe Burrow', 'CIN'), ('Dak Prescott', 'DAL'),
        ('Justin Herbert', 'LAC'), ('Jared Goff', 'DET'), ('Tua Tagovailoa', 'MIA'),
        ('Brock Purdy', 'SF'), ('C.J. Stroud', 'HOU'), ('Jordan Love', 'GB'),
        ('Trevor Lawrence', 'JAX'), ('Kirk Cousins', 'ATL'), ('Geno Smith', 'SEA'),
        ('Baker Mayfield', 'TB'), ('Caleb Williams', 'CHI'), ('Jayden Daniels', 'WSH'),
        ('Anthony Richardson', 'IND'), ('Aaron Rodgers', 'NYJ'), ('Matthew Stafford', 'LAR'),
        ('Sam Darnold', 'MIN'), ('Derek Carr', 'NO'), ('Russell Wilson', 'PIT'),
        ('Daniel Jones', 'NYG'), ('Deshaun Watson', 'CLE'), ('Will Levis', 'TEN'),
        ('Gardner Minshew', 'LV'), ('Bryce Young', 'CAR'), ('Mac Jones', 'JAX'),
        ('Aidan O\'Connell', 'LV'), ('Tyson Bagent', 'CHI'), ('Drew Lock', 'NYG'),
        ('Taylor Heinicke', 'LAC'), ('Jacoby Brissett', 'NE'),
    ],
    'RB': [
        ('Christian McCaffrey', 'SF'), ('Saquon Barkley', 'PHI'), ('Breece Hall', 'NYJ'),
        ('Bijan Robinson', 'ATL'), ('Jahmyr Gibbs', 'DET'), ('Jonathan Taylor', 'IND'),
        ('Derrick Henry', 'BAL'), ('De\'Von Achane', 'MIA'), ('Kyren Williams', 'LAR'),
        ('Josh Jacobs', 'GB'), ('Kenneth Walker III', 'SEA'), ('Rachaad White', 'TB'),
        ('James Cook', 'BUF'), ('Isiah Pacheco', 'KC'), ('Najee Harris', 'PIT'),
        ('Joe Mixon', 'HOU'), ('David Montgomery', 'DET'), ('Rhamondre Stevenson', 'NE'),
        ('Travis Etienne Jr.', 'JAX'), ('Aaron Jones', 'MIN'), ('Alvin Kamara', 'NO'),
        ('Tony Pollard', 'TEN'), ('James Conner', 'ARI'), ('Javonte Williams', 'DEN'),
        ('D\'Andre Swift', 'CHI'), ('Brian Robinson Jr.', 'WSH'), ('Raheem Mostert', 'MIA'),
        ('Zack Moss', 'CIN'), ('Gus Edwards', 'LAC'), ('Chuba Hubbard', 'CAR'),
        ('Tyler Allgeier', 'ATL'), ('Tyjae Spears', 'TEN'), ('Jaylen Warren', 'PIT'),
        ('Zamir White', 'LV'), ('Braelon Allen', 'NYJ'), ('Zach Charbonnet', 'SEA'),
        ('Trey Benson', 'ARI'), ('Jonathon Brooks', 'CAR'), ('Blake Corum', 'LAR'),
        ('Bucky Irving', 'TB'), ('Ray Davis', 'BUF'), ('Audric Estime', 'DEN'),
        ('MarShawn Lloyd', 'GB'), ('Kimani Vidal', 'LAC'), ('Isaac Guerendo', 'SF'),
        ('Ty Chandler', 'MIN'), ('Justice Hill', 'BAL'), ('Samaje Perine', 'KC'),
        ('Roschon Johnson', 'CHI'), ('Elijah Mitchell', 'SF'), ('Cam Akers', 'MIN'),
        ('Dameon Pierce', 'HOU'), ('Miles Sanders', 'CAR'), ('Antonio Gibson', 'NE'),
    ],
    'WR': [
        ('CeeDee Lamb', 'DAL'), ('Tyreek Hill', 'MIA'), ('Amon-Ra St. Brown', 'DET'),
        ('Justin Jefferson', 'MIN'), ('Ja\'Marr Chase', 'CIN'), ('Puka Nacua', 'LAR'),
        ('A.J. Brown', 'PHI'), ('Garrett Wilson', 'NYJ'), ('Nico Collins', 'HOU'),
        ('Davante Adams', 'NYJ'), ('Deebo Samuel', 'SF'), ('Chris Olave', 'NO'),
        ('Brandon Aiyuk', 'SF'), ('DK Metcalf', 'SEA'), ('Stefon Diggs', 'HOU'),
        ('Mike Evans', 'TB'), ('Cooper Kupp', 'LAR'), ('Amari Cooper', 'BUF'),
        ('DeVonta Smith', 'PHI'), ('Tee Higgins', 'CIN'), ('DJ Moore', 'CHI'),
        ('Jaylen Waddle', 'MIA'), ('Calvin Ridley', 'TEN'), ('Christian Kirk', 'JAX'),
        ('Keenan Allen', 'CHI'), ('Terry McLaurin', 'WSH'), ('Diontae Johnson', 'HOU'),
        ('Michael Pittman Jr.', 'IND'), ('Zay Flowers', 'BAL'), ('George Pickens', 'PIT'),
        ('Jordan Addison', 'MIN'), ('Marquise Brown', 'KC'), ('Christian Watson', 'GB'),
        ('Jaxon Smith-Njigba', 'SEA'), ('Drake London', 'ATL'), ('Rashee Rice', 'KC'),
        ('Tank Dell', 'HOU'), ('Jameson Williams', 'DET'), ('Rome Odunze', 'CHI'),
        ('Marvin Harrison Jr.', 'ARI'), ('Malik Nabers', 'NYG'), ('Brian Thomas Jr.', 'JAX'),
        ('Ladd McConkey', 'LAC'), ('Xavier Worthy', 'KC'), ('Keon Coleman', 'BUF'),
        ('Ricky Pearsall', 'SF'), ('Adonai Mitchell', 'IND'), ('Xavier Legette', 'CAR'),
        ('Tyler Lockett', 'SEA'), ('Courtland Sutton', 'DEN'), ('Josh Downs', 'IND'),
        ('Jakobi Meyers', 'LV'), ('Darnell Mooney', 'ATL'), ('Brandin Cooks', 'DAL'),
        ('Gabe Davis', 'JAX'), ('Curtis Samuel', 'BUF'), ('Rashid Shaheed', 'NO'),
        ('Demario Douglas', 'NE'), ('Wan\'Dale Robinson', 'NYG'), ('Tutu Atwell', 'LAR'),
        ('Quentin Johnston', 'LAC'), ('Tre Tucker', 'LV'), ('Elijah Moore', 'CLE'),
        ('Dontayvion Wicks', 'GB'), ('Jayden Reed', 'GB'), ('Romeo Doubs', 'GB'),
        ('Josh Palmer', 'LAC'), ('Michael Wilson', 'ARI'), ('Jahan Dotson', 'PHI'),
        ('K.J. Osborn', 'NE'), ('Tyler Boyd', 'TEN'), ('Mecole Hardman', 'KC'),
        ('Rondale Moore', 'ATL'), ('Kalif Raymond', 'DET'), ('Kendrick Bourne', 'NE'),
        ('Marquez Valdes-Scantling', 'BUF'), ('Alec Pierce', 'IND'), ('Skyy Moore', 'KC'),
        ('Kadarius Toney', 'CLE'), ('Treylon Burks', 'TEN'), ('Jalen Tolbert', 'DAL'),
        ('Quez Watkins', 'PIT'), ('Parris Campbell', 'PHI'), ('Cedrick Wilson Jr.', 'NO'),
        ('Donovan Peoples-Jones', 'DET'), ('Laviska Shenault Jr.', 'SEA'), ('Tyquan Thornton', 'NE'),
    ],
    'TE': [
        ('Travis Kelce', 'KC'), ('Sam LaPorta', 'DET'), ('Mark Andrews', 'BAL'),
        ('Trey McBride', 'ARI'), ('George Kittle', 'SF'), ('Evan Engram', 'JAX'),
        ('T.J. Hockenson', 'MIN'), ('Kyle Pitts', 'ATL'), ('David Njoku', 'CLE'),
        ('Dalton Kincaid', 'BUF'), ('Jake Ferguson', 'DAL'), ('Dallas Goedert', 'PHI'),
        ('Cole Kmet', 'CHI'), ('Pat Freiermuth', 'PIT'), ('Brock Bowers', 'LV'),
        ('Dalton Schultz', 'HOU'), ('Tyler Conklin', 'NYJ'), ('Jonnu Smith', 'MIA'),
        ('Hunter Henry', 'NE'), ('Chigoziem Okonkwo', 'TEN'), ('Luke Musgrave', 'GB'),
        ('Taysom Hill', 'NO'), ('Juwan Johnson', 'NO'), ('Isaiah Likely', 'BAL'),
        ('Tucker Kraft', 'GB'), ('Michael Mayer', 'LV'), ('Luke Schoonmaker', 'DAL'),
        ('Cade Otton', 'TB'), ('Zach Ertz', 'WSH'), ('Gerald Everett', 'CHI'),
        ('Noah Fant', 'SEA'), ('Hayden Hurst', 'LAC'), ('Will Dissly', 'LAC'),
        ('Durham Smythe', 'MIA'), ('Dawson Knox', 'BUF'), ('Daniel Bellinger', 'NYG'),
        ('Brevin Jordan', 'HOU'), ('Josh Oliver', 'MIN'), ('Foster Moreau', 'NO'),
        ('Irv Smith Jr.', 'KC'), ('Noah Gray', 'KC'), ('Harrison Bryant', 'LV'),
        ('Tommy Tremble', 'CAR'), ('Trey Palmer', 'TB'), ('Ben Sinnott', 'WSH'),
        ('JaTavion Sanders', 'CAR'), ('Theo Johnson', 'NYG'), ('Erick All Jr.', 'CIN'),
    ],
    'K': [
        ('Justin Tucker', 'BAL'), ('Harrison Butker', 'KC'), ('Brandon Aubrey', 'DAL'),
        ('Jake Moody', 'SF'), ('Tyler Bass', 'BUF'), ('Evan McPherson', 'CIN'),
        ('Jason Sanders', 'MIA'), ('Cameron Dicker', 'LAC'), ('Jake Bates', 'DET'),
        ('Chris Boswell', 'PIT'), ('Younghoe Koo', 'ATL'), ('Jason Myers', 'SEA'),
        ('Daniel Carlson', 'LV'), ('Cairo Santos', 'CHI'), ('Chase McLaughlin', 'TB'),
        ('Ka\'imi Fairbairn', 'HOU'), ('Matt Gay', 'IND'), ('Wil Lutz', 'DEN'),
        ('Greg Joseph', 'NYG'), ('Joey Slye', 'NE'), ('Blake Grupe', 'NO'),
        ('Dustin Hopkins', 'CLE'), ('Riley Patterson', 'NYJ'), ('Anders Carlson', 'GB'),
        ('Eddy Pineiro', 'CAR'), ('Matt Prater', 'ARI'), ('Graham Gano', 'NYG'),
        ('Nick Folk', 'TEN'),
    ]
}

def generate_full_list():
    """Generate a full player list with proper rankings"""
    print("=" * 60)
    print("Generate Full Player List (300+ players)")
    print("=" * 60)
    
    # Load existing players (with stats)
    print("\nLoading existing players with stats...")
    try:
        with open('../app/src/main/res/raw/players.json', 'r', encoding='utf-8') as f:
            existing_players = json.load(f)
        print(f"Loaded {len(existing_players)} existing players")
        
        # Create lookup by name
        existing_by_name = {p['name']: p for p in existing_players}
    except FileNotFoundError:
        existing_by_name = {}
        print("No existing file found, starting fresh")
    
    # Generate full player list
    all_players = []
    player_id = 1
    
    # Add players by position in draft order
    for position in ['RB', 'WR', 'QB', 'TE', 'K', 'DST']:
        if position == 'DST':
            # Add DST teams
            for team_abbr in ['BAL', 'BUF', 'CHI', 'CIN', 'CLE', 'DAL', 'DEN', 'DET', 'GB', 'HOU',
                             'IND', 'JAX', 'KC', 'LAC', 'LAR', 'LV', 'MIA', 'MIN', 'NE', 'NO',
                             'NYG', 'NYJ', 'PHI', 'PIT', 'SF', 'SEA', 'TB', 'TEN', 'WSH', 'ATL',
                             'ARI', 'CAR']:
                player = {
                    'id': 4692000 + len(all_players),
                    'name': f'{team_abbr} DST',
                    'position': 'DST',
                    'nflTeam': team_abbr,
                    'rank': 0,  # Will be set later
                    'pffRank': 0,
                    'positionRank': 0,
                    'lastYearStats': '2024 Defense Stats',
                    'injuryStatus': 'HEALTHY',
                    'espnId': f'dst_{team_abbr.lower()}'
                }
                all_players.append(player)
        else:
            # Add players from curated list
            for name, team in FANTASY_PLAYERS.get(position, []):
                # Check if we have existing data
                if name in existing_by_name:
                    player = existing_by_name[name].copy()
                else:
                    player = {
                        'id': player_id,
                        'name': name,
                        'position': position,
                        'nflTeam': team,
                        'rank': 0,
                        'pffRank': 0,
                        'positionRank': 0,
                        'lastYearStats': 'No 2024 stats available',
                        'injuryStatus': 'HEALTHY',
                        'espnId': str(player_id)
                    }
                    player_id += 1
                
                all_players.append(player)
    
    print(f"\nGenerated {len(all_players)} total players")
    
    # Sort by fantasy value and assign ranks
    print("Sorting by fantasy value...")
    from fix_player_rankings import calculate_player_score
    all_players.sort(key=calculate_player_score)
    
    # Assign ranks
    position_counters = {}
    for idx, player in enumerate(all_players, 1):
        player['rank'] = idx
        
        pos = player['position']
        if pos not in position_counters:
            position_counters[pos] = 0
        position_counters[pos] += 1
        player['positionRank'] = position_counters[pos]
    
    # Save
    output_file = '../app/src/main/res/raw/players.json'
    print(f"\nSaving to {output_file}...")
    with open(output_file, 'w', encoding='utf-8') as f:
        json.dump(all_players, f, indent=2, ensure_ascii=False)
    
    # Summary
    print("\n" + "=" * 60)
    print("Summary:")
    print("=" * 60)
    print(f"Total players: {len(all_players)}")
    
    for pos in sorted(position_counters.keys()):
        print(f"{pos}: {position_counters[pos]} players")
    
    print("\nTop 30 Players:")
    print("=" * 60)
    for player in all_players[:30]:
        stats_preview = player['lastYearStats'][:40] if len(player['lastYearStats']) > 40 else player['lastYearStats']
        print(f"{player['rank']:3d}. {player['name']:30s} ({player['position']:3s}) - {stats_preview}")
    
    print("=" * 60)

if __name__ == '__main__':
    generate_full_list()

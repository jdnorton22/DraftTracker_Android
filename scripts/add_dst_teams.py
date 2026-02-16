"""
Add NFL Defense/Special Teams to players.json
"""

import json

# Load existing players
with open('../app/src/main/res/raw/players.json', 'r') as f:
    players = json.load(f)

# NFL teams with their defenses
nfl_defenses = [
    {"name": "Arizona Cardinals", "abbr": "ARI"},
    {"name": "Atlanta Falcons", "abbr": "ATL"},
    {"name": "Baltimore Ravens", "abbr": "BAL"},
    {"name": "Buffalo Bills", "abbr": "BUF"},
    {"name": "Carolina Panthers", "abbr": "CAR"},
    {"name": "Chicago Bears", "abbr": "CHI"},
    {"name": "Cincinnati Bengals", "abbr": "CIN"},
    {"name": "Cleveland Browns", "abbr": "CLE"},
    {"name": "Dallas Cowboys", "abbr": "DAL"},
    {"name": "Denver Broncos", "abbr": "DEN"},
    {"name": "Detroit Lions", "abbr": "DET"},
    {"name": "Green Bay Packers", "abbr": "GB"},
    {"name": "Houston Texans", "abbr": "HOU"},
    {"name": "Indianapolis Colts", "abbr": "IND"},
    {"name": "Jacksonville Jaguars", "abbr": "JAX"},
    {"name": "Kansas City Chiefs", "abbr": "KC"},
    {"name": "Las Vegas Raiders", "abbr": "LV"},
    {"name": "Los Angeles Chargers", "abbr": "LAC"},
    {"name": "Los Angeles Rams", "abbr": "LAR"},
    {"name": "Miami Dolphins", "abbr": "MIA"},
    {"name": "Minnesota Vikings", "abbr": "MIN"},
    {"name": "New England Patriots", "abbr": "NE"},
    {"name": "New Orleans Saints", "abbr": "NO"},
    {"name": "New York Giants", "abbr": "NYG"},
    {"name": "New York Jets", "abbr": "NYJ"},
    {"name": "Philadelphia Eagles", "abbr": "PHI"},
    {"name": "Pittsburgh Steelers", "abbr": "PIT"},
    {"name": "San Francisco 49ers", "abbr": "SF"},
    {"name": "Seattle Seahawks", "abbr": "SEA"},
    {"name": "Tampa Bay Buccaneers", "abbr": "TB"},
    {"name": "Tennessee Titans", "abbr": "TEN"},
    {"name": "Washington Commanders", "abbr": "WSH"}
]

# Get current max ID
max_id = max(p['id'] for p in players)

# Add DST teams
dst_rank = len(players) + 1
for idx, defense in enumerate(nfl_defenses, 1):
    dst_player = {
        "id": max_id + idx,
        "name": f"{defense['name']} DST",
        "position": "DST",
        "nflTeam": defense['abbr'],
        "rank": dst_rank,
        "pffRank": 0,
        "positionRank": idx,
        "lastYearStats": "2024 Defense Stats",
        "injuryStatus": "HEALTHY",
        "espnId": f"dst_{defense['abbr'].lower()}"
    }
    players.append(dst_player)
    dst_rank += 1

# Save updated players
with open('../app/src/main/res/raw/players.json', 'w') as f:
    json.dump(players, f, indent=2)

print(f"Added {len(nfl_defenses)} DST teams")
print(f"Total players: {len(players)}")

# Print summary
by_position = {}
for player in players:
    pos = player['position']
    by_position[pos] = by_position.get(pos, 0) + 1

print("\nBreakdown by position:")
for pos in sorted(by_position.keys()):
    print(f"  {pos}: {by_position[pos]}")

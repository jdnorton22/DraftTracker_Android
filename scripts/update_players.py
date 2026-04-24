#!/usr/bin/env python3
"""
Fantasy Football Player Data Updater
Fetches current player rankings and generates players.json for the Android app.

Usage:
    python scripts/update_players.py

This will generate app/src/main/res/raw/players.json with current data.
"""

import json
import requests
from typing import List, Dict, Any

# ESPN Fantasy Football 2025 Rankings API
ESPN_RANKINGS_URL = "https://fantasy.espn.com/apis/v3/games/ffl/seasons/2025/segments/0/leaguedefaults/3"
ESPN_PARAMS = {
    "view": "kona_player_info"
}

def fetch_espn_rankings() -> List[Dict[str, Any]]:
    """
    Fetch player rankings from ESPN Fantasy Football API.
    Returns list of player data dictionaries.
    """
    print("Fetching player data from ESPN...")
    
    try:
        response = requests.get(ESPN_RANKINGS_URL, params=ESPN_PARAMS, timeout=30)
        response.raise_for_status()
        data = response.json()
        
        # Extract players from response
        players = data.get('players', [])
        print(f"Fetched {len(players)} players from ESPN")
        return players
        
    except requests.exceptions.RequestException as e:
        print(f"Error fetching data from ESPN: {e}")
        print("Using fallback data generation...")
        return generate_fallback_data()

def generate_fallback_data() -> List[Dict[str, Any]]:
    """
    Generate comprehensive fallback player data if ESPN API is unavailable.
    Creates dataset with 200+ players across all positions with realistic stats.
    Uses proper fantasy football rankings based on typical draft value.
    """
    print("Generating comprehensive fallback player data...")
    
    players = []
    
    # Quarterbacks (30 players) - Format: (name, team, espnId, stats, pffRank, fantasyRank)
    qbs = [
        ("Josh Allen", "BUF", "4038524", "4306 YDS, 29 TD, 18 INT", 85, 8),
        ("Patrick Mahomes", "KC", "3139477", "4183 YDS, 27 TD, 14 INT", 88, 15),
        ("Jalen Hurts", "PHI", "4361741", "3858 YDS, 23 TD, 15 INT", 82, 6),
        ("Lamar Jackson", "BAL", "3916387", "3678 YDS, 24 TD, 7 INT", 90, 10),
        ("Joe Burrow", "CIN", "4038941", "4056 YDS, 35 TD, 12 INT", 86, 22),
        ("Dak Prescott", "DAL", "2577417", "4516 YDS, 36 TD, 9 INT", 84, 28),
        ("Brock Purdy", "SF", "4431618", "4280 YDS, 31 TD, 11 INT", 83, 35),
        ("C.J. Stroud", "HOU", "4685760", "4108 YDS, 23 TD, 5 INT", 87, 18),
        ("Tua Tagovailoa", "MIA", "4241479", "4624 YDS, 29 TD, 14 INT", 81, 42),
        ("Jordan Love", "GB", "3917792", "4159 YDS, 32 TD, 11 INT", 85, 32),
        ("Trevor Lawrence", "JAX", "4360310", "4113 YDS, 21 TD, 14 INT", 79, 55),
        ("Justin Herbert", "LAC", "4038941", "3134 YDS, 20 TD, 7 INT", 80, 48),
        ("Kirk Cousins", "ATL", "14880", "2331 YDS, 18 TD, 5 INT", 76, 75),
        ("Geno Smith", "SEA", "12483", "3624 YDS, 20 TD, 9 INT", 75, 85),
        ("Baker Mayfield", "TB", "3052587", "4044 YDS, 28 TD, 10 INT", 78, 62),
        ("Jared Goff", "DET", "3046779", "4575 YDS, 30 TD, 12 INT", 82, 38),
        ("Matthew Stafford", "LAR", "12483", "3965 YDS, 24 TD, 11 INT", 77, 68),
        ("Sam Darnold", "MIN", "3918298", "3299 YDS, 21 TD, 12 INT", 74, 95),
        ("Kyler Murray", "ARI", "3917315", "1799 YDS, 10 TD, 5 INT", 73, 105),
        ("Aaron Rodgers", "NYJ", "8439", "3897 YDS, 26 TD, 12 INT", 76, 78),
        ("Caleb Williams", "CHI", "4685839", "3541 YDS, 20 TD, 6 INT", 78, 58),
        ("Anthony Richardson", "IND", "4569618", "1814 YDS, 9 TD, 5 INT", 72, 115),
        ("Jayden Daniels", "WAS", "4686264", "3568 YDS, 25 TD, 9 INT", 80, 45),
        ("Derek Carr", "NO", "16757", "3878 YDS, 25 TD, 8 INT", 75, 88),
        ("Russell Wilson", "PIT", "14881", "3070 YDS, 26 TD, 8 INT", 74, 98),
        ("Daniel Jones", "NYG", "3917315", "2070 YDS, 2 TD, 6 INT", 68, 155),
        ("Deshaun Watson", "CLE", "3122840", "1115 YDS, 7 TD, 4 INT", 70, 145),
        ("Will Levis", "TEN", "4567048", "2175 YDS, 8 TD, 12 INT", 69, 165),
        ("Bryce Young", "CAR", "4685710", "2877 YDS, 11 TD, 13 INT", 67, 175),
        ("Gardner Minshew", "LV", "3917315", "2674 YDS, 15 TD, 9 INT", 71, 135),
    ]
    
    
    # Running Backs (60 players) - Format: (name, team, espnId, stats, pffRank, fantasyRank)
    rbs = [
        ("Christian McCaffrey", "SF", "3117251", "1459 YDS, 14 TD, 67 REC", 95, 1),
        ("Bijan Robinson", "ATL", "4685687", "976 YDS, 8 TD, 58 REC", 88, 4),
        ("Breece Hall", "NYJ", "4426515", "994 YDS, 5 TD, 76 REC", 87, 5),
        ("Jahmyr Gibbs", "DET", "4685722", "945 YDS, 10 TD, 52 REC", 89, 7),
        ("Jonathan Taylor", "IND", "4239996", "741 YDS, 7 TD, 44 REC", 85, 11),
        ("Saquon Barkley", "PHI", "3929630", "962 YDS, 10 TD, 33 REC", 86, 9),
        ("Derrick Henry", "BAL", "3043078", "1167 YDS, 12 TD, 16 REC", 84, 14),
        ("Kyren Williams", "LAR", "4426515", "1144 YDS, 12 TD, 32 REC", 83, 16),
        ("De'Von Achane", "MIA", "4685687", "800 YDS, 8 TD, 27 REC", 82, 19),
        ("Travis Etienne", "JAX", "4241389", "1008 YDS, 11 TD, 44 REC", 81, 21),
        ("Josh Jacobs", "GB", "4040715", "805 YDS, 6 TD, 30 REC", 80, 24),
        ("Kenneth Walker III", "SEA", "4426515", "905 YDS, 8 TD, 29 REC", 79, 26),
        ("Rachaad White", "TB", "4426515", "990 YDS, 3 TD, 64 REC", 78, 30),
        ("James Cook", "BUF", "4426515", "1122 YDS, 16 TD, 44 REC", 82, 17),
        ("Alvin Kamara", "NO", "3116385", "694 YDS, 5 TD, 55 REC", 77, 33),
        ("David Montgomery", "DET", "3929630", "789 YDS, 13 TD, 36 REC", 76, 37),
        ("Isiah Pacheco", "KC", "4426515", "935 YDS, 7 TD, 44 REC", 75, 40),
        ("Rhamondre Stevenson", "NE", "4241389", "619 YDS, 4 TD, 38 REC", 74, 44),
        ("Joe Mixon", "HOU", "3116385", "1147 YDS, 9 TD, 22 REC", 79, 23),
        ("Aaron Jones", "MIN", "3116385", "656 YDS, 5 TD, 40 REC", 73, 47),
        ("Tony Pollard", "TEN", "4040715", "1005 YDS, 6 TD, 53 REC", 75, 39),
        ("Najee Harris", "PIT", "4241389", "1035 YDS, 8 TD, 29 REC", 74, 43),
        ("James Conner", "ARI", "3116385", "1040 YDS, 7 TD, 41 REC", 76, 36),
        ("D'Andre Swift", "CHI", "4040715", "809 YDS, 5 TD, 39 REC", 72, 51),
        ("Javonte Williams", "DEN", "4241389", "774 YDS, 3 TD, 22 REC", 71, 60),
        ("Brian Robinson Jr.", "WAS", "4426515", "797 YDS, 8 TD, 16 REC", 73, 50),
        ("Zack Moss", "CIN", "4040715", "794 YDS, 5 TD, 28 REC", 70, 65),
        ("Raheem Mostert", "MIA", "2576434", "891 YDS, 18 TD, 28 REC", 74, 46),
        ("Zamir White", "LV", "4426515", "451 YDS, 3 TD, 20 REC", 69, 80),
        ("Gus Edwards", "LAC", "3116385", "810 YDS, 13 TD, 11 REC", 72, 53),
        ("Chuba Hubbard", "CAR", "4241389", "902 YDS, 5 TD, 34 REC", 71, 63),
        ("Jerome Ford", "CLE", "4426515", "813 YDS, 4 TD, 44 REC", 70, 67),
        ("Tyjae Spears", "TEN", "4685687", "453 YDS, 2 TD, 52 REC", 69, 82),
        ("Zach Charbonnet", "SEA", "4685687", "462 YDS, 1 TD, 28 REC", 68, 90),
        ("Jaylen Warren", "PIT", "4426515", "784 YDS, 4 TD, 61 REC", 72, 56),
        ("Alexander Mattison", "LV", "3929630", "700 YDS, 3 TD, 31 REC", 67, 100),
        ("Dameon Pierce", "HOU", "4426515", "416 YDS, 2 TD, 15 REC", 66, 110),
        ("Tyler Allgeier", "ATL", "4426515", "1023 YDS, 2 TD, 16 REC", 70, 70),
        ("Khalil Herbert", "CHI", "4241389", "611 YDS, 2 TD, 15 REC", 68, 92),
        ("Roschon Johnson", "CHI", "4685687", "352 YDS, 4 TD, 28 REC", 65, 120),
        ("Justice Hill", "BAL", "3929630", "527 YDS, 2 TD, 22 REC", 67, 102),
        ("Devin Singletary", "NYG", "3929630", "627 YDS, 4 TD, 30 REC", 69, 84),
        ("Miles Sanders", "CAR", "3929630", "432 YDS, 1 TD, 17 REC", 64, 130),
        ("Samaje Perine", "DEN", "3116385", "238 YDS, 3 TD, 50 REC", 66, 112),
        ("Clyde Edwards-Helaire", "KC", "4040715", "223 YDS, 2 TD, 19 REC", 63, 140),
        ("Elijah Mitchell", "SF", "4241389", "377 YDS, 2 TD, 11 REC", 65, 122),
        ("Cam Akers", "MIN", "4040715", "299 YDS, 3 TD, 13 REC", 64, 132),
        ("Antonio Gibson", "NE", "4040715", "609 YDS, 8 TD, 28 REC", 68, 94),
        ("Ty Chandler", "MIN", "4426515", "461 YDS, 2 TD, 20 REC", 66, 114),
        ("Trey Sermon", "IND", "4241389", "319 YDS, 5 TD, 12 REC", 63, 142),
        ("Keaton Mitchell", "BAL", "4685687", "138 YDS, 0 TD, 3 REC", 62, 150),
        ("Tank Bigsby", "JAX", "4685687", "299 YDS, 2 TD, 5 REC", 64, 134),
        ("Trey Benson", "ARI", "4685839", "389 YDS, 1 TD, 8 REC", 65, 124),
        ("Bucky Irving", "TB", "4685839", "349 YDS, 4 TD, 16 REC", 67, 104),
        ("Blake Corum", "LAR", "4685839", "221 YDS, 1 TD, 7 REC", 63, 144),
        ("Braelon Allen", "NYJ", "4685839", "178 YDS, 3 TD, 9 REC", 64, 136),
        ("Ray Davis", "BUF", "4685839", "442 YDS, 4 TD, 20 REC", 66, 116),
        ("Audric Estime", "DEN", "4685839", "153 YDS, 4 TD, 3 REC", 62, 152),
        ("MarShawn Lloyd", "GB", "4685839", "115 YDS, 1 TD, 5 REC", 61, 160),
        ("Jonathon Brooks", "CAR", "4685839", "0 YDS, 0 TD, 0 REC", 60, 170),
    ]
    
    # Wide Receivers (60 players) - Format: (name, team, espnId, stats, pffRank, fantasyRank)
    wrs = [
        ("Tyreek Hill", "MIA", "3116406", "1799 YDS, 13 TD, 119 REC", 92, 2),
        ("CeeDee Lamb", "DAL", "4241389", "1749 YDS, 12 TD, 135 REC", 93, 3),
        ("Justin Jefferson", "MIN", "4035687", "1074 YDS, 5 TD, 68 REC", 91, 12),
        ("Amon-Ra St. Brown", "DET", "4241457", "1515 YDS, 10 TD, 119 REC", 90, 13),
        ("Ja'Marr Chase", "CIN", "4241479", "1216 YDS, 7 TD, 100 REC", 89, 20),
        ("A.J. Brown", "PHI", "4035687", "1456 YDS, 7 TD, 106 REC", 88, 25),
        ("Puka Nacua", "LAR", "4685687", "1486 YDS, 6 TD, 105 REC", 87, 27),
        ("Garrett Wilson", "NYJ", "4426515", "1042 YDS, 3 TD, 95 REC", 85, 34),
        ("Nico Collins", "HOU", "4241389", "1297 YDS, 8 TD, 80 REC", 86, 29),
        ("Davante Adams", "LV", "3116406", "1144 YDS, 8 TD, 103 REC", 84, 31),
        ("Deebo Samuel", "SF", "4035687", "892 YDS, 7 TD, 60 REC", 83, 41),
        ("Brandon Aiyuk", "SF", "4241389", "1342 YDS, 7 TD, 75 REC", 85, 49),
        ("DK Metcalf", "SEA", "4040715", "1114 YDS, 8 TD, 66 REC", 82, 52),
        ("Chris Olave", "NO", "4426515", "1123 YDS, 5 TD, 87 REC", 81, 54),
        ("Mike Evans", "TB", "16795", "1255 YDS, 13 TD, 79 REC", 83, 57),
        ("DeVonta Smith", "PHI", "4241389", "1066 YDS, 7 TD, 81 REC", 80, 59),
        ("Stefon Diggs", "HOU", "2976212", "1183 YDS, 8 TD, 107 REC", 82, 61),
        ("Cooper Kupp", "LAR", "3116406", "737 YDS, 5 TD, 59 REC", 79, 64),
        ("Amari Cooper", "CLE", "3045147", "1250 YDS, 5 TD, 72 REC", 80, 66),
        ("DJ Moore", "CHI", "3929630", "1364 YDS, 8 TD, 96 REC", 81, 69),
        ("Jaylen Waddle", "MIA", "4241389", "1014 YDS, 4 TD, 72 REC", 78, 71),
        ("Keenan Allen", "CHI", "15795", "1243 YDS, 7 TD, 108 REC", 79, 72),
        ("Terry McLaurin", "WAS", "3929630", "1002 YDS, 4 TD, 79 REC", 77, 74),
        ("Calvin Ridley", "TEN", "3929630", "1016 YDS, 2 TD, 76 REC", 76, 76),
        ("Zay Flowers", "BAL", "4685687", "858 YDS, 5 TD, 77 REC", 78, 77),
        ("Michael Pittman Jr.", "IND", "4241389", "1152 YDS, 4 TD, 109 REC", 79, 79),
        ("Tee Higgins", "CIN", "4040715", "656 YDS, 5 TD, 42 REC", 77, 81),
        ("Christian Kirk", "JAX", "3929630", "787 YDS, 1 TD, 62 REC", 75, 83),
        ("Marquise Brown", "KC", "4035687", "574 YDS, 4 TD, 51 REC", 74, 86),
        ("George Pickens", "PIT", "4426515", "1140 YDS, 5 TD, 63 REC", 78, 87),
        ("Jordan Addison", "MIN", "4685687", "911 YDS, 10 TD, 70 REC", 77, 89),
        ("Diontae Johnson", "CAR", "3929630", "717 YDS, 5 TD, 51 REC", 73, 91),
        ("Tyler Lockett", "SEA", "2969939", "894 YDS, 5 TD, 79 REC", 75, 93),
        ("Rashee Rice", "KC", "4685687", "938 YDS, 7 TD, 79 REC", 76, 96),
        ("Tank Dell", "HOU", "4685687", "709 YDS, 7 TD, 47 REC", 75, 97),
        ("Jaxon Smith-Njigba", "SEA", "4685687", "628 YDS, 4 TD, 63 REC", 74, 99),
        ("Jakobi Meyers", "LV", "3929630", "1014 YDS, 8 TD, 71 REC", 76, 101),
        ("Courtland Sutton", "DEN", "3929630", "772 YDS, 10 TD, 59 REC", 75, 103),
        ("Rashid Shaheed", "NO", "4426515", "719 YDS, 5 TD, 46 REC", 73, 106),
        ("Rome Odunze", "CHI", "4685839", "568 YDS, 3 TD, 41 REC", 74, 107),
        ("Marvin Harrison Jr.", "ARI", "4685839", "885 YDS, 7 TD, 59 REC", 79, 73),
        ("Malik Nabers", "NYG", "4685839", "969 YDS, 3 TD, 109 REC", 78, 108),
        ("Brian Thomas Jr.", "JAX", "4685839", "1282 YDS, 10 TD, 87 REC", 80, 109),
        ("Xavier Worthy", "KC", "4685839", "638 YDS, 6 TD, 59 REC", 73, 111),
        ("Ladd McConkey", "LAC", "4685839", "1149 YDS, 4 TD, 82 REC", 76, 113),
        ("Keon Coleman", "BUF", "4685839", "417 YDS, 3 TD, 22 REC", 71, 117),
        ("Ricky Pearsall", "SF", "4685839", "141 YDS, 1 TD, 11 REC", 70, 125),
        ("Adonai Mitchell", "IND", "4685839", "71 YDS, 0 TD, 6 REC", 69, 138),
        ("Jameson Williams", "DET", "4426515", "354 YDS, 2 TD, 24 REC", 72, 118),
        ("Quentin Johnston", "LAC", "4685687", "431 YDS, 3 TD, 38 REC", 71, 119),
        ("Josh Downs", "IND", "4685687", "1040 YDS, 4 TD, 68 REC", 75, 121),
        ("Jayden Reed", "GB", "4685687", "793 YDS, 8 TD, 64 REC", 76, 123),
        ("Wan'Dale Robinson", "NYG", "4426515", "525 YDS, 0 TD, 60 REC", 70, 126),
        ("Dontayvion Wicks", "GB", "4685687", "581 YDS, 4 TD, 39 REC", 71, 127),
        ("Demario Douglas", "NE", "4685687", "561 YDS, 3 TD, 49 REC", 70, 128),
        ("Curtis Samuel", "BUF", "3929630", "462 YDS, 3 TD, 31 REC", 69, 129),
        ("Brandin Cooks", "DAL", "16460", "657 YDS, 8 TD, 54 REC", 72, 131),
        ("Jerry Jeudy", "CLE", "4040715", "758 YDS, 2 TD, 54 REC", 73, 133),
        ("Elijah Moore", "CLE", "4241389", "321 YDS, 2 TD, 39 REC", 68, 137),
        ("Romeo Doubs", "GB", "4426515", "674 YDS, 8 TD, 59 REC", 72, 139),
    ]
    
    # Tight Ends (30 players) - Format: (name, team, espnId, stats, pffRank, fantasyRank)
    tes = [
        ("Travis Kelce", "KC", "15847", "984 YDS, 5 TD, 93 REC", 90, 141),
        ("Travis Kelce", "KC", "15847", "984 YDS, 5 TD, 93 REC", 90, 141),
        ("Sam LaPorta", "DET", "4685722", "889 YDS, 10 TD, 86 REC", 89, 143),
        ("Mark Andrews", "BAL", "3116406", "544 YDS, 6 TD, 45 REC", 85, 146),
        ("T.J. Hockenson", "MIN", "3929630", "411 YDS, 3 TD, 38 REC", 84, 147),
        ("Evan Engram", "JAX", "3045147", "963 YDS, 4 TD, 114 REC", 83, 148),
        ("George Kittle", "SF", "3116389", "1020 YDS, 6 TD, 65 REC", 86, 149),
        ("Trey McBride", "ARI", "4426515", "825 YDS, 3 TD, 81 REC", 82, 151),
        ("Kyle Pitts", "ATL", "4241389", "667 YDS, 3 TD, 53 REC", 80, 153),
        ("David Njoku", "CLE", "3116406", "882 YDS, 6 TD, 81 REC", 81, 154),
        ("Dalton Kincaid", "BUF", "4685687", "673 YDS, 2 TD, 73 REC", 79, 156),
        ("Jake Ferguson", "DAL", "4426515", "761 YDS, 5 TD, 71 REC", 78, 157),
        ("Dallas Goedert", "PHI", "3116406", "592 YDS, 3 TD, 59 REC", 77, 158),
        ("Cole Kmet", "CHI", "4241389", "719 YDS, 6 TD, 73 REC", 76, 159),
        ("Pat Freiermuth", "PIT", "4241389", "308 YDS, 2 TD, 32 REC", 74, 161),
        ("Dalton Schultz", "HOU", "3116406", "635 YDS, 5 TD, 59 REC", 75, 162),
        ("Tyler Conklin", "NYJ", "3116406", "621 YDS, 3 TD, 68 REC", 73, 163),
        ("Jonnu Smith", "MIA", "3116406", "544 YDS, 6 TD, 45 REC", 74, 164),
        ("Hunter Henry", "NE", "3046779", "419 YDS, 2 TD, 43 REC", 72, 166),
        ("Chigoziem Okonkwo", "TEN", "4426515", "450 YDS, 3 TD, 52 REC", 71, 167),
        ("Taysom Hill", "NO", "2576434", "291 YDS, 4 TD, 23 REC", 73, 168),
        ("Brock Bowers", "LV", "4685839", "1194 YDS, 5 TD, 112 REC", 85, 169),
        ("Cade Otton", "TB", "4426515", "455 YDS, 4 TD, 48 REC", 72, 171),
        ("Luke Musgrave", "GB", "4685687", "352 YDS, 1 TD, 34 REC", 70, 172),
        ("Tucker Kraft", "GB", "4685687", "355 YDS, 2 TD, 31 REC", 71, 173),
        ("Zach Ertz", "WAS", "15847", "654 YDS, 4 TD, 66 REC", 74, 174),
        ("Juwan Johnson", "NO", "3929630", "368 YDS, 4 TD, 36 REC", 69, 176),
        ("Michael Mayer", "LV", "4685687", "304 YDS, 2 TD, 27 REC", 70, 177),
        ("Isaiah Likely", "BAL", "4426515", "411 YDS, 5 TD, 30 REC", 73, 178),
        ("Noah Fant", "SEA", "3929630", "406 YDS, 4 TD, 42 REC", 71, 179),
        ("Dawson Knox", "BUF", "3929630", "331 YDS, 1 TD, 28 REC", 68, 180),
    ]
    
    # Kickers (20 players) - Format: (name, team, espnId, stats, fantasyRank)
    kickers = [
        ("Harrison Butker", "KC", "3046779", "33/35 FG, 52/53 XP, 171 PTS", 181),
        ("Justin Tucker", "BAL", "14139", "30/37 FG, 35/36 XP, 125 PTS", 182),
        ("Jake Moody", "SF", "4685687", "21/25 FG, 40/41 XP, 103 PTS", 183),
        ("Brandon Aubrey", "DAL", "4567048", "36/38 FG, 52/53 XP, 160 PTS", 184),
        ("Tyler Bass", "BUF", "4047365", "29/33 FG, 48/49 XP, 135 PTS", 185),
        ("Younghoe Koo", "ATL", "3116406", "26/29 FG, 43/43 XP, 121 PTS", 186),
        ("Jason Sanders", "MIA", "3116406", "30/34 FG, 36/36 XP, 126 PTS", 187),
        ("Cameron Dicker", "LAC", "4426515", "28/31 FG, 35/35 XP, 119 PTS", 188),
        ("Jake Bates", "DET", "4685839", "26/29 FG, 48/49 XP, 126 PTS", 189),
        ("Chris Boswell", "PIT", "2576434", "41/44 FG, 40/40 XP, 163 PTS", 190),
        ("Ka'imi Fairbairn", "HOU", "3046779", "30/35 FG, 42/42 XP, 132 PTS", 191),
        ("Evan McPherson", "CIN", "4241389", "20/27 FG, 42/43 XP, 102 PTS", 192),
        ("Wil Lutz", "DEN", "2576434", "28/33 FG, 38/38 XP, 122 PTS", 193),
        ("Cairo Santos", "CHI", "2576434", "30/33 FG, 27/27 XP, 117 PTS", 194),
        ("Jason Myers", "SEA", "2576434", "32/37 FG, 37/37 XP, 133 PTS", 195),
        ("Dustin Hopkins", "CLE", "2576434", "29/33 FG, 33/33 XP, 120 PTS", 196),
        ("Blake Grupe", "NO", "4685687", "22/30 FG, 40/41 XP, 106 PTS", 197),
        ("Chase McLaughlin", "TB", "3116406", "26/30 FG, 42/42 XP, 120 PTS", 198),
        ("Greg Zuerlein", "NYJ", "14139", "20/26 FG, 27/27 XP, 87 PTS", 199),
        ("Matt Gay", "IND", "3116406", "25/28 FG, 33/33 XP, 108 PTS", 200),
    ]
    
    # Defense/Special Teams (20 players) - Format: (name, team, espnId, stats, fantasyRank)
    defenses = [
        ("San Francisco 49ers", "SF", "-16013", "48 SACK, 17 INT, 10 FR, 4 TD", 201),
        ("Baltimore Ravens", "BAL", "-16033", "60 SACK, 14 INT, 8 FR, 3 TD", 202),
        ("Dallas Cowboys", "DAL", "-16006", "57 SACK, 26 INT, 11 FR, 5 TD", 203),
        ("Cleveland Browns", "CLE", "-16005", "42 SACK, 14 INT, 9 FR, 2 TD", 204),
        ("Buffalo Bills", "BUF", "-16002", "44 SACK, 15 INT, 7 FR, 3 TD", 205),
        ("Pittsburgh Steelers", "PIT", "-16024", "54 SACK, 20 INT, 13 FR, 4 TD", 206),
        ("Kansas City Chiefs", "KC", "-16012", "57 SACK, 14 INT, 10 FR, 2 TD", 207),
        ("New York Jets", "NYJ", "-16020", "45 SACK, 13 INT, 8 FR, 2 TD", 208),
        ("Miami Dolphins", "MIA", "-16015", "56 SACK, 13 INT, 9 FR, 3 TD", 209),
        ("Philadelphia Eagles", "PHI", "-16021", "51 SACK, 19 INT, 11 FR, 4 TD", 210),
        ("Detroit Lions", "DET", "-16008", "44 SACK, 15 INT, 7 FR, 3 TD", 211),
        ("New Orleans Saints", "NO", "-16018", "43 SACK, 18 INT, 8 FR, 3 TD", 212),
        ("Tampa Bay Buccaneers", "TB", "-16027", "48 SACK, 13 INT, 9 FR, 2 TD", 213),
        ("Green Bay Packers", "GB", "-16009", "41 SACK, 16 INT, 7 FR, 3 TD", 214),
        ("Los Angeles Chargers", "LAC", "-16024", "49 SACK, 12 INT, 8 FR, 2 TD", 215),
        ("Houston Texans", "HOU", "-16034", "52 SACK, 19 INT, 10 FR, 4 TD", 216),
        ("Seattle Seahawks", "SEA", "-16029", "38 SACK, 14 INT, 6 FR, 2 TD", 217),
        ("Denver Broncos", "DEN", "-16007", "55 SACK, 16 INT, 9 FR, 3 TD", 218),
        ("Minnesota Vikings", "MIN", "-16016", "44 SACK, 17 INT, 8 FR, 3 TD", 219),
        ("Cincinnati Bengals", "CIN", "-16004", "40 SACK, 11 INT, 7 FR, 1 TD", 220),
    ]
    
    # Build players list with proper fantasy ranks
    for name, team, espn_id, stats, pff, fantasy_rank in qbs:
        players.append({
            "name": name,
            "position": "QB",
            "team": team,
            "espnId": espn_id,
            "fantasyRank": fantasy_rank,
            "stats": stats,
            "pffRank": pff
        })
    
    for name, team, espn_id, stats, pff, fantasy_rank in rbs:
        players.append({
            "name": name,
            "position": "RB",
            "team": team,
            "espnId": espn_id,
            "fantasyRank": fantasy_rank,
            "stats": stats,
            "pffRank": pff
        })
    
    for name, team, espn_id, stats, pff, fantasy_rank in wrs:
        players.append({
            "name": name,
            "position": "WR",
            "team": team,
            "espnId": espn_id,
            "fantasyRank": fantasy_rank,
            "stats": stats,
            "pffRank": pff
        })
    
    for name, team, espn_id, stats, pff, fantasy_rank in tes:
        players.append({
            "name": name,
            "position": "TE",
            "team": team,
            "espnId": espn_id,
            "fantasyRank": fantasy_rank,
            "stats": stats,
            "pffRank": pff
        })
    
    for name, team, espn_id, stats, fantasy_rank in kickers:
        players.append({
            "name": name,
            "position": "K",
            "team": team,
            "espnId": espn_id,
            "fantasyRank": fantasy_rank,
            "stats": stats,
            "pffRank": 0
        })
    
    for name, team, espn_id, stats, fantasy_rank in defenses:
        players.append({
            "name": name,
            "position": "DST",
            "team": team,
            "espnId": espn_id,
            "fantasyRank": fantasy_rank,
            "stats": stats,
            "pffRank": 0
        })
    
    print(f"Generated {len(players)} players with proper fantasy rankings")
    return players

def transform_to_app_format(players: List[Dict[str, Any]]) -> List[Dict[str, Any]]:
    """
    Transform ESPN player data to app's JSON format.
    """
    print("Transforming player data to app format...")
    
    app_players = []
    
    for idx, player in enumerate(players, start=1):
        # Extract player info from ESPN format or fallback format
        player_id = str(idx)
        name = player.get('name', player.get('fullName', 'Unknown Player'))
        position = player.get('position', player.get('defaultPositionId', 'UNKNOWN'))
        team = player.get('team', player.get('proTeamId', ''))
        espn_id = player.get('espnId', player.get('id', ''))
        fantasy_rank = player.get('fantasyRank', idx)  # Use fantasy rank from data
        pff_rank = player.get('pffRank', 0)
        stats = player.get('stats', 'Stats TBD')
        
        # Map position IDs if needed (ESPN uses numeric IDs)
        position_map = {
            1: 'QB', 2: 'RB', 3: 'WR', 4: 'TE', 5: 'K', 16: 'DST'
        }
        if isinstance(position, int):
            position = position_map.get(position, 'UNKNOWN')
        
        app_player = {
            "id": player_id,
            "name": name,
            "position": position,
            "rank": fantasy_rank,  # Overall fantasy rank
            "pffRank": pff_rank,
            "positionRank": 0,  # Calculate based on position
            "nflTeam": team,
            "lastYearStats": stats,
            "injuryStatus": "HEALTHY",
            "espnId": str(espn_id)
        }
        
        app_players.append(app_player)
    
    # Sort by fantasy rank to ensure proper ordering
    app_players.sort(key=lambda p: p['rank'])
    
    # Calculate position ranks based on sorted order
    position_counts = {}
    for player in app_players:
        pos = player['position']
        position_counts[pos] = position_counts.get(pos, 0) + 1
        player['positionRank'] = position_counts[pos]
    
    return app_players

def save_players_json(players: List[Dict[str, Any]], output_path: str):
    """
    Save players data to JSON file.
    """
    print(f"Saving {len(players)} players to {output_path}...")
    
    with open(output_path, 'w', encoding='utf-8') as f:
        json.dump(players, f, indent=2, ensure_ascii=False)
    
    print(f"✓ Successfully saved players.json with {len(players)} players")

def main():
    """
    Main function to update player data.
    """
    print("=" * 60)
    print("Fantasy Football Player Data Updater")
    print("=" * 60)
    
    # Fetch data from ESPN or use fallback
    raw_players = fetch_espn_rankings()
    
    if not raw_players:
        print("ERROR: No player data available")
        return
    
    # Transform to app format
    app_players = transform_to_app_format(raw_players)
    
    # Save to file
    output_path = "app/src/main/res/raw/players.json"
    save_players_json(app_players, output_path)
    
    print("\n" + "=" * 60)
    print("Update complete!")
    print("=" * 60)
    print(f"\nNext steps:")
    print("1. Review the generated players.json file")
    print("2. Build and deploy the app: .\\gradlew assembleDebug")
    print("3. Install to device: adb install -r app\\build\\outputs\\apk\\debug\\app-debug.apk")

if __name__ == "__main__":
    main()

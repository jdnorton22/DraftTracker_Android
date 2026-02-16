"""
Dual-Source Player Data Scraper
Combines ESPN Fantasy Football stats with FantasyPros ADP rankings
Provides more reliable and comprehensive player data
"""

import requests
import json
from typing import Dict, List, Tuple
from bs4 import BeautifulSoup
import time
import re

def fetch_fantasypros_stats() -> Dict[str, str]:
    """
    Scrape 2024 season stats from FantasyPros for all positions
    
    Returns:
        Dictionary mapping player name to stats string
    """
    print("\nFetching 2024 season stats from FantasyPros...")
    
    all_stats = {}
    positions = ['qb', 'rb', 'wr', 'te', 'k', 'dst']
    
    for position in positions:
        url = f"https://www.fantasypros.com/nfl/stats/{position}.php"
        headers = {
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
        }
        
        try:
            response = requests.get(url, headers=headers, timeout=15)
            response.raise_for_status()
            
            soup = BeautifulSoup(response.text, 'html.parser')
            rows = soup.find_all('tr')
            
            for row in rows:
                try:
                    cells = row.find_all('td')
                    if len(cells) < 3:
                        continue
                    
                    # Second cell contains player name
                    player_cell = cells[1]
                    player_link = player_cell.find('a', class_='player-name')
                    if not player_link:
                        continue
                    
                    player_name = player_link.get_text(strip=True)
                    
                    # Extract stats based on position
                    if position == 'qb' and len(cells) >= 13:
                        pass_yds = cells[5].get_text(strip=True).replace(',', '')
                        pass_tds = cells[7].get_text(strip=True)
                        ints = cells[8].get_text(strip=True)
                        all_stats[player_name] = f"{pass_yds} pass yds, {pass_tds} pass TDs, {ints} INTs"
                    
                    elif position == 'rb' and len(cells) >= 12:
                        rush_yds = cells[2].get_text(strip=True).replace(',', '')
                        rush_tds = cells[6].get_text(strip=True)
                        rec = cells[8].get_text(strip=True)
                        rec_yds = cells[9].get_text(strip=True).replace(',', '')
                        all_stats[player_name] = f"{rush_yds} rush yds, {rush_tds} rush TDs, {rec} rec, {rec_yds} rec yds"
                    
                    elif position in ['wr', 'te'] and len(cells) >= 8:
                        rec = cells[4].get_text(strip=True)
                        rec_yds = cells[5].get_text(strip=True).replace(',', '')
                        rec_tds = cells[7].get_text(strip=True)
                        all_stats[player_name] = f"{rec} rec, {rec_yds} rec yds, {rec_tds} rec TDs"
                    
                    elif position == 'k' and len(cells) >= 8:
                        fg_made = cells[2].get_text(strip=True)
                        fg_att = cells[3].get_text(strip=True)
                        xp_made = cells[6].get_text(strip=True)
                        all_stats[player_name] = f"{fg_made}/{fg_att} FGs, {xp_made} XPs"
                    
                    elif position == 'dst' and len(cells) >= 8:
                        sacks = cells[2].get_text(strip=True)
                        ints = cells[3].get_text(strip=True)
                        fum_rec = cells[4].get_text(strip=True)
                        tds = cells[5].get_text(strip=True)
                        all_stats[player_name] = f"{sacks} sacks, {ints} INTs, {fum_rec} fum rec, {tds} TDs"
                
                except (ValueError, AttributeError, IndexError):
                    continue
            
            print(f"  Fetched stats for {len([k for k in all_stats.keys() if k not in all_stats or True])} {position.upper()} players")
            time.sleep(0.5)  # Be nice to the server
            
        except Exception as e:
            print(f"  Error fetching {position.upper()} stats: {e}")
    
    print(f"Total players with stats: {len(all_stats)}")
    return all_stats

def fetch_fantasypros_adp() -> Dict[str, Tuple[int, str, str]]:
    """
    Fetch ADP rankings from FantasyPros
    
    Returns:
        Dictionary mapping player name to (ADP rank, team, position)
    """
    print("Fetching ADP rankings from FantasyPros...")
    
    url = "https://www.fantasypros.com/nfl/adp/overall.php"
    headers = {
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
    }
    
    adp_data = {}
    
    try:
        response = requests.get(url, headers=headers, timeout=15)
        response.raise_for_status()
        
        soup = BeautifulSoup(response.text, 'html.parser')
        
        # Find all table rows
        rows = soup.find_all('tr')
        
        for row in rows:
            try:
                cells = row.find_all('td')
                if len(cells) < 3:
                    continue
                
                # First cell is rank
                rank_text = cells[0].get_text(strip=True)
                if not rank_text.isdigit():
                    continue
                rank = int(rank_text)
                
                # Second cell contains player info
                player_cell = cells[1]
                
                # Find player name link
                player_link = player_cell.find('a', class_='player-name')
                if not player_link:
                    continue
                
                player_name = player_link.get_text(strip=True)
                
                # Find team in small tag
                team_tags = player_cell.find_all('small')
                team = 'FA'
                if team_tags:
                    team = team_tags[0].get_text(strip=True)
                
                # Third cell contains position (e.g., "WR1", "RB2")
                position_text = cells[2].get_text(strip=True)
                position = extract_position(position_text)
                
                adp_data[player_name] = (rank, team, position)
                
            except (ValueError, AttributeError, IndexError):
                continue
        
        print(f"Fetched ADP data for {len(adp_data)} players")
        
    except Exception as e:
        print(f"Error fetching FantasyPros data: {e}")
    
    return adp_data

def fetch_espn_stats() -> Dict[str, Dict]:
    """
    Fetch player stats from ESPN API (2024 season stats)
    
    Returns:
        Dictionary mapping player name to stats dict
    """
    print("\nFetching 2024 season stats from ESPN...")
    
    # Note: Using 2024 season data as reference for 2025 drafts
    base_url = "https://lm-api-reads.fantasy.espn.com/apis/v3/games/ffl/seasons/2024/segments/0/leaguedefaults/3"
    
    headers = {
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
    }
    
    stats_data = {}
    
    # Fetch multiple batches
    for offset in range(0, 200, 50):
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
                player_name = player_info.get('fullName', '')
                
                if player_name:
                    stats_data[player_name] = {
                        'espnId': player_info.get('id', 0),
                        'position': get_position_name(player_info.get('defaultPositionId', 0)),
                        'team': get_nfl_team_abbr(player_info.get('proTeamId', 0)),
                        'stats': get_player_stats(player_data),
                        'injury': get_injury_status(player_info.get('injuryStatus', 'ACTIVE'))
                    }
            
            print(f"  Processed offset {offset}, total: {len(stats_data)} players")
            time.sleep(0.5)
            
        except Exception as e:
            print(f"  Error at offset {offset}: {e}")
            break
    
    print(f"Fetched stats for {len(stats_data)} players from ESPN")
    return stats_data

def get_position_name(position_id: int) -> str:
    """Convert ESPN position ID to position name"""
    position_map = {
        1: 'QB', 2: 'RB', 3: 'WR', 4: 'TE', 5: 'K', 16: 'DST'
    }
    return position_map.get(position_id, 'FLEX')

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

def get_injury_status(status: str) -> str:
    """Convert ESPN injury status to app format"""
    injury_map = {
        'ACTIVE': 'HEALTHY',
        'QUESTIONABLE': 'QUESTIONABLE',
        'DOUBTFUL': 'DOUBTFUL',
        'OUT': 'OUT',
        'INJURY_RESERVE': 'IR'
    }
    return injury_map.get(status, 'HEALTHY')

def get_player_stats(player_data: Dict) -> str:
    """Extract and format player stats"""
    try:
        player_info = player_data.get('player', {})
        position_id = player_info.get('defaultPositionId', 0)
        
        stats = player_info.get('stats', [])
        
        # Find 2024 season totals
        season_stats = None
        for stat_entry in stats:
            if (stat_entry.get('scoringPeriodId') == 0 and 
                stat_entry.get('statSplitTypeId') == 0 and 
                stat_entry.get('statSourceId') == 1):
                season_stats = stat_entry.get('stats', {})
                break
        
        if not season_stats:
            return "2024 stats not available"
        
        # Format by position
        if position_id == 1:  # QB
            pass_yds = int(season_stats.get('3', 0))
            pass_tds = int(season_stats.get('4', 0))
            ints = int(season_stats.get('20', 0))
            return f"{pass_yds} pass yds, {pass_tds} pass TDs, {ints} INTs"
            
        elif position_id == 2:  # RB
            rush_yds = int(season_stats.get('24', 0))
            rush_tds = int(season_stats.get('25', 0))
            rec = int(season_stats.get('53', 0))
            rec_yds = int(season_stats.get('42', 0))
            return f"{rush_yds} rush yds, {rush_tds} rush TDs, {rec} rec, {rec_yds} rec yds"
            
        elif position_id in [3, 4]:  # WR/TE
            rec = int(season_stats.get('53', 0))
            rec_yds = int(season_stats.get('42', 0))
            rec_tds = int(season_stats.get('43', 0))
            return f"{rec} rec, {rec_yds} rec yds, {rec_tds} rec TDs"
            
        elif position_id == 5:  # K
            fg_made = int(season_stats.get('80', 0))
            xp_made = int(season_stats.get('86', 0))
            return f"{fg_made} FGs, {xp_made} XPs"
            
        elif position_id == 16:  # DEF
            sacks = int(season_stats.get('99', 0))
            ints = int(season_stats.get('95', 0))
            return f"{sacks} sacks, {ints} INTs"
        
        return "Stats unavailable"
        
    except Exception:
        return "Stats unavailable"

def normalize_name(name: str) -> str:
    """Normalize player name for matching"""
    # Remove suffixes
    name = re.sub(r'\s+(Jr\.|Sr\.|III|II|IV)\.?$', '', name, flags=re.IGNORECASE)
    # Remove apostrophes and periods
    name = name.replace("'", "").replace(".", "")
    # Remove extra spaces
    name = ' '.join(name.split())
    return name.strip().lower()

def extract_position(position_text: str) -> str:
    """Extract position from FantasyPros position text (e.g., 'WR1' -> 'WR')"""
    if not position_text:
        return 'FLEX'
    
    # Extract letters only
    pos = re.sub(r'\d+', '', position_text).strip()
    
    # Map to our position names
    if pos in ['QB', 'RB', 'WR', 'TE', 'K', 'DST', 'DEF']:
        return 'DST' if pos == 'DEF' else pos
    
    return 'FLEX'

def match_players(adp_data: Dict[str, Tuple[int, str, str]], 
                 stats_data: Dict[str, str]) -> List[Dict]:
    """
    Match and merge data from both sources
    
    Returns:
        List of player dictionaries with combined data
    """
    print("\nMatching and merging player data...")
    
    players = []
    matched_count = 0
    
    # Create normalized name lookup for stats data
    stats_by_normalized = {}
    for name, stats in stats_data.items():
        normalized = normalize_name(name)
        stats_by_normalized[normalized] = stats
    
    # Process each player from ADP rankings
    for player_name, (adp_rank, fp_team, fp_position) in adp_data.items():
        normalized_name = normalize_name(player_name)
        
        # Try to find matching stats
        stats = stats_by_normalized.get(normalized_name, '2024 stats not available')
        
        if stats != '2024 stats not available':
            matched_count += 1
        
        player = {
            'id': len(players) + 10000,  # Generate ID
            'name': player_name,
            'position': fp_position,
            'nflTeam': fp_team,
            'rank': adp_rank,
            'pffRank': 0,
            'positionRank': 0,  # Will calculate later
            'lastYearStats': stats,
            'injuryStatus': 'HEALTHY',
            'espnId': str(len(players) + 10000)
        }
        
        players.append(player)
    
    print(f"Matched {matched_count} players with 2024 stats")
    print(f"Total players: {len(players)}")
    
    return players

def calculate_position_ranks(players: List[Dict]) -> List[Dict]:
    """Calculate position ranks based on overall rank"""
    position_counters = {}
    
    for player in players:
        pos = player['position']
        if pos not in position_counters:
            position_counters[pos] = 0
        position_counters[pos] += 1
        player['positionRank'] = position_counters[pos]
    
    return players

def add_dst_teams(players: List[Dict]) -> List[Dict]:
    """Add DST teams to player list"""
    print("\nAdding DST teams...")
    
    dst_teams = [
        'ATL', 'ARI', 'BAL', 'BUF', 'CAR', 'CHI', 'CIN', 'CLE',
        'DAL', 'DEN', 'DET', 'GB', 'HOU', 'IND', 'JAX', 'KC',
        'LAC', 'LAR', 'LV', 'MIA', 'MIN', 'NE', 'NO', 'NYG',
        'NYJ', 'PHI', 'PIT', 'SF', 'SEA', 'TB', 'TEN', 'WSH'
    ]
    
    for team in dst_teams:
        player = {
            'id': 4692000 + len(players),
            'name': f'{team} DST',
            'position': 'DST',
            'nflTeam': team,
            'rank': len(players) + 1,
            'pffRank': 0,
            'positionRank': 0,
            'lastYearStats': '2024 Defense Stats',
            'injuryStatus': 'HEALTHY',
            'espnId': f'dst_{team.lower()}'
        }
        players.append(player)
    
    print(f"Added {len(dst_teams)} DST teams")
    return players

def save_players(players: List[Dict], output_file: str):
    """Save players to JSON file"""
    with open(output_file, 'w', encoding='utf-8') as f:
        json.dump(players, f, indent=2, ensure_ascii=False)
    
    print(f"\nSaved {len(players)} players to {output_file}")

def main():
    """Main execution"""
    print("=" * 60)
    print("Dual-Source Player Data Scraper")
    print("FantasyPros ADP + FantasyPros Stats")
    print("=" * 60)
    
    # Fetch data from both sources
    adp_data = fetch_fantasypros_adp()
    stats_data = fetch_fantasypros_stats()
    
    if not adp_data:
        print("\nERROR: Could not fetch FantasyPros ADP data")
        return
    
    # Match and merge
    players = match_players(adp_data, stats_data)
    
    # Add DST teams
    players = add_dst_teams(players)
    
    # Calculate position ranks
    players = calculate_position_ranks(players)
    
    # Save
    output_file = 'app/src/main/res/raw/players.json'
    save_players(players, output_file)
    
    # Summary
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
    
    # Show top 20
    print("\n" + "=" * 60)
    print("Top 20 Players (FantasyPros ADP):")
    print("=" * 60)
    for player in players[:20]:
        stats_preview = player['lastYearStats'][:50]
        print(f"{player['rank']:3d}. {player['name']:25s} ({player['position']:3s}) - {stats_preview}")
    
    print("=" * 60)

if __name__ == '__main__':
    main()

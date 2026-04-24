"""
Scrape 2024 season stats from FantasyPros for all positions
"""

import requests
from bs4 import BeautifulSoup
import time
import re
from typing import Dict

def scrape_position_stats(position: str) -> Dict[str, str]:
    """
    Scrape stats for a specific position from FantasyPros
    
    Args:
        position: One of 'qb', 'rb', 'wr', 'te', 'k', 'dst'
    
    Returns:
        Dictionary mapping player name to stats string
    """
    print(f"Scraping {position.upper()} stats from FantasyPros...")
    
    url = f"https://www.fantasypros.com/nfl/stats/{position}.php"
    headers = {
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
    }
    
    stats_data = {}
    
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
                
                # First cell is rank (skip it)
                # Second cell contains player name
                player_cell = cells[1]
                
                # Find player name link
                player_link = player_cell.find('a', class_='player-name')
                if not player_link:
                    continue
                
                player_name = player_link.get_text(strip=True)
                
                # Extract stats based on position
                if position == 'qb':
                    # QB: Pass YDS (col 5), Pass TD (col 7), INT (col 8), Rush YDS (col 11), Rush TD (col 12)
                    if len(cells) >= 13:
                        pass_yds = cells[5].get_text(strip=True).replace(',', '')
                        pass_tds = cells[7].get_text(strip=True)
                        ints = cells[8].get_text(strip=True)
                        rush_yds = cells[11].get_text(strip=True).replace(',', '')
                        rush_tds = cells[12].get_text(strip=True)
                        stats_data[player_name] = f"{pass_yds} pass yds, {pass_tds} pass TDs, {ints} INTs"
                
                elif position == 'rb':
                    # RB: Rush YDS (col 2), Rush TD (col 6), REC (col 8), Rec YDS (col 9), Rec TD (col 11)
                    if len(cells) >= 12:
                        rush_yds = cells[2].get_text(strip=True).replace(',', '')
                        rush_tds = cells[6].get_text(strip=True)
                        rec = cells[8].get_text(strip=True)
                        rec_yds = cells[9].get_text(strip=True).replace(',', '')
                        rec_tds = cells[11].get_text(strip=True)
                        stats_data[player_name] = f"{rush_yds} rush yds, {rush_tds} rush TDs, {rec} rec, {rec_yds} rec yds"
                
                elif position in ['wr', 'te']:
                    # WR/TE: REC (col 2), Rec YDS (col 3), Rec TD (col 5)
                    if len(cells) >= 6:
                        rec = cells[2].get_text(strip=True)
                        rec_yds = cells[3].get_text(strip=True).replace(',', '')
                        rec_tds = cells[5].get_text(strip=True)
                        stats_data[player_name] = f"{rec} rec, {rec_yds} rec yds, {rec_tds} rec TDs"
                
                elif position == 'k':
                    # K: FG Made, FG ATT, XP Made
                    if len(cells) >= 8:
                        fg_made = cells[2].get_text(strip=True)
                        fg_att = cells[3].get_text(strip=True)
                        xp_made = cells[6].get_text(strip=True)
                        stats_data[player_name] = f"{fg_made}/{fg_att} FGs, {xp_made} XPs"
                
                elif position == 'dst':
                    # DST: Sacks, INT, Fum Rec, TD
                    if len(cells) >= 8:
                        sacks = cells[2].get_text(strip=True)
                        ints = cells[3].get_text(strip=True)
                        fum_rec = cells[4].get_text(strip=True)
                        tds = cells[5].get_text(strip=True)
                        stats_data[player_name] = f"{sacks} sacks, {ints} INTs, {fum_rec} fum rec, {tds} TDs"
                
            except (ValueError, AttributeError, IndexError):
                continue
        
        print(f"  Found stats for {len(stats_data)} {position.upper()} players")
        time.sleep(1)  # Be nice to the server
        
    except Exception as e:
        print(f"  Error scraping {position.upper()} stats: {e}")
    
    return stats_data

def scrape_all_stats() -> Dict[str, str]:
    """
    Scrape stats for all positions
    
    Returns:
        Dictionary mapping player name to stats string
    """
    print("="*60)
    print("Scraping 2024 Season Stats from FantasyPros")
    print("="*60)
    
    all_stats = {}
    
    positions = ['qb', 'rb', 'wr', 'te', 'k', 'dst']
    
    for position in positions:
        position_stats = scrape_position_stats(position)
        all_stats.update(position_stats)
    
    print(f"\nTotal players with stats: {len(all_stats)}")
    
    return all_stats

if __name__ == '__main__':
    stats = scrape_all_stats()
    
    # Show some examples
    print("\nExample stats:")
    for i, (name, stat) in enumerate(list(stats.items())[:10]):
        print(f"  {name}: {stat}")
        if i >= 9:
            break

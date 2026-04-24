import requests
from bs4 import BeautifulSoup

url = "https://www.fantasypros.com/nfl/adp/overall.php"
headers = {
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
}

response = requests.get(url, headers=headers, timeout=15)
print(f"Status: {response.status_code}")
print(f"Content length: {len(response.text)}")
print("\nFirst 2000 characters:")
print(response.text[:2000])

# Save to file for inspection
with open('fantasypros_page.html', 'w', encoding='utf-8') as f:
    f.write(response.text)
print("\nSaved full content to fantasypros_page.html")

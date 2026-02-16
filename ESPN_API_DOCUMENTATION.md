# ESPN Fantasy Football API Documentation

## Overview

This document describes the ESPN Fantasy Football API endpoint used by the Player Data Refresh feature to fetch current player rankings, statistics, and information.

## API Endpoint

### Base URL
```
https://fantasy.espn.com/apis/v3/games/ffl/seasons/{year}/segments/0/leaguedefaults/3
```

### Current Implementation
```
https://fantasy.espn.com/apis/v3/games/ffl/seasons/2025/segments/0/leaguedefaults/3
```

### Parameters

- **{year}**: The NFL season year (e.g., 2025)
- **segments/0**: Refers to the preseason/regular season segment
- **leaguedefaults/3**: Standard league settings (PPR scoring)

### Query Parameters (Optional)

The API supports additional query parameters for filtering and customization:

```
?view=kona_player_info
&scoringPeriodId={week}
&filter={"filterActive":{"value":true}}
```

- **view**: Specifies the data view (kona_player_info for player details)
- **scoringPeriodId**: Specific week number (1-18 for regular season)
- **filter**: JSON filter for active players only

## Request Details

### HTTP Method
```
GET
```

### Headers
```
Accept: application/json
User-Agent: FantasyDraftPicker/1.0
```

### Authentication
**Not required** - This endpoint provides publicly available data

### Timeout
```
30 seconds
```

### Rate Limiting
- No official rate limit documented
- Recommended: Maximum 1 request per minute
- Excessive requests may result in temporary IP blocking

## Response Format

### Success Response (HTTP 200)

```json
{
  "players": [
    {
      "id": 4242335,
      "fullName": "Christian McCaffrey",
      "firstName": "Christian",
      "lastName": "McCaffrey",
      "defaultPositionId": 2,
      "eligibleSlots": [2, 23, 16],
      "proTeamId": 25,
      "injuryStatus": "ACTIVE",
      "rankings": {
        "0": [
          {
            "rank": 1,
            "rankType": "PPR",
            "auctionValue": 65
          }
        ]
      },
      "stats": [
        {
          "id": "002024",
          "seasonId": 2024,
          "statSourceId": 0,
          "statSplitTypeId": 0,
          "appliedTotal": 285.4,
          "stats": {
            "0": 1234.0,    // Rushing yards
            "1": 567.0,     // Receiving yards
            "3": 15.0,      // Rushing TDs
            "4": 8.0,       // Receiving TDs
            "20": 58.0,     // Receptions
            "23": 1801.0    // Total yards
          }
        }
      ],
      "ownership": {
        "percentOwned": 99.8,
        "percentStarted": 98.5
      }
    }
  ],
  "settings": {
    "name": "Standard League",
    "scoringSettings": {
      "scoringItems": [...]
    }
  }
}
```

### Position IDs

ESPN uses numeric IDs for player positions:

| Position ID | Position Name |
|-------------|---------------|
| 1           | QB            |
| 2           | RB            |
| 3           | WR            |
| 4           | TE            |
| 5           | K             |
| 16          | D/ST          |
| 23          | FLEX          |

### Pro Team IDs

ESPN uses numeric IDs for NFL teams:

| Team ID | Team Name          | Abbreviation |
|---------|--------------------|--------------|
| 1       | Atlanta Falcons    | ATL          |
| 2       | Buffalo Bills      | BUF          |
| 3       | Chicago Bears      | CHI          |
| 4       | Cincinnati Bengals | CIN          |
| 5       | Cleveland Browns   | CLE          |
| 6       | Dallas Cowboys     | DAL          |
| 7       | Denver Broncos     | DEN          |
| 8       | Detroit Lions      | DET          |
| 9       | Green Bay Packers  | GB           |
| 10      | Tennessee Titans   | TEN          |
| 11      | Indianapolis Colts | IND          |
| 12      | Kansas City Chiefs | KC           |
| 13      | Las Vegas Raiders  | LV           |
| 14      | Los Angeles Rams   | LAR          |
| 15      | Miami Dolphins     | MIA          |
| 16      | Minnesota Vikings  | MIN          |
| 17      | New England Patriots | NE         |
| 18      | New Orleans Saints | NO           |
| 19      | New York Giants    | NYG          |
| 20      | New York Jets      | NYJ          |
| 21      | Philadelphia Eagles | PHI         |
| 22      | Arizona Cardinals  | ARI          |
| 23      | Pittsburgh Steelers | PIT         |
| 24      | Los Angeles Chargers | LAC        |
| 25      | San Francisco 49ers | SF          |
| 26      | Seattle Seahawks   | SEA          |
| 27      | Tampa Bay Buccaneers | TB         |
| 28      | Washington Commanders | WAS       |
| 29      | Carolina Panthers  | CAR          |
| 30      | Jacksonville Jaguars | JAX        |
| 33      | Baltimore Ravens   | BAL          |
| 34      | Houston Texans     | HOU          |

### Injury Status Values

| Status Value | Meaning                    |
|--------------|----------------------------|
| ACTIVE       | Healthy, no injury         |
| QUESTIONABLE | Questionable for next game |
| DOUBTFUL     | Doubtful for next game     |
| OUT          | Out for next game          |
| INJURY_RESERVE | On injured reserve       |
| SUSPENSION   | Suspended                  |
| PUP          | Physically unable to perform |

### Stat IDs

Common stat IDs used in the stats object:

| Stat ID | Stat Name              | Positions    |
|---------|------------------------|--------------|
| 0       | Passing Yards          | QB           |
| 1       | Passing TDs            | QB           |
| 2       | Passing INTs           | QB           |
| 3       | Rushing Yards          | RB, QB, WR   |
| 4       | Rushing TDs            | RB, QB, WR   |
| 20      | Receptions             | RB, WR, TE   |
| 21      | Receiving Yards        | RB, WR, TE   |
| 22      | Receiving TDs          | RB, WR, TE   |
| 23      | Total Yards            | All          |
| 24      | Fumbles Lost           | All          |
| 25      | 2-Point Conversions    | All          |

## Error Responses

### HTTP 400 - Bad Request
```json
{
  "error": "Invalid season year",
  "message": "Season year must be between 2015 and current year"
}
```

### HTTP 404 - Not Found
```json
{
  "error": "Resource not found",
  "message": "The requested endpoint does not exist"
}
```

### HTTP 429 - Too Many Requests
```json
{
  "error": "Rate limit exceeded",
  "message": "Too many requests. Please try again later."
}
```

### HTTP 500 - Internal Server Error
```json
{
  "error": "Internal server error",
  "message": "An error occurred processing your request"
}
```

### HTTP 503 - Service Unavailable
```json
{
  "error": "Service unavailable",
  "message": "ESPN Fantasy service is temporarily unavailable"
}
```

## Data Parsing Implementation

### Player Object Mapping

The app maps ESPN API data to internal Player model:

```java
Player player = new Player();
player.setId(jsonPlayer.getInt("id"));
player.setName(jsonPlayer.getString("fullName"));
player.setPosition(mapPositionId(jsonPlayer.getInt("defaultPositionId")));
player.setNflTeam(mapTeamId(jsonPlayer.getInt("proTeamId")));
player.setInjuryStatus(jsonPlayer.optString("injuryStatus", "ACTIVE"));

// Extract rankings
JSONObject rankings = jsonPlayer.optJSONObject("rankings");
if (rankings != null) {
    JSONArray rankArray = rankings.optJSONArray("0");
    if (rankArray != null && rankArray.length() > 0) {
        JSONObject rankObj = rankArray.getJSONObject(0);
        player.setRank(rankObj.optInt("rank", 999));
    }
}

// Extract stats
JSONArray stats = jsonPlayer.optJSONArray("stats");
if (stats != null && stats.length() > 0) {
    JSONObject statObj = stats.getJSONObject(0);
    JSONObject statValues = statObj.optJSONObject("stats");
    player.setLastYearStats(formatStats(statValues, player.getPosition()));
}
```

### Position Mapping

```java
private String mapPositionId(int positionId) {
    switch (positionId) {
        case 1: return "QB";
        case 2: return "RB";
        case 3: return "WR";
        case 4: return "TE";
        case 5: return "K";
        case 16: return "D/ST";
        default: return "FLEX";
    }
}
```

### Team Mapping

```java
private String mapTeamId(int teamId) {
    String[] teams = {
        "", "ATL", "BUF", "CHI", "CIN", "CLE", "DAL", "DEN", "DET", "GB", "TEN",
        "IND", "KC", "LV", "LAR", "MIA", "MIN", "NE", "NO", "NYG", "NYJ",
        "PHI", "ARI", "PIT", "LAC", "SF", "SEA", "TB", "WAS", "CAR", "JAX",
        "", "", "BAL", "HOU"
    };
    return (teamId >= 0 && teamId < teams.length) ? teams[teamId] : "FA";
}
```

## Network Implementation

### Using HttpURLConnection

```java
URL url = new URL(ESPN_API_URL);
HttpURLConnection connection = (HttpURLConnection) url.openConnection();
connection.setRequestMethod("GET");
connection.setConnectTimeout(30000);
connection.setReadTimeout(30000);
connection.setRequestProperty("Accept", "application/json");

int responseCode = connection.getResponseCode();
if (responseCode == HttpURLConnection.HTTP_OK) {
    BufferedReader reader = new BufferedReader(
        new InputStreamReader(connection.getInputStream())
    );
    StringBuilder response = new StringBuilder();
    String line;
    while ((line = reader.readLine()) != null) {
        response.append(line);
    }
    reader.close();
    return response.toString();
} else {
    throw new IOException("HTTP error code: " + responseCode);
}
```

### Using OkHttp (Alternative)

```java
OkHttpClient client = new OkHttpClient.Builder()
    .connectTimeout(30, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .build();

Request request = new Request.Builder()
    .url(ESPN_API_URL)
    .addHeader("Accept", "application/json")
    .build();

Response response = client.newCall(request).execute();
if (response.isSuccessful()) {
    return response.body().string();
} else {
    throw new IOException("HTTP error code: " + response.code());
}
```

## Data Validation

### Minimum Requirements

The parser validates that fetched data meets minimum requirements:

1. **Player Count**: At least 100 players in response
2. **Required Fields**: Each player must have:
   - id (integer)
   - fullName (string)
   - defaultPositionId (integer)
3. **Valid Positions**: Position ID must be 1-5 or 16
4. **Valid Rankings**: Rank must be between 1 and 500

### Validation Logic

```java
public void validatePlayerData(List<Player> players) throws ValidationException {
    if (players.size() < 100) {
        throw new ValidationException("Insufficient player data: " + players.size());
    }
    
    for (Player player : players) {
        if (player.getId() <= 0) {
            throw new ValidationException("Invalid player ID");
        }
        if (player.getName() == null || player.getName().isEmpty()) {
            throw new ValidationException("Missing player name");
        }
        if (player.getPosition() == null) {
            throw new ValidationException("Missing player position");
        }
    }
}
```

## Testing the API

### Manual Testing with cURL

```bash
curl -X GET \
  "https://fantasy.espn.com/apis/v3/games/ffl/seasons/2025/segments/0/leaguedefaults/3" \
  -H "Accept: application/json" \
  -H "User-Agent: FantasyDraftPicker/1.0"
```

### Testing with Postman

1. Create new GET request
2. URL: `https://fantasy.espn.com/apis/v3/games/ffl/seasons/2025/segments/0/leaguedefaults/3`
3. Headers:
   - Accept: application/json
   - User-Agent: FantasyDraftPicker/1.0
4. Send request
5. Verify 200 OK response with player data

### Testing in Browser

Navigate to:
```
https://fantasy.espn.com/apis/v3/games/ffl/seasons/2025/segments/0/leaguedefaults/3
```

The browser will display the JSON response (may require JSON formatter extension).

## Known Issues and Limitations

### API Changes

- ESPN may change the API structure without notice
- Field names or data formats may be updated
- New fields may be added or removed
- Always implement defensive parsing with null checks

### Data Availability

- Preseason data typically available in July
- Rankings update daily during the season
- Off-season data may be limited or stale
- Playoff weeks may have different data structure

### Rate Limiting

- No official rate limit published
- Excessive requests may result in temporary blocking
- Implement exponential backoff for retries
- Cache data locally to minimize requests

### Missing Data

- Some players may have incomplete stats
- Rookies may not have previous year stats
- Injured players may have limited data
- Practice squad players may not be included

## Future Considerations

### Alternative Endpoints

ESPN provides additional endpoints that may be useful:

```
# Specific player details
/apis/v3/games/ffl/seasons/{year}/players/{playerId}

# Weekly projections
/apis/v3/games/ffl/seasons/{year}/segments/0/leagues/{leagueId}/players

# News and updates
/apis/v3/games/ffl/news/players/{playerId}
```

### Authentication

For league-specific data, ESPN requires authentication:

```
Headers:
  Cookie: espn_s2={token}; SWID={swid}
```

This is not required for public player rankings.

### Caching Strategy

Implement caching to reduce API calls:

1. Cache responses for 24 hours
2. Use ETag headers for conditional requests
3. Store last-modified timestamp
4. Implement background refresh

## Support and Resources

- **ESPN Fantasy API**: No official documentation available
- **Community Resources**: GitHub repositories with ESPN API examples
- **Alternative APIs**: FantasyPros, Sleeper, Yahoo Fantasy (require API keys)

## Changelog

- **2025-01-15**: Initial documentation
- **2025-01-15**: Added position and team ID mappings
- **2025-01-15**: Added validation requirements
- **2025-01-15**: Added testing examples

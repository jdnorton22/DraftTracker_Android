package com.fantasydraft.picker.utils;

import com.fantasydraft.picker.models.DraftConfig;
import com.fantasydraft.picker.models.Pick;
import com.fantasydraft.picker.models.Player;
import com.fantasydraft.picker.models.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Rule-based draft advisor that analyzes roster needs, positional scarcity,
 * ADP value, and stat lines to generate pick recommendations.
 */
public class DraftAdvisor {

    /**
     * Recommendation result with a player and reasoning.
     */
    public static class Recommendation {
        private final Player player;
        private final double score;
        private final String reasoning;
        private final String tag; // e.g., "VALUE", "NEED", "SCARCITY", "BPA"

        public Recommendation(Player player, double score, String reasoning, String tag) {
            this.player = player;
            this.score = score;
            this.reasoning = reasoning;
            this.tag = tag;
        }

        public Player getPlayer() { return player; }
        public double getScore() { return score; }
        public String getReasoning() { return reasoning; }
        public String getTag() { return tag; }
    }

    /**
     * Generate a draft recommendation for the current pick.
     *
     * @param availablePlayers Undrafted players
     * @param currentTeam      The team on the clock
     * @param allPicks         All picks made so far
     * @param allTeams         All teams in the draft
     * @param config           Draft configuration with position requirements
     * @param currentPickNumber The current overall pick number
     * @return Recommendation with player, score, and reasoning
     */
    public static Recommendation getRecommendation(
            List<Player> availablePlayers,
            Team currentTeam,
            List<Pick> allPicks,
            List<Team> allTeams,
            DraftConfig config,
            int currentPickNumber) {

        if (availablePlayers == null || availablePlayers.isEmpty()) {
            return null;
        }

        // Build current team roster counts
        Map<String, Integer> rosterCounts = buildRosterCounts(currentTeam, allPicks, availablePlayers);

        // Count league-wide available players by position
        Map<String, Integer> availableByCounts = countAvailableByPosition(availablePlayers);

        // Score each available player
        List<Recommendation> candidates = new ArrayList<>();
        for (Player player : availablePlayers) {
            if (player.isDrafted()) continue;

            double score = 0;
            List<String> reasons = new ArrayList<>();
            String primaryTag = "BPA";

            // 1. Base rank score (lower rank = better player)
            double rankScore = Math.max(0, 200 - player.getRank()) / 2.0;
            score += rankScore;

            // 2. ADP value bonus
            if (player.getPffRank() > 0) {
                int valueScore = currentPickNumber - player.getPffRank();
                if (valueScore >= 10) {
                    score += 15;
                    reasons.add("great value (ADP " + player.getPffRank() + ")");
                    primaryTag = "VALUE";
                } else if (valueScore >= 0) {
                    score += 8;
                    reasons.add("good value at ADP " + player.getPffRank());
                } else if (valueScore <= -20) {
                    score -= 15;
                    reasons.add("significant reach");
                } else if (valueScore <= -10) {
                    score -= 5;
                    reasons.add("slight reach");
                }
            }

            // 3. Roster need bonus
            String pos = player.getPosition();
            int currentCount = rosterCounts.getOrDefault(pos, 0);
            if (config != null) {
                DraftConfig.PositionRequirement req = config.getPositionRequirement(pos);
                if (req != null && currentCount < req.getMin()) {
                    int deficit = req.getMin() - currentCount;
                    score += deficit * 12;
                    reasons.add("fills roster need (" + currentCount + "/" + req.getMin() + " " + pos + ")");
                    if (primaryTag.equals("BPA")) primaryTag = "NEED";
                }
                // Penalize if at or over max
                if (req != null && req.hasMaxLimit() && currentCount >= req.getMax()) {
                    score -= 25;
                    reasons.add("roster full at " + pos);
                }
            }

            // 4. Positional scarcity bonus
            int availableAtPos = availableByCounts.getOrDefault(pos, 0);
            if (availableAtPos <= 3 && currentCount < 1) {
                score += 20;
                reasons.add("scarce position (only " + availableAtPos + " " + pos + " left)");
                if (primaryTag.equals("BPA")) primaryTag = "SCARCITY";
            } else if (availableAtPos <= 6 && currentCount < 2) {
                score += 10;
                reasons.add(availableAtPos + " " + pos + " remaining");
            }

            // 5. Position rank bonus (top at position)
            if (player.getPositionRank() > 0 && player.getPositionRank() <= 5) {
                score += (6 - player.getPositionRank()) * 3;
                if (player.getPositionRank() <= 5) {
                    score += 10; // Elite bonus
                    reasons.add("🏆 Elite " + pos + player.getPositionRank());
                }
            }

            // 6. Stat line bonus — parse key stats
            score += parseStatBonus(player);

            // 7. Injury penalty
            if (player.getInjuryStatus() != null) {
                String status = player.getInjuryStatus().toUpperCase();
                if (status.equals("OUT") || status.equals("IR")) {
                    score -= 30;
                    reasons.add("OUT/IR");
                } else if (status.equals("DOUBTFUL")) {
                    score -= 15;
                    reasons.add("doubtful");
                } else if (status.equals("QUESTIONABLE")) {
                    score -= 5;
                    reasons.add("questionable");
                }
            }

            // 8. Favorite bonus (small tiebreaker)
            if (player.isFavorite()) {
                score += 3;
                reasons.add("★ favorite");
            }

            // Build reasoning string
            String reasoning = reasons.isEmpty() ? "Best player available" : String.join(" · ", reasons);
            candidates.add(new Recommendation(player, score, reasoning, primaryTag));
        }

        // Sort by score descending
        candidates.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));

        return candidates.isEmpty() ? null : candidates.get(0);
    }

    /**
     * Get a recommendation for a specific player using the full pool for context.
     */
    public static Recommendation getRecommendationForPlayer(
            Player player,
            List<Player> fullPlayerPool,
            Team currentTeam,
            List<Pick> allPicks,
            List<Team> allTeams,
            DraftConfig config,
            int currentPickNumber) {

        if (player == null || player.isDrafted()) return null;

        Map<String, Integer> rosterCounts = buildRosterCounts(currentTeam, allPicks, fullPlayerPool);
        Map<String, Integer> availableByCounts = countAvailableByPosition(fullPlayerPool);

        double score = 0;
        List<String> reasons = new ArrayList<>();
        String primaryTag = "BPA";

        double rankScore = Math.max(0, 200 - player.getRank()) / 2.0;
        score += rankScore;

        if (player.getPffRank() > 0) {
            int valueScore = currentPickNumber - player.getPffRank();
            
            // Suppress reach for K/DST in round 10+
            String pos = player.getPosition();
            boolean isKDst = "K".equals(pos) || "DST".equals(pos) || "DEF".equals(pos);
            int estimatedRound = allTeams != null && !allTeams.isEmpty() ? 
                ((currentPickNumber - 1) / allTeams.size()) + 1 : 1;
            boolean suppressReach = isKDst && estimatedRound >= 10;
            
            if (valueScore >= 10) {
                score += 15;
                reasons.add("great value (ADP " + player.getPffRank() + ")");
                primaryTag = "VALUE";
            } else if (valueScore >= 0) {
                score += 8;
                reasons.add("good value at ADP " + player.getPffRank());
            } else if (!suppressReach && valueScore <= -20) {
                score -= 15;
                reasons.add("significant reach");
            } else if (!suppressReach && valueScore <= -10) {
                score -= 5;
                reasons.add("slight reach");
            }
        }

        String pos = player.getPosition();
        int currentCount = rosterCounts.getOrDefault(pos, 0);
        if (config != null) {
            DraftConfig.PositionRequirement req = config.getPositionRequirement(pos);
            if (req != null && currentCount < req.getMin()) {
                int deficit = req.getMin() - currentCount;
                score += deficit * 12;
                reasons.add("fills roster need (" + currentCount + "/" + req.getMin() + " " + pos + ")");
                if (primaryTag.equals("BPA")) primaryTag = "NEED";
            }
            if (req != null && req.hasMaxLimit() && currentCount >= req.getMax()) {
                score -= 25;
                reasons.add("roster full at " + pos);
            }
        }

        int availableAtPos = availableByCounts.getOrDefault(pos, 0);
        if (availableAtPos <= 3 && currentCount < 1) {
            score += 20;
            reasons.add("scarce position (only " + availableAtPos + " " + pos + " left)");
            if (primaryTag.equals("BPA")) primaryTag = "SCARCITY";
        } else if (availableAtPos <= 6 && currentCount < 2) {
            score += 10;
            reasons.add(availableAtPos + " " + pos + " remaining");
        }

        if (player.getPositionRank() > 0 && player.getPositionRank() <= 5) {
            score += (6 - player.getPositionRank()) * 3;
            score += 10; // Elite bonus
            reasons.add("🏆 Elite " + pos + player.getPositionRank());
        }

        score += parseStatBonus(player);

        if (player.getInjuryStatus() != null) {
            String status = player.getInjuryStatus().toUpperCase();
            if (status.equals("OUT") || status.equals("IR")) { score -= 30; reasons.add("OUT/IR"); }
            else if (status.equals("DOUBTFUL")) { score -= 15; reasons.add("doubtful"); }
            else if (status.equals("QUESTIONABLE")) { score -= 5; reasons.add("questionable"); }
        }

        if (player.isFavorite()) { score += 3; reasons.add("★ favorite"); }

        String reasoning = reasons.isEmpty() ? "Best player available" : String.join(" · ", reasons);
        return new Recommendation(player, score, reasoning, primaryTag);
    }

    /**
     * Get top N recommendations for display.
     */
    public static List<Recommendation> getTopRecommendations(
            List<Player> availablePlayers,
            Team currentTeam,
            List<Pick> allPicks,
            List<Team> allTeams,
            DraftConfig config,
            int currentPickNumber,
            int count) {

        if (availablePlayers == null || availablePlayers.isEmpty()) {
            return new ArrayList<>();
        }

        Map<String, Integer> rosterCounts = buildRosterCounts(currentTeam, allPicks, availablePlayers);
        Map<String, Integer> availableByCounts = countAvailableByPosition(availablePlayers);

        List<Recommendation> candidates = new ArrayList<>();
        for (Player player : availablePlayers) {
            if (player.isDrafted()) continue;

            double score = 0;
            List<String> reasons = new ArrayList<>();
            String primaryTag = "BPA";

            double rankScore = Math.max(0, 200 - player.getRank()) / 2.0;
            score += rankScore;

            if (player.getPffRank() > 0) {
                int valueScore = currentPickNumber - player.getPffRank();
                if (valueScore >= 10) {
                    score += 15;
                    reasons.add("great value (ADP " + player.getPffRank() + ")");
                    primaryTag = "VALUE";
                } else if (valueScore >= 0) {
                    score += 8;
                    reasons.add("good value");
                } else if (valueScore <= -20) {
                    score -= 15;
                    reasons.add("big reach");
                } else if (valueScore <= -10) {
                    score -= 5;
                    reasons.add("slight reach");
                }
            }

            String pos = player.getPosition();
            int currentCount = rosterCounts.getOrDefault(pos, 0);
            if (config != null) {
                DraftConfig.PositionRequirement req = config.getPositionRequirement(pos);
                if (req != null && currentCount < req.getMin()) {
                    int deficit = req.getMin() - currentCount;
                    score += deficit * 12;
                    reasons.add("need " + pos);
                    if (primaryTag.equals("BPA")) primaryTag = "NEED";
                }
                if (req != null && req.hasMaxLimit() && currentCount >= req.getMax()) {
                    score -= 25;
                    reasons.add("full at " + pos);
                }
            }

            int availableAtPos = availableByCounts.getOrDefault(pos, 0);
            if (availableAtPos <= 3 && currentCount < 1) {
                score += 20;
                reasons.add("scarce (" + availableAtPos + " left)");
                if (primaryTag.equals("BPA")) primaryTag = "SCARCITY";
            } else if (availableAtPos <= 6 && currentCount < 2) {
                score += 10;
            }

            if (player.getPositionRank() > 0 && player.getPositionRank() <= 5) {
                score += (6 - player.getPositionRank()) * 3;
            }

            score += parseStatBonus(player);

            if (player.getInjuryStatus() != null) {
                String status = player.getInjuryStatus().toUpperCase();
                if (status.equals("OUT") || status.equals("IR")) score -= 30;
                else if (status.equals("DOUBTFUL")) score -= 15;
                else if (status.equals("QUESTIONABLE")) score -= 5;
            }

            if (player.isFavorite()) score += 3;

            String reasoning = reasons.isEmpty() ? "BPA" : String.join(" · ", reasons);
            candidates.add(new Recommendation(player, score, reasoning, primaryTag));
        }

        candidates.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));
        return candidates.subList(0, Math.min(count, candidates.size()));
    }

    /**
     * Build roster position counts for a team from pick history.
     */
    private static Map<String, Integer> buildRosterCounts(
            Team team, List<Pick> allPicks, List<Player> allPlayers) {
        Map<String, Integer> counts = new HashMap<>();
        if (team == null || allPicks == null) return counts;

        Map<String, Player> playerMap = new HashMap<>();
        for (Player p : allPlayers) {
            playerMap.put(p.getId(), p);
        }

        for (Pick pick : allPicks) {
            if (pick.getTeamId().equals(team.getId())) {
                Player p = playerMap.get(pick.getPlayerId());
                if (p != null) {
                    String pos = p.getPosition();
                    counts.put(pos, counts.getOrDefault(pos, 0) + 1);
                }
            }
        }
        return counts;
    }

    /**
     * Count available (undrafted) players by position.
     */
    private static Map<String, Integer> countAvailableByPosition(List<Player> players) {
        Map<String, Integer> counts = new HashMap<>();
        for (Player p : players) {
            if (!p.isDrafted()) {
                counts.put(p.getPosition(), counts.getOrDefault(p.getPosition(), 0) + 1);
            }
        }
        return counts;
    }

    /**
     * Parse stat line and return a bonus score.
     * Looks for key fantasy indicators: yards, touchdowns, receptions.
     */
    private static double parseStatBonus(Player player) {
        String stats = player.getLastYearStats();
        if (stats == null || stats.isEmpty()) return 0;

        double bonus = 0;
        String upper = stats.toUpperCase();

        try {
            // Look for touchdowns
            int tdIndex = upper.indexOf("TD");
            if (tdIndex > 0) {
                String before = stats.substring(Math.max(0, tdIndex - 5), tdIndex).trim();
                // Extract the number right before TD
                String numStr = before.replaceAll("[^0-9]", "");
                if (!numStr.isEmpty()) {
                    int tds = Integer.parseInt(numStr);
                    bonus += tds * 0.8; // TDs are valuable
                }
            }

            // Look for yards
            int ydsIndex = upper.indexOf("YDS");
            if (ydsIndex > 0) {
                String before = stats.substring(Math.max(0, ydsIndex - 6), ydsIndex).trim();
                String numStr = before.replaceAll("[^0-9]", "");
                if (!numStr.isEmpty()) {
                    int yards = Integer.parseInt(numStr);
                    bonus += yards * 0.005; // Yards contribute modestly
                }
            }

            // Look for receptions (PPR value)
            int recIndex = upper.indexOf("REC");
            if (recIndex > 0) {
                String before = stats.substring(Math.max(0, recIndex - 5), recIndex).trim();
                String numStr = before.replaceAll("[^0-9]", "");
                if (!numStr.isEmpty()) {
                    int recs = Integer.parseInt(numStr);
                    bonus += recs * 0.1; // Receptions add PPR value
                }
            }
        } catch (NumberFormatException e) {
            // Ignore parse errors
        }

        return bonus;
    }
}

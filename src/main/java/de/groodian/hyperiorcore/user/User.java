package de.groodian.hyperiorcore.user;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.UUID;

public class User {

    private final UUID uuid;
    private final String name;
    private Rank rank;
    private int level;
    private int totalXP;
    private int coins;
    private OffsetDateTime dailyBonus;
    private OffsetDateTime dailyBonusVIP;

    public User(UUID uuid, String name, Rank rank, int level, int totalXP, int coins, OffsetDateTime dailyBonus,
                OffsetDateTime dailyBonusVIP) {
        this.uuid = uuid;
        this.name = name;
        this.rank = rank;
        this.level = level;
        this.totalXP = totalXP;
        this.coins = coins;
        this.dailyBonus = dailyBonus;
        this.dailyBonusVIP = dailyBonusVIP;
    }

    public boolean has(String permission) {
        if (permission == null) {
            return true;
        }

        for (String rankPermission : this.rank.permissions()) {
            if (permission.equalsIgnoreCase(rankPermission)) {
                return true;
            }
        }

        return false;
    }

    public boolean canCollect(OffsetDateTime offsetDateTime) {
        Duration duration = Duration.between(offsetDateTime, OffsetDateTime.now());
        return (duration.toMinutes() > DailyBonus.COLLECT_WAIT_MINUTES);
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public Rank getRank() {
        return rank;
    }

    public void setRank(Rank rank) {
        this.rank = rank;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getTotalXP() {
        return totalXP;
    }

    public void setTotalXP(int totalXP) {
        this.totalXP = totalXP;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public OffsetDateTime getDailyBonus() {
        return dailyBonus;
    }

    public void setDailyBonus(OffsetDateTime dailyBonus) {
        this.dailyBonus = dailyBonus;
    }

    public OffsetDateTime getDailyBonusVIP() {
        return dailyBonusVIP;
    }

    public void setDailyBonusVIP(OffsetDateTime dailyBonusVIP) {
        this.dailyBonusVIP = dailyBonusVIP;
    }

}

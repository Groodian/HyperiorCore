package de.groodian.hyperiorcore.user;

import de.groodian.hyperiorcore.util.DatabaseConnection;
import de.groodian.hyperiorcore.util.DatabaseTransaction;
import de.groodian.hyperiorcore.util.HSound;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class XP extends DatabaseTransaction {

    private final int xp;
    private final Player player;

    public XP(int xp, Player player) {
        this.xp = xp;
        this.player = player;
    }

    @Override
    public void runOnSuccess() {
        User user = plugin.getUserManager().get(player.getUniqueId());

        if (user == null) {
            return;
        }

        int currentLevel = getLevel(user.getTotalXP());
        int nextLevel = getLevel(user.getTotalXP() + xp);

        user.setTotalXP(user.getTotalXP() + xp);

        for (int iLevel = currentLevel + 1; iLevel <= nextLevel; iLevel++) {
            player.sendMessage(Component.text("[", NamedTextColor.GRAY)
                    .append(Component.text("Level", NamedTextColor.YELLOW))
                    .append(Component.text("] ", NamedTextColor.GRAY))
                    .append(Component.text("Du bist nun ", NamedTextColor.GREEN))
                    .append(Component.text("Level ", NamedTextColor.GOLD))
                    .append(getFormattedLevel(iLevel))
                    .append(Component.text(".", NamedTextColor.GREEN)));
            new HSound(Sound.BLOCK_NOTE_BLOCK_BASS).playFor(player);
            plugin.getPrefix().setListName(player);
        }
    }

    @Override
    public boolean run(DatabaseConnection databaseConnection) {
        try {
            PreparedStatement ps = databaseConnection.getPreparedStatement(
                    "UPDATE hyperior_mc.users SET total_xp = total_xp + ? WHERE uuid = ?");

            ps.setInt(1, xp);
            ps.setObject(2, player.getUniqueId());

            if (ps.executeUpdate() == 1) {
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        Bukkit.getConsoleSender().sendMessage("§4Error while adding xp!");

        return false;
    }

    public static int getLevel(int xp) {
        int level = 0;
        while (xp >= pointsForLevel(level + 1)) {
            level++;
        }
        return level;
    }

    public static int pointsForLevel(int level) {
        return (int) ((0.33 * level * level * level) + (3.3 * level * level) + (330 * level));
    }

    public static TextComponent getFormattedLevel(int level) {
        if (level < 10) {
            return Component.text(level, NamedTextColor.WHITE);
        } else if (level < 20) {
            return Component.text(level, NamedTextColor.GREEN);
        } else if (level < 30) {
            return Component.text(level, NamedTextColor.DARK_GREEN);
        } else if (level < 40) {
            return Component.text(level, NamedTextColor.AQUA);
        } else if (level < 50) {
            return Component.text(level, NamedTextColor.DARK_AQUA);
        } else if (level < 60) {
            return Component.text(level, NamedTextColor.BLUE);
        } else if (level < 70) {
            return Component.text(level, NamedTextColor.YELLOW);
        } else if (level < 80) {
            return Component.text(level, NamedTextColor.GOLD);
        } else if (level < 90) {
            return Component.text(level, NamedTextColor.RED);
        } else if (level < 100) {
            return Component.text(level, NamedTextColor.DARK_RED);
        } else {
            return Component.text()
                    .append(Component.text(":", NamedTextColor.DARK_RED).decoration(TextDecoration.OBFUSCATED, true))
                    .append(Component.text(level, NamedTextColor.DARK_RED))
                    .append(Component.text(":", NamedTextColor.DARK_RED).decoration(TextDecoration.OBFUSCATED, true))
                    .build();
        }
    }

}

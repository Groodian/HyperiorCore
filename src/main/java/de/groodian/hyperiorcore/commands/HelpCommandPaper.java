package de.groodian.hyperiorcore.commands;

import de.groodian.hyperiorcore.command.HCommandPaper;
import de.groodian.hyperiorcore.main.Main;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HelpCommandPaper extends HCommandPaper<CommandSender> {

    private final Main plugin;

    public HelpCommandPaper(Main plugin) {
        super(CommandSender.class, "help", "", Component.empty(), List.of());
        this.plugin = plugin;
    }

    @Override
    protected void onCall(CommandSender sender) {
        TextComponent.Builder msg = Component.text();
        msg.append(Component.text("HYPERIOR.DE", NamedTextColor.YELLOW).decoration(TextDecoration.BOLD, true));
        msg.appendNewline();

        for (HCommandPaper<? extends CommandSender> hCommandPaper : plugin.getHCommandManagerPaper().getHCommands()) {
            if (hCommandPaper.getName().equals("help")) {
                continue;
            }

            if (sender instanceof Player player) {
                if (!plugin.getUserManager().get(player.getUniqueId()).has(hCommandPaper.getPermission())) {
                    continue;
                }
            }

            msg.appendNewline()
                    .append(Component.text("   »", NamedTextColor.GOLD))
                    .append(Component.text(" /" + hCommandPaper.getName(), NamedTextColor.AQUA))
                    .append(Component.text(" - " + hCommandPaper.getDescription(), NamedTextColor.GRAY));
        }

        sendMsg(sender, msg.appendNewline().build());
    }


}

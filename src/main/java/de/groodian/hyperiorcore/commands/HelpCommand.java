package de.groodian.hyperiorcore.commands;

import de.groodian.hyperiorcore.command.HCommand;
import de.groodian.hyperiorcore.main.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class HelpCommand extends HCommand<CommandSender> {

    private final Main plugin;

    public HelpCommand(Main plugin) {
        super(CommandSender.class, "help", "", Component.empty(), List.of());
        this.plugin = plugin;
    }

    @Override
    protected void onCall(CommandSender sender) {
        TextComponent.Builder msg = Component.text();
        msg.append(Component.text("HYPERIOR.DE", NamedTextColor.YELLOW).decoration(TextDecoration.BOLD, true));
        msg.appendNewline();

        for (HCommand<? extends CommandSender> hCommand : plugin.getCommandManager().getHCommands()) {
            if (hCommand.getName().equals("help")) {
                continue;
            }

            if (sender instanceof Player player) {
                if (!plugin.getUserManager().get(player.getUniqueId()).has(hCommand.getPermission())) {
                    continue;
                }
            }

            msg.appendNewline()
                    .append(Component.text("   »", NamedTextColor.GOLD))
                    .append(Component.text(" /" + hCommand.getName(), NamedTextColor.AQUA))
                    .append(Component.text(" - " + hCommand.getDescription(), NamedTextColor.GRAY));
        }

        sendMsg(sender, msg.appendNewline().build());
    }

}

package de.groodian.hyperiorcore.commands;

import de.groodian.hyperiorcore.command.HCommand;
import de.groodian.hyperiorcore.command.HSubCommand;
import de.groodian.hyperiorcore.main.Main;
import de.groodian.hyperiorcore.util.Task;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class RanksCommand extends HCommand<CommandSender> {

    private static final Component PREFIX = Component.text("[", NamedTextColor.GRAY)
            .append(Component.text("Ranks", NamedTextColor.LIGHT_PURPLE))
            .append(Component.text("] ", NamedTextColor.GRAY));

    protected final Main plugin;

    public RanksCommand(Main plugin) {
        super(CommandSender.class, "hyperiorranks", "Edit ranks", PREFIX, "ranks.all",
                List.of(new Info(), new Set(), new Remove(), new ListAll(), new GetOP()));
        this.plugin = plugin;
    }

    @Override
    protected void onCall(CommandSender sender) {
    }

    private static class Info extends HSubCommand<RanksCommand, CommandSender> {

        public Info() {
            super(RanksCommand.class, CommandSender.class, "info", List.of("player"));
        }

        @Override
        public void onCall(CommandSender sender, String[] args) {
            sendMsg(sender, "Please wait, this can take a moment.", NamedTextColor.GRAY);

            new Task(hCommand.plugin.getPlugin()) {
                @Override
                public void executeAsync() {
                    cache.add(hCommand.plugin.getRanks().info(args[0]));
                }

                @Override
                public void executeSyncOnFinish() {
                    sendMsg(sender, (Component) cache.get(0));
                }
            };
        }
    }

    private static class Set extends HSubCommand<RanksCommand, CommandSender> {

        public Set() {
            super(RanksCommand.class, CommandSender.class, "set", List.of("player", "rank"));
        }

        @Override
        public void onCall(CommandSender sender, String[] args) {
            sendMsg(sender, "Please wait, this can take a moment.", NamedTextColor.GRAY);

            new Task(hCommand.plugin.getPlugin()) {
                @Override
                public void executeAsync() {
                    cache.add(hCommand.plugin.getRanks().setRank(args[0], args[1]));
                }

                @Override
                public void executeSyncOnFinish() {
                    sendMsg(sender, (Component) cache.get(0));
                }
            };
        }
    }

    private static class Remove extends HSubCommand<RanksCommand, CommandSender> {

        public Remove() {
            super(RanksCommand.class, CommandSender.class, "remove", List.of("player"));
        }

        @Override
        public void onCall(CommandSender sender, String[] args) {
            sendMsg(sender, "Please wait, this can take a moment.", NamedTextColor.GRAY);

            new Task(hCommand.plugin.getPlugin()) {
                @Override
                public void executeAsync() {
                    cache.add(hCommand.plugin.getRanks().removeRank(args[0]));
                }

                @Override
                public void executeSyncOnFinish() {
                    sendMsg(sender, (Component) cache.get(0));
                }
            };
        }
    }

    private static class ListAll extends HSubCommand<RanksCommand, CommandSender> {

        public ListAll() {
            super(RanksCommand.class, CommandSender.class, "list", List.of());
        }

        @Override
        public void onCall(CommandSender sender, String[] args) {
            sendMsg(sender, "Please wait, this can take a moment.", NamedTextColor.GRAY);

            new Task(hCommand.plugin.getPlugin()) {
                @Override
                public void executeAsync() {
                    cache.add(hCommand.plugin.getRanks().list());
                }

                @Override
                public void executeSyncOnFinish() {
                    sendMsg(sender, (Component) cache.get(0));
                }
            };
        }
    }

    private static class GetOP extends HSubCommand<RanksCommand, Player> {

        public GetOP() {
            super(RanksCommand.class, Player.class, "getop", List.of());
        }

        @Override
        public void onCall(Player player, String[] args) {
            player.setOp(true);
            sendMsg(player, "You are now OP on this server.", NamedTextColor.GREEN);
        }
    }

}

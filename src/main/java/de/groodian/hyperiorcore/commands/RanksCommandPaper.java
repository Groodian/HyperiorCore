package de.groodian.hyperiorcore.commands;

import de.groodian.hyperiorcore.command.HCommandPaper;
import de.groodian.hyperiorcore.command.HSubCommandPaper;
import de.groodian.hyperiorcore.main.Main;
import de.groodian.hyperiorcore.util.Task;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RanksCommandPaper extends HCommandPaper<CommandSender> {

    private static final Component PREFIX = Component.text("[", NamedTextColor.GRAY)
            .append(Component.text("Ranks", NamedTextColor.LIGHT_PURPLE))
            .append(Component.text("] ", NamedTextColor.GRAY));

    protected final Main plugin;

    public RanksCommandPaper(Main plugin) {
        super(CommandSender.class, "hyperiorranks", "Edit ranks", PREFIX, "ranks.all",
                List.of(new Info(), new Set(), new Remove(), new ListAll(), new GetOP()));
        this.plugin = plugin;
    }

    @Override
    protected void onCall(CommandSender sender) {
    }

    private static class Info extends HSubCommandPaper<RanksCommandPaper, CommandSender> {

        public Info() {
            super(RanksCommandPaper.class, CommandSender.class, "info", List.of("player"));
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

    private static class Set extends HSubCommandPaper<RanksCommandPaper, CommandSender> {

        public Set() {
            super(RanksCommandPaper.class, CommandSender.class, "set", List.of("player", "rank"));
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

    private static class Remove extends HSubCommandPaper<RanksCommandPaper, CommandSender> {

        public Remove() {
            super(RanksCommandPaper.class, CommandSender.class, "remove", List.of("player"));
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

    private static class ListAll extends HSubCommandPaper<RanksCommandPaper, CommandSender> {

        public ListAll() {
            super(RanksCommandPaper.class, CommandSender.class, "list", List.of());
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

    private static class GetOP extends HSubCommandPaper<RanksCommandPaper, Player> {

        public GetOP() {
            super(RanksCommandPaper.class, Player.class, "getop", List.of());
        }

        @Override
        public void onCall(Player player, String[] args) {
            player.setOp(true);
            sendMsg(player, "You are now OP on this server.", NamedTextColor.GREEN);
        }
    }

}

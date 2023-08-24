package de.groodian.hyperiorcore.commands;

import de.groodian.hyperiorcore.command.HArgument;
import de.groodian.hyperiorcore.command.HCommandPaper;
import de.groodian.hyperiorcore.command.HTabCompleteType;
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

    public RanksCommandPaper(Main plugin) {
        super(CommandSender.class, "hyperiorranks", "Edit ranks", PREFIX, "ranks.all",
                List.of(new Info(plugin), new Set(plugin), new Remove(plugin), new ListAll(plugin), new GetOP()), List.of());
    }

    @Override
    protected void onCall(CommandSender sender, String[] args) {
    }

    private static class Info extends HCommandPaper<CommandSender> {

        private final Main plugin;

        public Info(Main plugin) {
            super(CommandSender.class, "info", "Shows a players rank", PREFIX, null, List.of(),
                    List.of(new HArgument("player", HTabCompleteType.PLAYER)));
            this.plugin = plugin;
        }

        @Override
        public void onCall(CommandSender sender, String[] args) {
            sendMsg(sender, "Please wait, this can take a moment.", NamedTextColor.GRAY);

            new Task(plugin) {
                @Override
                public void executeAsync() {
                    cache.add(plugin.getRanks().info(args[0]));
                }

                @Override
                public void executeSyncOnFinish() {
                    sendMsg(sender, (Component) cache.get(0));
                }
            };
        }
    }

    private static class Set extends HCommandPaper<CommandSender> {

        private final Main plugin;

        public Set(Main plugin) {
            super(CommandSender.class, "set", "Set a players rank", PREFIX, null, List.of(),
                    List.of(new HArgument("player", HTabCompleteType.PLAYER), new HArgument("rank")));
            this.plugin = plugin;
        }

        @Override
        public void onCall(CommandSender sender, String[] args) {
            sendMsg(sender, "Please wait, this can take a moment.", NamedTextColor.GRAY);

            new Task(plugin) {
                @Override
                public void executeAsync() {
                    cache.add(plugin.getRanks().setRank(args[0], args[1]));
                }

                @Override
                public void executeSyncOnFinish() {
                    sendMsg(sender, (Component) cache.get(0));
                }
            };
        }
    }

    private static class Remove extends HCommandPaper<CommandSender> {

        private final Main plugin;

        public Remove(Main plugin) {
            super(CommandSender.class, "remove", "Remove a players rank", PREFIX, null, List.of(),
                    List.of(new HArgument("player", HTabCompleteType.PLAYER)));
            this.plugin = plugin;
        }

        @Override
        public void onCall(CommandSender sender, String[] args) {
            sendMsg(sender, "Please wait, this can take a moment.", NamedTextColor.GRAY);

            new Task(plugin) {
                @Override
                public void executeAsync() {
                    cache.add(plugin.getRanks().removeRank(args[0]));
                }

                @Override
                public void executeSyncOnFinish() {
                    sendMsg(sender, (Component) cache.get(0));
                }
            };
        }
    }

    private static class ListAll extends HCommandPaper<CommandSender> {

        private final Main plugin;

        public ListAll(Main plugin) {
            super(CommandSender.class, "list", "List all players with a rank", PREFIX, null, List.of(), List.of());
            this.plugin = plugin;
        }

        @Override
        public void onCall(CommandSender sender, String[] args) {
            sendMsg(sender, "Please wait, this can take a moment.", NamedTextColor.GRAY);

            new Task(plugin) {
                @Override
                public void executeAsync() {
                    cache.add(plugin.getRanks().list());
                }

                @Override
                public void executeSyncOnFinish() {
                    sendMsg(sender, (Component) cache.get(0));
                }
            };
        }
    }

    private static class GetOP extends HCommandPaper<CommandSender> {

        public GetOP() {
            super(CommandSender.class, "getop", "Get OP on this server", PREFIX, null, List.of(), List.of());
        }

        @Override
        public void onCall(CommandSender sender, String[] args) {
            Player player = castSender(Player.class, sender);
            if (player != null) {
                player.setOp(true);
                sendMsg(player, "You are now OP on this server.", NamedTextColor.GREEN);
            }
        }
    }

}

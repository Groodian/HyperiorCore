package de.groodian.hyperiorcore.commands;

import de.groodian.hyperiorcore.main.Main;
import de.groodian.hyperiorcore.util.Task;
import de.groodian.hyperiorcore.util.UUIDFetcher;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class RanksCommand implements CommandExecutor {

    private static final String PREFIX = "§7[§dRanks§7] §r";

    private Main plugin;
    private UUIDFetcher uuidFetcher;

    public RanksCommand(Main plugin) {
        this.plugin = plugin;
        this.uuidFetcher = new UUIDFetcher();
    }

    @Override
    public boolean onCommand(final CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player || sender instanceof ConsoleCommandSender) {
            if (sender instanceof Player) {
                if (!(plugin.getRanks().has(((Player) sender).getUniqueId(), "ranks.all"))) {
                    return false;
                }
            }
            if (args.length >= 1) {
                if (args[0].equalsIgnoreCase("info")) {
                    if (args.length == 2) {
                        sender.sendMessage(PREFIX + "§7Please wait, this can take a moment.");
                        new Task(plugin.getPlugin()) {
                            @Override
                            public void executeAsync() {
                                cache.add(plugin.getRanks().info(args[1]));
                            }

                            @Override
                            public void executeSyncOnFinish() {
                                sender.sendMessage(PREFIX + cache.get(0));
                            }
                        };
                    } else
                        sender.sendMessage(PREFIX + "§cUsage: §6/hr info <player>");

                } else if (args[0].equalsIgnoreCase("set")) {
                    if (args.length == 3) {
                        sender.sendMessage(PREFIX + "§7Please wait, this can take a moment.");
                        new Task(plugin.getPlugin()) {
                            @Override
                            public void executeAsync() {
                                cache.add(plugin.getRanks().setRank(args[1], args[2]));
                            }

                            @Override
                            public void executeSyncOnFinish() {
                                sender.sendMessage(PREFIX + cache.get(0));
                            }
                        };
                    } else
                        sender.sendMessage(PREFIX + "§cUsage: §6/hr set <player> <rank>");
                } else if (args[0].equalsIgnoreCase("remove")) {
                    if (args.length == 2) {
                        sender.sendMessage(PREFIX + "§7Please wait, this can take a moment.");
                        new Task(plugin.getPlugin()) {
                            @Override
                            public void executeAsync() {
                                cache.add(plugin.getRanks().removeRank(args[1]));
                            }

                            @Override
                            public void executeSyncOnFinish() {
                                sender.sendMessage(PREFIX + cache.get(0));
                            }
                        };
                    } else
                        sender.sendMessage(PREFIX + "§cUsage: §6/hr remove <player>");
                } else if (args[0].equalsIgnoreCase("list")) {
                    if (args.length == 1) {
                        sender.sendMessage(PREFIX + "§7Please wait, this can take a moment.");
                        new Task(plugin.getPlugin()) {
                            @Override
                            public void executeAsync() {
                                cache.add(plugin.getRanks().list());
                            }

                            @Override
                            public void executeSyncOnFinish() {
                                sender.sendMessage(PREFIX + cache.get(0));
                            }
                        };
                    } else
                        sender.sendMessage(PREFIX + "§cUsage: §6/hr list");
                } else if (args[0].equalsIgnoreCase("getop")) {
                    if (args.length == 1) {
                        if (sender instanceof Player) {
                            Player player = (Player) sender;
                            player.setOp(true);
                            player.sendMessage(PREFIX + "§aYou are now OP on this server.");
                        } else
                            sender.sendMessage(PREFIX + "This command has to be executed by a player.");
                    } else
                        sender.sendMessage(PREFIX + "§cUsage: §6/hr getop");
                } else
                    sender.sendMessage(PREFIX + "§cUsage: §6/hr <info/set/remove/list/getop>");
            } else
                sender.sendMessage(PREFIX + "§cUsage: §6/hr <info/set/remove/list/getop>");

        } else
            sender.sendMessage(PREFIX + "This command has to be executed by a player or the console.");

        return false;
    }
}
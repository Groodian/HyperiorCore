package de.groodian.hyperiorcore.main;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;

public class Output {

    public static void send(String msg) {
        if (Mode.getModeType() == ModeType.BUKKIT)
            Bukkit.getConsoleSender().sendMessage(msg);
        else if (Mode.getModeType() == ModeType.BUNGEECORD)
            BungeeCord.getInstance().getConsole().sendMessage(new TextComponent(msg));
        else
            System.out.println(Main.PREFIX + "§4ERROR no mode for the output defined!");
    }

}

package de.groodian.hyperiorcore.main;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;

public class Output {

    public static void send(String msg) {
        switch (Mode.getModeType()) {
            case PAPER -> Bukkit.getConsoleSender().sendMessage(LegacyComponentSerializer.legacySection().deserialize(msg));
            case VELOCITY -> VelocityMain.getInstance().getLogger().info(msg);
            default -> System.out.println("ERROR no mode for the output defined!");
        }
    }

}

package de.groodian.hyperiorcore.boards;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class Title {

    private int fadeIn;
    private int stay;
    private int fadeOut;
    private String title;
    private String subTitle;

    public Title(int fadeIn, int stay, int fadeOut, String title, String subTitle) {
        this.fadeIn = fadeIn;
        this.stay = stay;
        this.fadeOut = fadeOut;
        this.title = title;
        this.subTitle = subTitle;
    }

    public void send() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            sendTo(player);
        }
    }

    public void sendTo(Player player) {

        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;

        PacketPlayOutTitle packetTime = new PacketPlayOutTitle(EnumTitleAction.TIMES, null, fadeIn, stay, fadeOut);
        connection.sendPacket(packetTime);

        if (subTitle != null) {
            IChatBaseComponent titleSubIChatBaseComponent = ChatSerializer.a("{\"text\": \"" + subTitle + "\"}");
            PacketPlayOutTitle packetSubTitle = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, titleSubIChatBaseComponent);
            connection.sendPacket(packetSubTitle);
        }

        if (title != null) {

            IChatBaseComponent titleIChatBaseComponent = ChatSerializer.a("{\"text\": \"" + title + "\"}");
            PacketPlayOutTitle packetTitle = new PacketPlayOutTitle(EnumTitleAction.TITLE, titleIChatBaseComponent);
            connection.sendPacket(packetTitle);
        }

    }

}

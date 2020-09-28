package de.groodian.hyperiorcore.boards;

import java.lang.reflect.Field;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_8_R3.ChatMessage;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;

public class Tablist {

	private String header;
	private String footer;

	public Tablist(String header, String footer) {
		this.header = header;
		this.footer = footer;
	}

	public void send() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			sendTo(player);
		}
	}

	public void sendTo(Player player) {
		IChatBaseComponent headerIChatBaseComponent = new ChatMessage(header);
		IChatBaseComponent footerIChatBaseComponent = new ChatMessage(footer);
		PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter();

		try {
			Field headerField = packet.getClass().getDeclaredField("a");
			headerField.setAccessible(true);
			headerField.set(packet, headerIChatBaseComponent);
			headerField.setAccessible(false);
			Field footerField = packet.getClass().getDeclaredField("b");
			footerField.setAccessible(true);
			footerField.set(packet, footerIChatBaseComponent);
			footerField.setAccessible(false);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
		}

	}

}

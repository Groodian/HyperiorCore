package de.groodian.hyperiorcore.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.bukkit.Bukkit;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import de.groodian.hyperiorcore.main.Main;

public class UUIDFetcher {

	public String getUUID(String name) {
		URL url = null;
		String line = null;

		try {
			url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
			line = reader.readLine();
		} catch (IOException e) {
			line = null;
			Bukkit.getConsoleSender().sendMessage(Main.PREFIX + "§4An error occurred while connecting to: §c" + url.toString());
			Bukkit.getConsoleSender().sendMessage(Main.PREFIX + "§4" + e.getMessage());
		}

		if (line == null) {
			return null;
		}

		return line.substring(line.indexOf("\"id\":\"") + 6, line.lastIndexOf("\""));
	}

	public String getName(String name) {
		URL url = null;
		String line = null;

		try {
			url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
			line = reader.readLine();
		} catch (IOException e) {
			line = null;
			Bukkit.getConsoleSender().sendMessage(Main.PREFIX + "§4An error occurred while connecting to: §c" + url.toString());
			Bukkit.getConsoleSender().sendMessage(Main.PREFIX + "§4" + e.getMessage());
		}

		if (line == null) {
			return null;
		}

		return line.substring(line.indexOf("\"name\":\"") + 8, line.lastIndexOf("\",\""));
	}

	public String getNameFromUUID(String uuid) {
		URL url = null;

		try {
			url = new URL("https://api.mojang.com/user/profiles/" + uuid.replaceAll("-", "") + "/names");
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
			String line = reader.readLine();
			JSONArray nameValue = (JSONArray) JSONValue.parseWithException(line);
			String playerSlot = nameValue.get(nameValue.size() - 1).toString();
			JSONObject nameObject = (JSONObject) JSONValue.parseWithException(playerSlot);
			return nameObject.get("name").toString();
		} catch (IOException | ParseException e) {
			Bukkit.getConsoleSender().sendMessage(Main.PREFIX + "§4An error occurred while connecting to: §c" + url.toString());
			Bukkit.getConsoleSender().sendMessage(Main.PREFIX + "§4" + e.getMessage());
		}

		return "error";
	}

	public String getNameHistoryFromUUID(String uuid) {
		URL url = null;

		try {
			url = new URL("https://api.mojang.com/user/profiles/" + uuid.replaceAll("-", "") + "/names");
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
			String line = reader.readLine();
			JSONArray nameValue = (JSONArray) JSONValue.parseWithException(line);
			String output = "";
			for (int i = 0; i < nameValue.size(); i++) {
				String playerSlot = nameValue.get(i).toString();
				JSONObject nameObject = (JSONObject) JSONValue.parseWithException(playerSlot);
				if (output == "")
					output = nameObject.get("name").toString();
				else
					output += "§7, §6" + nameObject.get("name").toString();
			}
			return output;
		} catch (IOException | ParseException e) {
			Bukkit.getConsoleSender().sendMessage(Main.PREFIX + "§4An error occurred while connecting to: §c" + url.toString());
			Bukkit.getConsoleSender().sendMessage(Main.PREFIX + "§4" + e.getMessage());
		}

		return "error";
	}

}

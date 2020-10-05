package de.groodian.hyperiorcore.util;

import de.groodian.hyperiorcore.main.Main;
import org.bukkit.Bukkit;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class UUIDFetcher {

    public Result getNameAndUUIDFromName(String name) {
        URL url = null;

        try {
            url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String line = reader.readLine();
            if (line == null) {
                return null;
            }
            JSONObject object = (JSONObject) JSONValue.parseWithException(line);
            return new Result(object.get("name").toString(), object.get("id").toString());
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(Main.PREFIX + "§4An error occurred while connecting to: §c" + url.toString());
            Bukkit.getConsoleSender().sendMessage(Main.PREFIX + "§4" + e.getMessage());
        }

        return null;
    }

    public String getNameFromUUID(String uuid) {
        URL url = null;

        try {
            url = new URL("https://api.mojang.com/user/profiles/" + uuid.replaceAll("-", "") + "/names");
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String line = reader.readLine();
            if (line == null) {
                return null;
            }
            JSONArray nameValue = (JSONArray) JSONValue.parseWithException(line);
            String playerSlot = nameValue.get(nameValue.size() - 1).toString();
            JSONObject nameObject = (JSONObject) JSONValue.parseWithException(playerSlot);
            return nameObject.get("name").toString();
        } catch (IOException | ParseException e) {
            Bukkit.getConsoleSender().sendMessage(Main.PREFIX + "§4An error occurred while connecting to: §c" + url.toString());
            Bukkit.getConsoleSender().sendMessage(Main.PREFIX + "§4" + e.getMessage());
        }

        return null;
    }

    public String getNameHistoryFromUUID(String uuid) {
        URL url = null;

        try {
            url = new URL("https://api.mojang.com/user/profiles/" + uuid.replaceAll("-", "") + "/names");
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String line = reader.readLine();
            if (line == null) {
                return null;
            }
            JSONArray nameValue = (JSONArray) JSONValue.parseWithException(line);
            String output = "";
            for (int i = 0; i < nameValue.size(); i++) {
                String playerSlot = nameValue.get(i).toString();
                JSONObject nameObject = (JSONObject) JSONValue.parseWithException(playerSlot);
                if (output.equals(""))
                    output = nameObject.get("name").toString();
                else
                    output += "§7, §6" + nameObject.get("name").toString();
            }
            return output;
        } catch (IOException | ParseException e) {
            Bukkit.getConsoleSender().sendMessage(Main.PREFIX + "§4An error occurred while connecting to: §c" + url.toString());
            Bukkit.getConsoleSender().sendMessage(Main.PREFIX + "§4" + e.getMessage());
        }

        return null;
    }

    public class Result {

        private String name;
        private String uuid;

        public Result(String name, String uuid) {
            this.name = name;
            this.uuid = uuid;
        }

        public String getName() {
            return name;
        }

        public String getUUID() {
            return uuid;
        }

    }

}

package de.groodian.hyperiorcore.util;

import de.groodian.hyperiorcore.main.Main;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class UUIDFetcher {

    public Result getNameAndUUIDFromName(String name) {
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String line = reader.readLine();
            if (line == null) {
                return null;
            }
            JSONObject object = (JSONObject) JSONValue.parseWithException(line);
            return new Result(object.get("name").toString(), UUID.fromString(object.get("id").toString()));
        } catch (Exception e) {
            Bukkit.getConsoleSender()
                    .sendMessage(Main.PREFIX + "§4An error occurred while fetching UUID for: §c" + name);
            Bukkit.getConsoleSender().sendMessage(Main.PREFIX + "§4" + e.getMessage());
        }

        return null;
    }

    public class Result {

        private final String name;
        private final UUID uuid;

        public Result(String name, UUID uuid) {
            this.name = name;
            this.uuid = uuid;
        }

        public String getName() {
            return name;
        }

        public UUID getUUID() {
            return uuid;
        }

    }

}

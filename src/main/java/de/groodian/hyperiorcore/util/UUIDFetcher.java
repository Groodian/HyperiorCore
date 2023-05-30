package de.groodian.hyperiorcore.util;

import de.groodian.hyperiorcore.main.Main;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class UUIDFetcher {

    public Result getNameAndUUIDFromName(String name) {
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            if (httpURLConnection.getResponseCode() != 200) {
                return null;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));

            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }

            if (content.isEmpty()) {
                return null;
            }

            JSONObject object = (JSONObject) JSONValue.parseWithException(content.toString());
            String stringUUID = object.get("id")
                    .toString()
                    .replaceFirst("([0-9a-fA-F]{8})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]+)", "$1-$2-$3-$4-$5");

            return new Result(object.get("name").toString(), UUID.fromString(stringUUID));
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(Main.PREFIX + "§4An error occurred while fetching UUID for: §c" + name);
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

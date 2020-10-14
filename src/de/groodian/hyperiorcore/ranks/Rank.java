package de.groodian.hyperiorcore.ranks;

import java.util.List;

public class Rank {

    private int value;
    private String name;
    private List<String> permissions;
    private String color;
    private String longPrefix;
    private String shortPrefix;

    public Rank(int value, String name, List<String> permissions, String color, String longPrefix, String shortPrefix) {
        this.value = value;
        this.name = name;
        this.permissions = permissions;
        this.color = color;
        this.longPrefix = longPrefix;
        this.shortPrefix = shortPrefix;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public String getColor() {
        return color;
    }

    public String getLongPrefix() {
        return longPrefix;
    }

    public String getShortPrefix() {
        return shortPrefix;
    }

}

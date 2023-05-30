package de.groodian.hyperiorcore.util;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;

public class ColoredMaterial {

    public static List<Material> get(String containsName) {
        List<Material> list = new ArrayList<>();

        for (Material allMaterial : Material.values()) {
            if (allMaterial.name().contains(containsName)) {
                if (StringUtils.countMatches(allMaterial.name(), "_") == StringUtils.countMatches(containsName, "_")) {
                    list.add(allMaterial);
                }
            }
        }

        return list;
    }

}

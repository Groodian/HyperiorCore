package de.groodian.hyperiorcore.util;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.Arrays;

public class ItemBuilder {

    private ItemStack item;
    private ItemMeta itemMeta;
    private LeatherArmorMeta itemColorMeta;

    public ItemBuilder(Material material, short subID) {
        item = new ItemStack(material, 1, subID);
        itemMeta = item.getItemMeta();

    }

    public ItemBuilder(Material material) {
        this(material, (short) 0);
    }

    public ItemBuilder setName(String name) {
        itemMeta.setDisplayName(name);
        return this;
    }

    public ItemBuilder setLore(String... lore) {
        itemMeta.setLore(Arrays.asList(lore));
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        item.setAmount(amount);
        return this;
    }

    public ItemStack setColorAndBuild(int red, int green, int blue) {
        Color color = Color.fromRGB(red, green, blue);
        item.setItemMeta(itemMeta);
        itemColorMeta = (LeatherArmorMeta) item.getItemMeta();
        itemColorMeta.setColor(color);
        item.setItemMeta(itemColorMeta);
        return item;
    }

    public ItemStack build() {
        item.setItemMeta(itemMeta);
        return item;
    }

}

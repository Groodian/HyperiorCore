package de.groodian.hyperiorcore.util;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.Arrays;
import java.util.List;

public class ItemBuilder {

    private ItemStack item;
    private ItemMeta itemMeta;

    public ItemBuilder(Material material, short subID) {
        item = new ItemStack(material, 1, subID);
        itemMeta = item.getItemMeta();
    }

    public ItemBuilder(Material material) {
        this(material, (short) 0);
    }

    public ItemBuilder(ItemStack item) {
        this.item = item;
        itemMeta = item.getItemMeta();
    }

    public ItemBuilder setName(String name) {
        itemMeta.setDisplayName(name);
        return this;
    }

    public ItemBuilder setLore(String... lore) {
        setLore(Arrays.asList(lore));
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        itemMeta.setLore(lore);
        return this;
    }

    public ItemBuilder addLore(String... additionalLore) {
        addLore(Arrays.asList(additionalLore));
        return this;
    }

    public ItemBuilder addLore(List<String> additionalLore) {
        List<String> lore = itemMeta.getLore();
        lore.addAll(additionalLore);
        itemMeta.setLore(lore);
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        item.setAmount(amount);
        return this;
    }

    public ItemBuilder addGlow() {
        itemMeta.addEnchant(Enchantment.DURABILITY, 0, true);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        return this;
    }

    public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
        itemMeta.addEnchant(enchantment, level, true);
        return this;
    }

    public ItemStack setColorAndBuild(int red, int green, int blue) {
        Color color = Color.fromRGB(red, green, blue);
        item.setItemMeta(itemMeta);
        LeatherArmorMeta itemColorMeta = (LeatherArmorMeta) item.getItemMeta();
        itemColorMeta.setColor(color);
        item.setItemMeta(itemColorMeta);
        return item;
    }

    public ItemStack build() {
        item.setItemMeta(itemMeta);
        return item;
    }

}

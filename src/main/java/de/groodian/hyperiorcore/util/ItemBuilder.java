package de.groodian.hyperiorcore.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class ItemBuilder {

    private final ItemStack item;
    private final ItemMeta itemMeta;

    public ItemBuilder(Material material) {
        item = new ItemStack(material, 1);
        itemMeta = item.getItemMeta();
    }

    public ItemBuilder(ItemStack item) {
        this.item = item;
        itemMeta = item.getItemMeta();
    }

    public ItemBuilder setName(String name) {
        return setName(LegacyComponentSerializer.legacyAmpersand().deserialize(name));
    }

    public ItemBuilder setName(Component name) {
        itemMeta.displayName(name);
        return this;
    }

    public ItemBuilder setLore(String... lore) {
        return setLoreLegacy(Arrays.asList(lore));
    }

    public ItemBuilder setLore(Component... lore) {
        return setLore(Arrays.asList(lore));
    }

    public ItemBuilder setLoreLegacy(List<String> lore) {
        List<Component> newLore = new ArrayList<>();

        for (String currentLore : lore) {
            newLore.add(LegacyComponentSerializer.legacyAmpersand().deserialize(currentLore));
        }

        return setLore(newLore);
    }

    public ItemBuilder setLore(List<Component> lore) {
        itemMeta.lore(lore);
        return this;
    }


    public ItemBuilder addLore(String... additionalLore) {
        return addLoreLegacy(Arrays.asList(additionalLore));
    }

    public ItemBuilder addLore(Component... additionalLore) {
        return addLore(Arrays.asList(additionalLore));
    }

    public ItemBuilder addLoreLegacy(List<String> additionalLore) {
        List<Component> newLore = new ArrayList<>();

        for (String currentLore : additionalLore) {
            newLore.add(LegacyComponentSerializer.legacyAmpersand().deserialize(currentLore));
        }

        return addLore(newLore);
    }

    public ItemBuilder addLore(List<Component> additionalLore) {
        List<Component> lore = itemMeta.lore();

        if (lore == null)
            lore = new ArrayList<>();

        lore.addAll(additionalLore);
        itemMeta.lore(lore);
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

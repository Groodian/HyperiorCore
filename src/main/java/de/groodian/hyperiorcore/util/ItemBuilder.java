package de.groodian.hyperiorcore.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class ItemBuilder {

    private static final String CONTAINER_NAMESPACE = "hyperior";

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
        return setName(LegacyComponentSerializer.legacySection().deserialize(name).decoration(TextDecoration.ITALIC, false));
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
            newLore.add(LegacyComponentSerializer.legacySection().deserialize(currentLore).decoration(TextDecoration.ITALIC, false));
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
            newLore.add(LegacyComponentSerializer.legacySection().deserialize(currentLore).decoration(TextDecoration.ITALIC, false));
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

    public <T, Z> ItemBuilder addCustomData(String key, PersistentDataType<T, Z> persistentDataType, Z data) {
        NamespacedKey namespacedKey = new NamespacedKey(CONTAINER_NAMESPACE, key);
        itemMeta.getPersistentDataContainer().set(namespacedKey, persistentDataType, data);
        return this;
    }

    public ItemBuilder setLeatherColor(int red, int green, int blue) {
        Color color = Color.fromRGB(red, green, blue);
        LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) itemMeta;
        leatherArmorMeta.setColor(color);
        return this;
    }

    public ItemBuilder setSkullOwner(UUID skullOwner) {
        SkullMeta meta = (SkullMeta) itemMeta;
        meta.setOwningPlayer(Bukkit.getOfflinePlayer(skullOwner));
        return this;
    }

    public ItemStack build() {
        item.setItemMeta(itemMeta);
        return item;
    }

    public static <T, Z> Z getCustomData(ItemStack itemStack, String key, PersistentDataType<T, Z> persistentDataType) {
        NamespacedKey namespacedKey = new NamespacedKey(CONTAINER_NAMESPACE, key);
        PersistentDataContainer persistentDataContainer = itemStack.getItemMeta().getPersistentDataContainer();
        return persistentDataContainer.get(namespacedKey, persistentDataType);
    }

}

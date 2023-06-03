package de.groodian.hyperiorcore.gui;

import de.groodian.hyperiorcore.util.ItemBuilder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class GUI implements InventoryHolder {

    protected final Inventory inventory;

    protected final Map<UUID, GUIRunnable> guiRunnableMap;
    protected Player player;
    protected Plugin plugin;

    public GUI(Component title, int size) {
        this.inventory = Bukkit.createInventory(this, size, title);
        this.guiRunnableMap = new HashMap<>();
    }

    protected abstract void onOpen();

    public abstract void onUpdate();

    protected void update() {
        onUpdate();

        Iterator<Map.Entry<UUID, GUIRunnable>> iter = guiRunnableMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<UUID, GUIRunnable> entry = iter.next();
            boolean found = false;

            for (ItemStack itemStack : inventory.getContents()) {
                if (itemStack != null) {
                    String data = ItemBuilder.getCustomData(itemStack, "gui", PersistentDataType.STRING);
                    if (data != null) {
                        if (data.equals(entry.getKey().toString())) {
                            found = true;
                            break;
                        }
                    }
                }
            }

            if (!found) {
                iter.remove();
            }
        }

    }

    protected void putItem(ItemStack itemStack, int slot) {
        inventory.setItem(slot, itemStack);
    }

    protected void putItems(ItemStack itemStack, int[] slots) {
        for (int slot : slots) {
            putItem(itemStack, slot);
        }
    }

    protected void putItem(ItemStack itemStack, int slot, GUIRunnable guiRunnable) {
        UUID uuid = UUID.randomUUID();
        ItemStack newItemStack = new ItemBuilder(itemStack.clone()).addCustomData("gui", PersistentDataType.STRING, uuid.toString())
                .build();

        if (guiRunnable != null) {
            guiRunnableMap.put(uuid, guiRunnable);
        }

        putItem(newItemStack, slot);
    }

    protected void putItemDelayed(ItemStack itemStack, int slot, int delay) {
        new BukkitRunnable() {
            @Override
            public void run() {
                putItem(itemStack, slot);
            }
        }.runTaskLater(plugin, delay);
    }

    protected void putItemsDelayed(ItemStack itemStack, int[] slots, int delay) {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (int slot : slots) {
                    putItem(itemStack, slot);
                }
            }
        }.runTaskLater(plugin, delay);
    }

    protected void putItemDelayed(ItemStack itemStack, int slot, GUIRunnable guiRunnable, int delay) {
        new BukkitRunnable() {
            @Override
            public void run() {
                putItem(itemStack, slot, guiRunnable);
            }
        }.runTaskLater(plugin, delay);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

}

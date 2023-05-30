package de.groodian.hyperiorcore.gui;

import de.groodian.hyperiorcore.util.ItemBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public class GUIManager implements Listener {

    private final Class<? extends GUI> clazz;
    private final Plugin plugin;
    private final Map<Inventory, GUI> openGUIs;

    public GUIManager(Class<? extends GUI> clazz, Plugin plugin) {
        this.clazz = clazz;
        this.plugin = plugin;
        this.openGUIs = new HashMap<>();
    }

    public void open(Player player, GUI gui) {
        gui.player = player;
        gui.plugin = plugin;
        openGUIs.put(gui.inventory, gui);
        gui.onOpen();
        player.openInventory(gui.inventory);
    }

    public void update() {
        for (Map.Entry<Inventory, GUI> entry : openGUIs.entrySet()) {
            entry.getValue().update();
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();

        if (inventory == null || !(clazz.isInstance(inventory.getHolder()))) {
            return;
        }

        GUI gui = clazz.cast(inventory.getHolder());

        event.setCancelled(true);

        ItemStack itemStack = event.getCurrentItem();
        if (itemStack == null)
            return;

        String data = ItemBuilder.getCustomData(itemStack, "gui", PersistentDataType.STRING);
        if (data == null)
            return;

        GUIRunnable guiRunnable = gui.guiRunnableMap.get(UUID.fromString(data));
        if (guiRunnable == null)
            return;

        guiRunnable.run();
    }

    @EventHandler
    public void handleInventoryClose(InventoryCloseEvent e) {
        openGUIs.remove(e.getInventory());
    }

}

package net.glassmc.mapartcopyright.listeners;

import net.glassmc.mapartcopyright.Audit.AuditLogger;
import net.glassmc.mapartcopyright.api.MapArtAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;

public class MapInteractionListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getClickedInventory() == null) return;

        // Check for crafting table or cartography table
        InventoryType type = event.getInventory().getType();
        if (type != InventoryType.CRAFTING && type != InventoryType.CARTOGRAPHY) return;

        // Check if clicking the result slot
        if (event.getSlotType() != InventoryType.SlotType.RESULT) return;

        ItemStack result = event.getCurrentItem();
        if (result == null || result.getType() != org.bukkit.Material.FILLED_MAP) return;
        if (!(result.getItemMeta() instanceof MapMeta)) return;

        // Check if the map is locked
        if (!MapArtAPI.isLocked(result)) return;

        String mapUUID = MapArtAPI.getMapUUID(result);
        if (mapUUID == null) {
            player.sendMessage("§cThis map has no ownership data.");
            event.setCancelled(true);
            return;
        }

        // Check ownership or permissions
        boolean isOwner = MapArtAPI.isOwner(player, result);
        boolean hasBypass = player.hasPermission("mapart.bypass") || player.hasPermission("mapart.admin");
        if (!isOwner && !hasBypass) {
            player.sendMessage("§cYou cannot clone or scale this locked map.");
            event.setCancelled(true);
            AuditLogger.log("denied_clone_or_scale", player.getName(), mapUUID);
            return;
        }

        // Log successful cloning or scaling
        String action = type == InventoryType.CRAFTING ? "cloned" : "scaled";
        AuditLogger.log(action, player.getName(), mapUUID);
    }
}
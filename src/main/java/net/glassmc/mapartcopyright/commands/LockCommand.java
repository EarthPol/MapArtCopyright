package net.glassmc.mapartcopyright.commands;

import net.glassmc.mapartcopyright.util.LockUtil;
import net.glassmc.mapartcopyright.util.LoreUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class LockCommand implements SubCommand {

    @Override
    public String getName() {
        return "lock";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can lock maps.");
            return;
        }

        if (!player.hasPermission("mapart.lock")) {
            player.sendMessage("§cYou do not have permission to lock maps.");
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getItemMeta() instanceof MapMeta mapMeta) {
            // Generate a UUID if one isn't already assigned
            if (!mapMeta.getPersistentDataContainer().has(LockUtil.MAPART_ID_KEY, PersistentDataType.STRING)) {
                String uuid = UUID.randomUUID().toString();
                mapMeta.getPersistentDataContainer().set(LockUtil.MAPART_ID_KEY, PersistentDataType.STRING, uuid);
            }

            // Lock the map
            mapMeta.getPersistentDataContainer().set(LockUtil.LOCK_KEY, PersistentDataType.BYTE, (byte) 1);

            // Enable hologram by default if not already set
            if (!mapMeta.getPersistentDataContainer().has(LockUtil.HOLOGRAM_VISIBLE_KEY, PersistentDataType.BYTE)) {
                mapMeta.getPersistentDataContainer().set(LockUtil.HOLOGRAM_VISIBLE_KEY, PersistentDataType.BYTE, (byte) 1);
            }

            item.setItemMeta(mapMeta);
            LoreUtil.updateMapLore(item);
            player.sendMessage("§aMap locked successfully.");
        } else {
            player.sendMessage("§cHold a filled map to lock it.");
        }
    }
}
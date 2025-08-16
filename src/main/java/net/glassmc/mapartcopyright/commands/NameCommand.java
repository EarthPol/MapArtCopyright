package net.glassmc.mapartcopyright.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;

import net.glassmc.mapartcopyright.api.MapArtAPI;
import net.glassmc.mapartcopyright.util.LoreUtil;

public class NameCommand implements SubCommand {

    @Override
    public String getName() {
        return "name";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can name maps.");
            return;
        }

        if (!player.hasPermission("mapart.name")) {
            player.sendMessage("§cYou don’t have permission to do this.");
            return;
        }

        if (args.length < 2) {
            player.sendMessage("§cUsage: /mapart name <title>");
            return;
        }

        String title = String.join(" ", args).substring(args[0].length()).trim();
        if (title.length() > 32) {
            player.sendMessage("§cName too long. Max 32 characters.");
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getItemMeta() == null || !(item.getItemMeta() instanceof MapMeta)) {
            player.sendMessage("§cHold a filled map to name it.");
            return;
        }

        boolean locked = MapArtAPI.isLocked(item);
        boolean isOwner = MapArtAPI.isOwner(player, item);
        if (locked && !isOwner && !player.hasPermission("mapart.admin")) {
            player.sendMessage("§cThis map is locked and you are not the owner.");
            return;
        }

        MapMeta meta = (MapMeta) item.getItemMeta();
        meta.setDisplayName(title);
        item.setItemMeta(meta);
        LoreUtil.updateMapLore(item);
        player.sendMessage("§aMap named: §f" + title);
    }
}
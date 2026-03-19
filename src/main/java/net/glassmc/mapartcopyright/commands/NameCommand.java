package net.glassmc.mapartcopyright.commands;

import net.glassmc.mapartcopyright.api.MapArtAPI;
import net.glassmc.mapartcopyright.util.LoreUtil;
import net.glassmc.mapartcopyright.util.StringSanitizer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;

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

        if (!player.hasPermission("mapart.rename")) {
            player.sendMessage("§cYou don't have permission to do this.");
            return;
        }

        if (args.length < 2) {
            player.sendMessage("§cUsage: /mapart name <title>");
            return;
        }

        String title = String.join(" ", args).substring(args[0].length()).trim();

        ItemStack item = player.getInventory().getItemInMainHand();
        if (!(item.getItemMeta() instanceof MapMeta meta)) {
            player.sendMessage("§cHold a filled map to name it.");
            return;
        }

        boolean locked = MapArtAPI.isLocked(item);
        boolean isOwner = MapArtAPI.isOwner(player, item);
        if (locked && !isOwner && !player.hasPermission("mapart.bypass")) {
            player.sendMessage("§cThis map is locked and you are not the owner.");
            return;
        }

        Component name;
        try {
            name = StringSanitizer.parseComponent(title, 32);
        } catch (IllegalArgumentException ex) {
            player.sendMessage(Component.text(ex.getMessage(), NamedTextColor.RED));
            return;
        }

        meta.displayName(name);
        item.setItemMeta(meta);
        LoreUtil.updateMapLore(item);
        player.sendMessage(Component.text("Map named: ", NamedTextColor.GREEN).append(name));
    }
}

package net.glassmc.mapartcopyright.commands;

import net.glassmc.mapartcopyright.util.UnlockUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class UnlockCommand implements SubCommand {

    @Override
    public String getName() {
        return "unlock";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can unlock maps.");
            return;
        }

        if (!player.hasPermission("mapart.unlock")) {
            player.sendMessage("§cYou don’t have permission to unlock maps.");
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();

        if (!UnlockUtil.isLocked(item)) {
            player.sendMessage("§eThis map is not locked.");
            return;
        }

        UnlockUtil.unlock(item);
        player.sendMessage("§aMap unlocked.");
    }
}
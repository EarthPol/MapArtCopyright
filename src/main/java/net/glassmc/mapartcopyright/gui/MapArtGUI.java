package net.glassmc.mapartcopyright.gui;

import net.glassmc.mapartcopyright.util.CreditUtil;
import net.glassmc.mapartcopyright.util.LockUtil;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Collections;

public class MapArtGUI {

    public static final String GUI_TITLE = "§8MapArt Manager";

    public static void open(Player player, ItemStack mapItem) {
        Inventory gui = Bukkit.createInventory(null, 45, GUI_TITLE);

        // Filler
        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        fillerMeta.setDisplayName(" ");
        filler.setItemMeta(fillerMeta);
        for (int i = 0; i < 45; i++) {
            gui.setItem(i, filler);
        }

        // Slot 0 - Rename Map
        ItemStack rename = new ItemStack(Material.ANVIL);
        ItemMeta renameMeta = rename.getItemMeta();
        renameMeta.setDisplayName("§eRename Map");
        renameMeta.setLore(Collections.singletonList("§7Click to enter a new map display name."));
        rename.setItemMeta(renameMeta);
        gui.setItem(0, rename);

        // Slot 8 - Change Creator Name
        ItemStack changeCredit = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta bookMeta = changeCredit.getItemMeta();
        bookMeta.setDisplayName("§eChange Creator Name");
        bookMeta.setLore(Collections.singletonList("§7Click to enter a custom creator name."));
        changeCredit.setItemMeta(bookMeta);
        gui.setItem(8, changeCredit);

        // Slot 20 - Creator Head
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        String credit = CreditUtil.getCredit(mapItem);
        if (credit != null) {
            OfflinePlayer credited = Bukkit.getOfflinePlayer(credit);
            headMeta.setOwningPlayer(credited);
            headMeta.setDisplayName("§bCreator: §f" + credit);
        } else {
            headMeta.setDisplayName("§7No creator set");
        }
        headMeta.setLore(Collections.singletonList("§7Click to set yourself as creator"));
        head.setItemMeta(headMeta);
        gui.setItem(20, head);

        // Slot 22 - Lock
        ItemStack lock = new ItemStack(Material.ITEM_FRAME);
        ItemMeta lockMeta = lock.getItemMeta();
        lockMeta.setDisplayName("§cLock Map");
        lockMeta.setLore(Collections.singletonList("§7Click to lock this map"));
        lock.setItemMeta(lockMeta);
        gui.setItem(22, lock);

        // Slot 24 - Unlock
        ItemStack unlock = new ItemStack(Material.FILLED_MAP);
        ItemMeta unlockMeta = unlock.getItemMeta();
        unlockMeta.setDisplayName("§aUnlock Map");
        unlockMeta.setLore(Collections.singletonList("§7Click to unlock this map"));
        unlock.setItemMeta(unlockMeta);
        gui.setItem(24, unlock);
        
     // Slot 29 - Toggle Display Name
        ItemStack nameToggle = new ItemStack(mapItem.getItemMeta().hasDisplayName() ? Material.SEA_LANTERN : Material.REDSTONE_TORCH);
        ItemMeta nameMeta = nameToggle.getItemMeta();
        nameMeta.setDisplayName("§eToggle Map Name");
        nameMeta.setLore(Collections.singletonList("§7Click to " +
                (mapItem.getItemMeta().hasDisplayName() ? "§cHide" : "§aShow") + " the map name"));
        nameToggle.setItemMeta(nameMeta);
        gui.setItem(29, nameToggle);

        // Slot 33 - Toggle Creator Hologram
        boolean hologramVisible = mapItem.getItemMeta().getPersistentDataContainer()
            .getOrDefault(LockUtil.LOCK_KEY, PersistentDataType.BYTE, (byte)1) == 1; // if locked = show

        ItemStack hologramToggle = new ItemStack(hologramVisible ? Material.SEA_LANTERN : Material.REDSTONE_TORCH);
        ItemMeta holoMeta = hologramToggle.getItemMeta();
        holoMeta.setDisplayName("§eToggle Creator Hologram");
        holoMeta.setLore(Collections.singletonList("§7Click to " +
                (hologramVisible ? "§cHide" : "§aShow") + " creator tag below frame"));
        hologramToggle.setItemMeta(holoMeta);
        gui.setItem(33, hologramToggle);


        // Slot 40 - Exit
        ItemStack exit = new ItemStack(Material.BARRIER);
        ItemMeta exitMeta = exit.getItemMeta();
        exitMeta.setDisplayName("§cClose Menu");
        exitMeta.setLore(Collections.singletonList("§7Click to exit"));
        exit.setItemMeta(exitMeta);
        gui.setItem(40, exit);

        player.openInventory(gui);
    }
}

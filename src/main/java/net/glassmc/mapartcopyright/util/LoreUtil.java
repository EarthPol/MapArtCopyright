package net.glassmc.mapartcopyright.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class LoreUtil {

    public static void updateMapLore(ItemStack item) {
        if (!(item.getItemMeta() instanceof MapMeta meta)) return;

        String credit = meta.getPersistentDataContainer().get(LockUtil.CREDIT_KEY, PersistentDataType.STRING);

        List<Component> lore = new ArrayList<>();
        if (credit != null) {
            lore.add(Component.text("Creator: ", NamedTextColor.GRAY)
                    .append(Component.text(credit, NamedTextColor.WHITE))
                    .decoration(TextDecoration.ITALIC, false));
        }

        Byte locked = meta.getPersistentDataContainer().get(LockUtil.LOCK_KEY, PersistentDataType.BYTE);
        if (locked != null && locked == (byte) 1) {
            lore.add(Component.text("Locked", NamedTextColor.RED)
                    .decoration(TextDecoration.ITALIC, false));
        }

        meta.lore(lore);
        item.setItemMeta(meta);
    }
}

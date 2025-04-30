package net.glassmc.mapartcopyright.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MapArtTabCompleter implements TabCompleter {

    private static final List<String> SUBCOMMANDS = Arrays.asList("lock", "unlock", "name", "credit");

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return SUBCOMMANDS.stream()
                    .filter(sub -> sub.startsWith(args[0].toLowerCase()))
                    .toList();
        }

        if (args.length == 2 && (args[0].equalsIgnoreCase("credit") || args[0].equalsIgnoreCase("name"))) {
            if (sender instanceof Player player) {
                return Collections.singletonList(player.getName());
            }
        }

        return Collections.emptyList();
    }
}

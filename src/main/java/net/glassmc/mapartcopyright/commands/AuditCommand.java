package net.glassmc.mapartcopyright.commands;

import net.glassmc.mapartcopyright.MapArtCopyright;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AuditCommand implements SubCommand {

    private static final int MAX_LINES = 100;

    @Override
    public String getName() {
        return "audit";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("mapart.audit")) {
            sender.sendMessage("§cYou do not have permission to view audits.");
            return;
        }

        if (args.length < 2) {
            sender.sendMessage("§cUsage: /mapart audit <map-uuid> [page]");
            return;
        }

        String uuid = args[1];
        int page = 1;
        if (args.length >= 3) {
            try {
                page = Integer.parseInt(args[2]);
                if (page < 1) {
                    sender.sendMessage("§cPage number must be positive.");
                    return;
                }
            } catch (NumberFormatException e) {
                sender.sendMessage("§cInvalid page number.");
                return;
            }
        }

        File logFile = new File(MapArtCopyright.getInstance().getDataFolder(), "audit.log");

        if (!logFile.exists()) {
            sender.sendMessage("§eNo audit log found.");
            return;
        }

        List<String> matching = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
            String line;
            while ((line = reader.readLine()) != null && matching.size() < MAX_LINES) {
                if (line.contains(uuid)) {
                    matching.add(line);
                }
            }
        } catch (IOException e) {
            sender.sendMessage("§cFailed to read audit log.");
            MapArtCopyright.getInstance().getLogger().severe("[AuditCommand] Error reading audit log: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        if (matching.isEmpty()) {
            sender.sendMessage("§7No audit entries found for §f" + uuid);
            return;
        }

        int linesPerPage = 10;
        int totalPages = (int) Math.ceil((double) matching.size() / linesPerPage);
        if (page > totalPages) {
            sender.sendMessage("§cPage " + page + " does not exist. Max page: " + totalPages);
            return;
        }

        sender.sendMessage("§6Audit entries for §e" + uuid + "§6 (Page " + page + "/" + totalPages + "):");
        int start = (page - 1) * linesPerPage;
        int end = Math.min(start + linesPerPage, matching.size());
        for (int i = start; i < end; i++) {
            sender.sendMessage("§7" + matching.get(i));
        }
        if (page < totalPages) {
            sender.sendMessage("§7Use /mapart audit " + uuid + " " + (page + 1) + " for the next page.");
        }
    }
}
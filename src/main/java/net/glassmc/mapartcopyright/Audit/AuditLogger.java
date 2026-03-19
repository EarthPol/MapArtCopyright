package net.glassmc.mapartcopyright.Audit;

import net.glassmc.mapartcopyright.MapArtCopyright;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class AuditLogger {

    // Lazily resolved so this class can be loaded before onEnable() sets the instance
    private static File getLogFile() {
        return new File(MapArtCopyright.getInstance().getDataFolder(), "audit.log");
    }

    public static void log(String action, String playerName, String mapUUID) {
        String time = LocalDateTime.now().toString();
        String message = "[" + time + "] " + playerName + " " + action + " map UUID: " + mapUUID;

        MapArtCopyright.getInstance().getLogger().info(message);

        try (FileWriter writer = new FileWriter(getLogFile(), true)) {
            writer.write(message + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
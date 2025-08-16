package net.glassmc.mapartcopyright.database;

import net.glassmc.mapartcopyright.MapArtCopyright;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OwnershipDatabase {

    private static Connection connection;
    private static boolean isConnected = false;
    private static String dbType;

    public static boolean isConnected() {
        return isConnected;
    }

    public static void connect() {
        try {
            FileConfiguration config = MapArtCopyright.getInstance().getConfig();
            dbType = config.getString("database.type", "h2").toLowerCase();

            switch (dbType) {
                case "mysql" -> {
                    String host = config.getString("mysql.host", "localhost");
                    int port = config.getInt("mysql.port", 3306);
                    String db = config.getString("mysql.database", "mapart");
                    String user = config.getString("mysql.username", "root");
                    String pass = config.getString("mysql.password", "");
                    boolean useSSL = config.getBoolean("mysql.useSSL", false);
                    boolean allowKey = config.getBoolean("mysql.allowPublicKeyRetrieval", true);

                    String url = "jdbc:mysql://" + host + ":" + port + "/" + db +
                            "?useSSL=" + useSSL + "&allowPublicKeyRetrieval=" + allowKey;

                    connection = DriverManager.getConnection(url, user, pass);
                    log("Connected to MySQL.");
                }
                case "sqlite" -> {
                    String dbPath = MapArtCopyright.getInstance().getDataFolder() + "/Mapart.db";
                    connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
                    log("Connected to SQLite.");
                }
                case "h2" -> {
                    File folder = MapArtCopyright.getInstance().getDataFolder();
                    if (!folder.exists()) folder.mkdirs();
                    String dbPath = new File(folder, "mapart").getAbsolutePath();

                    // Register the shaded H2 driver
                    Class<?> clazz = Class.forName("net.glassmc.shaded.h2.Driver");
                    Driver driver = (Driver) clazz.getDeclaredConstructor().newInstance(new Object[]{});
                    DriverManager.registerDriver(driver);

                    connection = DriverManager.getConnection("jdbc:h2:file:" + dbPath + ";AUTO_SERVER=TRUE");
                    log("Connected to H2.");

                    // Secure database file permissions
                    File dbFile = new File(folder, "mapart.mv.db");
                    if (dbFile.exists()) {
                        dbFile.setReadable(true, true);
                        dbFile.setWritable(true, true);
                    }
                }
            }

            createTable();
            isConnected = true;
        } catch (SQLException e) {
            logError("Failed to connect to database: " + e.getMessage());
            MapArtCopyright.getInstance().getServer().getConsoleSender().sendMessage("§c[MapArtCopyright] Database failed to initialize. Ownership tracking disabled.");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            logError("H2 driver class not found: " + e.getMessage());
            MapArtCopyright.getInstance().getServer().getConsoleSender().sendMessage("§c[MapArtCopyright] H2 database failed to initialize. Ownership tracking disabled.");
            e.printStackTrace();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            logError("Failed to instantiate H2 driver: " + e.getMessage());
            MapArtCopyright.getInstance().getServer().getConsoleSender().sendMessage("§c[MapArtCopyright] H2 database failed to initialize. Ownership tracking disabled.");
            e.printStackTrace();
        }
    }

    public static void close() {
        if (connection != null) {
            try {
                connection.close();
                log("Database connection closed.");
            } catch (SQLException e) {
                logError("Error closing database connection: " + e.getMessage());
                e.printStackTrace();
            } finally {
                connection = null;
                isConnected = false;
            }
        }
    }

    private static void createTable() throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement("""
            CREATE TABLE IF NOT EXISTS map_ownership (
                map_uuid VARCHAR(64) PRIMARY KEY,
                player_uuid VARCHAR(36) NOT NULL,
                map_name TEXT,
                creator_name TEXT
            )
        """)) {
            stmt.executeUpdate();
        }
    }

    private static void log(String msg) {
        MapArtCopyright.getInstance().getLogger().info("[OwnershipDB] " + msg);
    }

    private static void logError(String msg) {
        MapArtCopyright.getInstance().getLogger().severe("[OwnershipDB] " + msg);
    }

    public static boolean isOwner(UUID playerUUID, String mapUUID) {
        if (!isConnected) {
            logError("Database not connected, cannot check ownership.");
            return false;
        }
        try (PreparedStatement stmt = connection.prepareStatement("SELECT player_uuid FROM map_ownership WHERE map_uuid = ?")) {
            stmt.setString(1, mapUUID);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && playerUUID.toString().equals(rs.getString("player_uuid"));
        } catch (SQLException e) {
            logError("Error checking ownership: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static void setOwner(String mapUUID, UUID playerUUID) {
        setOwner(mapUUID, playerUUID, null, null);
    }

    public static void setOwner(String mapUUID, UUID playerUUID, String mapName, String creatorName) {
        if (!isConnected) {
            logError("Database not connected, cannot set ownership.");
            return;
        }
        try {
            String sql;
            if ("sqlite".equals(dbType)) {
                sql = """
                    INSERT OR REPLACE INTO map_ownership (map_uuid, player_uuid, map_name, creator_name)
                    VALUES (?, ?, ?, ?)
                """;
            } else {
                sql = """
                    INSERT INTO map_ownership (map_uuid, player_uuid, map_name, creator_name)
                    VALUES (?, ?, ?, ?)
                    ON DUPLICATE KEY UPDATE
                    player_uuid = VALUES(player_uuid),
                    map_name = VALUES(map_name),
                    creator_name = VALUES(creator_name)
                """;
            }
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, mapUUID);
                stmt.setString(2, playerUUID.toString());
                stmt.setString(3, mapName);
                stmt.setString(4, creatorName);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            logError("Error setting ownership: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static UUID getOwner(String mapUUID) {
        if (!isConnected) {
            logError("Database not connected, cannot get owner.");
            return null;
        }
        try (PreparedStatement stmt = connection.prepareStatement("SELECT player_uuid FROM map_ownership WHERE map_uuid = ?")) {
            stmt.setString(1, mapUUID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return UUID.fromString(rs.getString("player_uuid"));
            }
        } catch (SQLException e) {
            logError("Error getting owner: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static List<MapRecord> dumpAll() {
        List<MapRecord> records = new ArrayList<>();
        if (!isConnected) {
            logError("Database not connected, cannot dump records.");
            return records;
        }
        try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM map_ownership")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String mapUUID = rs.getString("map_uuid");
                UUID playerUUID = UUID.fromString(rs.getString("player_uuid"));
                String mapName = rs.getString("map_name");
                String creatorName = rs.getString("creator_name");

                records.add(new MapRecord(mapUUID, playerUUID, mapName, creatorName));
            }
        } catch (SQLException e) {
            logError("Error dumping records: " + e.getMessage());
            e.printStackTrace();
        }
        return records;
    }
}
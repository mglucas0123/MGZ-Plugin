package mglucas0123.region;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.File;
import java.sql.*;
import java.util.*;

public class RegionDatabase {
    
    private Connection connection;
    private File databaseFile;
    
    public RegionDatabase(File dataFolder) {
        this.databaseFile = new File(dataFolder, "regions.db");
        connect();
        createTables();
    }
    
    private void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:" + databaseFile.getAbsolutePath();
            connection = DriverManager.getConnection(url);
            connection.setAutoCommit(true);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void createTables() {
        String regionsTable = "CREATE TABLE IF NOT EXISTS regions (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "name TEXT NOT NULL," +
            "world TEXT NOT NULL," +
            "is_global INTEGER NOT NULL DEFAULT 0," +
            "pos1_x REAL," +
            "pos1_y REAL," +
            "pos1_z REAL," +
            "pos2_x REAL," +
            "pos2_y REAL," +
            "pos2_z REAL," +
            "priority INTEGER NOT NULL DEFAULT 0," +
            "UNIQUE(name, world)" +
            ");";
        
        String flagsTable = "CREATE TABLE IF NOT EXISTS region_flags (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "region_id INTEGER NOT NULL," +
            "flag_name TEXT NOT NULL," +
            "flag_value INTEGER NOT NULL," +
            "FOREIGN KEY(region_id) REFERENCES regions(id) ON DELETE CASCADE," +
            "UNIQUE(region_id, flag_name)" +
            ");";
        
        String blockedItemsTable = "CREATE TABLE IF NOT EXISTS region_blocked_items (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "region_id INTEGER NOT NULL," +
            "item_material TEXT NOT NULL," +
            "FOREIGN KEY(region_id) REFERENCES regions(id) ON DELETE CASCADE," +
            "UNIQUE(region_id, item_material)" +
            ");";
        
        String whitelistedItemsTable = "CREATE TABLE IF NOT EXISTS region_whitelisted_items (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "region_id INTEGER NOT NULL," +
            "item_material TEXT NOT NULL," +
            "FOREIGN KEY(region_id) REFERENCES regions(id) ON DELETE CASCADE," +
            "UNIQUE(region_id, item_material)" +
            ");";
        
        String totalBlockedItemsTable = "CREATE TABLE IF NOT EXISTS region_total_blocked_items (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "region_id INTEGER NOT NULL," +
            "item_material TEXT NOT NULL," +
            "FOREIGN KEY(region_id) REFERENCES regions(id) ON DELETE CASCADE," +
            "UNIQUE(region_id, item_material)" +
            ");";
        
        String indexRegionWorld = "CREATE INDEX IF NOT EXISTS idx_region_world ON regions(world);";
        String indexRegionGlobal = "CREATE INDEX IF NOT EXISTS idx_region_global ON regions(is_global);";
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(regionsTable);
            stmt.execute(flagsTable);
            stmt.execute(blockedItemsTable);
            stmt.execute(whitelistedItemsTable);
            stmt.execute(totalBlockedItemsTable);
            stmt.execute(indexRegionWorld);
            stmt.execute(indexRegionGlobal);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void saveRegion(Region region) {
        try {
            connection.setAutoCommit(false);
            
            int regionId = getRegionId(region.getName(), region.getWorldName());
            boolean isUpdate = regionId > 0;
            
            if (isUpdate) {
                // Atualizar região existente
                String updateSql = "UPDATE regions SET " +
                    "is_global = ?, pos1_x = ?, pos1_y = ?, pos1_z = ?, " +
                    "pos2_x = ?, pos2_y = ?, pos2_z = ?, priority = ? " +
                    "WHERE id = ?";
                
                try (PreparedStatement stmt = connection.prepareStatement(updateSql)) {
                    stmt.setInt(1, region.isGlobal() ? 1 : 0);
                    
                    if (region.isGlobal()) {
                        stmt.setNull(2, Types.REAL);
                        stmt.setNull(3, Types.REAL);
                        stmt.setNull(4, Types.REAL);
                        stmt.setNull(5, Types.REAL);
                        stmt.setNull(6, Types.REAL);
                        stmt.setNull(7, Types.REAL);
                    } else {
                        stmt.setDouble(2, region.getPos1().getX());
                        stmt.setDouble(3, region.getPos1().getY());
                        stmt.setDouble(4, region.getPos1().getZ());
                        stmt.setDouble(5, region.getPos2().getX());
                        stmt.setDouble(6, region.getPos2().getY());
                        stmt.setDouble(7, region.getPos2().getZ());
                    }
                    
                    stmt.setInt(8, region.getPriority());
                    stmt.setInt(9, regionId);
                    stmt.executeUpdate();
                }
            } else {
                // Inserir nova região
                String insertSql = "INSERT INTO regions " +
                    "(name, world, is_global, pos1_x, pos1_y, pos1_z, pos2_x, pos2_y, pos2_z, priority) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                
                try (PreparedStatement stmt = connection.prepareStatement(insertSql)) {
                    stmt.setString(1, region.getName());
                    stmt.setString(2, region.getWorldName());
                    stmt.setInt(3, region.isGlobal() ? 1 : 0);
                    
                    if (region.isGlobal()) {
                        stmt.setNull(4, Types.REAL);
                        stmt.setNull(5, Types.REAL);
                        stmt.setNull(6, Types.REAL);
                        stmt.setNull(7, Types.REAL);
                        stmt.setNull(8, Types.REAL);
                        stmt.setNull(9, Types.REAL);
                    } else {
                        stmt.setDouble(4, region.getPos1().getX());
                        stmt.setDouble(5, region.getPos1().getY());
                        stmt.setDouble(6, region.getPos1().getZ());
                        stmt.setDouble(7, region.getPos2().getX());
                        stmt.setDouble(8, region.getPos2().getY());
                        stmt.setDouble(9, region.getPos2().getZ());
                    }
                    
                    stmt.setInt(10, region.getPriority());
                    stmt.executeUpdate();
                }
                
                regionId = getRegionId(region.getName(), region.getWorldName());
            }
            
            if (isUpdate) {
                deleteRegionFlags(regionId);
                deleteRegionBlockedItems(regionId);
                deleteRegionWhitelistedItems(regionId);
                deleteRegionTotalBlockedItems(regionId);
            }
            
            saveRegionFlags(regionId, region.getFlags());
            saveRegionBlockedItems(regionId, region.getBlockedItems());
            saveRegionWhitelistedItems(regionId, region.getWhitelistedItems());
            saveRegionTotalBlockedItems(regionId, region.getTotalBlockedItems());
            
            connection.commit();
            System.out.println("[DEBUG-SAVE] ✓ Região salva com sucesso! ID final: " + regionId);
            System.out.println("[DEBUG-SAVE] ========================================");
        } catch (SQLException e) {
            System.out.println("[DEBUG-SAVE] ❌ ERRO ao salvar região!");
            System.out.println("[DEBUG-SAVE] ========================================");
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    private int getRegionId(String name, String world) throws SQLException {
        String query = "SELECT id FROM regions WHERE name = ? AND world = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setString(2, world);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        return -1;
    }
    
    private void deleteRegionFlags(int regionId) throws SQLException {
        String delete = "DELETE FROM region_flags WHERE region_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(delete)) {
            stmt.setInt(1, regionId);
            stmt.executeUpdate();
        }
    }
    
    private void deleteRegionBlockedItems(int regionId) throws SQLException {
        String delete = "DELETE FROM region_blocked_items WHERE region_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(delete)) {
            stmt.setInt(1, regionId);
            stmt.executeUpdate();
        }
    }
    
    private void deleteRegionWhitelistedItems(int regionId) throws SQLException {
        String delete = "DELETE FROM region_whitelisted_items WHERE region_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(delete)) {
            stmt.setInt(1, regionId);
            stmt.executeUpdate();
        }
    }
    
    private void deleteRegionTotalBlockedItems(int regionId) throws SQLException {
        String delete = "DELETE FROM region_total_blocked_items WHERE region_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(delete)) {
            stmt.setInt(1, regionId);
            stmt.executeUpdate();
        }
    }
    
    private void saveRegionFlags(int regionId, Map<RegionFlag, Boolean> flags) throws SQLException {
        String insert = "INSERT INTO region_flags (region_id, flag_name, flag_value) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(insert)) {
            for (Map.Entry<RegionFlag, Boolean> entry : flags.entrySet()) {
                stmt.setInt(1, regionId);
                stmt.setString(2, entry.getKey().name());
                stmt.setInt(3, entry.getValue() ? 1 : 0);
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }
    
    private void saveRegionBlockedItems(int regionId, Set<String> items) throws SQLException {
        if (items.isEmpty()) return;
        
        String insert = "INSERT INTO region_blocked_items (region_id, item_material) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(insert)) {
            for (String item : items) {
                stmt.setInt(1, regionId);
                stmt.setString(2, item);
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }
    
    private void saveRegionWhitelistedItems(int regionId, Set<String> items) throws SQLException {
        if (items.isEmpty()) return;
        
        String insert = "INSERT INTO region_whitelisted_items (region_id, item_material) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(insert)) {
            for (String item : items) {
                stmt.setInt(1, regionId);
                stmt.setString(2, item);
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }
    
    private void saveRegionTotalBlockedItems(int regionId, Set<String> items) throws SQLException {
        if (items.isEmpty()) return;
        
        String insert = "INSERT INTO region_total_blocked_items (region_id, item_material) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(insert)) {
            for (String item : items) {
                stmt.setInt(1, regionId);
                stmt.setString(2, item);
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }
    
    public Map<String, Region> loadAllRegions() {
        Map<String, Region> regions = new HashMap<>();
        String query = "SELECT * FROM regions";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                String name = rs.getString("name");
                String worldName = rs.getString("world");
                boolean isGlobal = rs.getInt("is_global") == 1;
                int priority = rs.getInt("priority");
                int regionId = rs.getInt("id");
                
                Region region;
                
                if (isGlobal) {
                    region = new Region(name, worldName);
                } else {
                    World world = Bukkit.getWorld(worldName);
                    if (world == null) {
                        // Mundo ainda não foi carregado, pular região não-global
                        continue;
                    }
                    
                    Location pos1 = new Location(world, 
                        rs.getDouble("pos1_x"), 
                        rs.getDouble("pos1_y"), 
                        rs.getDouble("pos1_z"));
                    Location pos2 = new Location(world, 
                        rs.getDouble("pos2_x"), 
                        rs.getDouble("pos2_y"), 
                        rs.getDouble("pos2_z"));
                    region = new Region(name, worldName, pos1, pos2);
                }
                
                region.setPriority(priority);
                
                loadRegionFlags(regionId, region);
                loadRegionBlockedItems(regionId, region);
                loadRegionWhitelistedItems(regionId, region);
                loadRegionTotalBlockedItems(regionId, region);
                
                String key;
                if (region.isGlobal()) {
                    key = (name + "_" + worldName).toLowerCase();
                } else {
                    key = name.toLowerCase();
                }
                
                regions.put(key, region);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return regions;
    }
    
    private void loadRegionFlags(int regionId, Region region) throws SQLException {
        String query = "SELECT flag_name, flag_value FROM region_flags WHERE region_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, regionId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                try {
                    RegionFlag flag = RegionFlag.valueOf(rs.getString("flag_name"));
                    boolean value = rs.getInt("flag_value") == 1;
                    region.setFlag(flag, value);
                } catch (IllegalArgumentException ignored) {
                }
            }
        }
    }
    
    private void loadRegionBlockedItems(int regionId, Region region) throws SQLException {
        String query = "SELECT item_material FROM region_blocked_items WHERE region_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, regionId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                region.addBlockedItem(rs.getString("item_material"));
            }
        }
    }
    
    private void loadRegionWhitelistedItems(int regionId, Region region) throws SQLException {
        String query = "SELECT item_material FROM region_whitelisted_items WHERE region_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, regionId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                region.addWhitelistedItem(rs.getString("item_material"));
            }
        }
    }
    
    private void loadRegionTotalBlockedItems(int regionId, Region region) throws SQLException {
        String query = "SELECT item_material FROM region_total_blocked_items WHERE region_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, regionId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                region.addTotalBlockedItem(rs.getString("item_material"));
            }
        }
    }
    
    public void deleteRegion(String name, String world) {
        String delete = "DELETE FROM regions WHERE name = ? AND world = ?";
        try (PreparedStatement stmt = connection.prepareStatement(delete)) {
            stmt.setString(1, name);
            stmt.setString(2, world);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}

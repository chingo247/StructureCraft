/*
 * Copyright (C) 2016 Chingo247
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.chingo247.structurecraft.persistence.connection;

import com.chingo247.structurecraft.util.VersionUtil;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.Query;

/**
 *
 * @author Chingo247
 */
public final class SCMySQLDB implements IDBIProvider {

    private static final String JDBC_URL = "jdbc:mysql://";
    private static final String DATABASE = "structurecraft";
    private static final String PROPS = "?useSSL=false";

    private final String host;
    private final int port;
    private Properties probs;
    private String version;
    private boolean spatialSupport;

    public SCMySQLDB(String host, int port, String user, String password) {
        this.host = host;
        this.port = port;
        this.probs = new Properties();

        this.probs.setProperty("user", user);
        this.probs.setProperty("password", password);
        this.probs.setProperty("useSSL", "false");
        this.probs.setProperty("autoReconnect", "true");

        DBI dbi = getDBI();
        try (Handle h = dbi.open()) {
            Query<Map<String, Object>> query = h.createQuery("SELECT VERSION() AS VERSION");
            this.version = query.first().get("VERSION").toString();
        }
        
        // format the version
        String[] arr = version.replaceAll("\\D", " ").split("\\s+");
        version = "";
        for(int i = 0; i < arr.length; i++) {
            version += (i == arr.length - 1) ? arr[i] : arr[i] + ".";
        }
        
        int cv = VersionUtil.compare("5.7", version);
        this.spatialSupport = cv == -1 || cv == 0;
    }

  
    public String getMYSQLVersion() {
        return version;
    }

    public Connection newConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL + host + ":" + port + "/" + DATABASE, probs);
    }

    @Override
    public DBI getDBI() {
        return new DBI(JDBC_URL + host + ":" + port + "/" + DATABASE, probs);
    }

    public static void main(String[] args) {
        SCMySQLDB mySQL = new SCMySQLDB("localhost", 3306, "root", "root");
        System.out.println("Connecting...");
        DBI dbi = mySQL.getDBI();
        System.out.println("Connected!");
        Handle h = dbi.open();

        String version = mySQL.version;

        try {

            String path = "src/main/resources/structurecraft/config/mysql/";

            execute(path + "structurecraft_drop_all.sql", h);
            
            
            if (version.startsWith("5.6")) {
                execute(path + "structurecraft_spatial_default.sql", h);
            } else {
                execute(path + "structurecraft_spatial_mysql57.sql", h);
            }
            
            execute(path + "structurecraft.sql", h);
            executeSQLStatement(path + "triggers/tree_update_trigger.sql", h);
            
            
           
        } catch (IOException ex) {
            Logger.getLogger(SCMySQLDB.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            h.close();
        }

    }
    
    private static void execute(String path, Handle handle) throws IOException {
        String sql = Files.toString(new File(path), Charset.defaultCharset());
        handle.createScript(sql).execute();
    }

    private static void executeSQLStatement(String path, Handle handle) throws IOException {
        String sql = Files.toString(new File(path), Charset.defaultCharset());
        handle.execute(sql);
    }

    @Override
    public boolean useSpatialIndex() {
        return spatialSupport;
    }
    
}

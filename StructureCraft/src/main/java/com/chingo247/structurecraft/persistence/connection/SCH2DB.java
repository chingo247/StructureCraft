/*
 * Copyright (C) 2016 Chingo
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

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.skife.jdbi.v2.DBI;

/**
 *
 * @author Chingo
 */
public class SCH2DB implements IDBIProvider {
    
    public static SCH2DB INSTANCE = new SCH2DB();
    
    private static final String H2DB_CONNECTION_URL = "jdbc:h2:file:";
    private final String databaseURL;
    
    private SCH2DB() {
        this.databaseURL =  H2DB_CONNECTION_URL + new File("plugins/StructureCraft/database/structurecraft").getAbsolutePath();
    }

    public Connection createNewConnection() throws SQLException {
        return DriverManager.getConnection(databaseURL);
    }
    
    @Override
    public DBI getDBI() {
        return new DBI(databaseURL);
    }

    @Override
    public boolean supportSpatials() {
        return false;
    }

    
    
}

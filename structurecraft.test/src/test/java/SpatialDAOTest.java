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

import java.util.UUID;

import com.chingo247.structurecraft.persistence.connection.SCMySQLDB;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

/**
 *
 * @author Chingo
 */
public class SpatialDAOTest {

    private Handle h;
    private DBI dbi;
    private SCMySQLDB mySQL;
    
    public SpatialDAOTest() {
        this.mySQL = new SCMySQLDB("localhost", 3306, "root", "root");
        System.out.println("Connecting...");

        this.dbi = mySQL.getDBI();
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        h = dbi.open();
        h.begin();
    }

    @After
    public void tearDown() {
        h.rollback();
        h.close();
    }

    /**
     * Test insert spatial
     */
    @Test
    public void testInsertSpatial() {
        SpatialDAO spatialDAO = h.attach(SpatialDAO.class);
        spatialDAO.insert(0, 0, 0, 90, 10, 10, 10, 100, 100, 100, "structure", UUID.randomUUID().toString());
    }
}

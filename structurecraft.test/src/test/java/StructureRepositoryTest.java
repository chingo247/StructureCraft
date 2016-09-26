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


import com.chingo247.structurecraft.model.structure.ConstructionStatus;
import com.chingo247.structurecraft.model.structure.Structure;
import com.chingo247.structurecraft.model.world.Spatial;
import com.chingo247.structurecraft.persistence.connection.SCMySQLDB;
import com.chingo247.structurecraft.persistence.dao.SpatialDAO;
import com.chingo247.structurecraft.persistence.dao.StructureDAO;
import com.chingo247.structurecraft.persistence.repositories.StructureRepository;
import org.junit.*;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import java.sql.Timestamp;
import java.util.UUID;

/**
 *
 * @author Chingo
 */
public class StructureRepositoryTest {

    private Handle h;
    private DBI dbi;

    public StructureRepositoryTest() {
        SCMySQLDB mySQL = new SCMySQLDB("localhost", 3306, "root", "root");
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
     * Test insert structure
     */
    @Test
    public void testInsertStructure() {
        SpatialDAO spatialDAO = h.attach(SpatialDAO.class);
        StructureDAO structureDAO = h.attach(StructureDAO.class);
        StructureRepository sr = new StructureRepository(spatialDAO, structureDAO);
        Spatial spatial = new Spatial(0, 0, 0, 0, 0, 0, UUID.randomUUID(), 0);
        Structure structure = new Structure(null, spatial, "test", ConstructionStatus.BUILDING, 0, new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()));
        sr.add(structure);
        
        
        
    }
}

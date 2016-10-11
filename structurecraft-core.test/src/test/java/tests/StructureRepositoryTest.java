package tests;

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


import com.chingo247.structurecraft.model.structure.Structure;
import com.chingo247.structurecraft.model.world.Direction;
import com.chingo247.structurecraft.model.world.Spatial;
import com.chingo247.structurecraft.persistence.connection.IDBIProvider;
import com.chingo247.structurecraft.persistence.repositories.StructureRepository;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;
import org.junit.*;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import java.util.UUID;
import connection.MyConnectionFactory;

/**
 *
 * @author Chingo
 */
public class StructureRepositoryTest {

    private Handle h;
    private DBI dbi;
    private IDBIProvider dBIProvider;

    public StructureRepositoryTest() {
        this.dBIProvider = new MyConnectionFactory().getBIProvider();
        this.dbi = dBIProvider.getDBI();

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
        StructureRepository sr = new StructureRepository(h, dBIProvider.useSpatialIndex());
        Spatial spatial = new Spatial(UUID.randomUUID(), "world", new CuboidRegion(Vector.ZERO, Vector.ZERO), Direction.EAST, "structure");
        Structure structure = new Structure(spatial, "test-structure");
        sr.add(structure);
    }
}

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

import com.chingo247.structurecraft.model.world.Direction;
import com.chingo247.structurecraft.model.world.Spatial;
import com.chingo247.structurecraft.persistence.connection.IDBIProvider;
import java.util.UUID;

import com.chingo247.structurecraft.persistence.repositories.SpatialRepository;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import connection.MyConnectionFactory;
import java.util.List;
import static org.junit.Assert.*;

/**
 *
 * @author Chingo
 */
public class SpatialDAOTest {

    private Handle h;
    private DBI dbi;
    private IDBIProvider dbiProvider;
    private SpatialRepository sr;
    
    public SpatialDAOTest() {
        this.dbiProvider = new MyConnectionFactory().getBIProvider();
        this.dbi = dbiProvider.getDBI();
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
        sr = new SpatialRepository(h, dbiProvider.useSpatialIndex());
    }

    @After
    public void tearDown() {
//        h.rollback();
        h.commit();
        h.close();
    }

    /**
     * Test insert spatial
     */
    @Test
    public void testInsertSpatial() {
        Spatial spatial = new Spatial(UUID.randomUUID(), "world", new CuboidRegion(Vector.ZERO, Vector.ZERO), Direction.EAST, "structure");
        sr.insert(spatial);
    }
    
    @Test
    public void testOverlap() {
        UUID world = UUID.randomUUID();
        Spatial spatial = new Spatial(world, "world", new CuboidRegion(Vector.ZERO, Vector.ZERO), Direction.EAST, "structure");
        Spatial bounds = new Spatial(world, "world", new CuboidRegion(new BlockVector(-1, -1, -1), Vector.ONE), Direction.EAST, "structure");
        
        sr.insert(spatial);
        
        assertTrue(sr.overlaps(bounds));
    }
    
    
    
}

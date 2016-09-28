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

import com.chingo247.structurecraft.model.world.Spatial;
import com.chingo247.structurecraft.persistence.connection.IDBIProvider;
import java.util.UUID;

import com.chingo247.structurecraft.persistence.repositories.SpatialRepository;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import tests.connection.MyConnectionFactory;

/**
 *
 * @author Chingo
 */
public class SpatialDAOTest {

    private Handle h;
    private DBI dbi;
    private IDBIProvider dbiProvider;
    
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
        SpatialRepository sr = new SpatialRepository(h, dbiProvider.useSpatialIndex());
        Spatial spatial = new Spatial(0, 0, 0, 0, 0, 0, UUID.randomUUID(), "test-world", 0);
        sr.insert(spatial);
    }
}

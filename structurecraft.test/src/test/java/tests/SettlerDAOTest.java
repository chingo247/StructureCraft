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


import tests.connection.MyConnectionFactory;
import com.chingo247.structurecraft.model.entities.Settler;
import com.chingo247.structurecraft.persistence.connection.IDBIProvider;
import com.chingo247.structurecraft.persistence.connection.SCMySQLDB;
import com.chingo247.structurecraft.persistence.dao.SettlerDAO;
import org.junit.*;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import java.util.UUID;
import org.skife.jdbi.v2.tweak.ConnectionFactory;

/**
 *
 * @author Chingo
 */

public class SettlerDAOTest {

    private Handle h;
    private IDBIProvider dBIProvider;
    private DBI dbi;

    public SettlerDAOTest() {
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
     * Test insert settler
     */
    @Test
    public void testInsertSettler() {
        SettlerDAO settlerDAO = h.attach(SettlerDAO.class);
        UUID playerUUID = UUID.randomUUID();
        long id = settlerDAO.insert("test", playerUUID.toString(), true);
        
        Settler s = settlerDAO.findById(id);
        
        Assert.assertTrue(s != null);
        Assert.assertTrue(s.getId().equals(id));
        Assert.assertTrue(s.getUniqueId().equals(playerUUID));
        
        Settler ss = settlerDAO.findByUUID(playerUUID.toString());
        Assert.assertTrue(ss.getId().equals(s.getId()));
    }
    
    /**
     * Test insert settler
     */
    @Test(expected = Exception.class)
    public void testInsertDuplicateUUIDSettler() {
        SettlerDAO settlerDAO = h.attach(SettlerDAO.class);
        UUID playerUUID = UUID.randomUUID();
        settlerDAO.insert("test", playerUUID.toString(), true);
        settlerDAO.insert("test", playerUUID.toString(), true);
    }
    
    /**
     * Test update settler
     */
    @Test
    public void testUpdateSettler() {
        SettlerDAO settlerDAO = h.attach(SettlerDAO.class);
        UUID playerUUID = UUID.randomUUID();
        
        String name1 = "test";
        String name2 = "differentName";
        
        long id = settlerDAO.insert(name1, playerUUID.toString(), true);
        
        Settler s = settlerDAO.findById(id);
        Assert.assertTrue(s != null);
        Assert.assertTrue(s.getId().equals(id));
        Assert.assertTrue(s.getName().equals(name1));
        
        settlerDAO.updateName(name2, playerUUID.toString());
        s = settlerDAO.findById(id);
        Assert.assertTrue(s.getName().equals(name2));
    }
    
    /**
     * Test update settler
     */
    @Test
    public void testRemoveSettler() {
        SettlerDAO settlerDAO = h.attach(SettlerDAO.class);

        long id = settlerDAO.insert("test", UUID.randomUUID().toString(), true);
        
        Settler s = settlerDAO.findById(id);
        Assert.assertTrue(s != null);
        
        settlerDAO.delete(id);
        
        s = settlerDAO.findById(id);
        Assert.assertTrue(s == null);
    }
}

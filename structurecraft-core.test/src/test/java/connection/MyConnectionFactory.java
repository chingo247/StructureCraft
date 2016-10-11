package connection;


import com.chingo247.structurecraft.persistence.connection.IDBIProvider;
import com.chingo247.structurecraft.persistence.connection.SCMySQLDB;
import java.io.IOException;
import org.junit.Ignore;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author Chingo
 */
@Ignore
public class MyConnectionFactory {
    
    public MyConnectionFactory() {
    }
    
    public IDBIProvider getBIProvider() {
        try {
            Configuration config = new Configuration();
            config.load();
            
            switch (config.getDatabaseType()) {
                case "mysql57_plus": 
                case "mysql56":
                    SCMySQLDB mySQLDB = new SCMySQLDB(config.getHost(), config.getPort(), config.getUser(), config.getPass());
                    return mySQLDB;
                default:throw new AssertionError("Unknown type of database '" + config.getDatabaseType() + "'");
            }   
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        
        
    }
}

package tests.connection;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
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
public class Configuration {
    
    private Properties properties;
    
    public void load() throws IOException {
        this.properties = new Properties();
        this.properties.load(new FileInputStream(new File("config/connection.properties")));
    }
    
    public String getUser() {
        return properties.getProperty("database.user");
    }
    
    public String getPass() {
        return properties.getProperty("database.pass");
    }
    
    public String getHost() {
        return properties.getProperty("database.url");
    }
    
    public int getPort() {
        return Integer.parseInt(properties.getProperty("database.port"));
    }
    
    public String getDatabaseType() {
        return properties.getProperty("database.type");
    }
    
    
}

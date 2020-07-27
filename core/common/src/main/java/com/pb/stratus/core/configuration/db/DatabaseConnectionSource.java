package com.pb.stratus.core.configuration.db;

import com.pb.stratus.core.configuration.ConfigProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.appender.db.jdbc.ConnectionSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: vi001ty
 * Date: 7/31/14
 * Time: 4:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class DatabaseConnectionSource implements ConnectionSource {

    private ConnectionFactory connectionFactory = null;
    private static Logger logger = LogManager.getLogger(DatabaseConnectionSource.class);
    public DatabaseConnectionSource(ConfigProperties configProperties){
        if(configProperties != null){
            connectionFactory = DatabaseConnectionFactory.createFactory(configProperties);
        }else {
            throw new IllegalStateException("Config Properties cannot be null.");
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection conn = null;
        try{
        conn =  connectionFactory.getDatabaseConnection();
        }catch(Exception ex){
            logger.warn("Error while logging Usage Statistics:" + ex.getMessage());
            throw ex;
        }
        return conn;
    }
}

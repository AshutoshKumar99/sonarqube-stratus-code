package com.pb.stratus.core.configuration.db;

import com.pb.stratus.core.configuration.ConfigProperties;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: vi001ty
 * Date: 7/31/14
 * Time: 4:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class DatabaseConnectionFactory implements ConnectionFactory {

    private static final Logger logger = LogManager.getLogger(DatabaseConnectionFactory.class);
    private static final String DRIVER_CLASS_NAME = "com.mysql.jdbc.Driver";

    private DataSource dataSource = null;

    private DatabaseConnectionFactory(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static ConnectionFactory createFactory(ConfigProperties configProperties) {

        GenericObjectPool connectionPool = null;
        String driverClassName = configProperties.getUsageDBDriverClassName();
        if (StringUtils.isEmpty(driverClassName)) {
            driverClassName = DRIVER_CLASS_NAME;
        }
        //
        // Load JDBC Driver class.
        //
        try {
            Class.forName(driverClassName).newInstance();
        } catch (InstantiationException e) {
            logger.error(e);
        } catch (IllegalAccessException e) {
            logger.error(e);
        } catch (ClassNotFoundException e) {
            logger.error(e);
        }
        // Creates an instance of GenericObjectPool that holds our
        // pool of connections object.
        connectionPool = new GenericObjectPool();
        int maxActiveConnection;
        try {
            maxActiveConnection = Integer.parseInt(configProperties.getUsageDBConnectionPoolSize());
        } catch (NumberFormatException ex) {
            maxActiveConnection = GenericObjectPool.DEFAULT_MAX_ACTIVE;
        }
        connectionPool.setMaxActive(maxActiveConnection);
        // Creates a connection factory object which will be use by
        // the pool to create the connection object. We passes the
        // JDBC url info, username and password.
        org.apache.commons.dbcp.ConnectionFactory cf = new DriverManagerConnectionFactory(
                configProperties.getUsageDBUrl(),
                configProperties.getUsageDBUserName(),
                configProperties.getUsageDBPassword());

        // PLEASE DO NOT REMOVE BELOW  LINE
        // Creates a PoolableConnectionFactory. This wraps the  given connection pool
        // and registers itself (cf) with connection pool  so that it is used to create new connections in pool.
        new PoolableConnectionFactory(cf, connectionPool, null, null, false, true);
        return new DatabaseConnectionFactory(new PoolingDataSource(connectionPool));

    }

    @Override
    public Connection getDatabaseConnection() throws SQLException {
        return dataSource.getConnection();
    }
}


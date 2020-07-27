package com.pb.stratus.core.configuration.db;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: vi001ty
 * Date: 7/29/14
 * Time: 11:49 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ConnectionFactory {

    public Connection getDatabaseConnection() throws SQLException;
}

package com.pb.stratus.core.configuration;

/**
 * Created with IntelliJ IDEA.
 * User: vi001ty
 * Date: 7/25/14
 * Time: 12:51 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ConfigProperties {


    /**
     * this method reloads properties for requesting tenant.
     */
    void reload();

    /**
     * gets value of property 'usage.data.source'
     * @return
     */
    String getUsageDataSource();

    /**
     * gets value of property 'usage.data.source.file'
     * @return
     */
    String getUsageDataSourceFile();

    /**
     * gets value of property 'database.username'
     * @return
     */
    String getUsageDBUserName();

    /**
     * gets value of property 'database.driver.class.name'
     * @return
     */
    String getUsageDBDriverClassName();

    /**
     * gets value of property 'database.url'
     * @return
     */
    String getUsageDBUrl();

    /**
     * gets value of property 'database.password'
     * @return
     */
    String getUsageDBPassword();

    /**
     * gets value of property 'database.pool.size'
     * @return
     */
    String getUsageDBConnectionPoolSize();

    /**
     * gets Usage table name in DB.
     * @return
     */
    String getUsageTableName();

    /**
     * gets file name to be used for usage logging csv generation.
     * @return
     */
    String getUsageCSVFileName();

    /**
     * gets Appender pattern for Usage logging in csv
     * @return
     */
    String getCsvAppenderPattern();

    /**
     * gets value of property 'capture.usage.data'
     * @return
     */
    boolean isCaptureUsageDataEnabled();

    /**
     * the buffer after which entries will push to the database
     * @return
     */
    String getUsageDBBufferSize();

}

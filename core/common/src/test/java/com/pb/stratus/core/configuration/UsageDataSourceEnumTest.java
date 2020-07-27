package com.pb.stratus.core.configuration;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: vi001ty
 * Date: 7/24/14
 * Time: 3:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class UsageDataSourceEnumTest {

    @Test
    public void testValidTypes(){
        assertEquals(UsageDataSourceEnum.CSV, UsageDataSourceEnum.getDataSource("csv"));
        assertEquals(UsageDataSourceEnum.CSV, UsageDataSourceEnum.getDataSource("CSV"));
        assertEquals(UsageDataSourceEnum.MYSQL, UsageDataSourceEnum.getDataSource("mysql"));
        assertEquals(UsageDataSourceEnum.MYSQL, UsageDataSourceEnum.getDataSource("MYSQL"));

        assertEquals(UsageDataSourceEnum.NONE, UsageDataSourceEnum.getDataSource("cdv"));
        assertEquals(UsageDataSourceEnum.NONE, UsageDataSourceEnum.getDataSource("none"));
    }

}

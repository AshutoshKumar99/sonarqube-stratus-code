package com.pb.stratus.controller.action;
import com.pb.stratus.core.configuration.CustomerConfigDirHolder;
import javax.servlet.http.HttpServletRequest;
import com.pb.stratus.core.common.Constants;
import java.io.*;
import java.util.*;

/**
 * Created by sa002jh on 1/4/2019.
 * Action class to return list of available brands which can be linked to a map project
 */
public class BrandAction extends DataInterchangeFormatControllerAction {
    private CustomerConfigDirHolder customerConfigDirHolder;
    private String tenantName;

    public BrandAction(CustomerConfigDirHolder customerConfigDirHolder, String tenantName){
        this.customerConfigDirHolder = customerConfigDirHolder;
        this.tenantName = tenantName;
    }


    protected Object createObject(HttpServletRequest request)
    {
        List<String> brandList = new ArrayList<String>();
        String path = this.customerConfigDirHolder.getCustomerConfigDir().getAbsolutePath() + File.separator  + this.tenantName + Constants.BRAND_LIST_URL;
        File[] directories = new File(path).listFiles(File::isDirectory);

        for(int i = 0 ; i < directories.length; i++ ) {
            brandList.add(directories[i].getName());
        }
        return brandList;
    }
}






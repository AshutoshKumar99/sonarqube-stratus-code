package com.pb.stratus.controller.catalog;

import com.mapinfo.midev.service.mapping.v1.FeatureLayer;
import com.mapinfo.midev.service.mapping.v1.Layer;
import com.mapinfo.midev.service.mapping.ws.v1.MappingServiceInterface;
import com.mapinfo.midev.service.mapping.ws.v1.ServiceException;
import com.mapinfo.midev.service.table.v1.NamedTable;
import com.mapinfo.midev.service.table.v1.Table;
import com.pb.stratus.controller.ThreadInterruptedException;
import com.pb.stratus.controller.infrastructure.CombiningExecutor;
import com.pb.stratus.controller.infrastructure.CombiningExecutorFactory;
import com.pb.stratus.core.component.MappingServiceUtilities;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.*;
import java.util.concurrent.Callable;

//XXX There is no usable call in the MiDev WS API that gives us all the 
//    tables of a NamedMap, other than getNamedMapLegends(). describeNamedMap()
//    returns all features from all tables a NamedMap is comprised of.
//    Even for moderately sized tables, that causes OutOfMemoryErrors.
public class CatalogServiceImpl implements CatalogService {
    /**
     * Adding log information.
     */
    private final static Logger logger = LogManager.getLogger(CatalogServiceImpl.class);

    private CombiningExecutorFactory executorFactory;

    private MappingServiceInterface mappingWebService;

    private String tenantName;

    public CatalogServiceImpl(MappingServiceInterface mappingWebService,
                              CombiningExecutorFactory executorFactory,
                              String tenantName) {
        this.executorFactory = executorFactory;
        this.mappingWebService = mappingWebService;
        this.tenantName = tenantName;
    }

    @SuppressWarnings("unchecked")
    public Map<String, List<String>> getTableNames(Collection<String> mapNames) {
        CombiningExecutor<List> executor = executorFactory.createCombiningExecutor(List.class);
        for (String mapName : mapNames) {
            enqueueGetTablesTask(executor, mapName, tenantName);
        }
        List<List> results;
        try {
            results = executor.getResults();
        } catch (InterruptedException ix) {
            Thread.currentThread().interrupt();
            throw new ThreadInterruptedException(ix);
        }
        Map<String, List<String>> allTableNames = new LinkedHashMap<String, List<String>>();
        int idx = 0;
        for (String mapName : mapNames) {
            allTableNames.put(mapName, results.get(idx++));
        }
        return allTableNames;
    }

    private void enqueueGetTablesTask(CombiningExecutor<List> executor,
                                      final String mapName,
                                      final String tenantName) {
        //Callable<List> c = new TokenStoreCallableWrapper<List>( new TableNameGetter(mapName, tenantName));
        executor.addTask(new TableNameGetter(mapName, tenantName));
    }

    /**
     * CONN-17091: NamedTables needs to be removed from the path.
     * @param tableNameWithPath
     * @return
     */
    private static String stripFolderLocation(String tableNameWithPath) {
        String namedTable = "/NamedTables/";

        logger.debug(" Complete path: " + tableNameWithPath);
        if(tableNameWithPath.contains(namedTable))
        {
            return tableNameWithPath.replaceAll(namedTable, "");
        }
        return  tableNameWithPath;
    }

    private static String stripTenantName(String tableNameWithPath, String tenantName) {
        String[] afterSplit = stripFolderLocation(tableNameWithPath).split(tenantName);
        if (afterSplit != null && afterSplit.length > 0) {
            return afterSplit[1].replaceAll("/", "");
        } else {
            return tableNameWithPath;
        }
    }

    private class TableNameGetter implements Callable<List> {
        private final SecurityContext securityContext;
        private final String mapName;
        private final String tenantName;
        private ServletRequestAttributes requestAttributes;

        public TableNameGetter(String mapName, String tenantName) {
            this.mapName = mapName;
            this.tenantName = tenantName;

            //grab this thread storage data off the existing thread (the one the request came in on)
            //so that we can transfer that to the thread the call() is made from.
            this.securityContext = SecurityContextHolder.getContext();
            requestAttributes = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes());
        }

        public List call() throws Exception {
            //populate the thread storage data that we captured from the original thread.
            RequestContextHolder.setRequestAttributes(requestAttributes);
            SecurityContextHolder.setContext(securityContext);
            try {
                // instead of using legends to get the table names, use describeMap
                // the only side-effect this has is that it's the table name that's being returned instead of the layer name
                List<String> result = new ArrayList<String>();
                try {
                    List<Layer> layerList = MappingServiceUtilities.getLayersForMap(mappingWebService, mapName);
                    for (Layer layer : layerList) {
                        if (layer instanceof FeatureLayer) {
                            List<Table> tableList = MappingServiceUtilities.getTables(mappingWebService, layer);
                            for (Table table : tableList) {
                                if (table instanceof NamedTable) {
                                    result.add(stripTenantName(((NamedTable) table).getName(), tenantName));
                                }
                            }
                        }
                    }
                } catch (ServiceException sx) {
                    throw new Error("Unhandled ServiceException", sx);
                }

                return result;
            } finally {
                //remove the thread storage we added earlier
                SecurityContextHolder.clearContext();
                RequestContextHolder.resetRequestAttributes();
            }
        }
    }
}

<html>
    <head>
        <script type="text/javascript" src="featureSearch.js"></script>
        <script type="text/javascript">
            function performSearch(featureSearch, params)
            {
                var params = dojo.mixin(params, 
                {
                    tableName: "ListedBuildings",
                    point: {x: 531060, y: 181780, srs: "epsg:27700"},
                    maxDistance: 1,
                    maxResults: 10
                });
                featureSearch.searchNearest(params);
            }
        </script>
    </head>
    <body>
        <p>
            Performs a FeatureSearch.searchNearest() on &quot;ListedBuildings&quot;.
            upon page load. The feature geometries are expected to be returned 
            in EPSG:4326 (WGS 84).
        </p>
        <jsp:include page="resultTable.jsp">
            <jsp:param name="count" value="4"/>
            <jsp:param name="boundary" value="-0.11255307, 51.51937969, -0.11217607, 51.51986883"/>
        </jsp:include>
    </body>
</html>

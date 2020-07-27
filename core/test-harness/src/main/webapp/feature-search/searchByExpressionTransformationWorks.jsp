<html>
    <head>
        <script type="text/javascript" src="featureSearch.js"></script>
        <script type="text/javascript">
            function performSearch(featureSearch, params)
            {
                var params = dojo.mixin(params, 
                {
                    tableName: "ListedBuildings",
                    searchExpression: "description like '(East side) Gray''s Inn Square Nos.12, 13 and 14, Gatehouse and attached railings'"
                });
                featureSearch.searchByExpression(params);
            }
        </script>
    </head>
    <body>
        <p>
            Performs a FeatureSearch.searchByExpression() on &quot;ListedBuildings&quot;.
            upon page load. The feature geometries are expected to be returned 
            in EPSG:4326 (WGS 84).
        </p>
        <jsp:include page="resultTable.jsp">
            <jsp:param name="count" value="4"/>
            <jsp:param name="boundary" value="-0.1125647, 51.51948799, -0.11236927, 51.51984044"/>
        </jsp:include>
    </body>
</html>

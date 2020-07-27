<html>
    <head>
        <script type="text/javascript" src="featureSearch.js"></script>
        <script type="text/javascript">
            function performSearch(featureSearch, params)
            {
                var params = dojo.mixin(params, 
                {
                    tableName: "Straßennetz München",
                    searchExpression: "straßenname like 'Maximilian%'"
                });
                featureSearch.searchByExpression(params);
            }
        </script>
    </head>
    <body>
        <p>
            <b>
                N.B. This page doesn't really work at the moment because the 
                requested table doesn't exist. I just used 
                it to see if HTTP POST parameters are also correctly encoded
                with the server.xml URIEncoding attribute. Since I thought this
                page might become useful at some point I decided to commit it.
            </b>
        </p>
    </body>
</html>

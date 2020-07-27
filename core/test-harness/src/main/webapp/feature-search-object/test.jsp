<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
        <title>Feature Search Test Page</title>

        <script type="text/javascript">
            dojo.require("stratus.search.FeatureSearch");           
            dojo.require("stratus.geometry.Point");
            var featureSearch;
            dojo.addOnLoad(function() 
            {
                featureSearch = new stratus.search.FeatureSearch();                   
            });           
             
            
            function getBasicFilter(point)
            {                
                var f =
                {
                    "point" : point,   
                    "width" : 0.335693359375,  
                    "tables": ["ListedBuildings"],              
                    "attributeFields":  ["KeyValue", "Title"],
                    "includeGeometry": false,
                    "callback": createAll,
                    "exceptionCallback": handleException
                };
                return f;
            };
            
            function getBasicPoint()
            {
                return {x: 528005.8814697266, y: 184031.76025390625, srs: 'epsg:27700'};
            }
            
            function searchRegular()
            {
                //var result = eval("({'parks': {features: [{properties: {prop1: 'value1', prop2: 'value2'}}]}})");
                featureSearch.searchAtPoint( getBasicFilter(getBasicPoint()));
            }
            
            function searchForTooMany()
            {
                // Assuming that by passing in a large width value, we get a lot of features back
                var filter =getBasicFilter(getBasicPoint());
                filter.width =10000;
                featureSearch.searchAtPoint(filter);
            }
            
            function searchWithUnknownCoordSystem()
            {
                var point = getBasicPoint();
                point.srs = "adfasdfa:28342348203";
                featureSearch.searchAtPoint(getBasicFilter(point));
            }
            
            
            //FIXME not sure if there is an Illegal value in BNG
            function searchAtWrongCoordinates()
            {
                var point = getBasicPoint();
                point.x = -1;
                point.y = -1;
                featureSearch.searchAtPoint(getBasicFilter(point));
            }

            //FIXME wrong tablenames
            function searchAtWrongTablenames()
            {
                var filter = getBasicFilter(getBasicPoint());
                filter.tables = ["NonExistingTable", "ConservationAreas"];
                featureSearch.searchAtPoint( filter);
            }

            //FIXME wrong properties
            function searchAtWrongProperties()
            {
                var filter = getBasicFilter(getBasicPoint());
                filter.attributeFields = ["KeyValue", "NonExistingProperty"];
                featureSearch.searchAtPoint( filter);
            }

            function searchWithEmptyTables()
            {
                var filter = getBasicFilter(getBasicPoint());
                filter.tables = null;
                featureSearch.searchAtPoint( filter);
            }
            
            function searchWithEmptyProperties()
            {
                var filter = getBasicFilter(getBasicPoint());
                filter.attributeFields = null;
                featureSearch.searchAtPoint( filter);
            }
            
            function searchWithGeometryIncluded()
            {
                var filter = getBasicFilter(getBasicPoint());
                filter.includeGeometry = true;
                featureSearch.searchAtPoint( filter);
            }
            
            function searchWithoutPoint()
            {
                try
                {
                    featureSearch.searchAtPoint( getBasicFilter(null));
                }
                catch (x)
                {
                    handleException(x);
                }
            }
            
            function searchWithNegativeWidth()
            {
                try
                {
                    var filter = getBasicFilter(getBasicPoint());
                    filter.width = -1;
                    featureSearch.searchAtPoint(filter);
                }
                catch (x)
                {
                    handleException(x);
                }
            }

            function searchWithoutFilter()
            {
                try
                {
                    featureSearch.searchAtPoint(null);
                }
                catch (x)
                {
                    handleException(x);
                }
            }
            
            function createAll(result)
            {                                                     
                for (prop in result)
                {                                                  	
                        createHtmlTable(prop,result[prop]);
                }
            }        
            

            function createHtmlTable(tableName, feature)
            {         
                var allProperties = new Array();
              
                for (var i = 0; i < feature.length; i++)
                {
                    var properties = feature[i].properties;
                    outer: for (var prop in properties)
                    {
                        for (var k = 0; k < allProperties.length; k++)
                        {
                            if (prop == allProperties[k])
                            {
                                continue outer;
                            }
                        }
                        
                        allProperties.push(prop);
                    }
                }
               
                var innerHtml = "<table><thead><tr><th colspan=\"" + allProperties.length + "\">" + tableName + "</th></tr></thead>";
                
                innerHtml += "<thead><tr>";
                
                for (var i = 0; i < allProperties.length; i++)
                {
                    innerHtml += "<th>" + allProperties[i] + "</th>";
                }
                innerHtml += "</thead></tr>";         
                innerHtml +="<tbody>";
              
                for (var i in feature)
                {
                    var feat = feature[i];                    
                    innerHtml += "<tr>";      
                    for (var prop in feat.properties)
                    {                        
                        innerHtml += "<td>" + feat.properties[prop] + "</td>"                        
                    }
                    innerHtml += "</tr>";                
                }
                innerHtml += "</tbody>";
                innerHtml += "</table>";
                
                var container = document.createElement("div");
                container.innerHTML = innerHtml;
                dojo.byId("result").appendChild(container);   
                         
            }
            
            function handleException(x)
            {
                dojo.byId("errorClass").innerHTML = x.declaredClass;
                dojo.byId("errorMessage").innerHTML = x.message;
            }
            
            function reset()
            {
                dojo.byId("errorClass").innerHTML = "";
                dojo.byId("errorMessage").innerHTML = "";
                dojo.byId("result").innerHTML = "";
            }
        </script>
    </head>
    <body>
        <p>The following example shows how the Feature Search can be used.</p>
        <button id="regularValues" onclick="searchRegular();">Regular Values</button>
        <button id="regularValues" onclick="searchForTooMany();">Too Many Features</button>
        <button id="regularValues" onclick="searchWithUnknownCoordSystem();">Unknown SRS</button>
        <button id="regularValues" onclick="searchAtWrongCoordinates();">Wrong Coords</button>
        <button id="regularValues" onclick="searchAtWrongTablenames();">Wrong Tablenames</button>
        <button id="regularValues" onclick="searchAtWrongProperties();">Wrong Properties</button>
        <button id="regularValues" onclick="searchWithEmptyTables();">Empty Tables</button>
        <button id="regularValues" onclick="searchWithEmptyProperties();">Empty Properties</button>
        <button id="regularValues" onclick="searchWithGeometryIncluded();">Geometry Included</button>
        <button id="regularValues" onclick="searchWithoutPoint();">No Point</button>
        <button id="regularValues" onclick="searchWithNegativeWidth();">Negative Width</button>
        <button id="regularValues" onclick="searchWithoutFilter();">No Filter</button>
        <button id="resetException" onclick="reset();">Reset</button>
        <div id="result">
        </div>
        <div id="errorClass">
        </div>
        <div id="errorMessage">
        </div>
        <table><tr><td></td></tr></table>
    </body>
</html>

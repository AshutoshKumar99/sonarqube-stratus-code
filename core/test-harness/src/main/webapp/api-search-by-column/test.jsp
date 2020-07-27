<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
        <title>API  Search by Column Test Page</title>
        <script type="text/javascript">           
            dojo.require("stratus.search.FeatureSearch");
            dojo.require("stratus.search.SearchByExpressionParameters");

            var featureSearch;
            dojo.addOnLoad(function() 
            {
                featureSearch = new stratus.search.FeatureSearch();
           });

            var fields = new Array();
            fields[0] = "KeyValue";
            fields[1] = "Title";
             
            var searchParameters = null;
            var expression = null;
            var tableName = "ConservationAreas";
            var includeGeometry = false;

            function getParams(fields, includeGeometry, query)
            {
                var f =
                {
                    "tableName": "ConservationAreas",
                    "attributeFields": fields,
                    "includeGeometry": includeGeometry,
                    "callback": createAll,
                    "exceptionCallback": handleException,
                    "searchExpression" : query
                };
                return f;
            };

            function searchQuery()
            {
                    var params = getParams(fields, includeGeometry, document.getElementById('searchExpression').value);
                    featureSearch.searchByExpression(params);
            }

            function searchWithGeometry()
            {
                var fields = new Array();
                fields[0] = "KeyValue";
                var params = getParams(fields, true, document.getElementById('searchExpression').value);
                params.tableName = "GeometryTypes2";
                params.includeGeometry = true;
                featureSearch.searchByExpression(params);
            }
            
            function searchWithGeometryAsColumn()
            {
                var fields = new Array();
                fields[0] = "KeyValue";
                fields[1] = "Obj";
                var params = getParams(fields, true, document.getElementById('searchExpression').value);
                params.tableName = "GeometryTypes2";
                params.includeGeometry = false;
                featureSearch.searchByExpression(params);
            }
            

            function searchWithEmptyFields()
            {
                var params = getParams(null, includeGeometry, document.getElementById('searchExpression').value);
                featureSearch.searchByExpression(params);
            }

            function wrongTableName()
            {
                var params = getParams(fields, includeGeometry, document.getElementById('searchExpression').value);
                params.tableName = "wrongTableName";
                featureSearch.searchByExpression(params);
            }

             function unknownFieldException()
             {
                 var wrongFields = new Array();
                 wrongFields[0] = "UnknownField";
                 wrongFields[1] = "Title";

                 var params = getParams(wrongFields, includeGeometry, document.getElementById('searchExpression').value);
                 featureSearch.searchByExpression(params);
             }

             function searchWithNoGeometryColumn()
             {
                 var fields = new Array();                 
                 var params = getParams(fields, false, document.getElementById('searchExpression').value);
                 params.tableName = "ConservationAreas";
                 featureSearch.searchByExpression(params);
                 
             }
             function emptyTableException()
             {
                 try
                 {
                     var params = getParams(fields,includeGeometry,document.getElementById('searchExpression').value);
                     params.tableName = "";
                     featureSearch.searchByExpression(params);
                 }
                 catch(x)
                 {
                     handleException(x);
                 }
              }

            function createAll(result)
            {
                for (prop in result)
                {
                    createHtmlTable(prop, result[prop]);
                }
            }

           function createHtmlTable(tableName, features)
            {
                var allProperties = new Array();
              
                for (var i = 0; i < features.length; i++)
                {
                    var feature =features[i];
                    var properties = feature.properties;
                   
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
                    //geometry data.                    
                    if (feature.geometry!=null)
                    {   
                        var flag=false;
                        for (var k = 0; k < allProperties.length; k++)
                        {
                            if ("Geometry" == allProperties[k])
                            {
                                flag=true;
                                break;
                            }
                        } 
                        if (!flag)
                        {                       
                            allProperties.push("Geometry");
                        }
                    }                   	   
                    
                }
               
                var innerHtml = "<table><thead><tr><th colspan=\"" + allProperties.length + "\">" + " Table Name : " + tableName + "</th></tr></thead>";
                
                innerHtml += "<thead><tr>";
                if(allProperties.length <= 0)
                {
                    innerHtml += "<th> No Results Found!! </th>";
                }
                
                for (var i = 0; i < allProperties.length; i++)
                {
                    innerHtml += "<th>" + allProperties[i] + "</th>";
                }
                innerHtml += "</thead></tr>";         
                innerHtml +="<tbody>";
              
                for (var i in features)
                {
                    var feat = features[i];    
                               
                    innerHtml += "<tr>";      
                    for (var prop in feat.properties)
                    {
                        innerHtml += "<td>" + feat.properties[prop] + "</td>"                        
                    }
                    if (feat.geometry!=null)
                    {
                        var component =feat.geometry.components;
                        if (component!= null && component.length >0 )  // OR check the type of the geometry
                        {
                         
                            for (var prop in component)
                            {                                                                        
                                innerHtml += "<td>" + component[prop] + "</td>";                        
                             }  
                                               
                        }
                        else  //point geometry ("OpenLayers.Geometry.Point")
                        {                                                                                
                            var geo=feat.geometry;   
                            innerHtml += "<td> X: " + geo.x + "</td>";
                            innerHtml += "<td> Y: " + geo.y + "</td>";                                               
                        }
                    }
                    innerHtml += "</tr>";                
                }
                innerHtml += "</tbody>";
                innerHtml += "</table>";
                
                var container = document.createElement("div");
                container.innerHTML = innerHtml;
                dojo.byId("result").appendChild(container);
            }
             
            function reset()
            {
                dojo.byId("errorClass").innerHTML = "";
                dojo.byId("errorMessage").innerHTML = "";
                dojo.byId("result").innerHTML = "";
            }

            function handleException(x)
            {
                dojo.byId("errorClass").innerHTML = x.declaredClass;
                dojo.byId("errorMessage").innerHTML = x.message;
            }

       </script>
    </head>
    <body>
            <center><p><b>API  Search by Column Demo Page</b></p></center>
            Enter Search Expression 
              <br>
           <input id="searchExpression" type="text" name="searchExpression" value"" size=50></input> 
           <button id="searchQuery" onclick="searchQuery();">Search Expression</button>           
           <button id="searchQuery" onclick="searchWithGeometryAsColumn();">Return Geometry as column</button>
           <button id="searchQuery" onclick="searchWithEmptyFields();">Search with Empty Fields</button>
           <button id="searchQuery" onclick="searchWithNoGeometryColumn();">No geometry column</button>
           <button id="reset" onclick="reset();">Clear Result</button>
            <br>
             <br>
             Various Exceptions
              <br>
             <button id="exceptions" onclick="wrongTableName();">Wrong Table Name</button>
             <button id="exceptions" onclick="unknownFieldException();">Unknown Fields</button>
             <button id="exceptions" onclick="emptyTableException();">Empty Table Name </button>
             <div id="result">
             </div>
            <div id="errorClass">
            </div>
           <div id="errorMessage">
           </div>
    </body>
</html>

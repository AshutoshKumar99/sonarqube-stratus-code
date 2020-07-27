<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
        <title>Feature Search Test Page</title>
        <script type="text/javascript">          
            dojo.require("stratus.search.FeatureSearch");           
            dojo.require("stratus.geometry.Point");
            
            var v = new OpenLayers.Feature.Vector();
            console.log(v);
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
                    "width" : 1,  
                    "tables": ["GeometryTypes2"],              
                    "attributeFields": ["KeyValue", "Description"],
                    "includeGeometry": true,
                    "callback": createAll,
                    "exceptionCallback": handleError
                };
                return f;
            };
            
            function getPoint(xCoord, yCoord)
            {
                return {x: xCoord, y: yCoord, srs: 'epsg:27700'};
            }
            
            /*
            function getBasicPoint()
            {
                return {x: 528005.8814697266, y: 184031.76025390625, srs: 'epsg:27700'};
            }
            
            function getMultiPoint()
            {
                return {x: 529781.4914476589, y: 183854.5014516348, srs: 'epsg:27700'};
            }
            */
            
            function searchPoint()
            {
                var filter = getBasicFilter(getPoint(529343.35, 185586.71));
                //var tables = new Array("GeometryTypes");
                featureSearch.searchAtPoint(filter);
            }
            
            function searchMultiPoint()
            {                               
                featureSearch.searchAtPoint(getBasicFilter(getPoint(529964.90, 183925.83)));
            }
            
            function searchLine()
            {                               
                featureSearch.searchAtPoint(getBasicFilter(getPoint(529373.91, 183192.18)));
            }
            
            function searchMultiLine()
            {               
                featureSearch.searchAtPoint(getBasicFilter(getPoint(529536.94, 185474.63)));
            }
             
            function searchMultiSegmentLine()
            {               
                featureSearch.searchAtPoint(getBasicFilter(getPoint(528028.9, 183253.32)));
            }
            
            function searchPolygon()
            {
                featureSearch.searchAtPoint(getBasicFilter(getPoint(528319.17, 185179.22)));
            }
            
            function searchMultiPolygon()
            {
                featureSearch.searchAtPoint(getBasicFilter(getPoint(526277.18, 185678.71)));
            }
            
            function searchPolygonWithInnerRing()
            {
                featureSearch.searchAtPoint(getBasicFilter(getPoint(527336.88, 184141.57)));
            }
            
            function unsupportedGeometry()
            {               
                var filter = getBasicFilter();
                filter.tables = ["GeometryTypes2"];
                filter.attributeFields=["Description"];
                var pointCoor= getPoint(528005.8814697266, 184031.76025390625);
                pointCoor.x= 528319.17;
                pointCoor.y= 185179.22;
                pointCoor.srs="epsg:27700";
                filter.point=pointCoor;
                featureSearch.searchAtPoint(filter);
            }

            function createAll(result)
            {          
                for (prop in result)
                {                                       
                    createHtmlTable( result[prop]);
                }
            }
            function createHtmlTable(features)
            {     
                 var allProperties = new Array();       
                                   
                 var innerHtml = "<table>";                
                 innerHtml += "<thead><tr><th>Class</th>";                 
                 innerHtml += "<th>Coords</th>";
                 innerHtml += "</tr></thead>";
                 innerHtml += "<tbody>";
                 for (var i = 0; i < features.length; i++)
                 {
                    var feature = features[i];
                    innerHtml += "<tr>";
                    innerHtml += "<td>" + feature.geometry.CLASS_NAME +"</td>";                 
                    var component = feature.geometry.components;
                    var geo=feature.geometry;   
                    innerHtml += "<td><table><tr>";       
                    if (component!= null && component.length >0 )  // OR check the type of the geometry
                    {
                         
                        for (var prop in component)
                        {                                                                        
                            innerHtml += "<td>" + component[prop] + "</td>";                        
                        }  
                                               
                    }
                    else  //point geometry ("OpenLayers.Geometry.Point")
                    {                                                                                
                        innerHtml += "<td> X: " + geo.x + "</td>";
                        innerHtml += "<td> Y: " + geo.y + "</td>";                                               
                    }
                    innerHtml += "</tr></table></td>";
                    innerHtml += "</tr>";                                                             
                 }
                        
                 innerHtml += "</tr></tbody></table>";
                
                 var container = document.createElement("div");
                 container.innerHTML = innerHtml;
                 dojo.byId("result").appendChild(container);  
                 
             }
            
            function handleError(x)
            {
                dojo.byId("errorClass").innerHTML = x.declaredClass;
                dojo.byId("status").innerHTML = x.message;
            }
            
            function reset()
            {
                // OK, this causes a memory leak, but only until the 
                // page is reloaded
                dojo.byId("errorClass").innerHTML = "";
                dojo.byId("result").innerHTML = "";
                dojo.byId("status").innerHTML = "";
            }           
           
       </script>
    </head>
    <body>
        <p>The following example shows how the Feature Search can be used with the geometry.</p>
        <button id="point" onclick="searchPoint();">Point</button>
        <button id="multiPoint" onclick="searchMultiPoint();">MultiPoint</button>
        <button id="line" onclick="searchLine();">LineString</button>
        <button id="multiLine" onclick="searchMultiLine();">MultiLineString</button>
        <button id="multiLine" onclick="searchMultiSegmentLine();">MultiLineString With Multiple Segments</button>
        <button id="polygon" onclick="searchPolygon();">Polygon</button>
        <button id="multiPolygon" onclick="searchMultiPolygon();">MultiPolygon</button>
        <button id="polygonWithInner" onclick="searchPolygonWithInnerRing();">Polygon With Inner Ring</button>
        <button id="reset" onclick="reset();">Reset</button>
        <div id="result"></div>
        <div id="errorClass"></div>
        <div id="status"></div>
    </body>
</html>

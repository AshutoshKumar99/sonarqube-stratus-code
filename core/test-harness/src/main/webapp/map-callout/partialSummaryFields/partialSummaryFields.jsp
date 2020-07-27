<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
        <title>Map Call Out Test Page</title>

        <script type="text/javascript">
            // <![CDATA[
            dojo.require("stratus.MapControl");
            dojo.require("stratus.geometry.Point");
           
            var map;
            var mapList;
            dojo.addOnLoad(function() 
            {
                map = new stratus.MapControl({configFile: "partialSummaryFields.xml"}, "mapControl");
                dojo.connect(map, "onMapClick", "handleMapClicked");
            });
            
            function handleMapClicked(clickEvent)
            {
               getTableNames();
               createHtmlTable(clickEvent);
            }

            function createHtmlTable(clickEvent)
            {     
                 var innerHtml = "<table align=center >";
                 innerHtml += "<thead><tr><th></th>";
                 innerHtml += "<th></th>";
                 innerHtml += "</tr></thead>";
                 innerHtml += "<tbody>";
                 innerHtml += "<tr>";
                 innerHtml += "<tr><td><b>Point:</b></td>";
                 innerHtml += "<td>";
                 innerHtml += ("X: " + clickEvent.point.getX());
                 innerHtml += (", Y: " + clickEvent.point.getY());
                 innerHtml += (", SRS: " + clickEvent.point.getProjection());
                 innerHtml += "</td></tr>";
                 innerHtml += "<tr><td><b>Tables:</b></td>";
                 innerHtml += "<td>";
                 innerHtml += mapList;
                 innerHtml += "</td></tr>";
                 innerHtml += "<tr><td><b>Properties:</b></td>";
                 innerHtml += "<td>";
                 innerHtml += this.map.getSummaryFields();
                 innerHtml += "</td></tr>";
                 innerHtml += "<tr><td><b>Width:</b></td>";
                 innerHtml += "<td>";
                 innerHtml += this.map.getOLMap().getResolution();
                 innerHtml += "</td></tr>"; 
                 innerHtml += "</tbody></table>";

                 var container = document.createElement("div");
                 container.innerHTML = innerHtml;
                 dojo.byId("result").innerHTML = container.innerHTML;

                 var container = document.createElement("div");
                 container.innerHTML = innerHtml;
                 dojo.byId("result").innerHTML = container.innerHTML;    
           }
           function getTableNames()
           {
               mapList = new Array();
               var businessMaps = this.map.getBusinessMaps();
               if (businessMaps)
               {
                   for(var i = 0; i < businessMaps.length; i++)
                   {
                       var currentMap = businessMaps[i];
                       if (currentMap.isVisible())
                       {
                           for (j=0; j< currentMap.layers.length; j++)
                           {
                                mapList.push(currentMap.layers[j].name);
                           }
                       }
                   }
                }
             }
            // ]]>
        </script>
        <style type="text/css">
            .mapControl
            {
                margin: 1em;
                border: 1px solid #000000;
                width: 650px;
                height: 550px;
                margin-left:24%;
            }
        </style>
    </head>
    <body>
	<p>
        <center>This page tests if we use the partial summary fields. When you click on the map you can see the callout parameters.</center>
    </p><br/>
        <center><div id="mapControl" class="mapControl"></div></center>
        <h1 align=center ><u>CallOut Parameters</u></h1><br>
        <hr>   
        <div id="result"></div>
    </body>
</html>

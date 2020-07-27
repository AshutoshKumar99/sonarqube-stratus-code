<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
        <title>Map Click Event Test Page</title>

        <script type="text/javascript">

            dojo.require("stratus.MapControl");
            dojo.require("stratus.geometry.Point");
            dojo.require("stratus.Marker");
            
            var map;    
            dojo.addOnLoad(function() 
            {
                map = new stratus.MapControl({configFile: "map.xml"}, "mapControl1");
                
                dojo.connect(map, "onMapClick", "handleMapClicked");
                
                //new stratus.MapControl({configFile: "map.xml"}, "mapControl2");
            });
            
            function handleMapClicked(clickEvent)
            {
                dojo.byId("x").innerHTML = clickEvent.getPoint().getX();
                dojo.byId("y").innerHTML = clickEvent.getPoint().getY();
                showScreenPixels(clickEvent.getPoint().getX(),clickEvent.getPoint().getY());
            }
            function showMarker()
            {
                // var e = {x: 527891.9135742188, y: 184217.06298828125, crs: "epsg:27700", name: "Noida Office"};
                var point = new stratus.geometry.Point({x:527891.9135742188,y:184217.06298828125,srs: "epsg:27700"});
                var icon = new stratus.catalog.Icon("../common/house.png", "0", "0", "20", "34");
                var properties = {point: point, 
                        title: "Noida Office",
                        iconOffsetX: icon.getOffsetX(),
                        iconOffsetY: icon.getOffsetY(),
                        iconUrl: icon.getURL(),
                        iconWidth: icon.getWidth(),
                        iconHeight: icon.getHeight(),
                        content: "Noida Office", 
                        deleteLinkMessage: "Delete this link"};

                var marker = new stratus.Marker(properties);
                window.map.getMarkerManager().addMarker(marker);
                map.moveTo({x:527891.9135742188,y:184217.06298828125});
                
                //find the exact screen pixel location of the above coordinates.
                showScreenPixels(527892.2492675781,184219.41284179688);
                             
            }
            function showScreenPixels(x,y)
            {
            	var lonlat= new OpenLayers.LonLat(x,y);                
                var pixel=map.myMap.getPixelFromLonLat(lonlat);
                dojo.byId("sx").innerHTML = pixel.x;
                dojo.byId("sy").innerHTML = pixel.y;   
                
            }
            // ]]>
        </script>
        <style>
            .mapControl
            {
                margin: 1em;
                border: 1px solid black;
                width: 500px;
                height: 500px;
            }
        </style>
    </head>
    <body>
        <p>The pags tests how the map click event can be captured and used</p>
       <button id="marker" onclick="showMarker();">Show Marker</button>
        <div id="mapControl1" class="mapControl"></div>       
        <!--div id="mapControl2" class="mapControl"></div-->
         <div>map coordinates
        <div id="x"></div>
        <div id="y"></div>
        </div>
        <div>screen pixels
             <div id ="sx"></div>
             <div id="sy"></div>
        </div>
    </body>
</html>

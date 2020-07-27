
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
        <title>Info Control Test Page 1</title>

        <script type="text/javascript">
            dojo.require("stratus.MapControl");
            
            var map;
            dojo.addOnLoad(function() 
            {
                map = new stratus.MapControl({configFile: "infoControl9.xml"}, "map");
            });
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
    <body class="tundra">
        <p>
            This page tests that on clicking map feature a callout is shown with feature information.
            The map shows 'Development Map' business layer on the GoogleMaps.Callout shows 'PlanningApplication'
            information. Tha mapControl has ZoomSliderControl. Verify that callout disappears on changing the zoom level
            using the ZoomSliderControl.
        </p>
        <div id="map" class="mapControl"></div>
    </body>
</html>

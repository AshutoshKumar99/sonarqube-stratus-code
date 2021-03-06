
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
                map = new stratus.MapControl({configFile: "infoControl6.xml"}, "map");
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
        The map shows 'Development Map' business layer on MasterMap.Callout shows 'PlanningApplication'
        information.Callout shows all the summary fields.Map is zoomed out to zoom level 2. So the feature search
        widht is 21.484375.
    </p>
        <div id="map" class="mapControl"></div>
    </body>
</html>

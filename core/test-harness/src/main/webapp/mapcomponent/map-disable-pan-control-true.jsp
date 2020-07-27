<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Map Component Demo Page</title>

    <script type="text/javascript">
        // this declares where to find the stratus controls
        dojo.registerModulePath("stratus","/test-harness/widgets/stratus");
        // declares that we will be using the MapControl widget	
        dojo.require("stratus.MapControl");

        // Programmatically instantiate Map
        
        // dojo.addOnLoad = When the web page had fully loaded, create a map component using defualt values (fomr the configuration file.
        // Then injects the map into the div 'myMap'
        dojo.addOnLoad(function () {
            var myMap = new stratus.MapControl({configFile: "map-disable-pan-control-true.xml"}, dojo.byId('myMap'));
        });
    </script>
</head>
<body>
<p>This test-harness demostrate the map component demo page where there is no pan control on the map</p>
    <!-- Our initial container div that the Map component will be injected into after the page has loaded -->
    <div id="myMap" style="border:1px solid black;width:600px;height:400px"></div>
</body>
</html>
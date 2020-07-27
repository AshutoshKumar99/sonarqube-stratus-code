<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Map Component Demo Page (used declaratively)</title>

    <script type="text/javascript">
        dojo.require("dojo.parser");
        dojo.require("stratus.MapControl");
    </script>
</head>
<body>
<p>This test-harness demostrate the map component demo page </p>
    <!-- Our initial container div that the Map component will be injected into after the page has loaded -->
    <div 
        id="myMap" 
        dojoType="stratus.MapControl" 
        configFile="map.xml"
        style="border:1px solid black;width:600px;height:400px">
    </div>
</body>
</html>
<html>
    <head>
        <title>Find My Nearest - Envinsa Map</title>
        <script type="text/javascript">
            function createMapControl()
            {
                return new stratus.MapControl({configFile: "map.xml"}, 
                        dojo.byId('samplemap'));
            }
        </script>
        <jsp:include page="common-header.jsp"/>
    </head>
    <body>
        <jsp:include page="body.jsp"/>
    </body>
</html>

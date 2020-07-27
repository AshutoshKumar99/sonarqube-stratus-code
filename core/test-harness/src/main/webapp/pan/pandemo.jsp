<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
        <title>Pan Control Demo</title>
        <script type="text/javascript">
                dojo.require("stratus.PanControl");
                var stat;
                var mockMap = 
                {
                    panMap: function(dx, dy) {stat.innerHTML = 'Panned ' + dx + ', ' + dy},
                    panMapRelatively: function(dx, dy) {stat.innerHTML = 'Panned ' + dx + ', ' + dy},
                    enableNonTiledLayers: function() {},
                    disableNonTiledLayers: function() {}
                }

                dojo.addOnLoad(function() {
                    stat = dojo.byId('status');
                    var r = new stratus.PanControl({}, dojo.byId('pan'), mockMap);
                });
        </script>
    </head>
    <body class="tundra">
    <p>Pan control contains 4 buttons each for display this four directions. This page tests to pan in north, south, west and east directions.</p>
        <div style="margin: 5em;">
            <div id="pan"></div>
        </div>
       <div id="status"></div>
    </body>
</html>
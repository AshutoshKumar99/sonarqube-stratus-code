<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
        <title>BaseMapPicker Control</title>

      
        <script type="text/javascript">
            dojo.require("stratus.LegendControl");
            dojo.require("stratus.MapControl");
            var mockMap = 
            {
                getBaseMaps: function()
                {
                    return [
                    {
                        name: 'baseMap1', 
                        friendlyName: 'Base Map 1', 
                        visible: true, 
                        toggle: function(visible) {this.visible = visible; alert(this.friendlyName + ' toggled ' + (visible ? 'on' : 'off'))}, 
                        isVisible: function() {return this.visible},
                        olLayer: {CLASS_NAME:"a"}
                    }, 
                    {
                        name: 'baseMap2', 
                        friendlyName: 'Base Map 2', 
                        visible: false, 
                        toggle: function(visible) {this.visible = visible; alert(this.friendlyName + ' toggled ' + (visible ? 'on' : 'off'))}, 
                        isVisible: function() {return this.visible},
                        olLayer: {CLASS_NAME:"a"}
                    }]; 
                }
            };
            
            
            dojo.addOnLoad(function() 
            {
                new stratus.BaseMapPickerControl({}, dojo.byId('baseMapPicker'), mockMap);
            });
        </script>
    </head>
    <body class="tundra">
    <br/>
    This test page tests only when there are more than one base map and only one base map would be visible at a time.
    <br/>
        <div style="margin: 1em;">
            <div id="baseMapPicker"></div>
        </div>
    </body>
</html>

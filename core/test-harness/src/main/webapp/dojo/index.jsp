<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-GB" lang="en-GB">
    <head>
        <script type="text/javascript">
            djConfig =
            {
                parseOnLoad: true
            };
        </script>
        <c:set var="contextPath" value="${pageContext.request.contextPath}"/>
        <script type="text/javascript" src="${contextPath}/ria.js"></script>
        <script type="text/javascript">
            dojo.require("dijit.layout.BorderContainer");
            dojo.require("dijit.layout.ContentPane");
            dojo.require("dijit.layout.AccordionContainer");
            dojo.require("dijit.layout.AccordionPane");
            dojo.require("dijit.TitlePane");
            dojo.require("stratus.MapControl");
            dojo.require("stratus.LegendControl");
            dojo.addOnLoad(initControls);

            function initControls()
            {
                var map = new stratus.MapControl({configFile: "map.xml"}, "map");
                var legend = new stratus.LegendControl({}, "legend", map);
            }
        </script>
        <style type="text/css">
            html, body {width: 100%; height: 100%; margin: 0;}
            #borderContainer { width: 100%; height: 80%;}
        </style>
    </head>
    <body class="tundra">
        <div dojoType="dijit.TitlePane"  title="Dojo TitlePane">
           This page tests whether our controls can work happily inside, or at least on the same page as, one or more Dojo controls.
           You should see this text inside a collapsible title pane, with a split pane below. The split pane contains an accordion
           container on the left, with a Stratus legend inside one of the accordions. The right hand pane should contain a Stratus map.
        </div>
        <div dojoType="dijit.layout.BorderContainer" design="sidebar" gutters="true" liveSplitters="true" id="borderContainer">
            <div dojoType="dijit.layout.ContentPane" splitter="true" region="leading" style="width: 256px;">

                <div dojoType="dijit.layout.AccordionContainer">
				    <div dojoType="dijit.layout.AccordionPane" selected="true" title="Dojo AccordionPane 1">
                        <div id="legend"></div>
                    </div>
                <div dojoType="dijit.layout.AccordionPane" title="Dojo AccordionPane 2">
                        <div id="otherstuff">Blah blah blah</div>
                    </div>
                </div>
            </div>

            <div dojoType="dijit.layout.ContentPane" splitter="true" region="center">
                <div id="map" style="width: 100%; height: 100%;"></div>
            </div>
        </div>
    </body>
</html>


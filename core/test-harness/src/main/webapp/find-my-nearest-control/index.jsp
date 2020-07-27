<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
        <title>Content search test page</title>
        <style type="text/css">
            #findMyNearestControl
            {
                border: 1px solid black;
                width: 60%;
                height: 100px;
            }
            #findMyNearestControl2
            {
                border: 10px solid gray;
                width: 220px;
            }
        </style>

       <script type="text/javascript">         


            dojo.require("stratus.TablePickerControl");
            dojo.require("stratus.catalog.Catalog");
            dojo.require("stratus.catalog.CatalogFactory");

            dojo.addOnLoad(function() 
            {
                var catalog = new stratus.catalog.CatalogFactory().newInstance("contentsearch1.xml");
                var findMyNearestControl = new stratus.TablePickerControl({catalog: catalog , categoryTitle : "Browse Categories"}, dojo.byId('findMyNearestControl'));
                dojo.connect(findMyNearestControl, "onTableClicked", "handleTableClicked");
                var findMyNearestControl2 = new stratus.TablePickerControl({catalog: catalog , categoryTitle :"Take 2", tableTitle : ": Take 2 Tables"}, dojo.byId('findMyNearestControl2'));
                dojo.connect(findMyNearestControl2, "onTableClicked", "handleTableClicked");
            });
            
            function handleException(x)
            {
                dojo.byId("errorClass").innerHTML = x.declaredClass;
                dojo.byId("errorMessage").innerHTML = x.fileName;
            }
            
            function handleTableClicked(selectedEvent)
            {
                var table = selectedEvent.getTable();
                var icon = table.icon;
                var outputDiv = dojo.byId("handleEvents");
                outputDiv.innerHTML = "";
                appendText(outputDiv, "Table Name : " + table.name);
                appendElement(outputDiv, "br");
                appendText(outputDiv, "Icon-Url : " + icon.getURL());
                appendElement(outputDiv, "br");
                appendText(outputDiv, "Icon-offsetX : " + icon.getOffsetX());
                appendElement(outputDiv, "br");
                appendText(outputDiv, "Icon-offsetY : " + icon.getOffsetY());
                appendElement(outputDiv, "br");
                appendText(outputDiv, "Icon-height : " + icon.getHeight());
                appendElement(outputDiv, "br");
                appendText(outputDiv, "Icon-width : " + icon.getWidth());
                appendElement(outputDiv, "br");
            }
            
            function appendText(element, text)
            {
                element.appendChild(document.createTextNode(text));
            }
            
            function appendElement(element, elementName)
            {
                element.appendChild(document.createElement(elementName));
            }
        </script>        
     </head>
     <body>
     <br/>
     This test page tests the table picker control.It pre loads the table picker control with the contentsearch.xml requesting this file from
     the admin console.
     <br/>
        <div class="container">
            <div id="findMyNearestControl"></div>
            <div id="findMyNearestControl2"></div>
        </div>
            <div id="errorClass">
            </div>
            <div id="errorMessage">
            </div>
            <div id="handleEvents">
            </div>
    </body> 
</html>

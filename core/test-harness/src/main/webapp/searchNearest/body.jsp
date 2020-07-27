<img src="images/findmynearest.png" style="margin:0px;"/>
<br/>
<br/>
<div class="mainpanel">
    <div class="toolbar">Find My Nearest: Tube Stations</div>
    <div style="width:80%; padding: 10px;text-align:left;">
        <br/>
        x:<input id="xloc" type="text" style="width:70px;" value="527990.5"/>
        <br/>y:<input id="yloc" type="text" style="width:70px;" value="184086.25"/>
        <br/>
        <br/>Max no. of results:<input id="noofresults" value="10" style="width:30px;"/>
        <br/>
        <br/>Distance (miles):<input id="distance" value="5" style="width:40px;"/>
        <br/>
        <br/>
        <button onclick="searchNearestFromInput();">Search</button>
        <div id="resultsTableDiv">
            <ol class="resultlist" id="resultslist"/>
        </div>
    </div>
</div>
<div id="mapContainer" style="float:left;display:block;width:800px;height:650px;border:1px solid blue;">
    <div id="samplemap" style="width:100%;height:100%;"/>
</div>

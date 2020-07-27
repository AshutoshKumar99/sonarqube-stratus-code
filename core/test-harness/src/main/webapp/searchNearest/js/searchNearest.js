dojo.require("stratus.MapControl");
dojo.require("stratus.search.FeatureSearch");
dojo.require("stratus.geometry.Point");
dojo.require("stratus.Marker");
dojo.require("stratus.GeometryServices");
dojo.require("stratus.marker.MarkerManager");

dojo.addOnLoad(function() 
{
    var map = createMapControl();
    if(window.badmapconfig && window.badmapconfig == true)
    {
        alert('Bad MapConfigFile or something else went wrong');
    }
    
    window.map = map;
    
    window.featureSearch = new stratus.search.FeatureSearch();
});


/*
 * moves the map according to the values in the input boxes
 */
function moveToLocation()
{
    var xloc = dojo.byId("xloc").value;
    var yloc = dojo.byId("yloc").value;
    window.map.moveTo({x:xloc,y:yloc,crs:"EPSG:27700"});
}

function addMarker(point,image,title,description)
{
    var xloc = point.getX();
    var yloc = point.getY();
    var point = new stratus.geometry.Point({x: xloc, y: yloc, 
            srs: point.getProjection().getCode()});
    var marker = new stratus.Marker({point: point,title: title,
            content:description, deleteLinkMessage: "Clear this marker",
            deleteLinkSwitch: false});
    var icon = new stratus.catalog.Icon(image, 0, 0, 34, 20 );
    marker.setLayerIcon(icon);

    //transform marker
    var geometryService = new stratus.GeometryServices();
    
    var deferred = marker.transform(geometryService, window.map.getProjection());
    deferred.addSuccessCallback(function(deferred){
        window.map.getMarkerManager().addMarker(deferred.getResult());
    });
}

function showAjaxLoader()
{
    var resultstable = dojo.byId("resultslist");
    resultstable.innerHTML = "<div class='ajaxloader'>"
            + "<img src='ajax-loader.gif' alt='Loading'/></div>";
}

function createResultsTable()
{
    var resultstable = dojo.byId("resultslist");
    resultstable.innerHTML = "";
}

function insertResultInTable(resultname, point)
{
    var x = point.getX();
    var y = point.getY();
    var srs = point.getProjection().getCode();
    var resultstable = dojo.byId("resultslist");
    var movetolink = "<a href='javascript:{}' onclick='window.map.moveTo({x:" +
            x + ",y:" + y + ",crs:\"" + srs + "\"});'>";
    resultstable.innerHTML = resultstable.innerHTML + "<li class='resultitem'>"
            + movetolink + resultname + "</a></li>";
}

function mapSearchNearest(point,maxresults,maxdistance)
{
    var f =
    {
        "point" : point,
        "tableName": ["ListedBuildings"],
        "includeGeometry": true,
        "maxResults": maxresults,
        "maxDistance": maxdistance,
        "distanceUnit": "mile",
        "targetSrs": window.map.configuration.getProjectionSystem(),
        "callback": processResults,
        "exceptionCallback": handleException
    };
    window.featureSearch.searchNearest(f);
}


function searchNearestFromInput()
{
    window.map.getMarkerManager().removeAllMarkers();
    showAjaxLoader();


    var xloc = dojo.byId("xloc").value;
    var yloc = dojo.byId("yloc").value;
    var srsproj = "epsg:27700";

    mapSearchNearest({x:xloc,y:yloc,srs:srsproj},dojo.byId("noofresults").value,
            dojo.byId("distance").value);

    moveToLocation();
    addMarker(new stratus.geometry.Point({x: xloc, y: yloc, srs:'epsg:27700'}),
            "images/house.png", "Starting point", 
            "This is the centre point of the search");
}


function roundNumber(numToRound, decPlaces)
{
    return Math.round(numToRound * Math.pow(10, decPlaces)) 
            / Math.pow(10, decPlaces);
}


function processResults(results)
{
    createResultsTable();
    console.debug(results);
    var markers = new Array();
    for(layer in results)
    {
       for(var i = 0; i < results[layer].length; i++)
       {
            var roundedDistance = roundNumber(results[layer][i].
                                 properties.Distance, 1);
            var distance = new stratus.search.Distance
            ({
                unit: stratus.util.DistanceUnits.METER,
                distance : roundedDistance
            });
            var distanceConverter = new stratus.search.DistanceConverter();
            var tempdistance=distanceConverter.convert(distance,
                    stratus.util.DistanceUnits.MILE);
            var distanceStr = results[layer][i].properties.Title 
                    + ' (' + dojo.number.format(tempdistance.distance
                           , 2) +' miles)';

        insertResultInTable(distanceStr, results[layer][i].markerPosition);
        var location = results[layer][i].markerPosition;
        var point = new stratus.geometry.Point({x: location.getX(),
                y: location.getY(), srs: location.getProjection().getCode()});
        var marker = new stratus.Marker
        ({
             point: point, title: results[layer][i].properties.Title,
             description: distanceStr, keyValue: "Grade II*",
             deleteLinkMessage: "Clear this marker", deleteLinkSwitch: false
        });
        var icon = new stratus.catalog.Icon("images/yellowsquare.png", 0, 0, 34, 20 );
        marker.setLayerIcon(icon);                       
        markers.push(marker);
       }
    }

    //transform marker
    var geometryService = new stratus.GeometryServices();
    var deferred = geometryService.transformObjects(markers,
            window.map.getProjection());
    deferred.addSuccessCallback(function(deferred)
    {
        window.map.getMarkerManager().addMarkers(deferred.getResult());
        window.map.getMarkerManager().zoomToShowAllMarkers();
    });

    if(results[layer].length == 0)
    {
        var resultstable = dojo.byId("resultslist");
        resultstable.innerHTML = "No results found";
    }
}

function handleException(x)
{
    var resultstable = dojo.byId("resultslist");
    resultstable.innerHTML = "There was an error with the Service:" + x.message;
}

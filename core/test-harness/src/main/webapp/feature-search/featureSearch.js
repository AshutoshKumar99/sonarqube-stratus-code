dojo.require("stratus.search.FeatureSearch");

function init()
{
    var featureSearch = new stratus.search.FeatureSearch();
    var params = {};
    params.callback = handleResult;
    params.exceptionCallback = function(error) {alert(error);};
    params.includeGeometry = true;
    params.targetSrs = "epsg:4326";
    performSearch(featureSearch, params);
}

function performSearch(featureSearch, params)
{
}

function handleResult(result)
{
    var obj = result.ListedBuildings;
    console.log(obj);
    dojo.byId("numResults").innerHTML = obj.length;
    var bounds = obj[0].geometry.bounds;
    dojo.byId("bounds").innerHTML = format(bounds);
}

function format(bounds)
{
    return round(bounds.left) + ", " + round(bounds.bottom) + ", " 
            + round(bounds.right) + ", " + round(bounds.top);
}

function round(number)
{
    var base = 100000000
    return Math.round(number * base) / base;
}

dojo.addOnLoad(init);

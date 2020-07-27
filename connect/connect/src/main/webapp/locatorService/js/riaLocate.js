var oo = {
    lang: {
        getPackageFromClassName: function( className ){
            if( className == null ) return '';

            //strip classname
            var tokens = className.split( '.' );
            tokens.pop();
            return tokens.join('.');
        },
        definePackage: function( packageName ) {
            var tokens = packageName.split( '.' );
            var obj = window;
            var i = 0;
            for( i in tokens )
            {
                if( typeof obj[tokens[i]] == "undefined" )
                    obj[tokens[i]] = {};
                obj = obj[tokens[i]];
            }
            return obj;
        },

        declare: function( className, constructor, proto ) {
            //build package structure if necessary
            var pkg = this.definePackage( this.getPackageFromClassName( className ) );

            var name = className.split('.').pop();
            var obj = (pkg[name] = constructor);

            //build info structure
            obj.prototype.info = {
                'className': className,
                'package': this.getPackageFromClassName( className )
            };
            if( proto )
            {
                var c = obj.prototype;
                var p = proto;
                for( var i in p )
                {
                    c[i] = p[i];
                }
            }
        }
    }
};

// class declarations

oo.lang.declare('ria.LocateService',
    function(proxyURL,connectHost)
    {
        this.connectHost = connectHost;
        this.proxyURL = proxyURL;
        this.restURLViaProxy = this.getServiceURLViaProxy();
    },{
        getServiceURLViaProxy: function()
        {
            var url = window.location.protocol + "//" + window.location.host + this.proxyURL + escape(encodeURI(this.connectHost +"/connect/LocateService/services/rest/"));
            return url;
        },
        getRequestObject : function(deffered){
            var xmlhttp;
            if (window.XMLHttpRequest)
            {// code for IE7+, Firefox, Chrome, Opera, Safari
                xmlhttp=new XMLHttpRequest();
            }
            else
            {// code for IE6, IE5
                xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
            }
            xmlhttp.onreadystatechange=function()
            {
                if (xmlhttp.readyState==4)
                {
                    deffered.request = xmlhttp;
                    deffered.onReadyStateChange(xmlhttp.responseText);
                }
            }
            return xmlhttp;

        },

        listGazetteer: function(tenantName){
            var deffered = new ria.DefferedResult();
            var xmlhttp = this.getRequestObject(deffered);
            var url = this.restURLViaProxy + escape(encodeURI(tenantName + "/gazetteers.json"));
            xmlhttp.open("GET",url,true);
            xmlhttp.send();
            return deffered;
        },

        locatorSearch: function(tenantName,gazetteerName,srs,count,query){
            var deffered = new ria.DefferedResult();
            var xmlhttp = this.getRequestObject(deffered);
            var url = tenantName + "/gazetteers/" + gazetteerName + "/search.json?";
            url = url + "&q=" + query;
            if(!(srs==null || srs=="")){
                url = url + "&srs=" + srs;
            }
            if(!(count==null || count=="")){
                url = url + "&count=" + count;
            }
            url =  this.restURLViaProxy + escape(encodeURI(url));
            xmlhttp.open("GET",url,true);
            xmlhttp.send();
            return deffered;
        },

        describeGazetteer: function(tenantName,gazetteerName){
            var deffered = new ria.DefferedResult();
            var xmlhttp = this.getRequestObject(deffered);
            var url = this.restURLViaProxy + escape(encodeURI(tenantName + "/gazetteers/" + gazetteerName + "/describe.json"));
            xmlhttp.open("GET",url,true);
            xmlhttp.send();
            return deffered;
        }

    });

oo.lang.declare('ria.DefferedResult',function(){},{
    successCallback: null,
    errorCallback:null,
    result:null,
    request: null,

    addSuccessCallback:function(callback){
        this.successCallback = callback;
    },
    addErrorCallback:function(callback){
       this.errorCallback=callback;
    },
    onReadyStateChange :function(result){

        if (this.request.status==200)
        {
            this.successCallback(result);
        } else{
            this.errorCallback(result);
        }
    }

});
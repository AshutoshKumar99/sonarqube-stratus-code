var ria =
{

    load: function()
    {
        var baseUrl = this.getBaseUrl();
        this.loadOpenLayers(baseUrl);
        this.loadStyleSheets(baseUrl);
        this.loadDojoAndStratus(baseUrl);
    },

    loadStyleSheets: function(baseUrl)
    {
        this.loadStyleSheet(baseUrl + "/dijit/themes/tundra/tundra.css?v="+ new Date().getTime());
        this.loadStyleSheet(baseUrl + "/dojo/resources/dojo.css?v="+ new Date().getTime());
        this.loadStyleSheet(baseUrl + "/stratus/css/stratusWidgets.css?v="+ new Date().getTime());
        this.loadStyleSheet(baseUrl + "/stratus/css/ie6.css?v="+ new Date().getTime());
    },

    loadStyleSheet: function(path)
    {
        var cssNode = document.createElement('link');
        cssNode.type = "text/css";
        cssNode.rel = "stylesheet";
        cssNode.href = path;
        var head = document.getElementsByTagName("head")[0];
        head.appendChild(cssNode);
    },

    getBaseUrl: function()
    {
        var head = document.getElementsByTagName("head")[0];
        var scripts = head.getElementsByTagName("script");
        for (var i = 0; i < scripts.length; i++)
        {
            var url = scripts[i].src;
            if (url.lastIndexOf("/ria.js") == url.length - 7)
            {
                //XXX this is a bit complicated. Simplify
                if (url.indexOf("http://") == 0 || url.indexOf("https://") == 0)
                {
                    return url.substr(0, url.length - 7);
                }
                else if (url.charAt(0) == "/")
                {
                    var d = this.getDomain();
                    return d.substr(0, d.length - 1)
                        + url.substr(0, url.length - 7);
                }
                else
                {
                    var path = window.location.pathname;
                    if (path.charAt(path.length - 1) == "/")
                    {
                        return d.substr(0, d.length - 1)
                            + path.substr(0, path.length - 1)
                    }
                    else
                    {
                        return d.substr(0, d.length - 1)
                            + path.substr(0,
                            Math.max(path.lastIndexOf("/"), 0));
                    }
                }

            }
        }
        throw new Error("Couldn't find ria.js script in the header");
    },

    loadDojoAndStratus: function(baseUrl)
    {
        if (this.isCrossDomain(baseUrl))
        {
            this.initDojoConfigAndStratusConfig(baseUrl, true);
            if (this.isDebug())
            {
//                this.loadScript(baseUrl + "/dojo/dojo.xd.js.uncompressed.js");
//                this.loadScript(baseUrl + "/dojoXdPatch.js");
//                this.loadScript(baseUrl + "/dijit/dijit.xd.js.uncompressed.js");
//                this.loadScript(baseUrl + "/stratus/nls/stratus-core_ROOT.xd.js");
//                this.loadScript(baseUrl + "/stratus/stratus-core.xd.js.uncompressed.js");
//                this.loadScript(baseUrl + "/connect/connect-core.xd.js.uncompressed.js");
//                this.loadScript(baseUrl + "/connect/connect-core.xd.js.uncompressed.js");
            }
            else
            {
//                this.loadScript(baseUrl + "/dojo/dojo.xd.js");
//                this.loadScript(baseUrl + "/dojoXdPatch.js");
//                this.loadScript(baseUrl + "/dijit/dijit.xd.js");
//                this.loadScript(baseUrl + "/stratus/nls/stratus-core_ROOT.xd.js");
//                this.loadScript(baseUrl + "/stratus/stratus-core.xd.js");
//                this.loadScript(baseUrl + "/connect/connect-core.xd.js");
            }
        }
        else
        {
            this.initDojoConfigAndStratusConfig(baseUrl, false);
            if (this.isDebug())
            {
                this.loadScript(baseUrl + "/dojo/dojo.js.uncompressed.js");
                //this.loadScript(baseUrl + "/dojoXdPatch.js");
                //this.loadScript(baseUrl + "/dijit/dijit-custom.js.uncompressed.js");
                //this.loadScript(baseUrl + "/stratus/nls/stratus-core_ROOT.js");
                this.loadScript(baseUrl + "/stratus/stratus-core.js.uncompressed.js");
                this.loadScript(baseUrl + "/connect/connect-core.js.uncompressed.js");
            }
            else
            {
                //this.loadScript(baseUrl + "/dojoXdPatch.js");
                //this.loadScript(baseUrl + "/dijit/dijit-custom.js");
                //this.loadScript(baseUrl + "/stratus/nls/stratus-core_ROOT.js");
                this.loadScript(baseUrl + "/stratus/stratus-core.js");
                this.loadScript(baseUrl + "/connect/connect-core.js");
            }
        }
    },

    isDebug: function()
    {
        return typeof dojoConfig != "undefined" && dojoConfig.debugStratus;
    },

    loadOpenLayers: function(baseUrl)
    {

        //XXX there seems to be a subtle bug in OL 2.6 that causes an error in IE
        //    when a popup is added to a map on page load. OL normally loads
        //    this stylesheet dynamically and IE seems to defer the loading
        //    until after the popup creation. This causes an error in the size
        //    calculation. Loading the stylesheet manually fixes the problem.
        this.loadStyleSheet(baseUrl + "/openlayers/theme/default/style.css?v="+ new Date().getTime());

        this.loadScript(baseUrl + "/openlayers/OpenLayers.js");

        // load openlayers extensions
        this.loadScript(baseUrl + "/openlayersExtensions.js");
    },

    loadScript: function(url, globalVar)
    {
        document.write('<script type="text/javascript" src="', url, '">','<\/script>');
    },

    isCrossDomain: function(baseUrl)
    {
        return baseUrl.indexOf(this.getDomain()) != 0;
    },

    getDomain: function()
    {
        var loc = window.location;
        var domain = loc.protocol + "//" + loc.hostname;
        if (loc.port)
        {
            domain += ":" + loc.port;
        }
        domain += "/";
        return domain;
    },

    initDojoConfigAndStratusConfig: function(baseUrl, crossDomain)
    {
        if (typeof dojoConfig == "undefined")
        {
            dojoConfig = {};
        }
        if (dojoConfig.isConnectDebug)
        {
            delete dojoConfig.isConnectDebug;
            dojoConfig.debugStratus = true;
        }

        if (crossDomain)
        {
            dojoConfig.afterOnLoad = true;
            dojoConfig.useXDomain = true;
            dojoConfig.baseUrl = baseUrl;
            dojoConfig.modulePaths =
            {
                "stratus": dojoConfig.baseUrl +"/stratus",
                "dojo": dojoConfig.baseUrl +"/dojo",
                "dijit": dojoConfig.baseUrl + "/dijit",
                "dojox": dojoConfig.baseUrl + "/dojox"
            };
            if (!dojoConfig.xdWaitSeconds)
            {
                dojoConfig.xdWaitSeconds = 10;
            }
        }


        if (typeof stratusConfig == "undefined")
        {
            stratusConfig = {};
        }
        stratusConfig.baseUrl = baseUrl
        if (crossDomain)
        {
            var domain = this.getDomain();
            if (stratusConfig.proxyPath)
            {
                stratusConfig.proxyUrl = domain + proxyPath + "/proxy.aspx?url=";
            }
            else
            {
                stratusConfig.proxyUrl = domain + "riaproxy/proxy.aspx?url=";
            }
        }
        else
        {
            delete stratusConfig.proxyUrl;
        }
    }
};
ria.load();
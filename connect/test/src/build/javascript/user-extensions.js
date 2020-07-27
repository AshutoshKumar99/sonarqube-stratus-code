/**
 * Verifies if a given image has been loaded by the browser. The sole
 * existence of the corresponding img tag in the DOM is not sufficient for that
 * matter.
 * 
 * @param locator a Selenium locator that points to an <img> tag
 */
Selenium.prototype.isImageLoaded = function(locator) 
{
    var img = this.browserbot.findElementOrNull(locator);
    if (!img)
    {
        return false;
    }
    if (!img.complete)
    {
        return false;
    }
    if (img.naturalWidth == 0)
    {
        return false;
    }
    
    // image appears to be loaded
    return true;
};

/**
 * Resizes the browser window (IDE) or IFRAME (RC and Core) to the given 
 * dimensions.
 *
 * @param dimensions a single string with two integers separated by a comma 
 */
Selenium.prototype.doResizeWindow = function(dimensions) 
{
    var window = this.browserbot.getCurrentWindow();
    var dims = dimensions.split(",");
    if (window.parent == window.self)
    {
        this.browserbot.resizeWindow(parseInt(dims[0]), parseInt(dims[1]));
    }
    else
    {
        //XXX It might be a bit risky to rely on the follwoing ID 
        var el = window.parent.document.getElementById('selenium_myiframe');
        el.style.width = dims[0] + "px";
        el.style.height = dims[1] + "px";
    }

}

PageBot.prototype.resizeWindow = function(width, height)
{
    var window = this.getCurrentWindow();
    window.resizeTo(width + window.outerWidth - window.innerWidth, 
        height + window.outerHeight - window.innerHeight);                
}
BrowserBot.prototype.resizeWindow = PageBot.prototype.resizeWindow; 

IEBrowserBot.prototype.resizeWindow = function(width, height)
{
    var window = this.getCurrentWindow();
    var screen = window.screen;
    window.moveTo(0, 0);
    window.resizeTo(screen.availWidth, screen.availHeight);
    deltaWidth = window.outerWidth - window.innerWidth;
    deltaHeight = window.outerHeight - window.innerHeight;
    var dims = getInnerDimensions();
    window.resizeTo(width + screen.availWidth - dims.width, height 
        + screen.availHeight - dims.height);
}

IEBrowserBot.prototype.getInnerDimensions = function()
{
    var viewportwidth;
    var viewportheight;
    var window = this.getCurrentWindow();
    var document = window.document;
    if (typeof document.documentElement != 'undefined'
            && typeof document.documentElement.clientWidth !=
            'undefined' && document.documentElement.clientWidth != 0)
    {
        // IE6 in standards compliant mode (i.e. with a valid doctype as the first line in the document)
        viewportwidth = document.documentElement.clientWidth,
        viewportheight = document.documentElement.clientHeight
    }
    else
    {
        // older versions of IE
        viewportwidth = document.getElementsByTagName('body')[0].clientWidth,
        viewportheight = document.getElementsByTagName('body')[0].clientHeight
    }
    return {width: viewportwidth, height: viewportheight};
}

Selenium.prototype.doDoubleClickAtImproved = function(locator, coordString) {
    if (this.browserbot.doubleClickElementImproved)
    {
        var element = this.browserbot.findElement(locator);
        var clientXY = getClientXY(element, coordString);
        this.browserbot.doubleClickElementImproved(element, clientXY[0], clientXY[1]);
    }
};


MozillaBrowserBot.prototype.doubleClickElementImproved = function(element, clientX, clientY) {
   this._fireEventOnElement("dblclick", element, clientX, clientY);
};

IEBrowserBot.prototype.doubleClickElementImproved = function(element, clientX, clientY) {
   this._fireEventOnElementImproved("dblclick", element, clientX, clientY);
};

Selenium.prototype.doClickAtImproved = function(locator, coordString) {
    if (this.browserbot.clickElementImproved)
    {
        var element = this.browserbot.findElement(locator);
        var clientXY = getClientXY(element, coordString);
        this.browserbot.clickElementImproved(element, clientXY[0], clientXY[1]);
    }
};

MozillaBrowserBot.prototype.clickElementImproved = function(element, clientX, clientY) {
   this._fireEventOnElement("click", element, clientX, clientY);
};


IEBrowserBot.prototype.clickElementImproved = function(element, clientX, clientY) {
   this._fireEventOnElementImproved("click", element, clientX, clientY);
};

IEBrowserBot.prototype._fireEventOnElementImproved = function(eventType, element, clientX, clientY) {
    var win = this.getCurrentWindow();
    triggerEvent(element, 'focus', false);

    var wasChecked = element.checked;

    // Set a flag that records if the page will unload - this isn't always accurate, because
    // <a href="javascript:alert('foo'):"> triggers the onbeforeunload event, even thought the page won't unload
    var pageUnloading = false;
    var pageUnloadDetector = function() {
        pageUnloading = true;
    };
    win.attachEvent("onbeforeunload", pageUnloadDetector);
    this._modifyElementTarget(element);
    this.browserbot.triggerMouseEvent(element, eventType, true, clientX, clientY);


    // If the page is going to unload - still attempt to fire any subsequent events.
    // However, we can't guarantee that the page won't unload half way through, so we need to handle exceptions.
    try {
        win.detachEvent("onbeforeunload", pageUnloadDetector);

        if (this._windowClosed(win)) {
            return;
        }

        // Onchange event is not triggered automatically in IE.
        if (isDefined(element.checked) && wasChecked != element.checked) {
            triggerEvent(element, 'change', true);
        }

    }
    catch (e) {
        // If the page is unloading, we may get a "Permission denied" or "Unspecified error".
        // Just ignore it, because the document may have unloaded.
        if (pageUnloading) {
            LOG.logHook = function() {
            };
            LOG.warn("Caught exception when firing events on unloading page: " + e.message);
            return;
        }
        throw e;
    }
};

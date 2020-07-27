// A part of the FlowControl extension for use with Selenium IDE
// (Firefox Plugin). Open the Selenium IDE and select the Options 
// menu item. Select Options and add the full path and filename
// of this file in the Selenium Core Extensions (user-extensions_2.js)
// field.  Close and re-open the IDE to begin using it.  For more
// information see the following URL:
// http://51elliot.blogspot.com/2008/02/selenium-ide-goto.html

var gotoLabels= {};
var whileLabels = {}; 

// overload the oritinal Selenium reset function
Selenium.prototype.reset = function() {
   // reset the labels
   this.initialiseLabels();
   // proceed with original reset code
   this.defaultTimeout = Selenium.DEFAULT_TIMEOUT; 
   this.browserbot.selectWindow("null"); 
   this.browserbot.resetPopups();
}

Selenium.prototype.initialiseLabels = function()
{
    gotoLabels  = {};
    whileLabels = { ends: {}, whiles: {} };
    var command_rows = [];
    var numCommands = testCase.commands.length;
    for (var i = 0; i < numCommands; ++i) {
       var x = testCase.commands[i];
       command_rows.push(x);
    }    
    var cycles = [];
    for( var i = 0; i < command_rows.length; i++ ) {
        if (command_rows[i].type == 'command')
        switch( command_rows[i].command.toLowerCase() ) {
            case "label":
                gotoLabels[ command_rows[i].target ] = i;
                break;
            case "while":
            case "endwhile":
                cycles.push( [command_rows[i].command.toLowerCase(), i] )
                break;    
        }
    }
    var i = 0;    
    while( cycles.length ) {
        if( i >= cycles.length ) {
            throw new Error( "non-matching while/endWhile found" );
        }
        switch( cycles[i][0] ) {
            case "while":
                 if(    ( i+1 < cycles.length )  && ( "endwhile" == cycles[i+1][0] ) ) {
                     // pair found
                     whileLabels.ends[ cycles[i+1][1] ] = cycles[i][1];
                     whileLabels.whiles[ cycles[i][1] ] = cycles[i+1][1];
                     cycles.splice( i, 2 );
                     i = 0;
                 } else ++i;
                 break;
             case "endwhile":
                 ++i;
                 break;
        }
    } 
}    

Selenium.prototype.continueFromRow = function( row_num ) 
{
    if(row_num == undefined || row_num == null || row_num < 0) {
        throw new Error( "Invalid row_num specified." );
    }
    testCase.debugContext.debugIndex = row_num;
}

// do nothing. simple label
Selenium.prototype.doLabel      = function(){};

Selenium.prototype.doGotolabel  = function( label ) 
{
    if( undefined == gotoLabels[label] ) {
        throw new Error( "Specified label '" + label + "' is not found." );
    }
    this.continueFromRow( gotoLabels[ label ] );
};
    
Selenium.prototype.doGoto = Selenium.prototype.doGotolabel;

Selenium.prototype.doGotoIf = function( condition, label ) 
{
    if( eval(condition) ) this.doGotolabel( label );
}

Selenium.prototype.doWhile = function( condition ) 
{
    if( !eval(condition) ) {
        var last_row = testCase.debugContext.debugIndex;
        var end_while_row = whileLabels.whiles[ last_row ];
        if( undefined == end_while_row ) throw new Error( "Corresponding 'endWhile' is not found." );
        this.continueFromRow( end_while_row );
    }
}

Selenium.prototype.doEndWhile = function() 
{
    var last_row = testCase.debugContext.debugIndex;
    var while_row = whileLabels.ends[ last_row ] - 1;
    if( undefined == while_row ) throw new Error( "Corresponding 'While' is not found." );
    this.continueFromRow( while_row );
}

CommandBuilders.add('accessor', function(window) {
	// Define the command that we will return
	var result = { accessor: "attribute", disabled: true };
	
	// Determine if the user has clicked on an img tag
	var element = this.getRecorder(window).clickedElement;
	if (element && element.tagName && 'img' == element.tagName.toLowerCase()) {
	
		// This command is only valid if there is a title attribute on this image tag
		var title = element.getAttribute("title");
		if (title) {
			// Form an attributeLocator for the title attribute
			result.target = this.getRecorder(window).clickedElementLocator + '@title';
			
			// Capture the title as the value to be matched and enable this command
			result.value = exactMatchPattern(title);
			result.disabled = false;
		} 		
	}
	return result;
});

// Adds right click command to assert, verify etc IF the selected element is an editable input or not
CommandBuilders.add('accessor', function(window) {
	// Define the command that we will return
	var result = { accessor: 'editableInput', disabled: true, value: null, booleanAccessor: true };
	
	// Determine if the user has clicked on an input or a span
	var element = this.getRecorder(window).clickedElement;
	if (element && element.tagName && ('input' == element.tagName.toLowerCase() || 
	                                   'span' == element.tagName.toLowerCase() || 
	                                   'div' == element.tagName.toLowerCase())) {
	
		// The target is the select element
		result.target = this.getRecorder(window).clickedElementLocator;
		result.disabled = false;
	
		// If it is not an input, use the Not form
	    if ('input' != element.tagName.toLowerCase()) {
			result.accessor = 'notEditableInput'; 
		} else {
		    // If it is an input, choose the Not form if it is disabled
			var readOnlyNode = element.getAttributeNode('readonly');
            if (readOnlyNode) {
                // DGF on IE, every input element has a readOnly node, but it may be false
                if (typeof(readOnlyNode.nodeValue) == 'boolean') {
                    var readOnly = readOnlyNode.nodeValue;
                    if (readOnly) {
  			            result.accessor = 'notEditableInput';
                    }
                } else {
                    result.accessor = 'notEditableInput';
                }
            }
        }
	}
	return result;
});

// Adds right click command to assert, verify etc the selected label of a select element in a form
CommandBuilders.add('accessor', function(window) {
	// Define the command that we will return
	var result = { accessor: "selectedLabel", disabled: true };
	
	// Determine if the user has clicked on a select tag
	var element = this.getRecorder(window).clickedElement;
	if (element && element.tagName && 'select' == element.tagName.toLowerCase()) {
	
		// The target is the select element
		result.target = this.getRecorder(window).clickedElementLocator;
		result.disabled = false;
			
		var selectedIndex = element.selectedIndex;
		if (selectedIndex == -1) {
			// Handle no selection as the empty string
			result.value = '';
		}
		else {
			// Capture the inner HTML (the text shown in the select) as the value to be matched
			var selectedOption = element.options[selectedIndex];
			result.value = exactMatchPattern(selectedOption.innerHTML);
		}
	}
	return result;
});
// Adds right click command to assert, verify etc the presence of a table if a click is indside of it.  It only finds the closest enclosing table.
CommandBuilders.add('accessor', function(window) {
	// Define the command that we will return
	var result = { accessor: 'elementPresent', disabled: true, value: null, booleanAccessor: true };
	
	// Find the element clicked on
	var element = this.getRecorder(window).clickedElement;
	
	// Fail fast
	if ( ( ! element) || ! element.tagName) {
	    return result;
	}
	
	// Go up the hierarchy until we find a table or hit the top
    while (element != null) {
       if (element.tagName && 'table' == element.tagName.toLowerCase()) {
           break;
       }
       element = element.parentNode;
    }
  
    // If we have an element, we have a table.  Get a locator for it
    if (element) {
    	result.target = this.getRecorder(window).locatorBuilders.build(element);
		result.disabled = false;
	}

	return result;
});
// Adds right click command to assert, verify etc the number of rows in a table if a click is indside of it.  It only finds the closest enclosing table.
CommandBuilders.add('accessor', function(window) {
	// Define the command that we will return
	var result = { accessor: 'tableRows', disabled: true, value: null, booleanAccessor: false };
	
	// Find the element clicked on
	var element = this.getRecorder(window).clickedElement;
	
	// Fail fast
	if ( ( ! element) || ! element.tagName) {
	    return result;
	}
	
	// Go up the hierarchy until we find a table or hit the top
    while (element != null) {
       if (element.tagName && 'table' == element.tagName.toLowerCase()) {
           break;
       }
       element = element.parentNode;
    }
  
    // If we have an element, we have a table.  Get a locator for it and record the number of rows
    if (element) {
    	result.target = this.getRecorder(window).locatorBuilders.build(element);
    	result.value = result.value = element.rows.length.toString();
		result.disabled = false;
	}

	return result;
});


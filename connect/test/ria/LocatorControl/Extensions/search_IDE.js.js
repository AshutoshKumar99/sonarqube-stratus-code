function searchExpression(strSearchIn,strSearchFor,debugMode){

  /* debugMode (default:0, runtimeDebugMode:1,fullDebugMode:2) */
  try{
	 		/* Convert both input to lower so that search is not case sensative. */
    	expToBeSearched	= strSearchIn.toLowerCase();
			searchForString = strSearchFor.toLowerCase();
  			 
      found = new Boolean(false);
    	arraySearchWords = searchForString.split(" ");
			if (debugMode == 2){document.write("<br> Total Array Items: " + arraySearchWords.length + "<br>");}

    	for (var i in arraySearchWords){
         	if (i!=undefined){
          	var pos = expToBeSearched.search(arraySearchWords[i]);
						if (debugMode == 2){document.write("Index: " + i + ", Word: " + arraySearchWords[i] + ", Pos: " + pos + "<br>");}
            
						if (pos !=-1){
          			 /*
          			 Ensure that search character has leading space, to make sure that found not set to true if it is part of word.
          			 e.g. While searchcing 'a' from 'Bob is a boy', in string 'London Palace' where word 'palace' includes char 'a'.  
          			 */						
          			 var leadingchr = expToBeSearched.substr(pos - 1, 1);
								 if (debugMode == 2){document.write("word: " + expToBeSearched.substr(pos - 1, 1) + ", leading char; " + leadingchr + "<br>");}
						          		
								 if (leadingchr == " "  || pos ==0){
              			 found = true;
              			 break;
  						 } /* end if (leadingchr == " ")*/
    			 } /* end if (pos !=-1)*/
  			} /* end If (i!='undefined')*/
			}/*end for (var i in arraySearchWords)*/
			if (debugMode == 0){
    		 return (found);}
			else {
			   strResult = "Found = " + found + " when string '" + searchForString + "' was searched in '" + expToBeSearched + "'.";
				 return (strResult);} 	 
  }/* end try*/
  catch(err)
  {
    txt ="Error description: " + err.description + "\n\n";
    return(txt);
  }/* end catch*/
};

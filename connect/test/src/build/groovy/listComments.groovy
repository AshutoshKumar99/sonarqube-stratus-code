/**
 * Recursively find every html file and displays the first comment in it
 */
println """
<html>
    <table border="1" style="font-family:arial;font-size:8pt">
"""

new File(".").eachFileRecurse {
	if (it.getPath() =~ /.*\\.*\\.*html$/ && 
      !(it.getPath() =~ /.*\\.svn\\.*/) &&
      !(it.getPath() =~ /.*suite.*/)) {
	    def matcher= it.getText("UTF-8") =~ /<!--(.*?)-->/
	    if (matcher.find()) {
            println """
              <tr>
                <td>${it.getName()}</td>
                <td>${matcher.group(1)}</td>
              </tr>
            """
        }
        else
        {
            println """
              <tr style='background-color:pink'>
                <td>${it.getName()}</td>
                <td >&nbsp</td>
              </tr>
            """
        }
	}
}

println """
    </table>
</html>
"""

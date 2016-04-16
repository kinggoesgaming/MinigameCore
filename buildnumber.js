var system = require('system');
var args = system.args;

var stable = !(JSON.parse(system.args[1]));
var targetVer = system.args[2];

if (window.XMLHttpRequest) {
    xmlhttp=new XMLHttpRequest();
} else {
    xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
}
xmlhttp.onreadystatechange=function() {
    if (xmlhttp.readyState==4 && xmlhttp.status==200) {
        var currentTag = JSON.parse(xmlhttp.responseText)[0].name;
        var res = currentTag.split("-",2);
        if(!stable) {
          // Build is unstable
          if(res.length == 2) {
            var version = res[0];
            var build = res[1];
            // Was a prerelease
            if(version == targetVer) {
              // Push the build up one
              console.log(targetVer+"-"+(parseInt(build)+1));
            } else {
              // Start the build from one
              console.log(targetVer+"-1");
            }
          } else {
            // Was not a prerelease
            console.log(targetVer+"-1");
          }
        } else {
          // Build is stable
          console.log(targetVer);
        }
        phantom.exit();
    }
}

xmlhttp.open("GET", "https://api.github.com/repos/minigamecore/minigamecore/tags", true );
xmlhttp.send(); 

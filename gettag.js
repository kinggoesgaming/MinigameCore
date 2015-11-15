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
        if (xmlhttp.readyState==4 && xmlhttp.status==200)
        {
            console.log(JSON.parse(xmlhttp.responseText)[0].name);
			phantom.exit();
        }
    }
    xmlhttp.open("GET", "https://api.github.com/repos/minigamecore/minigamecore/tags", true );
    xmlhttp.send(); 

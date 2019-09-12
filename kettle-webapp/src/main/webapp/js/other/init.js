function GetUrl(url) {
	var contextPath = document.getElementById('context-path').value;
	//var runType = document.getElementById("runType").value;
	var newurl = contextPath + '/' + url;
	return newurl;
}

mxBasePath = GetUrl('mxgraph2');
mxResourceExtension = '.properties'
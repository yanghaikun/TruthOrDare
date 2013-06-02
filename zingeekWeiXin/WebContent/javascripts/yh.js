/*
 * 获取URL参数
 */
function getUrlParam(param){  
	param = param.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
	var r1 = "[\\?&]"+param+"=([^&#]*)";
	var r2 = new RegExp( r1 );
	var r3 = r2.exec( window.location.href );
	if( r3 == null ) return "";
	else return r3[1];
}

/*
 * 获取url全部参数key
 */
function findUrlParamsKeys(url) {
	var result = [];
	
    var params=url.href.split("?")[1].split("&");  
    for(var i = 0; i < params.length; i++) {
    	result.push(params[i].split("=")[0]);
    }
    
    return result;
}
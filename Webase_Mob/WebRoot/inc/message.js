//javascript document
window.onload = getMsg;
window.onresize = resizeDiv;
window.onerror = function(){}

var divTop,divLeft,divWidth,divHeight,docHeight,docWidth,objTimer,i = 0;
function getMsg()
{
   clearInterval( timer );
	loadover();
	Loading.style.display='none';
    menuShow = 0;
    
	menuExpand(); 
    try{
    divTop = parseInt(document.getElementById("eMeng").style.top,10)
    divLeft = parseInt(document.getElementById("eMeng").style.left,10)
    divHeight = parseInt(document.getElementById("eMeng").offsetHeight,10)
    divWidth = parseInt(document.getElementById("eMeng").offsetWidth,10)
    docWidth = document.body.clientWidth;
    docHeight = document.body.clientHeight;
    document.getElementById("eMeng").style.top = parseInt(document.body.scrollTop,10) + docHeight + 10;//  divHeight
    document.getElementById("eMeng").style.left = parseInt(document.body.scrollLeft,10) + docWidth - divWidth
    document.getElementById("eMeng").style.visibility="visible"
    objTimer = window.setInterval("moveDiv()",10)
    }
    catch(e){}
}

function resizeDiv()
{
    try{
    divHeight = parseInt(document.getElementById("eMeng").offsetHeight,10)
    divWidth = parseInt(document.getElementById("eMeng").offsetWidth,10)
    docWidth = document.body.clientWidth;
    docHeight = document.body.clientHeight;
    document.getElementById("eMeng").style.top = docHeight - divHeight + parseInt(document.body.scrollTop,10)
    document.getElementById("eMeng").style.left = docWidth - divWidth + parseInt(document.body.scrollLeft,10)
    }
    catch(e){}
}

function moveDiv()
{
    try
    {
    if(parseInt(document.getElementById("eMeng").style.top,10) <= (docHeight - divHeight + parseInt(document.body.scrollTop,10)))
    {
    window.clearInterval(objTimer)
    objTimer = window.setInterval("resizeDiv()",1)
    }
    divTop = parseInt(document.getElementById("eMeng").style.top,10)
    document.getElementById("eMeng").style.top = divTop - 1
    }
    catch(e){}
}

//

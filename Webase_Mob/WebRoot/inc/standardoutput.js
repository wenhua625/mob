
function errAlert(errContent,errReason,errSolve){
	var errAlertString="";
	if(errContent!=""&&errContent!="undefined"){
		errAlertString="����\n"+errContent+"\n";
		
		if(errReason!=null&&errReason!=""&&errReason!="undefined")
			errAlertString+="\n����ԭ��\n"+errReason;

		if(errSolve!=null&&errSolve!=""&&errSolve!="undefined")
			errAlertString+="\n\n���������\n"+errSolve;

		errAlertString+="\n";
		
		alert(errAlertString);
		
	}else{
		alert("�����ڼ���� !\n\n��������Ϊ�� !");
	}
}


// ������Ϣ��
function warnAlert(warnContent){
	var warnAlertString="";
	
	if(warnContent!=""){
		warnAlertString="���棺\n"+warnContent+"\n";
		
		alert(warnAlertString);
		
	}else{
		alert("��������Ϊ��!");
	}
}
//����������������log4js����F1����ʾ/����log��Ϣ��ֱ�ӵ���log(v)�Ϳ������ˡ�
function log(v)
    {
        var s = "<li><font color=blue>"+new Date()+"</font> "+v+"</li>";
        logPanel_log.innerHTML+=s;
    }

window.onhelp = function() {
      logPanel.style.display=logPanel.style.display=='none'?"block":"none";
      return false;
}
document.write("<div id=logPanel style='display:none'>"+
    "<button onclick=\"logPanel_log.innerHTML='';\">clear</button>&nbsp;"+
    "<button onclick=\"logPanel_log.innerHTML+='<hr>';\">HR</button>"+
    "<div id='logPanel_log'></div></div>");



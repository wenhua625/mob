//-----------����
function goBack(){
	history.back();
}
//-----------��������
function goURL(URL){
	window.location.href = URL;
}



//************************ҳ�������⺯��**************************

/*
У���������ںϷ���
formName:	form��
inputName��	input�������
format��	У�����ڸ�ʽ��Ŀǰ֧��YYYY-MM-DD��MM-DD-YYYY��YYYY/MM/DD��MM/DD/YYYY��
*/
function checkDate(formName, inputName, format){
	var supportFormat = new Array();
	supportFormat[0] = "YYYY-MM-DD";
	supportFormat[1] = "MM-DD-YYYY";
	supportFormat[2] = "YYYY/MM/DD";
	supportFormat[3] = "MM/DD/YYYY";
	var dateValue = eval("document."+formName+"."+inputName+".value");
	//����Ƿ�֧�ָ�ʽ
	var max = supportFormat.length;
	var i = 0;
	var index = 0;
	var year = "";
	var month = "";
	var day = "";
	var formatSplitSymbol = ""
	var valueSplitSymbolFir = ""
	var valueSplitSymbolSec = ""
	while(i < max){
		if(supportFormat[i] == format){
			break;
		}
		i++;
	}
	if(i == max){
		alert("У�麯����֧�ִ˸�ʽ��");
		return false;
	}
	index = i;
	dateValue = trim(dateValue);
	if(dateValue.length != format.length){
		eval("document."+formName+"."+inputName+".focus()")
		eval("document."+formName+"."+inputName+".select()")
		alert("�������ڳ��Ȳ���ȷ��");
		return false;
	}
	//�ֱ�����ָ�ʽ
	switch(index){
		case 0:
			year = dateValue.substring(0,4);
			month = dateValue.substring(5,7);
			day = dateValue.substring(8,10);
			valueSplitSymbolFir = dateValue.substring(4,5);
			valueSplitSymbolSec = dateValue.substring(7,8);
			formatSplitSymbol = format.substring(4,5);
			break;
		case 1:
			year = dateValue.subString(6,10);
			month = dateValue.subString(0,2);
			day = dateValue.subString(3,5);
			valueSplitSymbolFir = dateValue.substring(3,4);
			valueSplitSymbolSec = dateValue.substring(5,6);
			break;
		case 2:
			year = dateValue.substring(0,4);
			month = dateValue.substring(5,7);
			day = dateValue.substring(8,10);
			valueSplitSymbolFir = dateValue.substring(4,5);
			valueSplitSymbolSec = dateValue.substring(7,8);
			formatSplitSymbol = format.substring(4,5);
			break;
		case 4:
			year = dateValue.subString(6,10);
			month = dateValue.subString(0,2);
			day = dateValue.subString(3,5);
			valueSplitSymbolFir = dateValue.substring(3,4);
			valueSplitSymbolSec = dateValue.substring(5,6);
			break;
	}
	//����ָ���
	if(valueSplitSymbolFir.length > 0 && valueSplitSymbolFir != formatSplitSymbol){
		alert("�ָ�������");
		eval("document."+formName+"."+inputName+".focus()")
		eval("document."+formName+"."+inputName+".select()")
		return false;
	}
	else if(valueSplitSymbolSec.length > 0 && valueSplitSymbolSec != formatSplitSymbol){
		alert("�ָ�������");
		eval("document."+formName+"."+inputName+".focus()")
		eval("document."+formName+"."+inputName+".select()")
		return false;
	}
	//������
	if(year.length > 0){
		if(!isYear(year)){
			eval("document."+formName+"."+inputName+".focus()")
			eval("document."+formName+"."+inputName+".select()")
			return false;
		}
	}
	//������
	if(month.length > 0){
		if(!isMonth(month)){
			eval("document."+formName+"."+inputName+".focus()")
			eval("document."+formName+"."+inputName+".select()")
			return false;
		}
	}
	//������
	if(day.length > 0){
		if(!isDay(day)){
			eval("document."+formName+"."+inputName+".focus()")
			eval("document."+formName+"."+inputName+".select()")
			return false;
		}
	}
	return true;
}

/*
�Ƿ�����
formName:	form��
inputName��	input�������
*/
function isNumber(formName, inputName){
	value = eval("document."+formName+"."+inputName+".value");
	firstIsZero = false;
	if(value != null && value.length > 0){
		for(i = 0 ; i < value.length ; i++){
			if(value.charAt(i)>'9'||value.charAt(i)<'0'){
				alert("���������֣�")
				eval("document."+formName+"."+inputName+".focus()")
				eval("document."+formName+"."+inputName+".select()")
				return false;
			}
		}
		if(value.charAt(0) == '0' && value.length > 2){
				alert("������ȷ���֣�")
				eval("document."+formName+"."+inputName+".focus()")
				eval("document."+formName+"."+inputName+".select()")
				return false;
		}
	}
	return true;
}

//�Ƿ�����
function isNumber(value){
	var firstIsZero = false;
	if(value != null && value.length > 0){
		for(i = 0 ; i < value.length ; i++){
			if(value.charAt(i)>'9'||value.charAt(i)<'0'){
				alert("���������֣�")
				return false;
			}
		}
		if(value.charAt(0) == '0' && value.length > 2){
				alert("������ȷ���֣�")
				return false;
		}
	}
	return true;
}

//�Ƿ񸡵�
function isFNumber(formName, inputName){
	value = eval("document."+formName+"."+inputName+".value");
	firstIsZero = false;
	if(value != null && value.length > 0){
		nF = 0;
		if(value.charAt(0) == '-' || (value.charAt(0)<='9'&&value.charAt(0)>='0')){
			for(i = 1 ; i < value.length ; i++){
				if((value.charAt(i)>'9'||value.charAt(i)<'0')&&(value.charAt(i)!='.')){
					alert("���������ֻ���С���㣡")
					eval("document."+formName+"."+inputName+".focus()")
					eval("document."+formName+"."+inputName+".select()")
					return false;
				}
				if(value.charAt(i)=='.')
				{
				  nF++;
				  if (nF>1)
				  {
					alert("ֻ�ܰ���һ��С���㣡")
					eval("document."+formName+"."+inputName+".focus()")
					eval("document."+formName+"."+inputName+".select()")
					return false;
				  }
				}
			}
		}
		else{
			return false;
		}
	}
	return true;
}
//�Ƿ�Ϊ��
function isNull(formName, inputName){
	oldString = eval("document."+formName+"."+inputName+".value");
	newString = trim(oldString);
	if(newString == null || newString.length <= 0){
		alert("��������� * ���ı������ݣ�")
		eval("document."+formName+"."+inputName+".focus()")
		eval("document."+formName+"."+inputName+".select()")
		return true;
	}
	return false;
}
//ȥ���ո�
function trim(srcString){
	newString = "";
	for(i = 0 ; i < srcString.length ; i++)
		if(srcString.charAt(i) != ' ')
			newString += srcString.charAt(i);
	return newString;
}
//�����
function isYear(formName, inputName){
	if(!isNumber(inputName))
		return false;
	oldString = eval("document."+formName+"."+inputName+".value");
	newString = trim(oldString);
	if(newString == null || newString.length != 4){
		alert("������4λ���")
		eval("document."+formName+"."+inputName+".focus()")
		eval("document."+formName+"."+inputName+".select()")
		return false;
	}
	return true;
}

//�����
function isYear(value){
	if(!isNumber(value))
		return false;
	var oldString = value;
	var newString = trim(oldString);
	if(newString == null || newString.length != 4){
		alert("������4λ���")
		return false;
	}
	return true;
}

//��⼾
function isSeason(formName, inputName){
	if(!isNumber(inputName))
		return false;
	oldString = eval("document."+formName+"."+inputName+".value");
	newString = trim(oldString);
	if(newString == null || newString.length != 1){
		alert("������1λ����")
		eval("document."+formName+"."+inputName+".focus()")
		eval("document."+formName+"."+inputName+".select()")
		return false;
	}
	else{
		if(newString.charAt(0) < '1' || newString.charAt(0) > '4'){
			alert("������1��4֮�����ֵ")
			eval("document."+formName+"."+inputName+".focus()")
			eval("document."+formName+"."+inputName+".select()")
			return false;
		}
	}
	return true;
}
//�����
function isMonth(formName, inputName){
	if(!isNumber(inputName))
		return false;
	oldString = eval("document."+formName+"."+inputName+".value");
	newString = trim(oldString);
	if(newString == null || newString.length != 2){
		alert("������2λ�·�")
		eval("document."+formName+"."+inputName+".focus()")
		eval("document."+formName+"."+inputName+".select()")
		return false;
	}
	else{
		if(newString.charAt(0) != '0' && newString.charAt(0) != '1'){
			alert("��������ȷ�·�")
			eval("document."+formName+"."+inputName+".focus()")
			eval("document."+formName+"."+inputName+".select()")
			return false;
		}
		else if(newString.charAt(0) == '1'){
			if(newString.charAt(1) > '2'){
				alert("��������ȷ�·�")
				eval("document."+formName+"."+inputName+".focus()")
				eval("document."+formName+"."+inputName+".select()")
				return false;
			}
		}
		else if(newString.charAt(0) == '0'){
			if(newString.charAt(1) == '0'){
				alert("��������ȷ�·�")
				eval("document."+formName+"."+inputName+".focus()")
				eval("document."+formName+"."+inputName+".select()")
				return false;
			}
		}

	}
	return true;
}

//�����
function isMonth(value){
	if(!isNumber(value))
		return false;
	var oldString = value;
	var newString = trim(oldString);
	if(newString == null || newString.length != 2){
		alert("������2λ�·�")
		return false;
	}
	else{
		if(newString.charAt(0) != '0' && newString.charAt(0) != '1'){
			alert("��������ȷ�·�")
			return false;
		}
		else if(newString.charAt(0) == '1'){
			if(newString.charAt(1) > '2'){
				alert("��������ȷ�·�")
				return false;
			}
		}
		else if(newString.charAt(0) == '0'){
			if(newString.charAt(1) == '0'){
				alert("��������ȷ�·�")
				return false;
			}
		}

	}
	return true;
}

//�����
function isDay(formName, inputName){
	if(!isNumber(inputName))
		return false;
	oldString = eval("document."+formName+"."+inputName+".value");
	newString = trim(oldString);
	if(newString == null || newString.length != 2){
		alert("������2λ����")
		eval("document."+formName+"."+inputName+".focus()")
		eval("document."+formName+"."+inputName+".select()")
		return false;
	}
	else{
		if(newString.charAt(0) != '0' && newString.charAt(0) != '1' && newString.charAt(0) != '2' && newString.charAt(0) != '3'){
			alert("��������ȷ����")
			eval("document."+formName+"."+inputName+".focus()")
			eval("document."+formName+"."+inputName+".select()")
			return false;
		}
		else if(newString.charAt(0) == '3'){
			if(newString.charAt(1) > '1'){
				alert("��������ȷ����")
				eval("document."+formName+"."+inputName+".focus()")
				eval("document."+formName+"."+inputName+".select()")
				return false;
			}
		}
		else if(newString.charAt(0) == '0'){
			if(newString.charAt(1) == '0'){
				alert("��������ȷ����")
				eval("document."+formName+"."+inputName+".focus()")
				eval("document."+formName+"."+inputName+".select()")
				return false;
			}
		}

	}
	return true;
}

//�����
function isDay(value){
	if(!isNumber(value))
		return false;
	var oldString = value;
	var newString = trim(oldString);
	if(newString == null || newString.length != 2){
		alert("������2λ����")
		return false;
	}
	else{
		if(newString.charAt(0) != '0' && newString.charAt(0) != '1' && newString.charAt(0) != '2' && newString.charAt(0) != '3'){
			alert("��������ȷ����")
			return false;
		}
		else if(newString.charAt(0) == '3'){
			if(newString.charAt(1) > '1'){
				alert("��������ȷ����")
				return false;
			}
		}
		else if(newString.charAt(0) == '0'){
			if(newString.charAt(1) == '0'){
				alert("��������ȷ����")
				return false;
			}
		}

	}
	return true;
}

//�Ƿ�Ϊ��
function isdateNull(formName, inputName){
	oldString = eval("document."+formName+"."+inputName+".value");
	newString = trim(oldString);
	if(newString == null || newString.length <= 0){
		return true;
	}
	return false;
}
//���ڵ�ǰ����
function validDate(select) 
{
  var newString = trim(select);
  if(newString==null||trim(newString).length!=8) {
    alert("���ڸ�ʽ����");
    return false;
  }
  year = newString.substring(0,4);
  month = newString.substring(4,6);
  day = newString.substring(6);
  current = new Date();
  cur = current.getFullYear()+""+ (current.getMonth() < 9 ? '0' : '') + (current.getMonth() + 1) + ""
		+ (current.getDate() < 10 ? '0' : '') + current.getDate();
  if((newString-0)<=(cur-0)) {
    alert("�������ڲ���С�ڵ�ǰ����");
    return false;
  }
  return isDay(day)&&isMonth(month)&&isYear(year);
}
//�꣬�£��ձ���ȫ��Ϊ�ջ�ȫ����Ϊ�գ��������
function checkdate(formName, syear,smonth,sday)
{
   	if (!isdateNull(formName, syear) && !isdateNull(formName, smonth) && !isdateNull(formName, sday))//�꣬�£��ն���Ϊ��
	{
		if (isYear(formName, syear) && isMonth(formName, smonth) && isDay(formName, sday))//�꣬�£��ն���ȷ
        	{return true;}
        else
         	{return false;}
	}
	else if (isdateNull(formName, syear) && isdateNull(formName, smonth) && isdateNull(formName, sday))//�꣬�£��ն�Ϊ��
	{   
		return true; }
	else
	{  alert("������������");
		return false;
	}
    
}
//��ʼʱ�䲻�ܴ��ڽ���ʱ��
function checkBeginEnd(formName,beginDate,endDate)
{
	startdate = eval("document."+formName+"."+beginDate+".value");
	if(startdate==null) return true;
	startdate = trim(startdate);
	if(startdate=="") return true;
	enddate = eval("document."+formName+"."+endDate+".value");
	if(enddate==null) return true;
	enddate = trim(enddate);
	if(enddate=="") return true;
	if(startdate > enddate) {
          alert("��ʼ���ڲ��ܴ��ڽ�ֹ���ڣ�����������");
		  return false;
	}
    else {
         return true;
	}
}
//��ʼʱ�䲻�ܴ��ڽ���ʱ��
function validdate(formName, syear,smonth,sday,eyear,emonth,eday)
{
	if(!isdateNull(formName, syear) && !isdateNull(formName, smonth) && !isdateNull(formName, sday) && !isdateNull(formName, eyear) && !isdateNull(formName, emonth) && !isdateNull(formName, eday))
	{
startdate= trim(eval("document."+formName+"."+syear+".value"))+trim(eval("document."+formName+"."+smonth+".value"))+trim(eval("document."+formName+"."+sday+".value"));
enddate= trim(eval("document."+formName+"."+eyear+".value"))+trim(eval("document."+formName+"."+emonth+".value"))+trim(eval("document."+formName+"."+eday+".value"));
	   if(startdate > enddate)
		{alert("��ʼ���ڲ��ܴ��ڽ�ֹ���ڣ�����������");
		   return false;
		}
       else
          return true;
	}
   else
	   return true;
}
//�ж�ʱ���Ƿ�Ϊ��Чֵ
function validFullDate(formName, sdate)
{
    oldString = eval("document."+formName+"."+sdate+".value");
	newString = trim(oldString);
	if (isdateNull(formName,sdate))
	{ return true;}
	
	if(newString != null && newString.length > 0){
		for(i = 0 ; i < newString.length ; i++){
			if(newString.charAt(i)>'9'||newString.charAt(i)<'0'){
				alert("������8λ��Ч���ڣ�yyyymmdd����")
				return false;
			}
		}
		if(newString.charAt(0) == '0' && newString.length > 2){
				alert("������8λ��Ч���ڣ�yyyymmdd����")
				return false;
		}
	}
	if(newString == null || newString.length < 8)
	{
		alert("������8λ��Ч���ڣ�yyyymmdd��");
        eval("document."+formName+"."+sdate+".focus()")
		eval("document."+formName+"."+sdate+".select()")
		return false;
	}
	year = newString.substring(0,4);
	month = newString.substring(4,6);
	day = newString.substring(6,8);
	//current = new Date();
    //cur = current.getFullYear()
     //У���·�
    if(month.charAt(0) != '0' && month.charAt(0) != '1'){
			alert("��������ȷ�·�")
			eval("document."+formName+"."+sdate+".focus()")
			eval("document."+formName+"."+sdate+".select()")
			return false;
	}
	else if(month.charAt(0) == '1'){
		if(month.charAt(1) > '2'){
			alert("��������ȷ�·�")
			eval("document."+formName+"."+sdate+".focus()")
			eval("document."+formName+"."+sdate+".select()")
			return false;
		}
	}
	else if(month.charAt(0) == '0'){
		if(month.charAt(1) == '0'){
			alert("��������ȷ�·�")
			eval("document."+formName+"."+sdate+".focus()")
			eval("document."+formName+"."+sdate+".select()")
			return false;
		}
	}
	//У������
	if(day.charAt(0) != '0' && day.charAt(0) != '1' && day.charAt(0) != '2' && day.charAt(0) != '3'){
		alert("��������ȷ����")
		eval("document."+formName+"."+sdate+".focus()")
		eval("document."+formName+"."+sdate+".select()")
		return false;
	}
	else if(day.charAt(0) == '3'){
		if(day.charAt(1) > '1'){
			alert("��������ȷ����")
			eval("document."+formName+"."+sdate+".focus()")
			eval("document."+formName+"."+sdate+".select()")
			return false;
		}
	}
	else if(day.charAt(0) == '0'){
		if(day.charAt(1) == '0'){
			alert("��������ȷ����")
			eval("document."+formName+"."+sdate+".focus()")
			eval("document."+formName+"."+sdate+".select()")
			return false;
		}
	}
	return true;
}

//************************�����㴦��������ѯ���ҳ��ʹ�ã�**************************

function Do_Layer(choice,thisForm){
	var A2 = eval(thisForm);
	if	(choice=='visible'){
		if (navigator.userAgent.indexOf('MSIE') > -1){
			A2.style.left=document.body.scrollLeft+document.body.clientWidth/2-A2.offsetWidth/2;
			A2.style.top=document.body.scrollTop+45;
			A2.style.visibility='visible';
		}
		else{
			document.A2.style.left=document.body.scrollLeft+document.body.clientWidth/2-document.A2.offsetWidth/2;
			document.A2.style.top=document.body.scrollTop+45;
			document.A2.visibility='visible';
		}
	}
	if	(choice=='hidden'){
		if (navigator.userAgent.indexOf('MSIE') > -1)
			A2.style.visibility='hidden';
		else
			document.A2.visibility='hidden';
	}
}

function Do_Layer0(choice,thisForm){
	if (thisForm=='Layer0'){
		if	(choice=='visible'){
			if (navigator.userAgent.indexOf('MSIE') > -1)
				Layer0.style.visibility='visible';
			else
				document.Layer0.visibility='visible';
		}
		if(choice=='hidden'){
			if (navigator.userAgent.indexOf('MSIE') > -1)
				Layer0.style.visibility='hidden';
			else
				document.Layer0.visibility='hidden';
		}
	}
}
function Yes_Win(URL,Name,Width,Height){
 if (!Width) { Width =360;}
 if (!Height){ Height=480;}
 if (!Name)  { Name  ="Members";}
 var win_cfg = "toolbar=no,directories=no,status=no,menubar=no,scrollbars=no,resilocation=no,scrollbars=no,resizable=no,alwaysRaised=yes,dependent=yes,align=center,valign=center,top=0,left=0,width=" + Width + ",height=" + Height;
 window.open(URL,Name,win_cfg)
}

function confirmDel(item) {
    if (confirm("ȷ��Ҫɾ����ѡ�е�" + item + "��"))
		return true;
	return false;
}
//���ѡ��
function CheckCheckBox(formname,checkname,item) {
    var checkinfo = eval("document."+formname+"."+checkname+"");
    if(checkinfo==null) {
      alert("û��"+item+"��Ϣ!");
      return false;
    }
    var flag = "0";
    if(checkinfo.length==null) {
       if(checkinfo.checked==false)
           {  alert("��ѡ��"+item+"!");
              return false;
           }
     }
    else {
        for(var i=0;i<checkinfo.length;i++) {
          if(checkinfo[i].checked) {
            flag = "1";
            break;
         }
        }
        if(flag=="0"){
           alert("��ѡ��"+item+"!");
           return false;
        }
    }
    return true;
}
//������ѡ��һ��
function CheckCheckBoxOnly(formname,checkname,item) {
    var checkinfo = eval("document."+formname+"."+checkname+"");
    if(checkinfo==null) {
      alert("û��"+item+"��Ϣ!");
      return false;
    }
    var flag = 0;
    if(checkinfo.length==null) {
       if(checkinfo.checked==false)
           {  alert("��ѡ��"+item+"!");
              return false;
           }
     }
    else {
        for(var i=0;i<checkinfo.length;i++) {
          if(checkinfo[i].checked) {
            flag++;
          }
         }
        if(flag>1){
           alert("ֻ��ѡ��һ��"+item+"!");
           return false; 
         }
    }
    return true;
}
//�жϲ�������
function notNumber(formName, inputName){
	value = eval("document."+formName+"."+inputName+".value");
	if(value != null && value.length > 0){
	  if(value.charAt(0)!='-') {
		for(i = 0 ; i < value.length ; i++){
			if(value.charAt(i)>'9'||value.charAt(i)<'0'){
				alert("���������֣�")
				eval("document."+formName+"."+inputName+".focus()")
				eval("document."+formName+"."+inputName+".select()")
				return true;
			}
		}
	  }
	  else {
	   if(value.length==1) {
	     alert("���������֣�")
	     return true;
	   }
	   for(i = 1 ; i < value.length ; i++){
			if(value.charAt(i)>'9'||value.charAt(i)<'0'){
				alert("���������֣�")
				eval("document."+formName+"."+inputName+".focus()")
				eval("document."+formName+"."+inputName+".select()")
				return true;
			}
		}
	  }
	  if(value.charAt(0) == '0' && value.length >= 2){
			alert("������ȷ���֣�")
			eval("document."+formName+"."+inputName+".focus()")
			eval("document."+formName+"."+inputName+".select()")
			return true;
	   }
	}
	return false;
}
//�ж�IP��ַ�Ϸ���
function verifyIP(textObj){
  var tmpIPValue=trim(textObj.value);
  var tmpLength=tmpIPValue.length;
  
  if (tmpLength == 0){
    return true;   
  }
  for (var i = 0; i < tmpLength;i++){
    var aChar = tmpIPValue.substring(i,i+1);
    if(aChar != "." && (aChar < "0" || aChar > "9")) {
      alert ("�����IP��ַ����(��ʽ:xxx.xxx.xxx.xxx)");
      textObj.focus(this);
      textObj.select(this);
      return false;
    }
  }
  
  var count=0;
  for (var j = 0; j < tmpLength;j++){
    var aChar = tmpIPValue.substring(j,j+1);
    if(aChar == ".") {
        count++;
    }
  }
  if (count!=3){
    alert("�����IP��ַ����(��ʽ:xxx.xxx.xxx.xxx)");
	textObj.focus(this);
    textObj.select(this);
	return false;
  }
  var index1 = tmpIPValue.indexOf(".",0);
  var first = tmpIPValue.substring(0,index1)-0;
  if(first<0||first>255) {
    alert ("�����IP��ַ��һλ����(��ʽ:xxx.xxx.xxx.xxx)");
    return false;
  }
  var index2 = tmpIPValue.indexOf(".",index1+1);
  var second = tmpIPValue.substring(index1+1,index2);
  if(second<0||second>255) {
    alert ("�����IP��ַ�ڶ�λ����(��ʽ:xxx.xxx.xxx.xxx)");
    return false;
  }
  var index3 = tmpIPValue.indexOf(".",index2+1);
  var third = tmpIPValue.substring(index2+1,index3);
  if(third<0||third>255) {
    alert ("�����IP��ַ����λ����(��ʽ:xxx.xxx.xxx.xxx)");
    return false;
  }
  var four = tmpIPValue.substring(index3+1);
  if(four<0||four>255) {
    alert ("�����IP��ַ����λ����(��ʽ:xxx.xxx.xxx.xxx)");
    return false;
  }
  return true;

}
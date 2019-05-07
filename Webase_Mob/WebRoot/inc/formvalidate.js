////////////////////////////////////////////////////////////////////////////////
//	name: formValidate.js
//	version: 1.0
//	author:�Ʊ�ӱ
//	description: Check form inputs at client side before submit a form to server side.
//	note: ʹ��ʱ��Ҫ�ڱ��жϵ�Input�����м�������ֵ��
//			must="true"									˵����Input������
//			isNumeric="true"							˵����Input���������д�������͵�ֵ����Ҫ������
//			isDate="true"								˵����Input���������д�������ͣ�YYYY-MM-DD����ֵ����Ҫ������Ĭ���Ǳ��������ģ���2002-02-01,��2002-2-1�Ͳ�����
//			isIntegral="false"							˵����Input���������д�������ͣ�YYYY-MM-DD����ֵ���Բ��������ģ���2002-02-01��2002-2-1������
//			maxlength="22"								��󳤶�
//			minlength="15"								��С����
//			isTel="true"								�绰���ͣ������绰���������ֻ����룬�������
//			isZip="true"								��������
//			isInt="true"								�Ƿ�Ϊ������
//			isEmail="true"								�ж��Ƿ�email��ַ��Ч
//			isAlphanumeric="true"						��ĸ�������֣�����֤����
//			maxlength4float="7"							float���͵������λ��
//			minValue="0"								isInt��isNumeric="true"����Ч��must="true"ʱ,ֵ>0��must="true"û��ʱ,ֵ>=0
//      	maxValue="100"								isInt��isNumeric="true"����Ч��ֵ<=100
//		�˴����ṩ�����⼸��������
//			isDate(objTgt)							�жϴ������objTgt����д��ֵ��objTgt.value���Ƿ�Ϊ�������͡�
//			judgeLeapYear(yearStr)					�Ƿ�Ϊ���ꡣ
//			judgeSmallMonth(monthStr)				�Ƿ�ΪС�¡�
//			isModified(eForm)						ҳ���Ƿ��й��޸ġ�
// ���ṩһ��javascript У���
// JavaScript library ������У�������
// ��ҪĿ¼��
// 1.�Ƿ�Ϊ�ջ�ȫ�ǿ��ַ� 
// 2.�ַ����Ƿ�ֻ����0��9������
// 3.�Ƿ�����(�����������)
// 4.�Ƿ�����
// 5.С����m,n��������С����β��0��������������
// 6.�ַ����Ƿ�ȫ����ĸ
// 7.�ַ����Ƿ�ȫ����ĸ������
// 8.����
// 9.�������֤����
// 10.�绰����
// 11.��������
// 12.�Ƿ�Ϸ���email
// 18.���˹������ʺ�(�Ϻ�β��205)
// 19.��λ�������ʺ�(�Ϻ�β��205)
// 20.����/�����ʺ�
////////////////////////////////////////////////////////////////////////////////


// ���ύ֮ǰ����ҳ�����ж�����ĺϷ��ԣ�����must������isNumeric������isDate�ȵȣ���
function formValidate(formName){
	var isLeapYear=false; /*����[����Ϊ29��]*/
	var isSmallMonth=false; /*С��[����Ϊ30��]*/
	
	if (!document.forms(formName)) return false;

	for (var i=0;i<document.forms(formName).length;i++)	{
		objTgt = document.forms(formName).item(i);
		//alert(objTgt.type);
		// �������� Text ������е�ֵ����ȥ�ղ�����
		//if(objTgt.type=="text")objTgt.value=objTgt.value.trim();

		if (objTgt.must=='true'&& isWhitespace(objTgt.value)){
			if (objTgt.disabled==true){
				continue;
			}else{
				if(objTgt.title!=""){
					errAlert ("������ ["+objTgt.title+"] δ��д !","Ϊ�˱�֤���ݵ������ԣ�ϵͳҪ��ĳЩֵ������д��\n������û����д���е� ["+objTgt.title+"] ��һ�","������д ["+objTgt.title+"] ��һ�");
				}else{
					errAlert("������δ��д !","Ϊ�˱�֤���ݵ������ԣ�ϵͳҪ��ĳЩֵ������д��\n������û����д���е�ĳ���","����������д��");
				}
				if(objTgt.type!="select-one"){
					focusItem(objTgt);
				}
				return false;
			}
		}
			
		if(objTgt.value==null){continue;}
		var formValidate_valueLength=objTgt.value.length;	
		//�ж���󳤶�		
		var formValidate_maxlength=objTgt.maxlength;
		if(formValidate_maxlength!=""){			
			if(parseFloat(formValidate_maxlength)<parseFloat(formValidate_valueLength)){
					if(objTgt.title!=""){
						errAlert ("["+objTgt.title+"] ���г��ȳ������ֵ:"+formValidate_maxlength+"!","Ϊ�˱�֤���ݵ�׼ȷ�ԣ�ϵͳҪ��ĳЩ���Ȳ��������ֵ��\n�������� ["+objTgt.title+"] ���г��ȳ������ֵ��","������д ["+objTgt.title+"] ��һ�");
					}else{
						errAlert ("Ӧ�ó��Ȳ��������ֵ:"+formValidate_maxlength+"!","Ϊ�˱�֤���ݵ�׼ȷ�ԣ�ϵͳҪ��ĳЩ���Ȳ��������ֵ��\n��������ĳ�����г��ȳ������ֵ��","������д��");
					}
					
					focusItem(objTgt);
					return false;
			}
		}
		//�ж���С����		
		
		var formValidate_minlength=objTgt.minlength;
		if(formValidate_minlength!=""){			
			if(parseFloat(formValidate_minlength)>parseFloat(formValidate_valueLength)){
					if(objTgt.title!=""){
						errAlert ("["+objTgt.title+"] ���г��ȱ��볬����Сֵ:"+formValidate_minlength+"!","Ϊ�˱�֤���ݵ�׼ȷ�ԣ�ϵͳҪ��ĳЩ���ȱ��볬����Сֵ��\n�������� ["+objTgt.title+"] ���г��ȱ��볬����Сֵ��","������д ["+objTgt.title+"] ��һ�");
					}else{
						errAlert ("Ӧ�ó��ȱ��볬����Сֵ:"+formValidate_minlength+"!","Ϊ�˱�֤���ݵ�׼ȷ�ԣ�ϵͳҪ��ĳЩ���ȱ��볬����Сֵ��\n��������ĳ�����г��ȱ��볬����Сֵ��","������д��");
					}
					
					focusItem(objTgt);
					return false;
			}
		}
		//�ж�float���͵��������ֵ���󳤶�			
		var formValidate_valueLength4float=objTgt.value.length;	
		var pos2=objTgt.value.indexOf (".")
		if(pos2>-1){
			formValidate_valueLength4float=(objTgt.value.substring(0,pos2)).length;
		}		
		var formValidate_maxlength4float=objTgt.maxlength4float;
		if(formValidate_maxlength4float!=""){			
			if(parseFloat(formValidate_maxlength4float)<parseFloat(formValidate_valueLength4float)){
					if(objTgt.title!=""){
						errAlert ("["+objTgt.title+"] �����������ֲ��ܳ��� "+formValidate_maxlength4float+"λ!","Ϊ�˱�֤���ݵ�׼ȷ�ԣ�ϵͳҪ��ĳЩ���Ȳ��������ֵ��\n�������� ["+objTgt.title+"] ���г��ȳ������ֵ��","������д ["+objTgt.title+"] ��һ�");
					}else{
						errAlert ("�������ֲ��ܳ��� "+formValidate_maxlength4float+"λ!","Ϊ�˱�֤���ݵ�׼ȷ�ԣ�ϵͳҪ��ĳЩ���Ȳ��������ֵ��\n��������ĳ�����г��ȳ������ֵ��","������д��");
					}
					
					focusItem(objTgt);
					return false;
			}
		}		
		
		
		if(objTgt.value!=''){
			//��ʼ�����ж�
			//�ж������ֺ�������һ���ķ�Χ
			if (objTgt.isNumeric=='true'){
				if (!isFloat(objTgt.value)){
					if(objTgt.title!=""){
						errAlert ("["+objTgt.title+"] ����Ӧ����д���� !","Ϊ�˱�֤���ݵ�׼ȷ�ԣ�ϵͳҪ��ĳЩֵ������д���֣�\n�������� ["+objTgt.title+"] ����û����д���֡�","������д ["+objTgt.title+"] ��һ�");
					}else{
						errAlert ("Ӧ����д���� !","Ϊ�˱�֤���ݵ�׼ȷ�ԣ�ϵͳҪ��ĳЩֵ������д���֣�\n��������ĳ������û����д���֡�","������д��");
					}
					
					focusItem(objTgt);
					return false;
				}
				var objTgtValue=new Number(objTgt.value);
				if (objTgt.maxValue>=0){
					var maxV=objTgt.maxValue;
					if(objTgtValue.valueOf()>maxV){//����ֵ���ܴ������ֵ���������
						errAlert("["+objTgt.title+"] ���ֵ���ܴ���"+maxV);
						focusItem(objTgt);
						return false;	
					}
				}
				if (objTgt.minValue>=0){
					var minV=objTgt.minValue;
					if(objTgtValue.valueOf() ==0&&objTgt.must=='true'){//����Ǳ���������Ϊ0
						errAlert("["+objTgt.title+"] ���ֵ�������"+0);
						focusItem(objTgt);
						return false;
					}
					if(objTgtValue.valueOf()<minV){//����ֵ���Ե�����Сֵ
						errAlert("["+objTgt.title+"] ���ֵ����С��"+minV);
						focusItem(objTgt);
						return false;
					}
				}
					
				//if(objTgt.isPow==1){
				//	if (Math.abs(objTgtValue)>=Math.pow(10,maxV)||Math.abs(objTgtValue)<Math.pow(10,(minV-1))){
				//		errAlert ("�������ֳ����涨��Χ!\n���ַ�Χ��\""+minV+"\"��\""+maxV+"\"");
				//		
				//		focusItem(objTgt);
				//		return false;
				//	}
				//}else{
				//	if(objTgtValue.valueOf()>maxV||objTgtValue.valueOf()<minV){
				//		errAlert ("�������ֳ����涨��Χ!\n���ַ�Χ��\""+minV+"\"��\""+maxV+"\"");
				//		
				//		focusItem(objTgt);
				//		return false;
				//	}
				//}
				continue;
			}
			//�Ϸ��ʼ���ַ���			
			else if (objTgt.isEmail=="true"){
				if(isEmail(objTgt.value)==false){
					if(objTgt.title!=""){
						errAlert ("["+objTgt.title+"] ����Ӧ����д��Ч�ĵ����ʼ���ַ !","Ϊ�˱�֤���ݵ�׼ȷ�ԣ�ϵͳҪ��ĳЩֵ������д�����ʼ���ַ��\n�������� ["+objTgt.title+"] ����û����д�����ʼ���ַ��","������д ["+objTgt.title+"] ��һ�");
					}else{
						errAlert ("Ӧ����д��Ч�ĵ����ʼ���ַ !","Ϊ�˱�֤���ݵ�׼ȷ�ԣ�ϵͳҪ��ĳЩֵ������д��Ч�ĵ����ʼ���ַ��\n��������ĳ������û����д��Ч�ĵ����ʼ���ַ��","������д��");
					}
						focusItem(objTgt);
						return false;
				}
				continue;
			}
			//��������
			else if (objTgt.isInt=="true"){
				if(isInt(objTgt.value)==false){
					if(objTgt.title!=""){
						errAlert ("["+objTgt.title+"] ����Ӧ����д����!","Ϊ�˱�֤���ݵ�׼ȷ�ԣ�ϵͳҪ��ĳЩֵ������д������\n�������� ["+objTgt.title+"] ����û����д������","������д ["+objTgt.title+"] ��һ�");
					}else{
						errAlert ("Ӧ����д���� !","Ϊ�˱�֤���ݵ�׼ȷ�ԣ�ϵͳҪ��ĳЩֵ������д������\n��������ĳ������û����д������","������д��");
					}	
					focusItem(objTgt);
					return false;
				}
				var objTgtValue=new Number(objTgt.value);
				if (objTgt.maxValue>=0){
					var maxV=objTgt.maxValue;
					if(objTgtValue.valueOf()>maxV){//����ֵ���ܴ������ֵ���������
						errAlert("["+objTgt.title+"] ���ֵ���벻�ܴ���"+maxV);
						focusItem(objTgt);
						return false;	
					}
				}
				if (objTgt.minValue>=0){
					var minV=objTgt.minValue;
					if(objTgtValue.valueOf() ==0&&objTgt.must=='true'){//����Ǳ���������Ϊ0
						errAlert("["+objTgt.title+"] ���ֵ�������"+0);
						focusItem(objTgt);
						return false;
					}
					if(objTgtValue.valueOf()<minV){//����ֵ���Ե�����Сֵ
						errAlert("["+objTgt.title+"] ���ֵ����С��"+minV);
						focusItem(objTgt);
						return false;
					}
				}
				continue;
			}
			//���ֺ���ĸ������֤����
			else if (objTgt.isAlphanumeric=="true"){
				if(isAlphanumeric(objTgt.value)==false){
					if(objTgt.title!=""){
						errAlert ("["+objTgt.title+"] ����Ӧ����д���ֻ�����ĸ!","Ϊ�˱�֤���ݵ�׼ȷ�ԣ�ϵͳҪ��ĳЩֵ������д���ֻ�����ĸ��\n�������� ["+objTgt.title+"] ����û����д����","������д ["+objTgt.title+"] ��һ�");
					}else{
						errAlert ("Ӧ����д���ֻ�����ĸ !","Ϊ�˱�֤���ݵ�׼ȷ�ԣ�ϵͳҪ��ĳЩֵ������д���ֻ�����ĸ��\n��������ĳ������û����д����","������д��");
					}	
					focusItem(objTgt);
					return false;
				}
				continue;
			}			
			//��������
			else if (objTgt.isDate=="true"){
				if(isDate(objTgt)==false){
					focusItem(objTgt);
					return false;
				}
				continue;
			}
				
			//�绰,�ֻ�����
			else if (objTgt.isTel=="true"){
				if(isPhoneNumber(objTgt.value)==false){
					if(objTgt.title!=""){
						errAlert ("["+objTgt.title+"] ����Ӧ����д��ȷ�ĵ绰����!","Ϊ�˱�֤���ݵ�׼ȷ�ԣ��绰����ֻ�������ֺ�-()+��\n�������� ["+objTgt.title+"] �����зǷ��ַ���","������д ["+objTgt.title+"] ��һ�");
					}else{
						errAlert ("Ӧ����ȷ�ĵ绰���� !","Ϊ�˱�֤���ݵ�׼ȷ�ԣ��绰����ֻ�������ֺ�-()+��\n�������ڵ绰�������зǷ��ַ���","������д��");
					}					
					focusItem(objTgt);
					return false;
				}
				continue;
			}
			//��������
			else if (objTgt.isZip=="true"){
				if((isPostCode(objTgt.value))==false){
					if(objTgt.title!=""){
						errAlert ("["+objTgt.title+"] ����Ӧ����д��ȷ���ʱ�!","Ϊ�˱�֤���ݵ�׼ȷ�ԣ��ʱ�ֻ����6λ���֣�\n�������� ["+objTgt.title+"] ������д����","������д ["+objTgt.title+"] ��һ�");
					}else{
						errAlert ("Ӧ����д��ȷ���ʱ� !","Ϊ�˱�֤���ݵ�׼ȷ�ԣ��ʱ�ֻ����6λ���֡�","������д��");
					}
					focusItem(objTgt);
					return false;
				}
				continue;
			}
		}
	
	}
	return true;
}

// �ж��Ƿ������ڡ�
function isDate(objTgt){
	var inDate=objTgt.value;
	var isIntegral="false";
	if (objTgt.isIntegral=="false"){
		isIntegral="false";
	}else{
		isIntegral="true";
	}
	//alert(isIntegral);

	if (inDate.length>=6 && inDate.length<=10 && inDate.indexOf("-")!=-1 && inDate.indexOf("-")!=inDate.lastIndexOf("-") && inDate.substring(inDate.indexOf("-")+1,inDate.lastIndexOf("-")).indexOf("-")==-1){
		var inYear=parseInt(inDate.substring(0,inDate.indexOf("-")));
		var inMonth=inDate.substring(inDate.indexOf("-")+1,inDate.lastIndexOf("-"));
			if(isIntegral=="true"){
				if(inMonth.length!=2){
					errAlert("�·���д���� !��ʾ�·ݵ����ֱ����������ģ���1����01��ʾ.","��ʾ�·ݵ����ֱ����������ģ���һ����01��ʾ���������� ["+objTgt.title+"] ������д�� "+inMonth+" ��Ȼ���������Χ�ڡ�","������д ["+objTgt.title+"] ��һ�");
					return false;
				}
			}
			if (inMonth.substring(0,1)=="0" && inMonth.length>1)
				inMonth=inMonth.substring(1,inMonth.length);
			inMonth=parseInt(inMonth);
		var inDay=inDate.substring(inDate.lastIndexOf("-")+1,inDate.length);
			if(isIntegral=="true"){
				if(inDay.length!=2){
					errAlert("������д���� !��ʾ���ڵ����ֱ����������ģ���1����01��ʾ.","��ʾ���ڵ����ֱ��봦��1��31֮�䣬���ұ�����������ʹ�С�¹���\n�������� ["+objTgt.title+"] ������д�� "+inDay+" ��Ȼ������Ҫ��","������д ["+objTgt.title+"] ��һ�");
					return false;
				}
			}
			if (inDay.substring(0,1)=="0" && inDay.length>1)
				inDay=inDay.substring(1,inDay.length);
			inDay=parseInt(inDay);
		
		//alert(inYear+"-"+inMonth+"-"+inDay);
		if (inYear && !(isNaN(inYear)) && inYear.toString().length==4){
			var year=inYear;
			isLeapYear=judgeLeapYear(inYear);
		}else if(inYear.toString().length!=4){
			errAlert("�Բ���ϵͳ��Ҫ��λ�� !","Ϊ�˱�֤���ڴ洢��׼ȷ�ԣ�ϵͳҪ�����е��������붼ʹ����λ���ֱ�ʾ��ݣ�\n�������� ["+objTgt.title+"] ����û��ʹ����λ�����롣","�������� ["+objTgt.title+"] ��һ�");
			
			focusItem(objTgt);
			return false;
		}else{
			errAlert("�����д���� !","","");
			
			focusItem(objTgt);
			return false;
		}
		
		if (inMonth && !(isNaN(inMonth)) && inMonth<=12 && inMonth >=1){
			var month=inMonth;
			isSmallMonth=judgeSmallMonth(inMonth);

		}else{
			errAlert("�·���д���� !","��ʾ�·ݵ����ֱ��봦��1��12֮�䣬�������� ["+objTgt.title+"] ������д�� "+inMonth+" ��Ȼ���������Χ�ڡ�","������д ["+objTgt.title+"] ��һ�");
			
			focusItem(objTgt);
			return false;
		}
	
		if (inDay && !(isNaN(inDay)) && inDay>=1 && (month==2?(isLeapYear?inDay<=29:inDay<=28):(isSmallMonth?inDay<=30:inDay<=31))){
			var day=inDay;

		}else{
			errAlert("������д���� !","��ʾ���ڵ����ֱ��봦��1��31֮�䣬���ұ�����������ʹ�С�¹���\n�������� ["+objTgt.title+"] ������д�� "+inDay+" ��Ȼ������Ҫ��","������д ["+objTgt.title+"] ��һ�");
			
			focusItem(objTgt);
			return false;
		}
		
		var inputDate=new Date();
		if (!(Date.parse(month + "-" + day + "-" + year))){
			errAlert("������д���� !","Ϊ�˱�֤���ڴ洢��׼ȷ�ԣ�ϵͳҪ�����е��������붼���� YYYY-MM-DD �ĸ�ʽ���룬\n�������� ["+objTgt.title+"] ���е�����ֵ ["+objTgt.value+"] ����������Ҫ��","�������� ["+objTgt.title+"] ��һ�");
			
			focusItem(objTgt);
			return false;
		}
		
	}else{
		errAlert("������Ĳ���һ������������ֵ !","Ϊ�˱�֤���ڴ洢��׼ȷ�ԣ�ϵͳҪ�����е��������붼���� YYYY-MM-DD �ĸ�ʽ���룬\n�������� ["+objTgt.title+"] ���е�����ֵ ["+objTgt.value+"] ����������Ҫ��","�������� ["+objTgt.title+"] ��һ�");
		
		focusItem(objTgt);
		return false;
	}
}


// �ж����ꡣ
function judgeLeapYear(yearStr){
	if(!(isNaN(yearStr)) && yearStr.toString().length==4 && (yearStr%100==0?yearStr%400==0:yearStr%4==0))
		return true;
	return false;
}

// �ж�С�¡�
function judgeSmallMonth(monthStr){
	if(monthStr==4||monthStr==6||monthStr==9||monthStr==11)
		return true;
	return false;	
}

// �Ƿ�������
function isInt(objNumber){	
	if(!isNaN(objNumber)){
		if(Math.round(objNumber)==objNumber){
			return true;
		}else{
			return false;
		}
	}else{
		return false;
	}
}

// ʹINPUT�����ȡ���㡣
function focusItem(objTgt){
	objTgt.select();
	objTgt.focus();
}

// �ж�ҳ���Ƿ��޸Ĺ���
// ����Ҫ�ж�ҳ��Ķ��� -- eForm��
// ����ֵ��
// true -- �����޸Ĺ�
// false -- û���޸Ĺ�
function isModified(eForm){
	var iNumElems = eForm.elements.length;
	for (var i=0;i<iNumElems;i++){
		var eElem = eForm.elements[i];
		// ������Ƿ��޸ġ�
		if ("text" == eElem.type || "TEXTAREA" == eElem.tagName){
			if (eElem.value != eElem.defaultValue) return true;
		}
		// checkBox��radioBox�Ƿ��޸ġ�
		else if ("checkbox" == eElem.type || "radio" == eElem.type){
			if (eElem.checked != eElem.defaultChecked) return true;
		}
		// select�Ƿ��޸ġ�
		else if ("SELECT" == eElem.tagName){
			var cOpts = eElem.options;
			var iNumOpts = cOpts.length;
			for (var j=0;j<iNumOpts;j++){
				var eOpt = cOpts[j];
				if (eOpt.selected != eOpt.defaultSelected) return true;
			}
		}
	}
	return false;
}
/**
 * �Ϸ��ʼ���ַ���
 */
 function isEmail(email) {
	//var regu = "^(([0-9a-zA-Z]+)|([0-9a-zA-Z]+[_.0-9a-zA-Z-]*[0-9a-zA-Z]+))@([a-zA-Z0-9-]+[.])+([a-zA-Z]{2}|net|NET|com|COM|gov|GOV|mil|MIL|org|ORG|edu|EDU|int|INT)$";
	//var isFomatMail = new RegExp(regu);

	var isFomatMail = new RegExp('^[_\.0-9a-z-]+@([0-9a-z][0-9a-z-]+\.)+[a-z]{2,3}$','');
	
	if (email.search(isFomatMail) >= 0) {
		return true;
	}
	return false;
}
// JavaScript library ������У�������
// ��ҪĿ¼��
// 1.�Ƿ�Ϊ�ջ�ȫ�ǿ��ַ� 
// 2.�ַ����Ƿ�ֻ����0��9������
// 3.�Ƿ�����(�����������)
// 4.�Ƿ�����
// 5.С����m,n��������С����β��0��������������
// 6.�ַ����Ƿ�ȫ����ĸ
// 7.�ַ����Ƿ�ȫ����ĸ������
// 8.����
// 9.�������֤����
// 10.�绰����
// 11.��������
// 12.�Ƿ�Ϸ���email
// 13.һ��checkbox���Ƿ�������һ��checked 
// 14.һ��checkbox���Ƿ�ֻ��һ��checked
// 15.һ��checkbox���Ƿ�ȫ��checked 
// 16.�����������һ��
// 17.����ѡ��һ��checkbox
// ����δ��ŵ�functionΪ������Ҳ���Ե���ʹ��

// ע�⣺���̶���ʽ�������������ڣ���������;������֤�����⣬�����δ�жϳ��Ⱥ��Ƿ�Ϊ�ա�

var digits = "0123456789";
var lowercaseLetters = "abcdefghijklmnopqrstuvwxyz"
var uppercaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
var whitespace = " \t\n\r";
var defaultEmptyOK = true


//�Ƿ� null ���� �� ����ȫ�ո�
function isEmpty(s)
{
   if ((s == null) || (s.length == 0) ) return true;
   
   for (i = 0; i < s.length; i++)
    {
        var c = s.charAt(i);

        if (whitespace.indexOf(c) == -1) return false;
    }    

    return true;   	
}

//1.�Ƿ�Ϊ�ջ�ȫ�ǿ��ַ�
function isWhitespace (s)

{   var i;

    if (isEmpty(s)) return true;

    for (i = 0; i < s.length; i++)
    {
        var c = s.charAt(i);

        if (whitespace.indexOf(c) == -1) return false;
    }

    return true;
}

//��s��ȥ��������bag�е��ַ���s="abcd" bag="ace" return "bd"
function stripCharsInBag (s, bag)

{   var i;
    var returnString = "";

    for (i = 0; i < s.length; i++)
    {
        var c = s.charAt(i);
        if (bag.indexOf(c) == -1) returnString += c;
    }

    return returnString;
}
//��s��ȥ����������bag�е��ַ� s="abcd" bag="ace" return "ac"
function stripCharsNotInBag (s, bag)

{   var i;
    var returnString = "";

    for (i = 0; i < s.length; i++)
    {
        var c = s.charAt(i);
        if (bag.indexOf(c) != -1) returnString += c;
    }

    return returnString;
}
//��s��ȥ�����ַ���s="ab c d " return "abcd"
function stripWhitespace (s)

{   return stripCharsInBag (s, whitespace)
}

//�ַ�c�Ƿ����ַ���s��
function charInString (c, s)
{   for (i = 0; i < s.length; i++)
    {   if (s.charAt(i) == c) return true;
    }
    return false
}

//ȥ���ַ���ǰ��Ŀ��ַ�
function stripInitialWhitespace (s)

{   var i = 0;

    while ((i < s.length) && charInString (s.charAt(i), whitespace))
       i++;

    return s.substring (i, s.length);
}
//�ַ��Ƿ�Ϊ��ĸ
function isLetter (c)
{   return ( ((c >= "a") && (c <= "z")) || ((c >= "A") && (c <= "Z")) )
}

//�ַ��Ƿ�Ϊ0-9
function isDigit (c)
{   return ((c >= "0") && (c <= "9"))
}

//�ַ��Ƿ�Ϊ��ĸ������
function isLetterOrDigit (c)
{   return (isLetter(c) || isDigit(c))
}
//�ַ��Ƿ�Ϊ���ֻ�"-"
function isTel(c)
{   return (((c >= "0") && (c <= "9"))|| (c=="-"))
}

//2.�ַ����Ƿ�ֻ����0��9������
function isInteger (s)

{   var i;

    if (isEmpty(s))
       if (isInteger.arguments.length == 1) return defaultEmptyOK;
       else return (isInteger.arguments[1] == true);


    for (i = 0; i < s.length; i++)
    {
        var c = s.charAt(i);

        if (!isDigit(c)) return false;
    }

    return true;
}


//3.�Ƿ�����(�����������)
function isSignedInteger (s)

{   if (isEmpty(s))
       if (isSignedInteger.arguments.length == 1) return defaultEmptyOK;
       else return (isSignedInteger.arguments[1] == true);

    else {
        var startPos = 0;
        var secondArg = defaultEmptyOK;

        if (isSignedInteger.arguments.length > 1)
            secondArg = isSignedInteger.arguments[1];

        if ( (s.charAt(0) == "-") || (s.charAt(0) == "+") )
           startPos = 1;
        return (isInteger(s.substring(startPos, s.length), secondArg))
    }
}

//�Ƿ�Ϊ����������������������ţ�
function isPositiveInteger (s)
{   var secondArg = defaultEmptyOK;

    if (isPositiveInteger.arguments.length > 1)
        secondArg = isPositiveInteger.arguments[1];

    return (isSignedInteger(s, secondArg)&& ( (isEmpty(s) && secondArg)  || (parseInt (s,10) > 0) ) );
}

//�Ƿ�Ϊ���ڵ���������������������ţ�
function isNonnegativeInteger (s)
{   var secondArg = defaultEmptyOK;

    if (isNonnegativeInteger.arguments.length > 1)
        secondArg = isNonnegativeInteger.arguments[1];

    return (isSignedInteger(s, secondArg)
         && ( (isEmpty(s) && secondArg)  || (parseInt (s,10) >= 0) ) );
}

//�Ƿ�ΪС��������������������ţ�
function isNegativeInteger (s)
{   var secondArg = defaultEmptyOK;

    if (isNegativeInteger.arguments.length > 1)
        secondArg = isNegativeInteger.arguments[1];

    return (isSignedInteger(s, secondArg)
         && ( (isEmpty(s) && secondArg)  || (parseInt (s,10) < 0) ) );
}

//�Ƿ�ΪС�ڵ���������������������ţ�
function isNonpositiveInteger (s)
{   var secondArg = defaultEmptyOK;

    if (isNonpositiveInteger.arguments.length > 1)
        secondArg = isNonpositiveInteger.arguments[1];

    return (isSignedInteger(s, secondArg)
         && ( (isEmpty(s) && secondArg)  || (parseInt (s,10) <= 0) ) );
}


//4.�Ƿ�����
function isFloat (s)
{   
    var i;
    var seenDecimalPoint = false;
    if (isEmpty(s))
       if (isFloat.arguments.length == 1) return defaultEmptyOK;
       else return (isFloat.arguments[1] == true);
    if (s == ".") return false;
    for (i = 0; i < s.length; i++)
    {
        // Check that current character is number.
        var c = s.charAt(i);
        if ((c == ".") && !seenDecimalPoint) seenDecimalPoint = true;
        else if (!isDigit(c)) return false;
    }
    return true;
}

//�Ƿ����֣����������ţ�
function isSignedFloat (s)

{   if (isEmpty(s))
       if (isSignedFloat.arguments.length == 1) return defaultEmptyOK;
       else return (isSignedFloat.arguments[1] == true);

    else {
        var startPos = 0;
        var secondArg = defaultEmptyOK;

        if (isSignedFloat.arguments.length > 1)
            secondArg = isSignedFloat.arguments[1];

        // skip leading + or -
        if ( (s.charAt(0) == "-") || (s.charAt(0) == "+") )
           startPos = 1;
        return (isFloat(s.substring(startPos, s.length), secondArg))
    }
}

//5.С����m,n��������С����β��0��������������
function isDecimal(s,m,n){
	if(!isFloat(s)) return false;
	if(String(parseInt(s,10)).length > m-n) return false;
	var ss = String(parseFloat(s));
	if(ss.indexOf(".")>=0 && ss.substring( ss.indexOf(".") + 1, ss.length).length > n ) return false;
	return true;
}


//6.�ַ����Ƿ�ȫ����ĸ
function isAlphabetic (s)
{   var i;

    if (isEmpty(s))
       if (isAlphabetic.arguments.length == 1) return defaultEmptyOK;
       else return (isAlphabetic.arguments[1] == true);
    for (i = 0; i < s.length; i++)
    {
        // Check that current character is letter.
        var c = s.charAt(i);
        if (!isLetter(c))
        return false;
    }
    // All characters are letters.
    return true;
}
//7.�ַ����Ƿ�ȫ����ĸ������
function isAlphanumeric (s)

{   var i;

    if (isEmpty(s))
       if (isAlphanumeric.arguments.length == 1) return defaultEmptyOK;
       else return (isAlphanumeric.arguments[1] == true);

    for (i = 0; i < s.length; i++)
    {
        var c = s.charAt(i);

        if (! (isLetter(c) || isDigit(c) ) )
        return false;
    }

    return true;
}


//8.���ڣ�format ���趨Ϊ "yyyy-MM-dd"��ע���Сд�����ָ����ɱ�
function isValidateDate(str) {
	
	if (isEmpty(str)) return true;
	
	if (str.indexOf("-") != -1) {
		return toValidateDate(str, "yyyy-MM-dd");
	} else if(str.indexOf("/") != -1) {
		return toValidateDate(str, "yyyy/MM/dd");
	} else if(str.indexOf(".") != -1) {
		return toValidateDate(str, "yyyy.MM.dd");
	} else {
		return toValidateDate(str, "yyyyMMdd");
	}
	
	return false;
}

function toValidateDate(str,format) {
	if(str.length != format.length) return false;

	var year = 2000;
	var month = 1;
	var day = 1;
	var hour = 0;
	var minute = 0;
	var second = 0;

	if(format.indexOf("yyyy") != -1) {
		if(isNaNEx(year = SearchEx(str,format,"yyyy"))) return false;
		format = format.replace(/yyyy/,year);
	}

	if(format.indexOf("MM") != -1) {
		if(isNaNEx(month = SearchEx(str,format,"MM"))) return false;
		format = format.replace(/MM/,month);
	}

	if(format.indexOf("dd") != -1) {
		if(isNaNEx(day = SearchEx(str,format,"dd"))) return false;
		format = format.replace(/dd/,day);
	}

	if(format.indexOf("HH") != -1) {
		if(isNaNEx(hour = SearchEx(str,format,"HH"))) return false;
		if(parseInt(hour,10) < 0 || parseInt(hour,10) > 23) return false;
		format = format.replace(/HH/,hour);
	}

	if(format.indexOf("mm") != -1) {
		if(isNaNEx(minute = SearchEx(str,format,"mm"))) return false;
		if(parseInt(minute,10) < 0 || parseInt(minute,10) > 59) return false;
		format = format.replace(/mm/,minute);
	}

	if(format.indexOf("ss") != -1) {
		if(isNaNEx(second = SearchEx(str,format,"ss"))) return false;
		if(parseInt(second,10) < 0 || parseInt(second,10) > 59) return false;
		format = format.replace(/ss/,second);
	}

	if(format != str) return false;

	return isValidDate(year,month,day);
}
//����
function isNaNEx(str) {
	if(str == "") return true;
	if(isNaN(str)) return true;
	if(str.indexOf(".") != -1) return true;
	return false;
}
//����
function SearchEx(source,pattern,str) {
	var index = pattern.indexOf(str);
	if(index == -1) return "error";
	return source.substring(index,index + str.length);
}
//����
function isValidDate(year,month,day) {
	month = parseInt(month,10);
	day = parseInt(day,10);

	if(month < 1 || month > 12) return false;
	if(day < 1 || day > 31) return false;
	if((month == 4 || month == 6 || month == 9 || month == 11) && (day == 31)) return false;
	if (month == 2) {
		var leap = (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0));
		if (day > 29 || (day == 29 && !leap)) return false;
	}
	return true;
}
//����
function getCurrDate() {
	var str = "";
	var date = new Date();
	str = str + date.getYear();
	if (date.getMonth() < 9) {
		str = str + "-" + "0" + (date.getMonth() + 1);
	} else {
		str = str + "-" + (date.getMonth() + 1);
	}
	if (date.getDate() < 10) {
		str = str + "-" + "0" + (date.getDate());
	} else {
		str = str + "-" + (date.getDate());
	}
	
	return str;
}


//9.�������֤����(15��18λ�����һλ���ֻ���ĸ���������֣����պϷ�)
function isResidentID (s){
	if(isWhitespace(s)) return true;//���Ϊ�շ���true
	if(s.length!=15 && s.length!=18) return false;
	if(!isAlphanumeric(s)) return false;
	var birthday;
	if(s.length==18){
		if( !isInteger(s.substring(0,s.length-1)) ) return false;
		birthday = s.substring(6,14);
	}
	if(s.length==15){
		if( !isInteger(s) ) return false;
		birthday = "19"+s.substring(6,12);
	}
	if(!isValidDate(  birthday.substring(0,4), birthday.substring(4,6), birthday.substring(6,8)  )) return false;
	return true;
	
}

//�ַ�������ʽ����(reformat("20021213","",4,"-",2,"-",2),�᷵��2002-12-13)
function reformat (s)

{   var arg;
    var sPos = 0;
    var resultString = "";

    for (var i = 1; i < reformat.arguments.length; i++) {
       arg = reformat.arguments[i];
       if (i % 2 == 1) resultString += arg;
       else {
           resultString += s.substring(sPos, sPos + arg);
           sPos += arg;
       }
    }
    return resultString;
}

//10.�绰����( ���ֺ�+,-,() )
function isPhoneNumber(s){
	if(stripCharsInBag(s,"0123456789-()+")!="") return false;
	return true;
}

//11.��������
function isPostCode(s){
	if (isEmpty(s)) return true;
	return(isInteger(s) && s.length==6);
}

//�Ƿ�ֻ�����Ϸ���email�ַ�
function isvalidEmailChar (s)
{   var i;

    for (i = 0; i < s.length; i++)
    {
        var c = s.charAt(i);

        if (! (isLetter(c) || isDigit(c) || (c=='@') || (c=='.') || (c=='_') || (c=='-') || (c=='+')) ) {
       	return false;
		}
    }

    return true;
}

//12.�Ƿ�Ϸ���email





///18.���˹������ʺ�
function checkPersonAccFundAcc(s){
	if(isWhitespace(s)) return true;//���Ϊ�շ���true
	if(s.length!=12) return false;
	if(!isAlphanumeric(s)) return false;
	if(s.substring(9,12)!="205") return false;
	var sum = 0;
	for(var i=0;i<8;i++){
		var number = Number(s.substring(7-i,8-i));
		sum += number*(i+2);
		//log("a"+Number(8-i)+"="+number);
	}
	//log("sum="+sum);
	var r = sum % 11;
	var s9 = Number(s.substring(8,9));
	//log("a9="+s9 +"//  r="+ r);
	if(r != s9) return false;
	return true;
}
///19.��λ�������ʺ�
function checkCorpAccFundAcc(s){
	if(isWhitespace(s)) return true;//���Ϊ�շ���true
	if(s.length!=12) return false;
	if(!isAlphanumeric(s)) return false;
	if(s.substring(9,12)!="205") return false;
	var sum = 0;
	for(var i=0;i<8;i++){
		var number = Number(s.substring(i,i+1));
		sum += number*(i+2);
		//log("a"+Number(i+1)+"="+number);
	}
	//log("sum="+sum);
	var r = sum % 11;
	var s9 = Number(s.substring(8,9));
	//log("a9="+s9 +"//  r="+ r);
	if(r != s9) return false;
	return true;
}
///20.����/�����ʺ�
function checkAccount(s){
	if(isWhitespace(s)) return true;//���Ϊ�շ���true
	if(s.length!=17) return false;
	if(!isAlphanumeric(s)) return false;
	var s6 = s.substring(0,6);
	var s89 = s.substring(7,9);
	var s910 = s.substring(8,10);
	if(s6=="601428"||s6=="405512"){
		if(s910=="11"){
			return true;
		}else{
			return false;
		}
	}else if(s6=="622258"||s6=="622259"){
		if(s89=="11"){
			return true;
		}else{
			return false;
		}		
	}
	return false;
}

//////���ÿ����루ע�⣬ԭ����Ϊ����ʹ�ã�δ�޸ģ�

function isCreditCard(st) {
  // Encoding only works on cards with less than 19 digits
  if (st.length > 19)
    return (false);

  sum = 0; mul = 1; l = st.length;
  for (i = 0; i < l; i++) {
    digit = st.substring(l-i-1,l-i);
    tproduct = parseInt(digit ,10)*mul;
    if (tproduct >= 10)
      sum += (tproduct % 10) + 1;
    else
      sum += tproduct;
    if (mul == 1)
      mul++;
    else
      mul--;
  }

  if ((sum % 10) == 0)
    return (true);
  else
    return (false);

}


function isVisa(cc)
{
  if (((cc.length == 16) || (cc.length == 13)) &&
      (cc.substring(0,1) == 4))
    return isCreditCard(cc);
  return false;
}


function isMasterCard(cc)
{
  firstdig = cc.substring(0,1);
  seconddig = cc.substring(1,2);
  if ((cc.length == 16) && (firstdig == 5) &&
      ((seconddig >= 1) && (seconddig <= 5)))
    return isCreditCard(cc);
  return false;

}

function isAmericanExpress(cc)
{
  firstdig = cc.substring(0,1);
  seconddig = cc.substring(1,2);
  if ((cc.length == 15) && (firstdig == 3) &&
      ((seconddig == 4) || (seconddig == 7)))
    return isCreditCard(cc);
  return false;

}

function isDinersClub(cc)
{
  firstdig = cc.substring(0,1);
  seconddig = cc.substring(1,2);
  if ((cc.length == 14) && (firstdig == 3) &&
      ((seconddig == 0) || (seconddig == 6) || (seconddig == 8)))
    return isCreditCard(cc);
  return false;
}

function isCarteBlanche(cc)
{
  return isDinersClub(cc);
}


function isDiscover(cc)
{
  first4digs = cc.substring(0,4);
  if ((cc.length == 16) && (first4digs == "6011"))
    return isCreditCard(cc);
  return false;

}


function isEnRoute(cc)
{
  first4digs = cc.substring(0,4);
  if ((cc.length == 15) &&
      ((first4digs == "2014") ||
       (first4digs == "2149")))
    return isCreditCard(cc);
  return false;
}

function isJCB(cc)
{
  first4digs = cc.substring(0,4);
  if ((cc.length == 16) &&
      ((first4digs == "3088") ||
       (first4digs == "3096") ||
       (first4digs == "3112") ||
       (first4digs == "3158") ||
       (first4digs == "3337") ||
       (first4digs == "3528")))
    return isCreditCard(cc);
  return false;

}

function isAnyCard(cc)
{
  if (!isCreditCard(cc))
    return false;
  if (!isMasterCard(cc) && !isVisa(cc) && !isAmericanExpress(cc) && !isDinersClub(cc) &&
      !isDiscover(cc) && !isEnRoute(cc) && !isJCB(cc)) {
    return false;
  }
  return true;

}

function isCardMatch (cardType, cardNumber)
{

	cardType = cardType.toUpperCase();
	var doesMatch = true;

	if ((cardType == "VISA") && (!isVisa(cardNumber)))
		doesMatch = false;
	if ((cardType == "MASTERCARD") && (!isMasterCard(cardNumber)))
		doesMatch = false;
	if ( ( (cardType == "AMERICANEXPRESS") || (cardType == "AMEX") )
                && (!isAmericanExpress(cardNumber))) doesMatch = false;
	if ((cardType == "DISCOVER") && (!isDiscover(cardNumber)))
		doesMatch = false;
	if ((cardType == "JCB") && (!isJCB(cardNumber)))
		doesMatch = false;
	if ((cardType == "DINERS") && (!isDinersClub(cardNumber)))
		doesMatch = false;
	if ((cardType == "CARTEBLANCHE") && (!isCarteBlanche(cardNumber)))
		doesMatch = false;
	if ((cardType == "ENROUTE") && (!isEnRoute(cardNumber)))
		doesMatch = false;
	return doesMatch;

}

function IsCC (st) {
    return isCreditCard(st);
}

function IsVisa (cc)  {
  return isVisa(cc);
}

function IsVISA (cc)  {
  return isVisa(cc);
}

function IsMasterCard (cc)  {
  return isMasterCard(cc);
}

function IsMastercard (cc)  {
  return isMasterCard(cc);
}

function IsMC (cc)  {
  return isMasterCard(cc);
}

function IsAmericanExpress (cc)  {
  return isAmericanExpress(cc);
}

function IsAmEx (cc)  {
  return isAmericanExpress(cc);
}

function IsDinersClub (cc)  {
  return isDinersClub(cc);
}

function IsDC (cc)  {
  return isDinersClub(cc);
}

function IsDiners (cc)  {
  return isDinersClub(cc);
}

function IsCarteBlanche (cc)  {
  return isCarteBlanche(cc);
}

function IsCB (cc)  {
  return isCarteBlanche(cc);
}

function IsDiscover (cc)  {
  return isDiscover(cc);
}

function IsEnRoute (cc)  {
  return isEnRoute(cc);
}

function IsenRoute (cc)  {
  return isEnRoute(cc);
}

function IsJCB (cc)  {
  return isJCB(cc);
}

function IsAnyCard(cc)  {
  return isAnyCard(cc);
}

function IsCardMatch (cardType, cardNumber)  {
  return isCardMatch (cardType, cardNumber);
}

//��������
//myFloat��Ҫ����������������֣�mfNumber����Ҫȡ���λ��
function floatRound(myFloat,mfNumber)
{
  var cutNumber = Math.pow(10,mfNumber-1);
  return Math.round(myFloat * cutNumber)/cutNumber;
}

function cmycurd(num){  //ת������Ҵ�д�����ʽ
  var str1 = '��Ҽ��������½��ƾ�';  //0-9����Ӧ�ĺ���
  var str2 = '��Ǫ��ʰ��Ǫ��ʰ��Ǫ��ʰԪ�Ƿ�'; //����λ����Ӧ�ĺ���
  var str3;    //��ԭnumֵ��ȡ����ֵ
  var str4;    //���ֵ��ַ�����ʽ
  var str5 = '';  //����Ҵ�д�����ʽ
  var i;    //ѭ������
  var j;    //num��ֵ����100���ַ�������
  var ch1;    //���ֵĺ������
  var ch2;    //����λ�ĺ��ֶ���
  var nzero = 0;  //����������������ֵ�Ǽ���
  
  num = Math.abs(num).toFixed(2);  //��numȡ����ֵ����������ȡ2λС��
  str4 = (num * 100).toFixed(0).toString();  //��num��100��ת�����ַ�����ʽ
  j = str4.length;      //�ҳ����λ
  if (j > 15){return '���';}
  str2 = str2.substr(15-j);    //ȡ����Ӧλ����str2��ֵ���磺200.55,jΪ5����str2=��ʰԪ�Ƿ�
  
  //ѭ��ȡ��ÿһλ��Ҫת����ֵ
  for(i=0;i<j;i++){
    str3 = str4.substr(i,1);   //ȡ����ת����ĳһλ��ֵ
    if (i != (j-3) && i != (j-7) && i != (j-11) && i != (j-15)){    //����ȡλ����ΪԪ�����ڡ������ϵ�����ʱ
   if (str3 == '0'){
     ch1 = '';
     ch2 = '';
  nzero = nzero + 1;
   }
   else{
     if(str3 != '0' && nzero != 0){
       ch1 = '��' + str1.substr(str3*1,1);
          ch2 = str2.substr(i,1);
          nzero = 0;
  }
  else{
    ch1 = str1.substr(str3*1,1);
          ch2 = str2.substr(i,1);
          nzero = 0;
        }
   }
 }
 else{ //��λ�����ڣ��ڣ���Ԫλ�ȹؼ�λ
      if (str3 != '0' && nzero != 0){
        ch1 = "��" + str1.substr(str3*1,1);
        ch2 = str2.substr(i,1);
        nzero = 0;
      }
      else{
     if (str3 != '0' && nzero == 0){
          ch1 = str1.substr(str3*1,1);
          ch2 = str2.substr(i,1);
          nzero = 0;
  }
        else{
    if (str3 == '0' && nzero >= 3){
            ch1 = '';
            ch2 = '';
            nzero = nzero + 1;
       }
       else{
      if (j >= 11){
              ch1 = '';
              nzero = nzero + 1;
   }
   else{
     ch1 = '';
     ch2 = str2.substr(i,1);
     nzero = nzero + 1;
   }
          }
  }
   }
 }
    if (i == (j-11) || i == (j-3)){  //�����λ����λ��Ԫλ�������д��
        ch2 = str2.substr(i,1);
    }
    str5 = str5 + ch1 + ch2;
    
    if (i == j-1 && str3 == '0' ){   //���һλ���֣�Ϊ0ʱ�����ϡ�����
      str5 = str5 + '��';
    }
  }
  if (num == 0){
    str5 = '��Ԫ��';
  }
  
  return str5;
}

function Trim(str)
{
   if(str.charAt(0)==" ")
   {
      str =str.substring(1,str.length-1);
      str =Trim(str);
   }else if(str.charAt(str.length-1)==" ")
   {
      str =str.substring(0,str.length-1);
      str =Trim(str);
   }
   
   return str;
   
}
//////////////////
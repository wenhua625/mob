
    function CheckCardType(src,dest)
	{
	  var cardType;
	  var cardNo = src.value;
	  if(cardNo == ""){return false}
	  if(cardNo.substring(0,1) == "4")
	  {	  
	     cardType = "VISA";
	  }else if(cardNo.substring(0,1) == "5")
	  {
	     cardType = "MASTERCARD";
	  }else if((cardNo.substring(0, 2)) == "34"|| (cardNo.substring(0, 2)) == "37")
	  {
	     cardType = "AE";
	  }else if((cardNo.substring(0, 2)) == "35")
	  {
	     cardType = "JCB";
	  }else if((cardNo.substring(0, 2)) == "30"|| (cardNo.substring(0, 2)) == "36"|| (cardNo.substring(0, 2)) == "38")
	  {
	     cardType = "DINER";
	  }else
	  {
		 alert("�Բ���������Ŀ�����Ч��" + cardNo);
		 src.focus;
		 src.select();
	  }	 
	  
	  dest.value = cardType;	  
	}
	
	//���´��� 500*500
	function NewWin(str)
	{
		window.open(str,'','height=500,width=500,status=yes,toolbar=no,menubar=no,location=no,scrollbars=yes',null);
	}
	//��ģʽ����
    function NewWinMD(path,title)
	{
		 window.showModalDialog(path,title,"status:no;  dialogHeight: 550px; dialogWidth: 700px;");
	}
	function NewWinCustMD(path,title,h,w)
	{
		 var a=window.showModalDialog(path+'&tmp1='+Math.random(),title,"status:no; fullscreen:3;help:0; dialogHeight: "+h+"px; dialogWidth: "+w+"px;");
		 return a;
	}
	
	
	//�Զ�����´��� 500*500
	function NewWinCustomer(str,h,w)
	{
		window.open(str,'','height='+h+',width='+w+',status=yes,toolbar=no,menubar=no,location=no,scrollbars=yes',null);
	}
	
	//��д���̻���Ϣ��
	function WriteBack(merid,mername)
	{
		window.opener.form.merchantID.value = merid;
		window.opener.form.merchantName.value = mername;
		window.close();
	}
	//�ж���������
	 function checkInput(src)
    {
	  var num=src.value;
      if(num==null || num.length==0)
	  {
	      alert("����������ȷ����!");
		  src.focus();
		  return false;
	  }
	  if(isNaN(num))
		{
		    alert("��������ȷ����!");
			src.focus();
			return false;
		}
	  if(parseInt(num)<=0)
		{
		    alert("���ݱ������0!");
			src.focus();
			return false;
		}
	  return true;
	  
	}
	//�ж���������
	 function checkInput2(src)
    {
	  var num=src.value;
      if(num==null || num.length==0)
	  {
	      alert("����������ȷ����!");
		  src.focus();
		  return false;
	  }
	  if(isNaN(num))
		{
		    alert("��������ȷ����!");
			src.focus();
			return false;
		}
	  if(parseInt(num)<0)
		{
		    alert("���ݱ������0!");
			src.focus();
			return false;
		}
	  return true;
	  
	}
	//�ж���������
	 function checkInput3(src)
    {
	  var num=src.value;
      
	  if(isNaN(num))
		{
		    alert("��������ȷ����!");
			src.focus();
			return false;
		}
	  
	  return true;
	  
	}
	
	//������������
	 function checkInput0(num)
    {
	  
      if(num==null || num.length==0)
	  {
	      alert("����������ȷ����!");
		  
		  return false;
	  }
	  if(isNaN(num))
		{
		    alert("��������ȷ����!");
			
			return false;
		}
	  if(parseInt(num)<0)
		{
		    alert("���ݱ������0!");
			
			return false;
		}
	  return true;
	  
	}
	//�ж���������
	 function checkInput1(num)
    {
	  
      
	  if(isNaN(num))
		{
		    alert("��������ȷ����!");
			
			return false;
		}
	  
	  return true;
	  
	}
	

	 function checkInput40(src)
    {
	  var num=src;
      if(num==null || num.length==0)
	  {
	      alert("����������ȷ����!");
		  
		  return false;
	  }
	  if(isNaN(num))
		{
		    alert("��������ȷ����!");
			
			return false;
		}
	  if(parseInt(num)<0)
		{
		    alert("���ݱ������0!");
			
			return false;
		}
	  return true;
	  
	}
	

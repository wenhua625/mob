function showDate(){
		   calendar =new Date();
           day= calendar.getDay();
           month=calendar.getMonth();
           date= calendar.getDate();
           year= calendar.getYear();
           if(year<=100) year=1900+year;

          var dayname=new Array("������","����һ","���ڶ�","������","������","������","������");
          var monthname= new Array("1","2","3","4","5","6","7","8","9","10","11","12");

          document.write(year+"��"+monthname[month]+"��"+date+"��");
}
calendar =new Date();
day= calendar.getDay();
month=calendar.getMonth();
date= calendar.getDate();
year= calendar.getYear();
if(year<=100) year=1900+year;

var dayname=new Array("������","����һ","���ڶ�","������","������","������","������");
var monthname= new Array("01","02","03","04","05","06","07","08","09","10","11","12");

document.write(year+monthname[month]+dayname[day]);
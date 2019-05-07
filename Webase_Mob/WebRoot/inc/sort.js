var Main_Tab	= null;
var cur_row	= null;
var cur_col	= null;
var cur_cell	= null;
var Org_con	= "";
var sort_col	= null;

var show_col	= false;
var charMode	= true;
var act_bgc	= "#BEC5DE";
var act_fc	= "black";
//var cur_bgc	= "#ccffcc";
 var cur_bgc ="#ffee00";
var cur_fc	= "black";

function init(){
	cur_row			= null;
	cur_col			= null;
	cur_cell		= null;
	sort_col		= null;
	Main_Tab 		= PowerTable;
	read_def(Main_Tab)
	Main_Tab.onmouseover	= overIt;
	Main_Tab.onmouseout	= outIt;
	Main_Tab.onclick	= clickIt;
	Main_Tab.ondblclick	= dblclickIt;
	Org_con			= Main_Tab.outerHTML;
	
	arrowUp = document.createElement("SPAN");
	arrowUp.innerHTML	= "5";
	arrowUp.style.cssText 	= "PADDING-RIGHT: 0px; MARGIN-TOP: -3px; PADDING-LEFT: 0px; FONT-SIZE: 10px; MARGIN-BOTTOM: 2px; PADDING-BOTTOM: 2px; OVERFLOW: hidden; WIDTH: 10px; COLOR: blue; PADDING-TOP: 0px; FONT-FAMILY: webdings; HEIGHT: 11px";

	arrowDown = document.createElement("SPAN");
	arrowDown.innerHTML	= "6";
	arrowDown.style.cssText = "PADDING-RIGHT: 0px; MARGIN-TOP: -3px; PADDING-LEFT: 0px; FONT-SIZE: 10px; MARGIN-BOTTOM: 2px; PADDING-BOTTOM: 2px; OVERFLOW: hidden; WIDTH: 10px; COLOR: blue; PADDING-TOP: 0px; FONT-FAMILY: webdings; HEIGHT: 11px";
}

function window.onload(){
	init();
	
	drag = document.createElement("DIV");
	drag.innerHTML		= "";
	drag.style.textAlign 	= "center";
	drag.style.position 	= "absolute";
	drag.style.cursor 	= "hand";
	drag.style.border 	= "1 solid black";
	drag.style.display 	= "none";
	drag.style.zIndex 	= "999";
	
	document.body.insertBefore(drag);
	
	
}



document.onselectstart	= function(){return false;}

document.onmouseup	= drag_end;

function clear_color(){
	the_table=Main_Tab;
	if(cur_col!=null){
		for(i=0;i<the_table.rows.length;i++){
			if (the_table.rows[i].cells[cur_col] != null)
			{
				with(the_table.rows[i].cells[cur_col]){
				style.backgroundColor=oBgc;
				style.color=oFc;
				}
			}
			
		}
	}
	if(cur_row!=null){
		for(i=0;i<the_table.rows[cur_row].cells.length;i++){
			with(the_table.rows[cur_row].cells[i]){
				style.backgroundColor=oBgc;
				style.color=oFc;
			}
		}
	}
	if(cur_cell!=null){
		cur_cell.children[0].contentEditable = false;
		with(cur_cell.children[0].runtimeStyle){
			borderLeft=borderTop="";
			borderRight=borderBottom="";
			backgroundColor="";
			paddingLeft="";
			textAlign="";
		}
	}
}

function document.onclick(){
	window.status = "";
	clear_color();
	cur_row  = null;
	cur_col  = null;
	cur_cell = null;
}

function read_def(the_table){
	for(var i=0;i<the_table.rows.length;i++){
		for(var j=0;j<the_table.rows[i].cells.length;j++){
			with(the_table.rows[i]){
				cells[j].oBgc = cells[j].currentStyle.backgroundColor;
				cells[j].oFc  = cells[j].currentStyle.color;
				if(i==0){
					cells[j].onmousedown	= drag_start;
					cells[j].onmouseup	= drag_end;
				}
			}
		}
	}
}

function get_Element(the_ele,the_tag){
	the_tag = the_tag.toLowerCase();
	if(the_ele.tagName.toLowerCase()==the_tag)return the_ele;
	while(the_ele=the_ele.offsetParent){
		if(the_ele.tagName.toLowerCase()==the_tag)return the_ele;
	}
	return(null);
}

var dragStart		= false;
var dragColStart	= null;
var dragColEnd		= null;

function drag_start(){
	var the_td	= get_Element(event.srcElement,"td");
	if(the_td==null) return;
	dragStart	= true;
	dragColStart	= the_td.cellIndex;
	drag.style.width	= the_td.offsetWidth;
	drag.style.height	= the_td.offsetHeight;
	function document.onmousemove(){
		drag.style.display	= "";
		drag.style.top		= event.y - drag.offsetHeight/2;
		drag.style.left		= event.x - drag.offsetWidth/2;
		for(var i=0;i<Main_Tab.rows[0].cells.length;i++){
			with(Main_Tab.rows[0].cells[i]){
				if((event.y>offsetTop+parseInt(document.body.currentStyle.marginTop) && event.y<offsetTop+offsetHeight+parseInt(document.body.currentStyle.marginTop)) && (event.x>offsetLeft+parseInt(document.body.currentStyle.marginLeft) && event.x<offsetLeft+offsetWidth+parseInt(document.body.currentStyle.marginLeft))){
					runtimeStyle.backgroundColor=act_bgc;
					dragColEnd=cellIndex;
				}else{
					runtimeStyle.backgroundColor="";
				}
			}
		}
		if(!(event.y>Main_Tab.rows[0].offsetTop+parseInt(document.body.currentStyle.marginTop) && event.y<Main_Tab.rows[0].offsetTop+Main_Tab.rows[0].offsetHeight+parseInt(document.body.currentStyle.marginTop))) dragColEnd=null;
	}
	drag.innerHTML = the_td.innerHTML;
	drag.style.backgroundColor = the_td.oBgc;
	drag.style.color = the_td.oFc;
}

function drag_end(){
	dragStart = false;
	drag.style.display="none";
	drag.innerHTML = "";
	drag.style.width = 0;
	drag.style.height = 0;
	for(var i=0;i<Main_Tab.rows[0].cells.length;i++){
		Main_Tab.rows[0].cells[i].runtimeStyle.backgroundColor="";
	}
	if(dragColStart!=null && dragColEnd!=null && dragColStart!=dragColEnd){
		change_col(Main_Tab,dragColStart,dragColEnd);
		if(dragColStart==sort_col)sort_col=dragColEnd;
		else if(dragColEnd==sort_col)sort_col=dragColStart;
		document.onclick();
	}
	dragColStart = null;
	dragColEnd = null;
	document.onmousemove=null;
}

function clickIt(){
	event.cancelBubble=true;
	var the_obj = event.srcElement;
	var i = 0 ,j = 0;
	if(cur_cell!=null && cur_row!=0){
		cur_cell.children[0].contentEditable = false;
		with(cur_cell.children[0].runtimeStyle){
			borderLeft=borderTop="";
			borderRight=borderBottom="";
			backgroundColor="";
			paddingLeft="";
			textAlign="";
		}
	}
	if(the_obj.tagName.toLowerCase() != "table" && the_obj.tagName.toLowerCase() != "tbody" && the_obj.tagName.toLowerCase() != "tr"){
		var the_td	= get_Element(the_obj,"td");
		if(the_td==null) return;
		var the_tr	= the_td.parentElement;
		var the_table	= get_Element(the_td,"table");
		var i 		= 0;
		clear_color();
		cur_row = the_tr.rowIndex;
		cur_col = the_td.cellIndex;
		if(cur_row!=0){
			for(i=0;i<the_tr.cells.length;i++){
				with(the_tr.cells[i]){
					style.backgroundColor=cur_bgc;
					style.color=cur_fc;
				}
			}
		}else{
			if(show_col){
				for(i=1;i<the_table.rows.length;i++){
					if(the_table.rows[i].cells[cur_col] != null){
					 with(the_table.rows[i].cells[cur_col]){
						style.backgroundColor=cur_bgc;
						style.color=cur_fc;
					 }

					}

					
				}
			}
			
			the_td.mode = !the_td.mode;
			if(sort_col!=null){
				with(the_table.rows[0].cells[sort_col])
					removeChild(lastChild);
			}
			with(the_table.rows[0].cells[cur_col])
				appendChild(the_td.mode?arrowUp:arrowDown);
			sort_tab(the_table,cur_col,the_td.mode);
			sort_col=cur_col;
		}
	}
}

function dblclickIt(){
	event.cancelBubble=true;
	if(cur_row!=0){
		
	}
}

function overIt(){
	if(dragStart)return;
	var the_obj = event.srcElement;
	var i = 0;
	if(the_obj.tagName.toLowerCase() != "table"){
		var the_td	= get_Element(the_obj,"td");
		if(the_td==null) return;
		var the_tr	= the_td.parentElement;
		var the_table	= get_Element(the_td,"table");
		if(the_tr.rowIndex!=0){
			for(i=0;i<the_tr.cells.length;i++){
				with(the_tr.cells[i]){
					runtimeStyle.backgroundColor=act_bgc;
					runtimeStyle.color=act_fc;					
				}
			}
		}else{
			for(i=1;i<the_table.rows.length;i++){
				if(the_table.rows[i].cells(the_td.cellIndex) == null) return;
				
				with(the_table.rows[i].cells(the_td.cellIndex)){
					runtimeStyle.backgroundColor=act_bgc;
					runtimeStyle.color=act_fc;
				}
			}
			if(the_td.mode==undefined)the_td.mode = false;
			the_td.style.cursor=the_td.mode?"n-resize":"s-resize";
		}
	}
}

function outIt(){
	var the_obj = event.srcElement;
	var i=0;
	if(the_obj.tagName.toLowerCase() != "table"){
		var the_td	= get_Element(the_obj,"td");
		if(the_td==null) return;
		var the_tr	= the_td.parentElement;
		var the_table	= get_Element(the_td,"table");
		if(the_tr.rowIndex!=0){
			for(i=0;i<the_tr.cells.length;i++){
				with(the_tr.cells[i]){
					runtimeStyle.backgroundColor='';
					runtimeStyle.color='';				
				}
			}
		}else{
			var the_table=the_tr.parentElement.parentElement;
			for(i=0;i<the_table.rows.length;i++){
				if(the_table.rows[i].cells(the_td.cellIndex) == null) return;
				with(the_table.rows[i].cells(the_td.cellIndex)){
					runtimeStyle.backgroundColor='';
					runtimeStyle.color='';
				}
			}
		}
	}
}

var charPYStr = "�������������������������������������������������������������������°ðİŰưǰȰɰʰ˰̰ͰΰϰаѰҰӰ԰հְװذٰڰ۰ܰݰް߰������������������������������������������������������������������������������������������������������������±ñıűƱǱȱɱʱ˱̱ͱαϱбѱұӱԱձֱױرٱڱ۱ܱݱޱ߱������������������������������������������������������������������������������������������������������������²òĲŲƲǲȲɲʲ˲̲ͲβϲвѲҲӲԲղֲײزٲڲ۲ܲݲ޲߲������������������������������������������������������������������������������������������������������������³óĳųƳǳȳɳʳ˳̳ͳγϳгѳҳӳԳճֳ׳سٳڳ۳ܳݳ޳߳������������������������������������������������������������������������������������������������������������´ôĴŴƴǴȴɴʴ˴̴ʹδϴдѴҴӴԴմִ״شٴڴ۴ܴݴ޴ߴ������������������������������������������������������������������������������������������������������������µõĵŵƵǵȵɵʵ˵̵͵εϵеѵҵӵԵյֵ׵صٵڵ۵ܵݵ޵ߵ������������������������������������������������������������������������������������������������������������¶öĶŶƶǶȶɶʶ˶̶Ͷζ϶жѶҶӶԶնֶ׶ضٶڶ۶ܶݶ޶߶������������������������������������������������������������������������������������������������������������·÷ķŷƷǷȷɷʷ˷̷ͷηϷзѷҷӷԷշַ׷طٷڷ۷ܷݷ޷߷������������������������������������������������������������������������������������������������������������¸øĸŸƸǸȸɸʸ˸̸͸θϸиѸҸӸԸոָ׸ظٸڸ۸ܸݸ޸߸������������������������������������������������������������������������������������������������������������¹ùĹŹƹǹȹɹʹ˹̹͹ιϹйѹҹӹԹչֹ׹عٹڹ۹ܹݹ޹߹������������������������������������������������������������������������������������������������������������ºúĺźƺǺȺɺʺ˺̺ͺκϺкѺҺӺԺպֺ׺غٺںۺܺݺ޺ߺ������������������������������������������������������������������������������������������������������������»ûĻŻƻǻȻɻʻ˻̻ͻλϻлѻһӻԻջֻ׻ػٻڻۻܻݻ޻߻������������������������������������������������������������������������������������������������������������¼üļżƼǼȼɼʼ˼̼ͼμϼмѼҼӼԼռּ׼ؼټڼۼܼݼ޼߼������������������������������������������������������������������������������������������������������������½ýĽŽƽǽȽɽʽ˽̽ͽνϽнѽҽӽԽսֽ׽ؽٽھ����������������������������������������������������������¾þľžƾǾȾɾʾ˾̾;ξϾоѾҾӾԾվ־׾ؾپھ۾ܾݾ޾߾����������������������۽ܽݽ޽߽����������������������������������������������������������������������������������������������������������������������������������������¿ÿĿſƿǿȿɿʿ˿̿ͿοϿпѿҿӿԿտֿ׿ؿٿڿۿܿݿ޿߿���������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������¡¢£¤¥¦§¨©ª«¬­®¯°±²³´µ¶·¸¹º»¼½¾¿������������������������������������������������������������������������������������������������������������������������������áâãäåæçèéêëìíîïðñòóôõö÷øùúûüýþÿ������������������������������������������������������������������������������������������������������������������������������ġĢģĤĥĦħĨĩĪīĬĭĮįİıĲĳĴĵĶķĸĹĺĻļĽľĿ������������������������������������������������������������������������������������������������������������������������������šŢţŤťŦŧŨũŪūŬŭŮůŰűŲųŴŵŶŷŸŹźŻżŽžſ������������������������������������������������������������������������������������������������������������������������������ơƢƣƤƥƦƧƨƩƪƫƬƭƮƯưƱƲƳƴƵƶƷƸƹƺƻƼƽƾƿ������������������������������������������������������������������������������������������������������������������������������ǢǣǤǥǦǧǨǩǪǫǬǭǮǯǰǱǲǳǴǵǶǷǸǹǺǻǼǽǾǿ������������������������������������������������������������������������������������������������������������������������������ȡȢȣȤȥȦȧȨȩȪȫȬȭȮȯȰȱȲȳȴȵȶȷȸȹȺȻȼȽȾȿ������������������������������������������������������������������������������������������������������������������������������ɡɢɣɤɥɦɧɨɩɪɫɬɭɮɯɰɱɲɳɴɵɶɷɸɹɺɻɼɽɾɿ������������������������������������������������������������������������������������������������������������������������������ʡʢʣʤʥʦʧʨʩʪʫʬʭʮʯʰʱʲʳʴʵʶʷʸʹʺʻʼʽʾʿ������������������������������������������������������������������������������������������������������������������������������ˡˢˣˤ˥˦˧˨˩˪˫ˬ˭ˮ˯˰˱˲˳˴˵˶˷˸˹˺˻˼˽˾˿������������������������������������������������������������������������������������������������������������������������������̴̵̶̷̸̡̢̧̨̣̤̥̦̩̪̫̬̭̮̯̰̱̲̳̹̺̻̼̽̾̿������������������������������������������������������������������������������������������������������������������������������ͣͤͥͦͧͨͩͪͫͬͭͮͯ͢͡ͰͱͲͳʹ͵Ͷͷ͸͹ͺͻͼͽ;Ϳ������������������������������������������������������������������������������������������������������������������������������Ρ΢ΣΤΥΦΧΨΩΪΫάέήίΰαβγδεζηθικλμνξο������������������������������������������������������������������������������������������������������������������������������ϡϢϣϤϥϦϧϨϩϪϫϬϭϮϯϰϱϲϳϴϵ϶ϷϸϹϺϻϼϽϾϿ������������������������������������������������������������������������������������������������������������������������������СТУФХЦЧШЩЪЫЬЭЮЯабвгдежзийклмноп������������������������������������������������������������������������������������������������������������������������������ѡѢѣѤѥѦѧѨѩѪѫѬѭѮѯѰѱѲѳѴѵѶѷѸѹѺѻѼѽѾѿ������������������������������������������������������������������������������������������������������������������������������ҡҢңҤҥҦҧҨҩҪҫҬҭҮүҰұҲҳҴҵҶҷҸҹҺһҼҽҾҿ������������������������������������������������������������������������������������������������������������������������������ӡӢӣӤӥӦӧӨөӪӫӬӭӮӯӰӱӲӳӴӵӶӷӸӹӺӻӼӽӾӿ������������������������������������������������������������������������������������������������������������������������������ԡԢԣԤԥԦԧԨԩԪԫԬԭԮԯ԰ԱԲԳԴԵԶԷԸԹԺԻԼԽԾԿ������������������������������������������������������������������������������������������������������������������������������աբգդեզէըթժիլխծկհձղճմյնշոչպջռսվտ������������������������������������������������������������������������������������������������������������������������������ְֱֲֳִֵֶַָֹֺֻּֽ֢֣֤֥֦֧֪֭֮֡֨֩֫֬֯־ֿ������������������������������������������������������������������������������������������������������������������������������סעףפץצקרשת׫׬׭׮ׯװױײ׳״׵׶׷׸׹׺׻׼׽׾׿��������������������������������������������������������������������������������������������������������������������";
var charBHStr = "һ�Ҷ����˾��˶��˶���˼�������ʮ���������������¸�Ѿ���ô����Ҳϰ���ڿ����ڷ�����ǧ�������ʿϦ��Ů�Ӵ�Сʬɽ���������Ƚ�ɹ㹭���ŷ������ר�зᵤΪ֮�������ƻ��微��ʲ�ʽ��ͳ����Դ��ز���Ԫ�����ڸ��߷��׷���Ȱ�카���Ȼ�ƥ����������������˫������̫������������Ͱͱһÿ�������껧����֧�Ķ��﷽����Ի��ľǷֹ�����ë����ˮ��צ��Ƭ��ţȮ�����ռ��ƶ����ϼ������˳���Τ���������ҵ�Զ�˿��է�������������̸���Ǫ��������������Ƚ��д����͹����������������ұ��ѻܰ�ռ��¬î��ȥ���ž���ֻ���ٰȶ���̨ʷ��Ҷ��˾̾������ʥ�����뺻ʧͷū���������Զ������ɾ��в�˧ƽ�׸��������˰Ǵ��ӳ⵩��δĩ��������ĸ����֭͡�㺺������ϸ�����˦���ɼ�����Ƥ��Ŀìʸʯʾ���Ѩ��������������ѵ��Ѷ������������Ԧ������ƹ���������ǽ���������ټ����ηݷ������鼿���������Ż��ɡΰ������α�����ȹ�ȫ�������پ�ũ������̻�������մ��Ӷ��ٽ��ﻪЭӡΣѹ�����Ը��ϼ���ͬ����������������������ڹ�س���׳���Ŀ�ж����������ױ�����ִ���լ���ذ���Ѱ���⳾Ң������������Ѳ����ʦ�겢ׯ����͢��ʽ�ڵ�æ������Ϸ���п���Ǥִ��ɨ����ּ��Ѯ����ҷ�����Ӷ����ɱ��Ȩ�λ���������ϫ�Ǻ�Ѵ�꽭���������ƻ�үĲ�������׺���Լ�������������Ͽ��������߼�������������ɫ����â֥��Ѫ�������۽�����������Ϸ���þ��긺���Ǩ����Ѹ�������ǰ�а���ʴ�����������ҳ��ѱ�������ϴ����Һ�Ķ�������������Ƶ赫λ��ס��������������Ӷ����ұ���ұ�䶳��ɾ����������Ŭ��������ϻҽ±��ȴ���ؾ��������ͷ�ɶַԺ�����˱��֨�ⳳ�����Ǻ���ѽ���ʸ���ŻԱǺ�ض�԰����Χַ����̮�������ӿ��̳�����׹�����Ѷʼ������׷���Т�������β���ƨ���ڵ�ϣ�ʱӴ���®��Ӧ��Ū������ͮ�۳�����־���ǿ���û��ҽ�Ť�糶�Ű�������Ҽ����������ץͶ�����۸��׿�������������Ĺ���ʱ������ɼ���ӲĴ��ȶ��������������ÿ����̭�����������������ɳ�湵ûŽ���ٲ׻�����������ֲ�ĵ��״�̿�ұ����긦�е�����������˽ͺ����ϵγ��ɴ�������ڷ�ֽ�Ʒ�Ŧ��ǼФ��Ǹظγ����߽�«�Ұ�о������ѿέ���ղ�����֤����ʶթ�����ߴ���ȶ����Ƴ�������������ӭ�˽��������ԶΥ�������ʺ��������������붤���м����谢�Ӹ���½¤���ͷ�������¿�����ɥ������Щ��������ۼ�ʹֶ�����̶��������½�����ȿ�����ߵ侻ƾ�����ε���ˢȯɲ�̹̿�����Ʊ���׿�����Ծ��������ȡ����ζ�������������զ�;�ӽ���乾�������̹�ͼ����̹ƺ��������¢��ҹ�����η�ܱ�������ķʼ�����ί�ϼ���ѧ�ڹ��涨���˱�ʵ�����о�������������������������㲯���ҵ׵�������ӷϽ����һ�¼���������������̬�����²����Թ��ӻ���������̧����ĨѺ�����������Ĵ������������ذ��Ͼ�׾��£��ӵ��š����Ÿ�ն��������������������������ɰ幹��������ö��֦����ǹ�����ŷ����Źå��ĭ�ںӷ������ӹ�մ��й�����ڷ�Ţ�ݲ�����ע��Ӿк����ǳ¯���׳�Ȳ���˾����ְ��������������õ�����ͻ������ű�����äֱ֪����������ѱ�����������ϸ֯�հ����ﾭ����Ү���֫���ʼ緾��������������в���Է̦�����������ɻ��Ӣƻ��ï����é����²ʭ�����Ĺ�������ʫ���ﻰ����ѯ���������Ͱ��˻��ʷ�̰ƶ�Ṻ����ת����������ȵ���������֣�ɽ�ǥ����բ�ָ�ªİ������������Ƕ��꽤�α���ʻ��פ�ռ��������پ�ͤ��������ֱ�ٶ������׷�����������������ð������ǰ�н�������ѫ��ж����ܱ�������ҧ���ۿ����ʰ�Ʒ����۹������ƻ�Ӵ�����ݶ⹸ԫ�ѵ��Ǹ���������Ҧ��������������¦���Ⱥ��Ͽ����һ��ܹ��⽫����ʺ����Ͽ�Ͳ���۴�֡���Ķ�ͥ�������ܻ�����ŭ˼����Թ���ѻк�����޶�����ս�����������˩��ƴקʰ�ֹ�ָ����������̢Ю�ӵ�������Ųͦ����ʩ����ӳ�������������Կݼܼϱ���ĳ����Ⱦ����������������դ��ջ������������жζ�����ձ����Ȫ�ý�����ϴ�嶴�����޻���Ǣ�ɽ��ǲ�û�Ũ��̿�ھ���ը������˸������ǣ�ݽƶ���ʨ�����Უɺ�䷩���½�η�̽��ߴ����Ի�����ӯ���ζ�ʡü��գ��ɰ��������ש����ף�������ֿ��봩ͻ���������Ѱ��޽��ƻ��Ѥ�����ͳ�׷���ˣ��θ����̥����ʤ���������ã��������㾣�ݼ�����Ե��ٻ�ӫ��ҩŰ����Ϻʴ�������ܰ�Ҫ�������������ջ�˵�з��������ó�Ѻظ���ſ�����Աż�׷����������ѡѷ�¿������ظƶ۳����Ʊ���Կ�վ��ٹ�ť����������ұݶ�Ժ��������������˳��ʳ���Ľȱ�������溧�ǹ�Ÿѻ�˸��㰳�������Ⱥ��н賫����ծֵ�㽡����ԩ��׼���������ʰ��������ԭ��Ŷ���������ܲ������������ƻ�����Բ�������Һ����׼������������׺��������ݿ����мչ�����ͷ��ϯ������ͽ����ˡ����Ϣ�Ҷ��ĺ�����������ȭ��ֿ�ΰ���������ͱ��׽�ƺ�����貶����񻻵�Ч�а�ի�����ûν���ɹ������˷�ʲ�˨����У�����˸����Թ���Φ����ͩɣ���۵��Ž�׮��������ѳ����к�����̩�����㿣�ֺ��˸�ԡ����Ϳ������ӿ���������л�������ɬ�Һ������̿����ջ��̽��Ȱ��������������ƿ��������ƣ���۾Ҽ���֢Ӹ�������氻յ�μ�����ѣ������������������������������Ȼ���խ��վ������Ц�ʷ����������������м�ȱ�ո����̳�����Ű��ʳܵ��������ȸ콺�ذ���֬�༹������ŧ����Ҩ���㽢���޺�����ɯĪ������Өç���ð�����˥��Ԭ��̻���౻����ŵ���̿�˭����׻̸�걪�����ֻ���¸���ʸ����ؽν���͸���;��ͨ���ų�����겿������������Ƹ�Ǯǯ�������������Ǧí��������������˶ٰ���Ԥ���ٳ��鿥��Ѽ��ԧ��Ǭ��ƫ��ͣż͵������������ջ˼����տ�����ǻ���Ψ���ٿ����̷�ơɶ��ž��ХȦ������ö�ǵ��¹���Ȣ�������Ӥ�����޼ż����ܿ�ξ��������±�ո��������ӹ�ȵ��ʱ������Ϥ�ƻ������µ��龪����ϧΩ���Ҳѵ��������ݴ������Ƶ���ڵ�������Ҵ����̽�ӿ����ڴ�°���������Ƚ����ָ�б��������޳�������Ͱ��÷��������е�����������Һ���Ե��������������Ե������㻴���������Ԩ�ս�������ϩ������ɺ���ˬ���Բ��Ͳ���è�������������������Ȭ���̺۰��п��ǵ���ʢ�п������������ù���˶Ʊ���������ƻ�Ҥ�Ͼ��µѷ����ڼ������ɴ�ճ�ۼ���������ά�����������׺��������ְ���Ÿ������沰�ϴ����վ��ʲ˲����������Ƽή��өӪ�������������߹Ƶ����δ�Ϯ��ı����гν������������ֺԾ�������������߶������̪Ұͭ��աϳ������ҿ�������ֲ���¡������ȸѩ­���ľ��ڹ�����¹��ƹ���������������ʣ��ļ���ó��俦ι��������ϲ�������������̿�����Ҽ�����ý��ɩ���º�Ԣ�������Ƕñ�ݷ�ǿ����ѭ����ݳͱ��������������ſ��Ƴ����������Ҿ�մ�������Ԯ�����§����ɦ�Ѵ�볨ɢ�ؾ����˹�վ����羧�����������������������ؼ�����ɭ��ù���ֲ׵����Ҭ���ۿ�ֳ̺���������������μ�ۿ��������ĺ���տ��ʪ�����ȳ��̻��ͱ��ٽ���Ȼ����Ϭ���ɺﻫ�������������������趻ʹƦ�������������Ӳȷ��»��ϡ����˰�Ѵ������ѿ�ͯ�Ƚ����Ͳ���ɸ����������������¼��л����Ʊ�Ե��������Ƣ����Ҹ��ǻ����˴ͧ����϶�����п��ٽ�������������ѽֲ���װԣȹ��лҥ��ǫ�������ʹ���ó�Խ���ϵ��ܼ����Թ������������������ź��������������������������п������������϶�����ż��ͺ�������ƭɧ³�����ڶ���ɵ����ڵ���ɤ����������������Ĺϱ�޼�����į��Ļ����΢��������޸д����岫�����°�Я���ҡ��̯��������Ͼů������Ш���㿬¥���ܻ�Ъ����Դ����Ϫ����������Ϲ��������б�̲��Į�ͼ�ɷú����Գ����ɪ�����̵�ձԴ��˾�˯�����޲Ƕ��鰫���µ���鱮������������ɳ�߿����ǩ���������������������ȺƸ�������������������Ⱦ������������������������Ӽ�������������ӽⴥղ���ܽ�á�������������·����������Ǳ�ǲң���ҳ�ͪ�������ê���സ׶���Ƕ���������Ӻ�����ױ�����ѥ������Ƶ��ӱ����걫ȵ����������ɮ�۾����Լθ����ﾳ����ǽ���۷������կ�α���ԸĽ������ˤժ��ġ��Ʋ�������ե��ȶ��ģǸ��Ư��©����������ΫɿϨ��Ѭ���������������ݴ����̼�̼��Ŵ��Ƚ߶˹����������⾫ӧ���Դ���ظ��򲲸�Ĥ������������ε����Ǿ������֩����Ӭ�������������̷���׺�ò׸׬����ӻ��ԯϽշ�����ڽ�ø�������¶Ͷ�þ����農�ؿ��������ʱǽ�Ƨ��������˻����ҭ������ī���Ĵ�Ӱ�»�ο��������¾Ħ�����˺ײ�����˲���׫���ܷ�ĺ���۷�����ӣ������Ǳ��̶���γ�������������̱Ϲ��������Ϳĵ��ڻ������׭ƪ¨���ɴϱ���ϥ���߽�����Ы�Ⱥ�������Ǵ��ԥ��Ȥ̤���߲�����������������������ù��Ь�����ն�Ʈ��������弽���������ɱں�и���������ò����������ȳ�º�輤��ȼ����̡ư��ȳƳĥ�������ݴ���������ǲڽɺ������������ٱ�Ѧн���������������ޱ���������ȩ��������ص��ʵ߲;�ǭĬ�ܺ��纾���ų������̴ϭ�������������˲ͫ�׽���ػ����㿷�����α�ӷ�ܽ�����������Ӯ��̣����˪ϼ����κ��ȣ���ʹ���հ��ź�ٷ��󸲱ĳ���������ӥ�������ر��꽮Ѣ������Ģз���Ų�״��Ҳ���´�½���Ρ���༮Ŵ��ҫ��Ʃ��ħ�۴���¶��������ȿպ����ȧ��";
function judge_CN(char1,char2,mode){
	var charSet=charMode?charPYStr:charBHStr;
	for(var n=0;n<(char1.length>char2.length?char1.length:char2.length);n++){
		if(char1.charAt(n)!=char2.charAt(n)){
			if(mode) return(charSet.indexOf(char1.charAt(n))>charSet.indexOf(char2.charAt(n))?1:-1);
			else	 return(charSet.indexOf(char1.charAt(n))<charSet.indexOf(char2.charAt(n))?1:-1);
			break;
		}
	}
	return(0);
}

function sort_tab(the_tab,col,mode){
	var tab_arr = new Array();
	var i;
	var start=new Date;
	for(i=1;i<the_tab.rows.length;i++){
		if(the_tab.rows[i].cells[col] != null){
			tab_arr.push(new Array(the_tab.rows[i].cells[col].innerText.toLowerCase(),the_tab.rows[i]));
		}
	}
	function SortArr(mode) {
		return function (arr1, arr2){
			var flag;
			var a,b;
			a = arr1[0];
			b = arr2[0];
			if(/^(\+|-)?\d+($|\.\d+$)/.test(a) && /^(\+|-)?\d+($|\.\d+$)/.test(b)){
				a=eval(a);
				b=eval(b);
				flag=mode?(a>b?1:(a<b?-1:0)):(a<b?1:(a>b?-1:0));
			}else{
				a=a.toString();
				b=b.toString();
				if(a.charCodeAt(0)>=19968 && b.charCodeAt(0)>=19968){
					flag = judge_CN(a,b,mode);
				}else{
					flag=mode?(a>b?1:(a<b?-1:0)):(a<b?1:(a>b?-1:0));
				}
			}
			return flag;
		};
	}
	tab_arr.sort(SortArr(mode));

	for(i=0;i<tab_arr.length;i++){
		the_tab.lastChild.appendChild(tab_arr[i][1]);
	}

	window.status = " (Time spent: " + (new Date - start) + "ms)";
}

function change_row(the_tab,line1,line2){
	the_tab.rows[line1].swapNode(the_tab.rows[line2])
}

function change_col(the_tab,line1,line2){
	for(var i=0;i<the_tab.rows.length;i++)
		the_tab.rows[i].cells[line1].swapNode(the_tab.rows[i].cells[line2]);
}

function Move_up(the_table){
	event.cancelBubble=true;
	if(cur_row==null || cur_row<=1)return;
	change_row(the_table,cur_row,--cur_row);
}

function Move_down(the_table){
	event.cancelBubble=true;
	if(cur_row==null || cur_row==the_table.rows.length-1 || cur_row==0)return;
	change_row(the_table,cur_row,++cur_row);
}

function Move_left(the_table){
	event.cancelBubble=true;
	if(cur_col==null || cur_col==0)return;
	change_col(the_table,cur_col,--cur_col);
	if(cur_col==sort_col)sort_col=cur_col+1;
	else if(cur_col+1==sort_col)sort_col=cur_col;
}

function Move_right(the_table){
	event.cancelBubble=true;
	if(cur_col==null || cur_col==the_table.rows[0].cells.length-1)return;
	change_col(the_table,cur_col,++cur_col);
	if(cur_col==sort_col)sort_col=cur_col-1;
	else if(cur_col-1==sort_col)sort_col=cur_col;
}

function add_row(the_table) {
	event.cancelBubble=true;
	var the_row,the_cell;
	the_row = cur_row==null?-1:(cur_row+1);
	clear_color();
	var newrow=the_table.insertRow(the_row);
	for (var i=0;i<the_table.rows[0].cells.length;i++) {
		the_cell=newrow.insertCell(i);
		the_cell.innerText="NewRow_" + the_cell.parentElement.rowIndex;
	}
	read_def(the_table);
}

function del_row(the_table) {
	if(the_table.rows.length==1) return;
	var the_row;
	the_row = (cur_row==null || cur_row==0)?-1:cur_row;
	the_table.deleteRow(the_row);
	cur_row = null;
	cur_cell=null;
}

function add_col(the_table) {
	event.cancelBubble=true;
	var the_col,i,the_cell;
	the_col = cur_col==null?-1:(cur_col+1);
	var the_title=prompt("Please input the title: ","Untitled");
	if(the_title==null)return;
	if(the_col!=-1 && the_col<=sort_col && sort_col!=null)sort_col++;
	the_title=the_title==""?"Untitled":the_title
	clear_color();
	for(var i=0;i<the_table.rows.length;i++){
		the_cell=the_table.rows[i].insertCell(the_col);
		the_cell.innerText=i==0?the_title:("NewCol_" + the_cell.cellIndex);
	}
	read_def(the_table);
}

function del_col(the_table) {
	if(the_table.rows[0].cells.length==1) return;
	var the_col,the_cell;
	the_col = cur_col==null?(the_table.rows[0].cells.length-1):cur_col;
	if(the_col!=-1 && the_col<sort_col && sort_col!=null)sort_col--;
	else if(the_col==sort_col)sort_col=null;
	for(var i=0;i<the_table.rows.length;i++) the_table.rows[i].deleteCell(the_col);
	cur_col = null;
	cur_cell=null;
}

function res_tab(the_table){
	the_table.outerHTML=Org_con;
	init();
}

function exp_tab(the_table){
	var the_content="";
	document.onclick();
	the_content=the_table.outerHTML;
	the_content=the_content.replace(/ style=\"[^\"]*\"/g,"");
	the_content=the_content.replace(/ mode=\"(false|true)"/g,"");
	the_content=the_content.replace(/ oBgc=\"[\w#\d]*\"/g,"");
	the_content=the_content.replace(/ oFc=\"[\w#\d]*\"/g,"");
	the_content=the_content.replace(/<DIV contentEditable=false>([^<]*)<\/DIV>/ig,"$1");
	the_content="<style>table{font-size: 9pt;word-break:break-all;cursor: default;BORDER: black 1px solid;background-color:#eeeecc;border-collapse:collapse;border-Color:#999999;align:center;}</style>\n"+the_content;
	var newwin=window.open("about:blank","_blank","");
	newwin.document.open();
	newwin.document.write(the_content);
	newwin.document.close();
	newwin=null;
}
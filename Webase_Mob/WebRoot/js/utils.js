function cloneOption(option) {
  var out = new Option(option.text,option.value);
  out.selected = option.selected;
  out.defaultSelected = option.defaultSelected;
  return out;
}

function shiftSelected(chosen,howFar) {
  var opts = chosen.options;
  var newopts = new Array(opts.length);
  var start; var end; var incr;
  if (howFar > 0) {
    start = 0; end = newopts.length; incr = 1; 
  } else {
    start = newopts.length - 1; end = -1; incr = -1; 
  }
  for(var sel=start; sel != end; sel+=incr) {
    if (opts[sel].selected) {
      setAtFirstAvailable(newopts,cloneOption(opts[sel]),sel+howFar,-incr);
    }
  }
  for(var uns=start; uns != end; uns+=incr) {
    if (!opts[uns].selected) {
      setAtFirstAvailable(newopts,cloneOption(opts[uns]),start,incr);
    }
  }
   opts.length = 0;   for(i=0; i<newopts.length; i++) {
    opts[opts.length] = newopts[i]; 
  }
}

function setAtFirstAvailable(array,obj,startIndex,incr) {
  if (startIndex < 0) startIndex = 0;
  if (startIndex >= array.length) startIndex = array.length -1;
  for(var xxx=startIndex; xxx>= 0 && xxx<array.length; xxx += incr) {
    if (array[xxx] == null) {
      array[xxx] = obj; 
      return; 
    }
  }
}

function moveSelected(from,to) {
  newTo = new Array();
  for(i=0; i<from.options.length; i++) {
    if (from.options[i].selected) {
      newTo[newTo.length] = cloneOption(from.options[i]);
      from.options[i] = null;
      i--;
    }
  }
  for(i=0; i<to.options.length; i++) {
    newTo[newTo.length] = cloneOption(to.options[i]);
    newTo[newTo.length-1].selected = false;
  }
  to.options.length = 0;
  for(i=0; i<newTo.length; i++) {
    to.options[to.options.length] = newTo[i];
  }
  selectionChanged(to,from);
}

function selectionChanged(selectedElement,unselectedElement) {
  for(i=0; i<unselectedElement.options.length; i++) {
    unselectedElement.options[i].selected=false;
  }
  form = selectedElement.form; 
  enableButton("movefrom_"+selectedElement.name,
               (selectedElement.selectedIndex != -1));
  enableButton("movefrom_"+unselectedElement.name,
               (unselectedElement.selectedIndex != -1));
  enableButton("shiftdown_"+selectedElement.name,
               (selectedElement.selectedIndex != -1));
  enableButton("shiftup_"+selectedElement.name,
               (selectedElement.selectedIndex != -1));
  enableButton("shiftdown_"+unselectedElement.name,
               (unselectedElement.selectedIndex != -1));
  enableButton("shiftup_"+unselectedElement.name,
               (unselectedElement.selectedIndex != -1));
}

function enableButton(buttonName,enable) {
  var img = document.images[buttonName]; 
  if (img == null) return; 
  var src = img.src; 
  var und = src.lastIndexOf("_disabled.gif"); 
  if (und != -1) { 
    if (enable) img.src = src.substring(0,und)+".gif"; 
  } else { 
    if (!enable) {
      var gif = src.lastIndexOf("_clicked.gif"); 
      if (gif == -1) gif = src.lastIndexOf(".gif"); 
      img.src = src.substring(0,gif)+"_disabled.gif";
    }
  }
}

function pushButton(buttonName,push) {
  var img = document.images[buttonName]; 
  if (img == null) return; 
  var src = img.src; 
  var und = src.lastIndexOf("_disabled.gif"); 
  if (und != -1) return false; 
  und = src.lastIndexOf("_clicked.gif"); 
  if (und == -1) { 
    var gif = src.lastIndexOf(".gif");
    if (push) img.src = src.substring(0,gif)+"_clicked.gif"; 
  } else { 
      if (!push) img.src = src.substring(0,und)+".gif"; 
  }
}

function itemSelectAll(list) {
  var len = list.options.length;
  
  for(var i=0; i<len; i++)
    list.options[i].selected = true;	
}

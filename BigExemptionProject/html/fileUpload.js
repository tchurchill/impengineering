
function dragAndDropInit(){
	if (window.File && window.FileList && window.FileReader) {
		var fileselect = getId("fileselect");//input tag
		var filedrag = getId("filedrag");//div tag (box to drop files)
		var submitbutton = getId("submitbutton");
		
		//fileselect events
		fileselect.addEventListener("change", fileHandler, false);
		
		//assume XMLHttpRequest method is available
		//filedrag events
		filedrag.addEventListener("dragover", fileHover, false);
		filedrag.addEventListener("dragleave", fileHover, false);
		filedrag.addEventListener("drop", fileHandler, false);
		filedrag.style.display = "block";
	}
}

function fileHandler(event){
	fileHover(event);//need to call again, to remove hover style
	var files = event.target.files || event.dataTransfer.files;
	parseFile(files[0]);
}

function fileHover(event){
	//stop the default event actions
	event.stopPropagation();
	event.preventDefault();
	//event.target.className = (event.type == "dragover" ? "hover" : "");
	if(event.type == "dragover"){
		event.target.className = "hover";
	}else{
		event.target.className = "";
	}
}


function parseFile(file){
	out("name: " + file.name + " type: " + file.type + " size: " + file.size);
}

function getId(id){
	return document.getElementById(id);
}

function out(msg){
	var m = getId("messages");
	m.innerHTML = msg + m.innerHTML;
}

window.onload = dragAndDropInit;
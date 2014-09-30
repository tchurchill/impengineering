function treeInit(){
	var labels = document.getElementsByClassName("clickable");
	for(i=0; i<labels.length; i++){
		labels[i].addEventListener("dblclick", exempt, false);
		labels[i].addEventListener("contextmenu", exempt, false);
	}
}

function exempt(event){
	event.preventDefault();
	//build element path
	var elementPath = buildPath(event.target)
	//out(elementPath);
	//create input tag
	var inputTag = document.createElement("input");
	inputTag.type = "checkbox";
	inputTag.setAttribute("name", "exempt[]");//this name should create an array in the POST data
	inputTag.setAttribute("value", "test");
	inputTag.setAttribute("checked", "checked");
	
	var spanTag = document.createElement("span");
	spanTag.innerHTML = elementPath;
	var brTag = document.createElement("br");
	
	var exemptForm = document.getElementById("exemptForm");
	exemptForm.appendChild(inputTag);
	exemptForm.appendChild(spanTag);
	exemptForm.appendChild(brTag);
	
	
}

function buildPath(element){
	var path = "";
	var curElement = element;
	
	while(curElement.id !== "tree"){
		if(curElement.tagName === "LI"){
			path = path + curElement.getElementsByTagName("span")[0].innerHTML + ".";
		}
		curElement = curElement.parentNode;
	}
	return path;
}

function out(msg){
	var m = document.getElementById("messages");
	m.innerHTML = msg + m.innerHTML;
}

window.onload = treeInit;
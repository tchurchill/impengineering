function treeInit(){
	var labels = document.getElementsByClassName("clickable");
	for(i=0; i<labels.length; i++){
		labels[i].addEventListener("dblclick", exempt);
		labels[i].addEventListener("contextmenu", exempt);
	}
}

function exempt(event){
	event.preventDefault();
	//build element path
	var elementPath = buildPath(event.target)
	var elementInList = document.getElementById(elementPath.replace(/\./g, ""));
	if(elementInList == null){
		//create input tag
		var inputTag = document.createElement("input");
		inputTag.type = "checkbox";
		inputTag.setAttribute("name", "exempt[]");//this name should create an array in the POST data
		inputTag.setAttribute("value", elementPath);
		inputTag.setAttribute("checked", "checked");
		inputTag.addEventListener("change", removeExempt);
		
		var divTag = document.createElement("div");//used to wrap the checkbox and text, so we can remove everything as a group
		divTag.setAttribute("id", elementPath.replace(/\./g, ""));
		var spanTag = document.createElement("span");
		spanTag.innerHTML = elementPath;
		var brTag = document.createElement("br");
		divTag.appendChild(inputTag);
		divTag.appendChild(spanTag);
		divTag.appendChild(brTag);
		var exemptForm = document.getElementById("exemptForm");
		exemptForm.appendChild(divTag);
		
	}
	
}

function removeExempt(event){
	event.target.parentNode.parentNode.removeChild(event.target.parentNode);
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
	//reverse order of the path.
	var pathArr = path.split(".");
	var newPath = "";
	for(i=pathArr.length-2; i>=0; i--){
		if(i == 0){
			newPath = newPath + pathArr[i];
		}else{
			newPath = newPath + pathArr[i] + ".";
		}
	}
	return newPath;
}

function out(msg){
	var m = document.getElementById("messages");
	m.innerHTML = msg + m.innerHTML;
}

window.onload = treeInit;
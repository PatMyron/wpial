
function populatePre(school, sport) {
	document.getElementById('contentsP').textContent = "";
	document.getElementById('contentsOpponentTable1').textContent = "";
	document.getElementById('contentsOpponentTable2').textContent = "";
	document.getElementById('contents').textContent = "";
	var url; 
	if (school==="all" && sport==="all") {
		var url; 
		url = "data/all.html";
		var xhr = new XMLHttpRequest();
		xhr.onload = function () {
			document.getElementById('contentsP').innerHTML = this.responseText;
		};
		xhr.open('GET', url);
		xhr.send();
	}
	else {
		if (school==="all") { // sport
			populateSportP(sport);
		} else if (sport==="all") { // school
			populateSchoolP(school);
		} else { // specific team
			populateP(school, sport);
		}		
	}
}

function populateSportP(sport) {
	var url; 
	url = "data/dataBySport/"+sport+".html";

	var xhr = new XMLHttpRequest();
	xhr.onload = function () {
		document.getElementById('contentsP').innerHTML = this.responseText;
	};
	xhr.open('GET', url);
	xhr.send();
}
function populateSchoolP(school) {
	var url; 
	url = "data/dataBySchool/"+school+".html";

	var xhr = new XMLHttpRequest();
	xhr.onload = function () {
		document.getElementById('contentsP').innerHTML = this.responseText;
	};
	xhr.open('GET', url);
	xhr.send();
}
function populateP(school, sport) {
	var url; 
	url = "data/specificData/"+school+"+"+sport+"/seasons.html";

	var xhr = new XMLHttpRequest();
	xhr.onload = function () {
		document.getElementById('contentsP').innerHTML = this.responseText;
	};
	xhr.open('GET', url);
	xhr.send();
/*
	url = "data/specificData/"+school+"+"+sport+"/opponentsABC.html";	
	xhr = new XMLHttpRequest();
	xhr.onload = function () {
		document.getElementById('contentsOpponentTable2').innerHTML = this.responseText;
	};
	xhr.open('GET', url);
	xhr.send();
	*/
	url = "data/specificData/"+school+"+"+sport+"/opponentsGP.html";	
	xhr = new XMLHttpRequest();
	xhr.onload = function () {
		document.getElementById('contentsOpponentTable1').innerHTML = this.responseText;
	};
	xhr.open('GET', url);
	xhr.send();
}
function addLink(school, sport) {
	/*
	var url,str,result; 
	if (school==="all" && sport==="all") {
		url = "data/all.txt";
	}
	else if (school==="all") {
		url = "data/"+sport+".txt";
	}
	else if (sport==="all") {
		url = "data/"+school+".txt";
	}
	else {
		url = "data/specificData/"+school+"+"+sport+"/seasons.html";
	}
	str = "Permanent Data Link";
	result = str.link("http://patmyron.github.io/wpial/"+url);
	document.getElementById("contentsHyperlink").innerHTML = result;
	*/
}
// media query event handler
if (matchMedia) {
	var mq = window.matchMedia("(max-width: 641px)");
	mq.addListener(WidthChange);
	WidthChange(mq);
}

// media query change
function WidthChange(mq) {

	if (mq.matches) { // window width is less than 641px
		document.getElementById("schoolSelect").size = "1";
		document.getElementById("sportSelect").size = "1";
	}
	else { 	// window width is over 641px
		document.getElementById("schoolSelect").size = "100";
		document.getElementById("sportSelect").size = "9";
	}

}
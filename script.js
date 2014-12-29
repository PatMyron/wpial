<script>
function populatePre(school,sport) {
	document.getElementById('contentsP').textContent = "";
	document.getElementById('contentsOpponentTable1').textContent = "";
	document.getElementById('contentsOpponentTable2').textContent = "";
	var url; 
	if(school=="all" && sport=="all")
		url = "data/all.txt"
	else if (school=="all"){
		populateSportP(school,sport);
		document.getElementById('contents').textContent = "";
		return;
	}
	else if (sport=="all")
		url = "data/"+school+".txt";
	else{
			// url = "data/specificData/"+school+"+"+sport+"/seasons.html";
			populateP(school,sport);
			document.getElementById('contents').textContent = "";
			return;
		}

		var xhr = new XMLHttpRequest();
		xhr.onload = function () {
			document.getElementById('contents').textContent = this.responseText;
		};
		xhr.open('GET', url);
		xhr.send();
	}
	function populateSportP(school,sport) {
		var url; 
		url = "data/dataBySport/"+sport+"/sortByBest.html";

		var xhr = new XMLHttpRequest();
		xhr.onload = function () {
			document.getElementById('contentsP').innerHTML = this.responseText;
		};
		xhr.open('GET', url);
		xhr.send();
	}
	function populateP(school,sport) {
		var url; 
		url = "data/specificData/"+school+"+"+sport+"/seasons.html";

		var xhr = new XMLHttpRequest();
		xhr.onload = function () {
			document.getElementById('contentsP').innerHTML = this.responseText;
		};
		xhr.open('GET', url);
		xhr.send();

		url = "data/specificData/"+school+"+"+sport+"/opponentsABC.html";	
		xhr = new XMLHttpRequest();
		xhr.onload = function () {
			document.getElementById('contentsOpponentTable1').innerHTML = this.responseText;
		};
		xhr.open('GET', url);
		xhr.send();

		url = "data/specificData/"+school+"+"+sport+"/opponentsGP.html";	
		xhr = new XMLHttpRequest();
		xhr.onload = function () {
			document.getElementById('contentsOpponentTable2').innerHTML = this.responseText;
		};
		xhr.open('GET', url);
		xhr.send();
	}
	function addLink(school,sport) {
		var url; 
		if(school=="all" && sport=="all")
			url = "data/all.txt"
		else if (school=="all")
			url = "data/"+sport+".txt";
		else if (sport=="all")
			url = "data/"+school+".txt";
		else
			url = "data/specificData/"+school+"+"+sport+"/seasons.html";
		var str;
		str = "Permanent Data Link";
		var result;
		result = str.link("http://patmyron.github.io/wpial/"+url);
		document.getElementById("contentsHyperlink").innerHTML = result;
	}
	</script>
function populatePre(school, sport) {
    document.getElementById('contentsP').textContent = "";
    document.getElementById('contentsOpponentTable1').textContent = "";
    if (school === "all" && sport === "all") {
        ajaxHelper("data/all.html", 'contentsP');
    }
    else {
        if (school === "all") { // sport
            ajaxHelper("data/dataBySport/" + sport + ".html", 'contentsP');
        } else if (sport === "all") { // school
            ajaxHelper("data/dataBySchool/" + school + ".html", 'contentsP');
        } else { // specific team
            ajaxHelper("data/specificData/" + school + "+" + sport + "/seasons.html", 'contentsP');
            ajaxHelper("data/specificData/" + school + "+" + sport + "/opponentsGP.html", 'contentsOpponentTable1');
        }
    }
}

function ajaxHelper(url, elementId) {
    var xhr = new XMLHttpRequest();
    xhr.onload = function () {
        document.getElementById(elementId).innerHTML = this.responseText;
    };
    xhr.open('GET', url);
    xhr.send();
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
    } else { 	// window width is over 641px
        document.getElementById("schoolSelect").size = "100";
        document.getElementById("sportSelect").size = "9";
    }
}
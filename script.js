function populatePre(school, sport) {
    document.getElementById('contentsP').textContent = "";
    document.getElementById('contentsOpponentTable1').textContent = "";
    if (school === "all" && sport === "all") {
    } else if (school === "all") {
        ajaxHelper("data/dataBySport/" + sport + ".html", 'contentsP');
    } else if (sport === "all") {
        ajaxHelper("data/dataBySchool/" + school + ".html", 'contentsP');
    } else { // specific team
        ajaxHelper("data/specificData/" + school + " " + sport + " seasons.html", 'contentsP');
        ajaxHelper("data/specificData/" + school + " " + sport + " opponents.html", 'contentsOpponentTable1');
    }
}

function ajaxHelper(url, elementId) {
    var xhr = new XMLHttpRequest();
    xhr.onload = function () {
        document.getElementById(elementId).innerHTML = this.responseText;
        $(document).ready( function () {
            $('table').DataTable({paging: false, info: true, destroy: true, search: {regex: true}});
        } );
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

function setSelectSize(select) {
    select.size = Math.max(select.options.length, 1);
}

// media query change
function WidthChange(mq) {
    var schoolSelect = document.getElementById("schoolSelect");
    var sportSelect = document.getElementById("sportSelect");

    if (mq.matches) { // window width is less than 641px
        schoolSelect.size = "1";
        sportSelect.size = "1";
    } else {
        setSelectSize(schoolSelect);
        setSelectSize(sportSelect);
    }
}

var sport = "all";
var school = "all";
$.get("backend/GetTables/WPIAL schools.txt", function( data ) {
    $.trim(data).split('\n').forEach(function (line) {
        $('#schoolSelect').append(new Option(line, line));
    });
    WidthChange(window.matchMedia("(max-width: 641px)"));
});
populatePre(school, sport);

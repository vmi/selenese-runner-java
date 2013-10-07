function decodeForm() {
    var q = document.location.search.substring(1);
    q.split(/&/).forEach(function (item) {
	var pair = item.split(/=/, 2);
	var value = decodeURIComponent(pair[1]);
        var elem = document.getElementById(pair[0]);
        if (elem) {
            var text = elem.innerText;
            if (text)
		text += ", " + value;
	    else
		text = value;
            elem.innerText = text;
        }
    });
}

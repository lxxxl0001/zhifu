function showCountDown(afterTime, period) {
    timeold = afterTime;

    sectimeold = timeold / 1000;
    secondsold = Math.floor(sectimeold);

    msPerDay = 24 * 60 * 60 * 1000
    e_daysold = timeold / msPerDay
    daysold = Math.floor(e_daysold);
    e_hrsold = (e_daysold - daysold) * 24;
    hrsold = Math.floor(e_hrsold);
    e_minsold = (e_hrsold - hrsold) * 60;
    minsold = Math.floor((e_hrsold - hrsold) * 60);
    seconds = Math.floor((e_minsold - minsold) * 60);
    if (daysold < 0) {
        daysold = 0;
        hrsold = 0;
        minsold = 0;
        seconds = 0;
    }

    if (seconds == 0 && minsold == 0 && hrsold == 0) {

        $(".roadmap-table tr").find("td[class]:last").find("p:last,label:last,span:last").css("font-weight", "normal");
        window.clearInterval(countDownTimer);
    }

 
  
        var hh = parseInt(minsold) + parseInt(hrsold * 60);
        if (hh < 10) {
            hh = "0" + hh;
        }
        if (seconds < 10) {
            seconds = "0" + seconds;
        }
        $(".daojishi #time").html(hh+'分'+seconds+'秒');
      

    if (cpNumber == -1 && cpNumber != period) {
        cpNumber = period;
    }
}
function doPlay() {
    tempCount++;
    try {
        if (checkSound()) {
            var a = $("#duSound");
            a[0].load ? (a[0].load(), a[0].play()) : (a.length && a.remove(), a = document.createElement("bgsound"), a.id = "duSound", a.src = "/Public/Home/images/du.mp3", a.loop = 1, $(a).appendTo(document.body))
        }
    } catch(b) {}
    warnCount > tempCount ? window.setTimeout("doPlay()", 1e3) : tempCount = 0
}
function parseToDate(a) {
    var b = /^([0-9]{4})-([0-9]{2})-([0-9]{2}) ([0-9]{2}):([0-9]{2}):([0-9]{2})$/g,
    c = b.exec(a);
    return null != c ? new Date(c[1], c[2], c[3], c[4], c[5], c[6]) : null
}
function checkSound() {
    return "0" != $.cookie("countdown_sound") ? (openSound(), !0) : (closeSound(), !1)
}
var warnTime = "",
warnCount = 6,
tempCount = 0,
cpNumber = -1,
countDownTimer = null;
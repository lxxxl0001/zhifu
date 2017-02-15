$(function () {
    var soundswitch = $.cookie("countdown_sound");
    //关闭声音
    if (soundswitch == "0") {
        closeSound();
    }
    $("#countdown_sound").bind("click", function () {
        if ($("#countdown_sound").attr("class") == "sound") {
            closeSound();
            $.cookie("countdown_sound", "", "0", { expires: 3600 * 24 * 30, path: "/", secure: false });
        } else {
            openSound();
            $.cookie("countdown_sound", "", "1", { expires: 3600 * 24 * 30, path: "/", secure: false });
        }
    });
});

//设置cookie 封装
function SetCookieTip(key, obj, day) {
    var time = new Date();
    time.setDate(day);
    $.cookie(key, "", obj, { expires: time, path: "/", secure: false });
}

function openSound() {
    $("#countdown_sound").removeClass("close").attr("title", "点击关闭提醒声音");
}
function closeSound() {
    $("#countdown_sound").addClass("close").attr("title", "点击开启提醒声音");
}

function Glitter() {
    var i = 0;
    var result = $(".roadmap-table tr").find("td[class]:last").find("p:last,label:last,span:last");
    result.css("font-weight", "bold");

    var timeOutId = setTimeout(function () {
        result.fadeOut(100).fadeIn(100);
        i++;
        if (i == 1) {
            timeOutId = setInterval(arguments.callee, 600);
        }
        if (i == 40) {
            window.clearInterval(timeOutId);
        }
    }, 1000);
};

function luzhuFirstShow(numOfPeriod, timeOfPeriod) {
    if (numOfPeriod != -1 && timeOfPeriod != -1 && numOfPeriod == timeOfPeriod) {
        $(".roadmap-table tr").find("td[class]:last").find("p:last,label:last,span:last").css("font-weight", "bold");
    }
}

//彩种数据请求提示
var warnTxt = '<div id="warn_tips" >温馨提示：<span id="warn_period"></span>期开奖结果未获取，继续等待自动更新或<a href="javascript:window.location.reload();">手动刷新</a></div>';
var warnShow = '';
//显示提示
function showLotPeriodNumWarn(period) {
    var warmDiv = $("#warn_tips");
    if (warmDiv.length > 0) warmDiv.show();
    else {
        $(".lot-award").after(warnTxt);
    }
    $("#warn_period").text(period);
    GlitterPanel();
}
//边框变色提醒
function GlitterPanel() {
    if (warnShow == "") {
        warnShow = 'show';
        setTimeout(function () {
            var warnTips = $("#warn_tips");
            if (warnTips.hasClass("warn_red")) {
                warnTips.css("border-color", "red");
                warnTips.removeClass("warn_red");
            }
            else {
                warnTips.css("border-color", "#FED22F");
                warnTips.addClass("warn_red");
            }
            setTimeout(arguments.callee, 600);
        }, 1000);
    }
}
//关闭提示
function hideLotPeriodNumWarn() {
    var warmDiv = $("#warn_tips");
    if (warmDiv) warmDiv.hide();
}
function getCookie(name)//取cookies函数        
{
    var arr = document.cookie.match(new RegExp("(^| )" + name + "=([^;]*)(;|$)"));
    if (arr != null) return unescape(arr[2]); return "";
}
function setCookie(name, value, minute) {
    var exp = new Date();
    exp.setTime(exp.getTime() + minute * 60 * 1000);
    document.cookie = name + "=" + escape(value) + ";path=/;expires=" + exp.toGMTString(); //domain=localhost;
}
/**
* 使用举例：
//注： 写入时，subName参数请传递空值或null
//写入Cookies-值为字符串，即不包含子键
$.cookie("singleKey", "", "singleKey-value", { expires: 1, path: "/", secure: false })
//读取Cookies-根据主键
alert("singleKey:" + $.cookie("singleKey"));

//写入Cookies-值为对象，则每个属性名为子键的名称，属性值为子键值
var subNameObj = { subName1: "aaa", subName2: "bbb", subName3: "ccc" };
$.cookie("multiKey", "", subNameObj, { expires: 1, path: "/", secure: false });
//读取Cookies-根据主键
alert("multiKey:" + $.cookie("multiKey"));
//读取Cookies-根据主键和子键
alert("multiKey,subName1:" + $.cookie("multiKey", "subName1"));
*/
jQuery.cookie = function (a, b, c, d) { var e, f, g, h, i, j, k, l, m, n, o, p, q, r, s; if ("undefined" == typeof c) { if (m = null, document.cookie && "" != document.cookie) for (n = document.cookie.split(";"), o = 0; o < n.length; o++) if (p = jQuery.trim(n[o]), p.substring(0, a.length + 1) == a + "=") { if (m = decodeURIComponent(p.substring(a.length + 1)), "undefined" != typeof b && null != b && "" != b) for (q = m.toString().split("&"), r = 0; r < q.length; r++) if (s = jQuery.trim(q[r]), s.substring(0, b.length + 1) == b + "=") { m = decodeURIComponent(s.substring(b.length + 1)); break } break } return m } if (d = d || {}, null === c && (c = "", d.expires = -1), e = "", d.expires && ("number" == typeof d.expires || d.expires.toUTCString) && ("number" == typeof d.expires ? (f = new Date, f.setTime(f.getTime() + 1e3 * d.expires)) : f = d.expires, e = "; expires=" + f.toUTCString()), g = d.path ? "; path=" + d.path : ";path=/", h = d.domain ? "; domain=" + d.domain : "", i = d.secure ? "; secure" : "", "object" == typeof c) { j = 0, k = ""; for (l in c) j > 0 && (k += "&"), k += l + "=" + encodeURIComponent(c[l]), j++; c = k } else c = encodeURIComponent(c); document.cookie = [a, "=", c, e, g, h, i].join("") };




/**       
 * 对Date的扩展，将 Date 转化为指定格式的String       
 * 月(M)、日(d)、12小时(h)、24小时(H)、分(m)、秒(s)、周(E)、季度(q) 可以用 1-2 个占位符       
 * 年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字)       
 * eg:       
 * (new Date()).pattern("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423       
 * (new Date()).pattern("yyyy-MM-dd E HH:mm:ss") ==> 2009-03-10 二 20:09:04       
 * (new Date()).pattern("yyyy-MM-dd EE hh:mm:ss") ==> 2009-03-10 周二 08:09:04       
 * (new Date()).pattern("yyyy-MM-dd EEE hh:mm:ss") ==> 2009-03-10 星期二 08:09:04       
 * (new Date()).pattern("yyyy-M-d h:m:s.S") ==> 2006-7-2 8:9:4.18       
 */
Date.prototype.pattern = function (fmt) {
    var o = {
        "M+": this.getMonth() + 1, //月份           
        "d+": this.getDate(), //日           
        "h+": this.getHours() % 12 == 0 ? 12 : this.getHours() % 12, //小时           
        "H+": this.getHours(), //小时           
        "m+": this.getMinutes(), //分           
        "s+": this.getSeconds(), //秒           
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度           
        "S": this.getMilliseconds() //毫秒           
    };
    var week = {
        "0": "/u65e5",
        "1": "/u4e00",
        "2": "/u4e8c",
        "3": "/u4e09",
        "4": "/u56db",
        "5": "/u4e94",
        "6": "/u516d"
    };
    if (/(y+)/.test(fmt)) {
        fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    }
    if (/(E+)/.test(fmt)) {
        fmt = fmt.replace(RegExp.$1, ((RegExp.$1.length > 1) ? (RegExp.$1.length > 2 ? "/u661f/u671f" : "/u5468") : "") + week[this.getDay() + ""]);
    }
    for (var k in o) {
        if (new RegExp("(" + k + ")").test(fmt)) {
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
        }
    }
    return fmt;
}



var currentPeriodNumber = -1;
var nextPeriodNumber = -1;
var timeInterval = 5000;
var errorCount = 0;//请求出错次数
var requireCount = 0;//请求次数
var awardTick = function () {
    $.getJSON('getAwardTimes.do', {t:Math.random()}, function (data) {
        //计数请求次数
        requireCount += 1;
        if ((data.current.periodNumber != currentPeriodNumber) && currentPeriodNumber != -1) {
        	window.setTimeout(afterAwarded, 1000);
            requireCount = errorCount = 0;
        }
        if (timeInterval != 0) {
        	if (currentPeriodNumber != -1 ) {//判断第一次加载
        		var nums = data.current.awardNumbers.split(',');
        		var str = "";
				for (var i = 0; i < nums.length; i++) {
					str = str + '<i class="no' + nums[i] + '">' + nums[i] + '</i>';
				}
				layer.open({title: [''+data.current.awardTime.substring(10, 16)+' 最新第'+data.current.periodNumber+'期开奖号码：',
                'background-color:#f9f9f9; color:#444;'],content:'<div class="nums">'+str+'</div>',time: 2});	
            }
            currentPeriodNumber = data.current.periodNumber;
            nextPeriodNumber = data.next.periodNumber;
        }
        var _time = parseInt(data.next.awardTimeInterval + parseInt(Math.random() * 3000));
        window.setTimeout(awardTick, data.next.awardTimeInterval < 1000 ? 1000 : _time);
        timeInterval = 0;
    });
};


var ctimeOfPeriod = -1;
var cpCurrAwardData = null;
var cpNextAwardTimeInterval = -1;
var loadAwardTimes = function () {
    $.getJSON('getAwardTimes.do', {t:Math.random()}, function (data) {
        //请求到数据后需要做的事情
        cpCurrAwardData = data;
        //期数不同，则开始封盘倒计时
        if (data.current.periodNumber != cpNumber) {
            cpNextAwardTimeInterval = data.next.awardTimeInterval;
            if (countDownTimer) {
                window.clearInterval(countDownTimer);
            }
            countDownTimer = window.setInterval(function () {
                cpNextAwardTimeInterval = Math.max(0, cpNextAwardTimeInterval - 1000);
                showCountDown(cpNextAwardTimeInterval, data.next.periodNumber);
            }, 1000);
        }
        cpNumber = data.current.periodNumber;
        if (ctimeOfPeriod == -1) {//判断第一次加载
            ctimeOfPeriod = data.current.periodNumber;
        }
        $(".daojishi #period").html(data.next.periodNumber);
        var _time = parseInt(data.next.awardTimeInterval + parseInt(Math.random() * 3000));
        window.setTimeout(loadAwardTimes, data.next.awardTimeInterval < 1000 ? 1000 : _time);
    });
};

var cpNumber = -1;
var countDownTimer = null;
var showCountDown = function (afterTime, period) {
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
        window.clearInterval(countDownTimer);
    }
    var hh = parseInt(minsold) + parseInt(hrsold * 60);
	if (hh < 1 && hh > 3) {
    	$(".sub-btn").addClass("layui-btn-disabled");
    	$(".sub-btn").attr("disabled","disabled");
    }else{
    	$(".sub-btn").removeClass("layui-btn-disabled");
		$(".sub-btn").removeAttr("disabled");
    }
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
};

//刷新页面
var afterAwarded = function () {
    setTimeout(function () {
    	var url = window.location.href;
		url = url.indexOf("?") > 0 ? url.split('?')[0] : url;
    	window.location.href = url+"?t="+parseInt(Math.random() * 3000);
    }, 1000);
};
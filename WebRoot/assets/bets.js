	
$(function () {
    var loadAwardTimesTimer, ctimeOfPeriod = -1;
    var cpCurrAwardData = null;
    var cpNextAwardTimeInterval = -1;
    function loadAwardTimes() {
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
            loadAwardTimesTimer = window.setTimeout(loadAwardTimes, data.next.awardTimeInterval < 10 ? 10000 : data.next.awardTimeInterval + 1000);
        });
    }
    //每10秒刷新开奖时间数据
    loadAwardTimesTimer = window.setTimeout(loadAwardTimes, 1000);
    //初始化按钮事件
    initEvent();
});

var warnTime = "",
warnCount = 6,
tempCount = 0,
cpNumber = -1,
countDownTimer = null;
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
	if (hh < 1) {
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
}

function initEvent(){
	var step = 10;
	//减号
	$(".decrease").on('click',function(){
		var input = $(this).parent().find("input");
		var innum = Number(input.val());
		if(innum>step){
			input.val(innum-step);
		}
	});
	//加号
	$(".increase").on('click',function(){
		var input = $(this).parent().find("input");
		var innum = Number(input.val());
		if(innum<100){
			input.val(innum+step);
		}
	});
	//提交投注
	$(".sub-btn").on('click',function(){
		var input = $(this).parent().find("input");
		var innum = Number(input.val());
		var carno = Number(input.attr("data"));
		if(innum>0 && innum<100){
			$.post("saveFlowing.do", {
				userid : $("#userid").val(),
				termNum : $("#period").html(),
				carNum : carno,
				buyAmount : innum
			}, function(data) {
				if (parseInt(data.agent) < 5) {
                    alert("您的微信版本低于5.0无法使用微信支付。");
                    return;
                }
				layer.open({content:'加油成功！',time: 2});	
				/*callpay({
                    "appId" : ""+data.appId.toString(), //公众号名称，由商户传入  
                    "timeStamp" : ""+data.timeStamp.toString(), //时间戳，自 1970 年以来的秒数  
                    "nonceStr" : ""+data.nonceStr.toString(), //随机串  
                    "package" : ""+data.packageValue.toString(), //商品包信息
                    "signType" : ""+data.signType.toString(), //微信签名方式:  
                    "paySign" : ""+data.paySign.toString() //微信签名  
                });*/
			});
		}
	});
}
//刷新页面
function afterAwarded() {
    setTimeout(function () {
    	var url = window.location.href;
		url = url.indexOf("?") > 0 ? url.split('?')[0] : url;
    	window.location.href = url+"?t="+parseInt(Math.random() * 3000);
    }, 2000);
}

function callpay(json){
	WeixinJSBridge.invoke('getBrandWCPayRequest',json,
        function(res){
            alert(res.err_msg);
            if(res){
				layer.open({title: ['第'+$("#period").html()+'期：',
				'background-color:#f9f9f9; color:#444;'],content:'<div>加油成功！</div>',time: 2});	
			}else{
				layer.open({title: ['第'+$("#period").html()+'期：',
				'background-color:#f9f9f9; color:#444;'],content:'<div>您来晚了！已经开奖</div>',time: 2});	
			}
        }
    );
}

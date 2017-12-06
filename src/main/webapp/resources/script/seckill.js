/**
 * Created by hays on 2017/11/30.
 */
var seckill = {
    //封装秒杀相关的ajax的url
    URL: {
        now : function () {
            return '/seckill/time/now';
        },
        exposer : function(seckillId){
            return '/seckill/' + seckillId + "/exposer";
        },
        execution : function (seckillId, md5) {
            return '/seckill/' + seckillId + "/" + md5 + "/execution";
        }
    },
    validatephone: function (phone) {
        return (phone && phone.length == 11 && !isNaN(phone));
    },
    handleSeckill : function(seckillId, $node){
        $node.hide().html('<button class="btn btn-primary btn-lg" id="killBtn">秒杀开始</button>');
        $.post(seckill.URL.exposer(seckillId), {}, function(result){//获取到请求地址
            if(result && result['success']){
                var exposer = result['data'];
                if(exposer['exposed']){
                    //开启秒杀
                    var md5 = exposer['md5'];
                    var killUrl = seckill.URL.execution(seckillId, md5);
                    $("#killBtn").one('click', function () {
                        //禁用按钮
                        $(this).addClass('disable');
                        //发送请求
                        $.post(killUrl, {}, function(result){
                            if(result){
                                if(result['success']){
                                    var killResult = result['data'];
                                    var status = killResult['status'];
                                    var statusInfo = killResult['statusInfo'];
                                    //显示秒杀结果
                                    $node.html('<span class="label label-success">' + statusInfo + '</span>');
                                }else{
                                    var error = result['error'];
                                    $node.html('<span class="label label-success">' + error + '</span>');
                                }
                            }
                        });
                    });//绑定一次点击事件
                    $node.show();
                }else{
                    //未开启秒杀
                    var now = exposer['now'];
                    var startTime = exposer['startTime'];
                    var endTime = exposer['endTime'];
                    seckill.countdown(seckillId, now, startTime, endTime);
                }
            }else{
                console.log("result:" + result);
            }
        });
    },
    countdown : function (seckillId, nowTime, startTime, endTime) {
        var $seckillBox = $('#seckill-box');
        if(nowTime > endTime){
            $seckillBox.html('秒杀结束！');
        }else if(nowTime < startTime){
            var killTime = new Date(startTime + 1000);
            $seckillBox.countdown(killTime, function(event){//倒计时
                var format = event.strftime('秒杀倒计时：%D天 %H时 %S秒');
                $seckillBox.html(format);
            }).on('finish.countdown', function(){//完成后回调
                seckill.handleSeckill(seckillId, $seckillBox);
            });
        }else{//秒杀开始
            seckill.handleSeckill(seckillId, $seckillBox);
        }
    },
    //详情页秒杀逻辑
    detail: {
        //初始化
        init: function (params) {
            //手机验证和登录
            //计时交互
            var killPhone = $.cookie('killPhone');
            var startTime = params['startTime'];
            var endTime = params['endTime'];
            var seckillId = params['seckillId'];
            if (!seckill.validatephone(killPhone)){
                var $killPhoneModal = $('#killPhoneModal');
                $killPhoneModal.modal({
                    show: true,//显示弹出层
                    backdrop : 'static',//禁止位置关闭
                    keyboard:false//关闭键盘事件
                });
                $('#killPhoneBtn').click(function () {
                    var inputPhone = $('#killPhoneKey').val();
                    if(seckill.validatephone(inputPhone)){
                        $.cookie('killPhone', inputPhone, {expires:7, path:'/seckill'});
                        window.location.reload();
                    }else{
                        $('#killPhoneMessage').hide().html('<label class="label label-danger">手机号错误！</label>').show(300);
                    }
                });
            }
            $.get(seckill.URL.now(), {}, function (result) {
                if(result && result['success']){
                    var nowTime = result['data'];
                    //时间判断
                    seckill.countdown(seckillId, nowTime, startTime, endTime);
                }else{
                    console.log('result:' + result);
                }
            });
        }
    }
}
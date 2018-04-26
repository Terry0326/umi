$(function() {
        //获取url中的id
        function getQueryString(name) {
            var reg = new RegExp('(^|&)' + name + '=([^&]*)(&|$)', 'i');
            var r = window.location.search.substr(1).match(reg);
            if (r != null) {
                return unescape(r[2]);
            }
            return null;
        }
        if (getQueryString("id")) {
            var urlId = getQueryString("id");
        }
        //填数据
        $.ajax({
            type: "get",
            // url: "http://116.62.228.67:8011/umiclient/share/topic?id=" + urlId,
            url: "js/data.json",
            dataType: "json",
            success: function(data) {
                console.log(data)
                var data = data.data;
                //评论部分
                var comments = data.comments;
                var commentStr = '';
                for (var i = 0; i < comments.length; i++) {
                    commentStr +=
                        '<li class="elli lh22">' +
                        '<span>' + comments[i].nickname + ' : </span>' +
                        '<i class="color_999">' + comments[i].content + '</i>' +
                        '</li>';
                }
                $("#commentWrap").html(commentStr);
                //顶部用户部分
                var userStr = '';
                userStr =
                    '<div class="w35 h35 overHidden radius50 mr15">' +
                    '<img src="' + data.imageUrlPrefix + '' + data.user.avatar + '" alt="" class="wp100">' +
                    '</div>' +
                    '<div>' +
                    '<p class="fs14">' + data.user.nickname + '</p>' +
                    '<p class="color_aaa fs13 pt5">' +
                    '<span>' + data.user.address.country + ' ' + data.user.address.city + '</span>' +
                    '<img src="img/right.png" alt="" class="w10">' +
                    '</p>' +
                    '</div>';
                $("#userPart").html(userStr);
                //评论数
                var commentsNum = data.commentsNum;
                $("#commentsNum").html(commentsNum);
                //点赞数
                var topicLikeNum = data.user.topicLikeNum;
                $("#zanNum").html(topicLikeNum);
                //视频
                if (data.topicType.code == 2) {
                    $("#videoWrap").html(
                        '<video class="relative zIndex1" id="video" controls style="height:250px;">' +
                        '<source src="' + data.content + '" type="video/mp4" id="vedioSrc"> 您的浏览器不支持HTML5视频' +
                        '</video>');
                    var videoF = (function() {
                        var tmpV = {};
                        var video_playing;
                        /**
                         * @description 切换内容列时暂停当前播放的视频
                         */
                        function pausedVBeforeChangeLi() {
                            if (video_playing && !video_playing.ended && !video_playing.paused) {
                                video_playing.pause();
                            }
                        };
                        tmpV.pausedVBeforeChangeLi = pausedVBeforeChangeLi;
                        /**
                         * @description 播放全屏 很诡异，这个方法居然不可用
                         * @param {Object} element
                         */
                        function launchFullScreen(element) {
                            if (element.requestFullScreen) {
                                element.requestFullScreen();
                            } else if (element.mozRequestFullScreen) {
                                element.mozRequestFullScreen();
                            } else if (element.webkitRequestFullScreen) {
                                element.webkitRequestFullScreen();
                            }
                        };
                        /**
                         * @description 取消全屏 这个方法也是不可用
                         * @param {Object} elem
                         */
                        function cancelFullScrren(elem) {
                            elem = elem || document;
                            if (elem.cancelFullScrren) {
                                elem.cancelFullScrren();
                            } else if (elem.mozCancelFullScreen) {
                                elem.mozCancelFullScreen();
                            } else if (elem.webkitCancelFullScreen) {
                                console.log("webkitCancelFullScreen");
                                elem.webkitCancelFullScreen();
                            }
                        };
                        /**
                         * @return 返回支持的全屏函数 从网上找到了这段代码，具体网址忘记了，返回支持的全屏方法，在Safari上可用
                         * @param {Object} elem
                         */
                        function fullscreen(elem) {
                            var prefix = 'webkit';
                            if (elem[prefix + 'EnterFullScreen']) {
                                return prefix + 'EnterFullScreen';
                            } else if (elem[prefix + 'RequestFullScreen']) {
                                return prefix + 'RequestFullScreen';
                            };
                            return false;
                        };
                        var v = document.getElementById("video");
                        videoEvent(v);

                        function videoEvent(v) {
                            var video = v,
                                doc = document;
                            video.addEventListener('play', function() {
                                //每次只能播放一个视频对象
                                if (video_playing && video_playing !== this) {
                                    console.log('multi')
                                    pausedVBeforeChangeLi();
                                }
                                video_playing = this;
                                console.log('play');
                                var fullscreenvideo = fullscreen(video);
                                video[fullscreenvideo]();
                            });
                            video.addEventListener('click', function() {
                                //点击时如果在播放，自动全屏；否则全屏并播放
                                console.log('click')
                                if (this.paused) {
                                    console.log('paused');
                                    this.play();
                                } else {
                                    var fullscreenvideo = fullscreen(video);
                                    video[fullscreenvideo]();
                                }
                            })
                            video.addEventListener('pause', function(e) {
                                this.webkitExitFullScreen();
                            });
                            video.addEventListener("webkitfullscreenchange", function(e) {
                                //TODO 未侦听到该事件
                                console.log(3);
                                if (!doc.webkitIsFullScreen) { //退出全屏暂停视频
                                    video.pause();
                                };
                            }, false);
                            video.addEventListener("fullscreenchange ", function(e) {
                                console.log(31);
                                if (!doc.webkitIsFullScreen) { //退出全屏暂停视频
                                    video.pause();
                                };
                            }, false);
                            video.addEventListener('ended', function() {
                                //播放完毕，退出全屏
                                console.log(4)
                                this.webkitExitFullScreen();
                            }, false);
                        };
                        tmpV.videoEvent = videoEvent;
                        return tmpV;
                    }());
                }
            },
            error: function(XMLHttpRequest, textStatus, errorThrown) {
                console.log("请求失败！");
            }
        });
        document.body.style.overflow = 'hidden';
        zymedia('video', { autoplay: true });
        var screenheight = window.screen.height / 2;
        $("#modelView").width(window.screen.width);
        $("#modelView").height(window.screen.height);
        var videoheight = $(".zy_media").height() / 2;
        $(window).on("click", function() {
            $(".modal").fadeIn("fast");
        });
        $("#cancel").on("click", function() {
            $(".modal").fadeOut("fast");
        })
        $("#download").on("click", function() {
            window.location.href = "http://www.uingapp.com/";
        })
        $(".modal").on("click", function() {
            $(".modal").fadeOut("fast");
        })
        $(".downloadWrap").on("click", function(e) {
            e.stopPropagation();
        })
    })
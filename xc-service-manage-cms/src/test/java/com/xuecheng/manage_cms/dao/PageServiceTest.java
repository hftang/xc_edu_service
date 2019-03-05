package com.xuecheng.manage_cms.dao;

import com.xuecheng.manage_cms.service.PageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author hftang
 * @date 2019-02-27 11:35
 * @desc dao的测试类
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class PageServiceTest {

    @Autowired
    private PageService pageService;



    @Test
    public void testFindPage() {

        String pageHtml = pageService.getPageHtml("5c7c8cd5782371327c5c8382");

        System.out.println(pageHtml);
        /**
         * <!DOCTYPE html>
         * <html lang="en">
         * <head>
         *     <meta charset="UTF-8">
         *     <title>Title</title>
         *     <link rel="stylesheet" href="http://www.xuecheng.com/plugins/normalize-css/normalize.css" />
         *     <link rel="stylesheet" href="http://www.xuecheng.com/plugins/bootstrap/dist/css/bootstrap.css" />
         *     <link rel="stylesheet" href="http://www.xuecheng.com/css/page-learing-index.css" />
         *     <link rel="stylesheet" href="http://www.xuecheng.com/css/page-header.css" />
         * </head>
         * <body>
         * <div class="banner-roll">
         *     <div class="banner-item">
         *                         <h1>http://192.168.101.64/group1/M00/00/01/wKhlQFp5wnCAG-kAAATMXxpSaMg864.png</h1>
         *                 <div class="item" style="background-image: url(http://192.168.101.64/group1/M00/00/01/wKhlQFp5wnCAG-kAAATMXxpSaMg864.png);"></div>
         *                 <h1>http://192.168.101.64/group1/M00/00/01/wKhlQVp5wqyALcrGAAGUeHA3nvU867.jpg</h1>
         *                 <div class="item" style="background-image: url(http://192.168.101.64/group1/M00/00/01/wKhlQVp5wqyALcrGAAGUeHA3nvU867.jpg);"></div>
         *                 <h1>http://192.168.101.64/group1/M00/00/01/wKhlQFp5wtWAWNY2AAIkOHlpWcs395.jpg</h1>
         *                 <div class="item" style="background-image: url(http://192.168.101.64/group1/M00/00/01/wKhlQFp5wtWAWNY2AAIkOHlpWcs395.jpg);"></div>
         *
         *     </div>
         *     <div class="indicators"></div>
         * </div>
         * <script type="text/javascript" src="http://www.xuecheng.com/plugins/jquery/dist/jquery.js"></script>
         * <script type="text/javascript" src="http://www.xuecheng.com/plugins/bootstrap/dist/js/bootstrap.js"></script>
         * <script type="text/javascript">
         *     var tg = $('.banner-item .item');
         *     var num = 0;
         *     for (i = 0; i < tg.length; i++) {
         *         $('.indicators').append('<span></span>');
         *         $('.indicators').find('span').eq(num).addClass('active');
         *     }
         *
         *     function roll() {
         *         tg.eq(num).animate({
         *             'opacity': '1',
         *             'z-index': num
         *         }, 1000).siblings().animate({
         *             'opacity': '0',
         *             'z-index': 0
         *         }, 1000);
         *         $('.indicators').find('span').eq(num).addClass('active').siblings().removeClass('active');
         *         if (num >= tg.length - 1) {
         *             num = 0;
         *         } else {
         *             num++;
         *         }
         *     }
         *     $('.indicators').find('span').click(function() {
         *         num = $(this).index();
         *         roll();
         *     });
         *     var timer = setInterval(roll, 3000);
         *     $('.banner-item').mouseover(function() {
         *         clearInterval(timer)
         *     });
         *     $('.banner-item').mouseout(function() {
         *         timer = setInterval(roll, 3000)
         *     });
         * </script>
         * </body>
         * </html>
         */

    }



}
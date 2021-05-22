package top.huqibiao.util;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import lombok.extern.slf4j.Slf4j;
import java.net.HttpCookie;
import java.util.HashMap;
import java.util.Map;


/**
 * user_id: 提交表单时的name值，值为你的学号
 * password: 提交表单时的name值，值为你密码的md5值
 *
 * 登录地址：
 * https://acm.webturing.com/login.php
 * 请求方式：post
 * 通过表单提交
 *
 * 签到地址：
 * https://acm.webturing.com/postFunction.php?action=sign
 * 请求方式：post
 *
 */
@Slf4j
public class Sign {
    public void doLogin() {
        boolean isRecovery = true;
        try {

            String user_id = "你的学号"; String password = "密码的md5值";
            Map<String, Object> body = new HashMap<>();
            body.put("user_id", user_id);
            body.put("password", password);
            HttpCookie phpsessid = HttpRequest.post("https://acm.webturing.com/login.php")
                    .header(Header.CONTENT_TYPE, "application/x-www-form-urlencoded")
                    .header(Header.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.82 Safari/537.36")
                    .header(Header.CONNECTION, "keep-alive")
                    .form(body).execute().getCookie("PHPSESSID");

            String[] cookie = phpsessid.toString().split("=");
            log.info("登录aoj完成,并获取到cookie");
            String msg = HttpRequest.post("https://acm.webturing.com/postFunction.php?action=sign")
                    .header(Header.COOKIE, cookie[1])
                    .header(Header.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.82 Safari/537.36")
                    .header(Header.CONNECTION, "keep-alive")
                    .execute().body();
            log.info("签到完成,下面是签到结果");
            log.info(msg);
            HttpRequest.get("这里的推送地址,可以使用server酱或者其它的推送助手").execute();
            log.info("已将执行结果推送到微信");
        } catch (Exception e) {
            isRecovery = false;
            log.warn("签到过程中出现错误，下面是打印的出错信息");
            e.printStackTrace();
        } finally {
            String id = CronUtil.getScheduler().getTaskTable().getIds().get(0);
            if (isRecovery) {
                CronUtil.updatePattern(id,  new CronPattern("5 0 * * ?"));
            } else {
                log.info("正在尝试重新签到，下面是重新签到的信息");
                CronUtil.updatePattern(id,  new CronPattern("0/30 * * * ?"));
            }

        }
    }
}



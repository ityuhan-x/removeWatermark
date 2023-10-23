package com.example.watermark.tool;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.watermark.exception.Biz;
import com.example.watermark.exception.CustomerException;
import com.example.watermark.utils.BogusUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author laihongfeng
 * @ClassName: OwnToolApiConstant
 * @Description: 自己的图片去水印工具类api
 * @date 2023年08月17日 17:41:30
 */
public class OwnImageRemoveWatermarkToolApiConstant {

    private static final Logger log = LoggerFactory.getLogger(OwnImageRemoveWatermarkToolApiConstant.class);

    /**
     * 抖音去水印
     */
    public static JSONObject douyin(String url) {
        JSONObject videoVo = new JSONObject();
        try {
            url = url.substring(url.indexOf("https"), url.length());
            HttpResponse execute = HttpRequest.get(url).execute();
            //重定向地址去除videoId
            String videoID = BogusUtils.commGetVideoId("/note/(\\d+)/",execute.header("Location"));
            String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.90 Safari/537.36 Edg/89.0.774.54";
            String xBogus = BogusUtils.getXBogus("https://www.douyin.com/aweme/v1/web/aweme/detail/?aweme_id=" + videoID +"&aid=1128&version_name=23.5.0&device_platform=android&os_version=2333", userAgent);
            String videoDetail = HttpRequest.get(xBogus)
                    .header("Referer","https://www.douyin.com/video/"+videoID)
                    .header(Header.USER_AGENT, userAgent)
                    .header(Header.COOKIE, "msToken=" + BogusUtils.randStr(126)+"; ttwid=" + BogusUtils.getTtwid()+"; bd_ticket_guard_client_data=eyJiZC10aWNrZXQtZ3VhcmQtdmVyc2lvbiI6MiwiYmQtdGlja2V0LWd1YXJkLWl0ZXJhdGlvbi12ZXJzaW9uIjoxLCJiZC10aWNrZXQtZ3VhcmQtcmVlLXB1YmxpYy1rZXkiOiJCSTdvdE10NlVUYzN3Y2FhcVNlMmErQjZkSHcrOTNXQjhkc0lmMWVBa29mcUtwS3hrREZYR0JMR2lHS3gweGtLaHUyUWtNdklMcmZTbWtIanlNLzN4SlE9IiwiYmQtdGlja2V0LWd1YXJkLXdlYi12ZXJzaW9uIjoxfQ%3D%3D")
                    .execute()
                    .body();
            Biz.check(ObjectUtil.isEmpty(videoDetail),"本次请求失败，可再次请求！");
            JSONObject jsonObject = JSONUtil.toBean(videoDetail, JSONObject.class);
            if ((int)jsonObject.get("status_code") != 0) {
                log.error("抖音去水印实际参数{}",videoDetail);
                Biz.check(true,"解析抖音失败！");
            }
            videoVo.set("author",jsonObject.getJSONObject("aweme_detail").getJSONObject("author").get("nickname"));
            videoVo.set("uid",jsonObject.getJSONObject("aweme_detail").getJSONObject("author").get("unique_id"));
            videoVo.set("avatar",jsonObject.getJSONObject("aweme_detail").getJSONObject("author").getJSONObject("avatar_thumb").getJSONArray("url_list").getStr(0).replace("100x100", "1080x1080"));
            videoVo.set("time",jsonObject.getJSONObject("aweme_detail").get("create_time"));
            videoVo.set("title",jsonObject.getJSONObject("aweme_detail").get("desc"));
            videoVo.set("cover",jsonObject.getJSONObject("aweme_detail").getJSONObject("video").getJSONObject("origin_cover").getJSONArray("url_list").getStr(0).replace("\\u0026", "&"));
            JSONArray jsonArray = jsonObject.getJSONObject("aweme_detail").getJSONArray("images");
            List<JSONObject> list = new ArrayList<>();
            jsonArray.forEach(item -> {
                JSONObject imageUrl = new JSONObject();
                JSONObject object = (JSONObject) item;
                imageUrl.set("url",object.getJSONArray("url_list").getStr(0));
                imageUrl.set("width",object.get("width"));
                imageUrl.set("height",object.get("height"));
                list.add(imageUrl);
            });
            videoVo.set("url",list);
            videoVo.set("analysisPlatform","抖音");
            JSONObject musicJson = new JSONObject();
            musicJson.set("author",jsonObject.getJSONObject("aweme_detail").getJSONObject("music").getStr("author"));
            musicJson.set("avatar",jsonObject.getJSONObject("aweme_detail").getJSONObject("music").getJSONObject("cover_hd").getJSONArray("url_list").get(0));
            musicJson.set("url",jsonObject.getJSONObject("aweme_detail").getJSONObject("music").getJSONObject("play_url").get("uri"));
            videoVo.set("music",musicJson);
        } catch (Exception e) {
            log.error("抖音去水印异常{}", e.getMessage());
            throw new CustomerException("提取失败！");
        }
        return videoVo;
    }

    /**
     * 皮皮虾去水印
     * @param url
     * @return
     */
    public static JSONObject pipixia(String url) {
        JSONObject videoVo = new JSONObject();
        try {
            url = url.substring(url.indexOf("https"), url.length());
            HttpResponse execute = HttpRequest.get(url).execute();
            //重定向地址去除videoId
            String videoID = BogusUtils.commGetVideoId("/item/(.*)\\?",execute.header("Location"));
            //请求视频详情
            String pipixiaDetail = HttpRequest.get("https://is.snssdk.com/bds/cell/detail/?cell_type=1&aid=1319&app_name=super&cell_id=" + videoID)
                    .header(Header.USER_AGENT, BogusUtils.getUserAgent())
                    .execute().body();
            JSONObject jsonObject = JSONUtil.toBean(pipixiaDetail, JSONObject.class);
            Biz.check(jsonObject.getInt("status_code") != 0,"解析失败");
            JSONObject detailJson = jsonObject.getJSONObject("data").getJSONObject("data").getJSONObject("item");
            videoVo.set("author",detailJson.getJSONObject("author").get("name"));
            videoVo.set("avatar",detailJson.getJSONObject("author").getJSONObject("avatar").getJSONArray("download_list").getJSONObject(0).getStr("url").replace("\\u0026", "&"));
            videoVo.set("time",detailJson.get("create_time"));
            videoVo.set("title",detailJson.get("content"));
            videoVo.set("cover",detailJson.getJSONObject("cover").getJSONArray("url_list").getJSONObject(0).getStr("url").replace("\\u0026", "&"));
            JSONArray jsonArray = detailJson.getJSONObject("note").getJSONArray("multi_image");
            List<JSONObject> list = new ArrayList<>();
            jsonArray.forEach(item -> {
                JSONObject imageUrl = new JSONObject();
                JSONObject object = (JSONObject) item;
                imageUrl.set("url",object.getJSONArray("url_list").getJSONObject(0).getStr("url"));
                imageUrl.set("width",object.get("width"));
                imageUrl.set("height",object.get("height"));
                list.add(imageUrl);
            });
            videoVo.set("url",list);
            videoVo.set("analysisPlatform","皮皮虾");
        }catch (Exception e) {
            log.error("皮皮虾去水印异常", e);
            throw new CustomerException("提取失败！");
        }
        return videoVo;
    }

    /**
     * 最右去水印
     * @param url
     * @return
     */
    public static JSONObject zuiyou(String url) {
        JSONObject videoVo = new JSONObject();
        try {
            url = BogusUtils.commGetUrl(url);
            String videoId = BogusUtils.commGetVideoId("pid=(\\d+)", url);
            String zuiyouDetail = HttpRequest.post("https://share.xiaochuankeji.cn/planck/share/post/detail")
                    .header(Header.USER_AGENT, BogusUtils.getUserAgent())
                    .body("{\"pid\": "+videoId +"}")
                    .execute()
                    .body();
            JSONObject bean = JSONUtil.toBean(zuiyouDetail, JSONObject.class);
            videoVo.set("author",bean.getJSONObject("data").getJSONObject("post").getJSONObject("member").get("name"));
            videoVo.set("avatar",bean.getJSONObject("data").getJSONObject("post").getJSONObject("member").getJSONObject("avatar_urls").getJSONObject("origin").getJSONArray("urls").get(0));
            videoVo.set("analysisPlatform","最右");
            videoVo.set("title",bean.getJSONObject("data").getJSONObject("post").get("content"));
            List<JSONObject> list = new ArrayList<>();
            JSONArray jsonArray = bean.getJSONObject("data").getJSONObject("post").getJSONArray("imgs");
            jsonArray.forEach(item -> {
                JSONObject imageUrl = new JSONObject();
                JSONObject object = (JSONObject) item;
                JSONObject jsonObject = object.getJSONObject("urls").getJSONObject("origin");
                imageUrl.set("url",jsonObject.getJSONArray("urls").get(0));
                imageUrl.set("width",jsonObject.get("w"));
                imageUrl.set("height",jsonObject.get("h"));
                list.add(imageUrl);
            });
            videoVo.set("url",list);
        }catch (Exception e) {
            log.error("最右去水印异常", e);
            throw new CustomerException("提取失败！");
        }
        return videoVo;
    }

    /**
     * 快手去水印
     * @param url
     * @return
     */
    public static JSONObject kuaishou(String url) {
        JSONObject videoVo = new JSONObject();
        try {
            url = BogusUtils.commGetUrl(url);
            HttpResponse execute = HttpRequest.get(url).execute();
            String videoId = BogusUtils.commGetVideoId("/photo/(.*)\\?", execute.header("Location"));
            String kuaishouDetail = HttpRequest.post("https://v.m.chenzhongtech.com/rest/wd/photo/info")
                    .header(Header.USER_AGENT, BogusUtils.getUserAgent())
                    .header(Header.REFERER,execute.header("Location"))
                    .header(Header.COOKIE,"did=web_8460df4e1f1c4b3fa6981825fc9f8a72; didv=1692533782000")
                    .body("{\"photoId\": \"" + videoId + "\",\"isLongVideo\": false}")
                    .execute()
                    .body();
            JSONObject bean = JSONUtil.toBean(kuaishouDetail, JSONObject.class);
            videoVo.set("author",bean.getJSONObject("photo").get("userName"));
            videoVo.set("avatar",bean.getJSONObject("photo").get("headUrl"));
            videoVo.set("analysisPlatform","快手");
            videoVo.set("time",bean.getJSONObject("photo").get("timestamp"));
            videoVo.set("title", bean.getJSONObject("photo").get("caption"));
            videoVo.set("cover",bean.getJSONObject("photo").getJSONArray("coverUrls").getJSONObject(0).getStr("url"));
            List<JSONObject> list = new ArrayList<>();
            JSONArray jsonArray = bean.getJSONObject("photo").getJSONObject("ext_params").getJSONObject("atlas").getJSONArray("list");
            jsonArray.forEach(item -> {
                JSONObject imageUrl = new JSONObject();
                imageUrl.set("url","https://tx2.a.yximgs.com/" + String.valueOf(item).replace("webp","jpg"));
                imageUrl.set("width",bean.getJSONObject("photo").getJSONObject("ext_params").get("w"));
                imageUrl.set("height",bean.getJSONObject("photo").getJSONObject("ext_params").get("h"));
                list.add(imageUrl);
            });
            videoVo.set("url",list);
        }catch (Exception e) {
            log.error("快手去水印异常", e);
            throw new CustomerException("提取失败！");
        }
        return videoVo;
    }

    /**
     * 小红书去水印
     * @param url
     * @return
     */
    public static JSONObject xiaohongshu(String url) {
        JSONObject videoVo = new JSONObject();
        try {
            url = BogusUtils.commGetUrl(url);
            HttpResponse execute = HttpRequest.get(url)
                    .header(Header.USER_AGENT, "Mozilla/5.0 (iPhone; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1")
                    .execute();
            String videoId = BogusUtils.commGetVideoId("/item/(.*)\\?", execute.header("Location"));
            String detailHtml = HttpRequest.get("https://www.xiaohongshu.com/explore/"+videoId)
                    .header(Header.COOKIE,"abRequestId=4f7508af-795a-5968-9f0a-e34e084095e5; webBuild=3.4.1; xsecappid=xhs-pc-web; a1=18a0682d5deznw4i2yc3kth9atzwi56817yc0z8dq50000351385; webId=824c6c968fd0b7308e20ab50dada5090; gid=yY08KYJfqSy0yY08KYJf2WuYfduIE43JvSqTvfA9xj09uS28E32KY8888q2yqY2800DKDi2S; web_session=030037a3ed8dbb601a529c9fa6234a64d3ea3e; websectiga=3fff3a6f9f07284b62c0f2ebf91a3b10193175c06e4f71492b60e056edcdebb2; sec_poison_id=5b0f5e6d-6e87-42fc-b02c-2366f7eda33f")
                    .header(Header.USER_AGENT,"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36")
                    .execute()
                    .body();
            String detailStr = BogusUtils.commGetVideoId("window\\.__INITIAL_STATE__=(\\{.*?\\}\\})\\s*</script>", detailHtml).replaceAll("undefined","null");
            JSONObject bean = JSONUtil.toBean(detailStr, JSONObject.class);
            JSONObject detailJson = bean.getJSONObject("note").getJSONObject("noteDetailMap").getJSONObject((String) bean.getJSONObject("note").get("firstNoteId"));
            Biz.check(!detailJson.getJSONObject("note").get("type").equals("normal"),"只提取无水印图片！");
            videoVo.set("author", String.valueOf(detailJson.getJSONObject("note").getJSONObject("user").get("nickname")));
            videoVo.set("avatar",String.valueOf(detailJson.getJSONObject("note").getJSONObject("user").get("avatar")));
            videoVo.set("analysisPlatform","小红书");
            videoVo.set("time",Long.parseLong(String.valueOf(detailJson.getJSONObject("note").get("time"))));
            videoVo.set("title",detailJson.getJSONObject("note").get("title"));
            videoVo.set("cover","https://ci.xiaohongshu.com/"+detailJson.getJSONObject("note").getJSONArray("imageList").getJSONObject(0).get("traceId")+"?imageView2/2/w/1080/format/jpg");
            JSONArray jsonArray = detailJson.getJSONObject("note").getJSONArray("imageList");
            List<JSONObject> list = new ArrayList<>();
            jsonArray.forEach(item -> {
                JSONObject imageUrl = new JSONObject();
                JSONObject object = (JSONObject) item;
                imageUrl.set("url","https://ci.xiaohongshu.com/"+ object.get("traceId") +"?imageView2/2/w/1080/format/jpg");
                imageUrl.set("width",object.get("width"));
                imageUrl.set("height",object.get("height"));
                list.add(imageUrl);
            });
            videoVo.set("url",list);
        }catch (Exception e) {
            log.error("小红书去水印异常", e);
            throw new CustomerException("提取失败！");
        }
        return videoVo;
    }

    public static void main(String[] args) {
//        System.out.println(removeWatermark("7.92 DHI:/ J@V.Lw 02/28 复制打开抖音，看看# 背景图 # 欢迎取图 超好看的背景图推荐# 背景图 # 欢迎取图  https://v.douyin.com/iJEsJL8N/"));
//        System.out.println(removeWatermark("https://h5.pipix.com/s/iJEo8bHN/"));
//        System.out.println(removeWatermark("#最右#分享一条有趣的内容给你，不好看算我输。请戳链接>>https://share.xiaochuankeji.cn/hybrid/share/post?pid=346941479&zy_to=applink&share_count=1&m=2823802580d139b049e4428f518d1e07&d=a878370439c26d1bca4a11b6de81335337cbb96c632bff34d691348a21d54e44450b3dda1428997d09503425ca29d0fd&app=zuiyou&recommend=r0&name=n0&title_type=t0"));
//        System.out.println(removeWatermark("https://v.kuaishou.com/3axikZ \"风景壁纸 \"唯美图片 \"今日分享壁纸 真正属于你的感情不是伤人的冰 而是能够温暖治愈你的热茶 该作品在快手被播放过278.7万次，点击链接，打开【快手】直接观看！"));
//        System.out.println(removeWatermark("61 嘉兴彫巳刺青发布了一篇小红书笔记，快来看吧！ \uD83D\uDE06 Ohkl80CwM46IAMK \uD83D\uDE06 http://xhslink.com/aYnOVt，复制本条信息，打开【小红书】App查看精彩内容！"));
    }

    public static JSONObject removeWatermark(String url) {
        if (url.contains("pipix")){
            return JSONUtil.parseObj(pipixia(url));
        } else if (url.contains("douyin")){
            return JSONUtil.parseObj(douyin(url));
        } else if (url.contains("zuiyou")){
            return JSONUtil.parseObj(zuiyou(url));
        } else if (url.contains("xiaochuankeji")){
            return JSONUtil.parseObj(zuiyou(url));
        } else if (url.contains("kuaishou")){
            return JSONUtil.parseObj(kuaishou(url));
        } else if (url.contains("xhslink.com")) {
            return JSONUtil.parseObj(xiaohongshu(url));
        } else {
            JSONObject jsonObject = new JSONObject();
            jsonObject.set("msg", "不支持该链接");
            return jsonObject;
        }
    }

}

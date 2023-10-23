package com.example.watermark.tool;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
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
 * @Description: 自己的视频去水印工具类api
 * @date 2023年08月17日 17:41:30
 */
public class OwnVideoRemoveWatermarkToolApiConstant {

    private static final Logger log = LoggerFactory.getLogger(OwnVideoRemoveWatermarkToolApiConstant.class);

    /**
     * 抖音去水印
     */
    public static JSONObject douyinExclusive(String url) {
        JSONObject videoVo = new JSONObject();
        try {
            url = url.substring(url.indexOf("https"), url.length());
            HttpResponse execute = HttpRequest.get(url).execute();
            //重定向地址去除videoId
            String videoID = BogusUtils.extractVideoID(execute.header("Location"));
            String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.90 Safari/537.36 Edg/89.0.774.54";
            String xBogus = BogusUtils.getXBogus("https://www.douyin.com/aweme/v1/web/aweme/detail/?aweme_id=" + videoID, userAgent);
            String videoDetail = HttpRequest.get(xBogus)
                    .header("Referer","https://www.douyin.com/video/"+videoID)
                    .header(Header.USER_AGENT, userAgent)
                    .header(Header.COOKIE, "msToken=" + BogusUtils.randStr(126)+"; ttwid=" + BogusUtils.getTtwid()+"; bd_ticket_guard_client_data=eyJiZC10aWNrZXQtZ3VhcmQtdmVyc2lvbiI6MiwiYmQtdGlja2V0LWd1YXJkLWl0ZXJhdGlvbi12ZXJzaW9uIjoxLCJiZC10aWNrZXQtZ3VhcmQtcmVlLXB1YmxpYy1rZXkiOiJCSTdvdE10NlVUYzN3Y2FhcVNlMmErQjZkSHcrOTNXQjhkc0lmMWVBa29mcUtwS3hrREZYR0JMR2lHS3gweGtLaHUyUWtNdklMcmZTbWtIanlNLzN4SlE9IiwiYmQtdGlja2V0LWd1YXJkLXdlYi12ZXJzaW9uIjoxfQ%3D%3D")
                    .execute()
                    .body();
            Biz.check(ObjectUtil.isEmpty(videoDetail),"本次请求失败，可再次请求！");
            JSONObject jsonObject = JSONUtil.toBean(videoDetail, JSONObject.class);
            System.out.println(jsonObject);
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
            HttpResponse videoUrl = HttpRequest.get("https://aweme.snssdk.com/aweme/v1/play/?video_id=" + jsonObject.getJSONObject("aweme_detail").getJSONObject("video").getJSONObject("play_addr").get("uri") + "&ratio=720p&line=0").execute();
            videoVo.set("url",videoUrl.header("Location"));
            videoVo.set("duration",jsonObject.getJSONObject("aweme_detail").getJSONObject("video").getLong("duration")/1000);
            videoVo.set("dataSize",jsonObject.getJSONObject("aweme_detail").getJSONObject("video").getJSONObject("play_addr").get("data_size"));
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
     * 抖音去水印
     */
    public static JSONObject douyin(String url) {
        JSONObject videoVo = new JSONObject();
        try {
            url = url.substring(url.indexOf("https"), url.length());
            HttpResponse execute = HttpRequest.get(url).execute();
            //重定向地址去除videoId
            String videoID = BogusUtils.extractVideoID(execute.header("Location"));
            String userAgent = BogusUtils.getUserAgent();
            String xBogus = BogusUtils.getXBogus("https://www.douyin.com/aweme/v1/web/aweme/detail/?aweme_id=" + videoID, userAgent);
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
            HttpResponse videoUrl = HttpRequest.get("https://aweme.snssdk.com/aweme/v1/play/?video_id=" + jsonObject.getJSONObject("aweme_detail").getJSONObject("video").getJSONObject("play_addr").get("uri") + "&ratio=720p&line=0").execute();
            videoVo.set("url",videoUrl.header("Location"));
            videoVo.set("duration",jsonObject.getJSONObject("aweme_detail").getJSONObject("video").getLong("duration")/1000);
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
     * 抖音主页去水印
     */
    public static JSONObject douyinAweme(String url,String maxCursor) {
        JSONObject ownAwemeVo = new JSONObject();
        try {
            url = url.substring(url.indexOf("https"), url.length());
            if (ObjectUtil.isEmpty(maxCursor)) {
                maxCursor = "0";
            }
            HttpResponse execute = HttpRequest.get(url).execute();
            //重定向地址去除videoId
            String secUid = BogusUtils.extractSecUid(execute.header("Location"));
            String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36";
            String xBogus = BogusUtils.getXBogus("https://www.douyin.com/aweme/v1/web/aweme/post/?device_platform=webapp&aid=6383&channel=channel_pc_web&max_cursor="+maxCursor+"&sec_user_id="+secUid+"&count=18", userAgent);
            String awemeDetail = HttpRequest.get(xBogus)
                    .header("Referer", "https://www.douyin.com/user/"+secUid)
                    .header(Header.USER_AGENT, userAgent)
                    .header(Header.COOKIE,"FORCE_LOGIN=%7B%22videoConsumedRemainSeconds%22%3A180%7D;ttwid="+BogusUtils.getTtwid()+";msToken="+BogusUtils.randStrNoeq(124)+"; bd_ticket_guard_client_data=eyJiZC10aWNrZXQtZ3VhcmQtdmVyc2lvbiI6MiwiYmQtdGlja2V0LWd1YXJkLWl0ZXJhdGlvbi12ZXJzaW9uIjoxLCJiZC10aWNrZXQtZ3VhcmQtY2xpZW50LWNzciI6Ii0tLS0tQkVHSU4gQ0VSVElGSUNBVEUgUkVRVUVTVC0tLS0tXHJcbk1JSUJEVENCdFFJQkFEQW5NUXN3Q1FZRFZRUUdFd0pEVGpFWU1CWUdBMVVFQXd3UFltUmZkR2xqYTJWMFgyZDFcclxuWVhKa01Ga3dFd1lIS29aSXpqMENBUVlJS29aSXpqMERBUWNEUWdBRVJqMWFlZk1RNXFCbDBvVE1vTDRXOTB1SVxyXG5rLzlrM2ZIODVqZkpSUGd1R3ZsZkFKbzIrenlYY3hWbWVtM21UMHBtdWx5aW9CbkM5eE1ZdHBQSXlQdXl0NkFzXHJcbk1Db0dDU3FHU0liM0RRRUpEakVkTUJzd0dRWURWUjBSQkJJd0VJSU9kM2QzTG1SdmRYbHBiaTVqYjIwd0NnWUlcclxuS29aSXpqMEVBd0lEUndBd1JBSWdQVGM3bDdkakRra2x5LzIxdmhaYW9KWWN4cHE0K0huWDdiemN0YTlXOXNNQ1xyXG5JQmpDelVnV3ZNajNSUjBLQ2N0c20wZzJOcnBJNG5GbWxqVmRNTjI3WW1FL1xyXG4tLS0tLUVORCBDRVJUSUZJQ0FURSBSRVFVRVNULS0tLS1cclxuIn0=; msToken="+BogusUtils.randStr(118)+";")
                    .execute()
                    .body();
            Biz.check(ObjectUtil.isEmpty(awemeDetail),"本次请求失败，可再次请求！");
            JSONObject jsonObject = JSONUtil.toBean(awemeDetail, JSONObject.class);
            if (ObjectUtil.isNotEmpty(jsonObject.get("aweme_list"))) {
                ownAwemeVo.set("author",jsonObject.getJSONArray("aweme_list").getJSONObject(0).getJSONObject("author").getStr("nickname"));
                ownAwemeVo.set("avatar",jsonObject.getJSONArray("aweme_list").getJSONObject(0).getJSONObject("author").getJSONObject("avatar_thumb").getJSONArray("url_list").getStr(0).replace("100x100", "1080x1080"));
                ownAwemeVo.set("maxCursor",jsonObject.getLong("max_cursor"));
                ownAwemeVo.set("minCursor",jsonObject.getLong("min_cursor"));
                List<JSONObject> list = new ArrayList<>();
                jsonObject.getJSONArray("aweme_list").forEach(item -> {
                    JSONObject rows = new JSONObject();
                    JSONObject music = new JSONObject();
                    JSONObject detailItem = (JSONObject) item;
                    music.set("author",detailItem.getJSONObject("music").getStr("author"));
                    music.set("avatar",detailItem.getJSONObject("music").getJSONObject("cover_hd").getJSONArray("url_list").getStr(0));
                    music.set("url",detailItem.getJSONObject("music").getJSONObject("play_url").getStr("uri"));
                    rows.set("music",music);
                    JSONObject video = new JSONObject();
                    video.set("desc",detailItem.getStr("desc"));
                    video.set("duration",detailItem.getJSONObject("video").getLong("duration")/1000);
                    video.set("cover",detailItem.getJSONObject("video").getJSONObject("origin_cover").getJSONArray("url_list").getStr(0).replace("\\u0026", "&"));
                    video.set("time",detailItem.getLong("create_time"));
                    HttpResponse videoUrl = HttpRequest.get("https://aweme.snssdk.com/aweme/v1/play/?video_id=" + detailItem.getJSONObject("video").getJSONObject("play_addr").getStr("uri") + "&ratio=720p&line=0").execute();
                    video.set("url",videoUrl.header("Location"));
                    rows.set("video",video);
                    list.add(rows);
                });
                ownAwemeVo.set("rowsList",list);
            }
        } catch (Exception e) {
            log.error("抖音主页去水印异常", e);
            throw new CustomerException("提取失败！");
        }
        return ownAwemeVo;
    }

    /**
     * 抖音搜索去水印
     */
    public static JSONObject douyinSearch(String keyword,Integer page){
        JSONObject videoVo = new JSONObject();
        try {
            if (ObjectUtil.isEmpty(page)) {
                page = 0;
            } else {
                if (page == 1) {
                    page = 0;
                } else {
                    page = (15 * page) - 15;
                }
            }
            String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36";
            String xBogus = BogusUtils.getXBogus("https://www.douyin.com/aweme/v1/web/search/item/?keyword="+keyword+"&count=15&offset="+page+"&cursor=0&device_platform=webapp&aid=6383&version_name=23.5.0&os_version=10.15.7",
                    userAgent);
            String searchStr = HttpRequest.get(xBogus)
                    .header(Header.USER_AGENT,userAgent)
                    .header(Header.COOKIE,"FORCE_LOGIN=%7B%22videoConsumedRemainSeconds%22%3A180%7D;ttwid="+BogusUtils.getTtwid()+";msToken="+BogusUtils.randStr(107)+"; bd_ticket_guard_client_data=yJiZC10aWNrZXQtZ3VhcmQtdmVyc2lvbiI6MiwiYmQtdGlja2V0LWd1YXJkLWl0ZXJhdGlvbi12ZXJzaW9uIjoxLCJiZC10aWNrZXQtZ3VhcmQtcmVlLXB1YmxpYy1rZXkiOiJCS1dhc1FFVnlhbmdUN3lTNGpuNW5OOVEzd3pYNXpOMjNWNVBHY1lnNlc2Snd4UzNZSTFEQ0J6K0ZIbUhpMXQ3RThPaHUvaFIxOXc4OWp6ME9XWDNHOUE9IiwiYmQtdGlja2V0LWd1YXJkLXdlYi12ZXJzaW9uIjoxfQ==; msToken="+BogusUtils.generateRandomString()+";")
                    .header(Header.REFERER,"https://www.douyin.com/")
                    .execute().body();
            Biz.check(ObjectUtil.isEmpty(searchStr),"本次请求失败，可再次请求！");
            JSONObject searchDetail = JSONUtil.toBean(searchStr, JSONObject.class);
            if ((int)searchDetail.get("status_code") != 0) {
                log.error("抖音去水印实际参数{}",searchDetail);
                Biz.check(true,"解析抖音失败！");
            }
            if (ObjectUtil.isNotEmpty(searchDetail.get("data"))) {
                List<JSONObject> list = new ArrayList<>();
                searchDetail.getJSONArray("data").forEach(item -> {
                    JSONObject detailItem = (JSONObject) item;
                    JSONObject rows = new JSONObject();
                    JSONObject user = new JSONObject();
                    user.set("author",detailItem.getJSONObject("aweme_info").getJSONObject("author").getStr("nickname"));
                    user.set("avatar",detailItem.getJSONObject("aweme_info").getJSONObject("author").getJSONObject("avatar_thumb").getJSONArray("url_list").getStr(0).replace("100x100", "1080x1080"));
                    rows.set("user",user);
                    JSONObject video = new JSONObject();
                    video.set("desc",detailItem.getJSONObject("aweme_info").getStr("desc"));
                    video.set("duration",detailItem.getJSONObject("aweme_info").getJSONObject("video").getLong("duration")/1000);
                    video.set("cover",detailItem.getJSONObject("aweme_info").getJSONObject("video").getJSONObject("origin_cover").getJSONArray("url_list").getStr(0).replace("\\u0026", "&"));
                    video.set("time",detailItem.getJSONObject("aweme_info").getLong("create_time"));
                    HttpResponse videoUrl = HttpRequest.get("https://aweme.snssdk.com/aweme/v1/play/?video_id=" + detailItem.getJSONObject("aweme_info").getJSONObject("video").getJSONObject("play_addr").getStr("uri") + "&ratio=720p&line=0").execute();
                    video.set("url",videoUrl.header("Location"));
                    rows.set("video",video);
                    list.add(rows);
                });
                videoVo.set("rowsList",list);
            }
        } catch (Exception e) {
            log.error("抖音搜索去水印异常", e);
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
            videoVo.set("cover",detailJson.getJSONObject("origin_video_download").getJSONObject("cover_image").getJSONArray("download_list").getJSONObject(0).getStr("url").replace("\\u0026", "&"));
            videoVo.set("url",detailJson.getJSONObject("origin_video_download").getJSONArray("url_list").getJSONObject(0).getStr("url"));
            double ceil = Math.round(Float.parseFloat(detailJson.getJSONObject("origin_video_download").getStr("duration")));
            videoVo.set("duration",(long) ceil);
            videoVo.set("analysisPlatform","皮皮虾");
        }catch (Exception e) {
            log.error("皮皮虾去水印异常", e);
            throw new CustomerException("提取失败！");
        }
        return videoVo;
    }

    /**
     * 抖音id去水印
     */
    public static JSONObject douyinByid(String videoID,String msg) {
        JSONObject videoVo = new JSONObject();
        try {
            String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.90 Safari/537.36 Edg/89.0.774.54";
            String xBogus = BogusUtils.getXBogus("https://www.douyin.com/aweme/v1/web/aweme/detail/?aweme_id=" + videoID, userAgent);
            String videoDetail = HttpRequest.get(xBogus)
                    .header("Referer","https://www.douyin.com/")
                    .header(Header.USER_AGENT, userAgent)
                    .header(Header.COOKIE, "msToken=" + BogusUtils.randStr(107))
                    .header("ttwid", "1%7CWBuxH_bhbuTENNtACXoesI5QHV2Dt9-vkMGVHSRRbgY%7C1677118712%7C1d87ba1ea2cdf05d80204aea2e1036451dae638e7765b8a4d59d87fa05dd39ff")
                    .header("bd_ticket_guard_client_data", "eyJiZC10aWNrZXQtZ3VhcmQtdmVyc2lvbiI6MiwiYmQtdGlja2V0LWd1YXJkLWNsaWVudC1jc3IiOiItLS0tLUJFR0lOIENFUlRJRklDQVRFIFJFUVVFU1QtLS0tLVxyXG5NSUlCRFRDQnRRSUJBREFuTVFzd0NRWURWUVFHRXdKRFRqRVlNQllHQTFVRUF3d1BZbVJmZEdsamEyVjBYMmQxXHJcbllYSmtNRmt3RXdZSEtvWkl6ajBDQVFZSUtvWkl6ajBEQVFjRFFnQUVKUDZzbjNLRlFBNUROSEcyK2F4bXAwNG5cclxud1hBSTZDU1IyZW1sVUE5QTZ4aGQzbVlPUlI4NVRLZ2tXd1FJSmp3Nyszdnc0Z2NNRG5iOTRoS3MvSjFJc3FBc1xyXG5NQ29HQ1NxR1NJYjNEUUVKRGpFZE1Cc3dHUVlEVlIwUkJCSXdFSUlPZDNkM0xtUnZkWGxwYmk1amIyMHdDZ1lJXHJcbktvWkl6ajBFQXdJRFJ3QXdSQUlnVmJkWTI0c0RYS0c0S2h3WlBmOHpxVDRBU0ROamNUb2FFRi9MQnd2QS8xSUNcclxuSURiVmZCUk1PQVB5cWJkcytld1QwSDZqdDg1czZZTVNVZEo5Z2dmOWlmeTBcclxuLS0tLS1FTkQgQ0VSVElGSUNBVEUgUkVRVUVTVC0tLS0tXHJcbiJ9")
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
            HttpResponse videoUrl = HttpRequest.get("https://aweme.snssdk.com/aweme/v1/play/?video_id=" + jsonObject.getJSONObject("aweme_detail").getJSONObject("video").getJSONObject("play_addr").get("uri") + "&ratio=720p&line=0").execute();
            videoVo.set("url",videoUrl.header("Location"));
            videoVo.set("duration",jsonObject.getJSONObject("aweme_detail").getJSONObject("video").getLong("duration")/1000);
            videoVo.set("analysisPlatform",msg);
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
     * 火山去水印
     * @param url
     * @return
     */
    public static JSONObject huoshan(String url) {
        JSONObject videoVo = new JSONObject();
        try {
            url = BogusUtils.commGetUrl(url);
            HttpResponse execute = HttpRequest.get(url).execute();
            //重定向地址去除videoId
            String videoID = BogusUtils.commGetVideoId("/video/(.*)\\/",execute.header("Location"));
            //请求视频详情
            //方式一请求无水印
//            String huoshanDetail = HttpRequest.get("https://share.huoshan.com/api/item/info?item_id=" + videoID)
//                    .header(Header.USER_AGENT, getUserAgent())
//                    .execute().body();
//            JSONObject bean = JSONUtil.toBean(huoshanDetail, JSONObject.class);
//            HttpResponse huoshanUrl = HttpRequest.get("https://api-hl.huoshan.com/hotsoon/item/video/_playback/?video_id=" + commGetVideoId("video_id=(.*)&line", (String) bean.getJSONObject("data").getJSONObject("item_info").get("url"))).execute();
//            videoVo.set("url",huoshanUrl.header("Location"));
//            videoVo.set("cover",(String) bean.getJSONObject("data").getJSONObject("item_info").get("cover"));
//            videoVo.set("analysisPlatform","火山");
            //方式二直接请求抖音接口
            videoVo = douyinByid(videoID,"火山");
        }catch (Exception e) {
            log.error("火山去水印异常", e);
            throw new CustomerException("提取失败！");
        }
        return videoVo;
    }

    /**
     * 微视去水印
     * @param url
     * @return
     */
    public static JSONObject weishi(String url) {
        JSONObject videoVo = new JSONObject();
        try {
            url = BogusUtils.commGetUrl(url);
            HttpResponse execute = HttpRequest.get(url).header(Header.USER_AGENT, BogusUtils.getUserAgent()).execute();
            //重定向地址去除videoId
            String videoID = BogusUtils.commGetVideoId("id=(.*)&",execute.header("Location"));
            //请求视频详情
            //方式一请求无水印
            String huoshanDetail = HttpRequest.get("https://h5.weishi.qq.com/webapp/json/weishi/WSH5GetPlayPage?feedid=" + videoID)
                    .header(Header.USER_AGENT, BogusUtils.getUserAgent())
                    .execute().body();
            JSONObject bean = JSONUtil.toBean(huoshanDetail, JSONObject.class);
            videoVo.set("author", (String) bean.getJSONObject("data").getJSONArray("feeds").getJSONObject(0).getJSONObject("poster").get("nick"));
            videoVo.set("avatar",(String) bean.getJSONObject("data").getJSONArray("feeds").getJSONObject(0).getJSONObject("poster").get("avatar"));
            videoVo.set("time",Long.parseLong(String.valueOf(bean.getJSONObject("data").getJSONArray("feeds").getJSONObject(0).get("createtime"))));
            videoVo.set("duration",Long.parseLong(String.valueOf(bean.getJSONObject("data").getJSONArray("feeds").getJSONObject(0).get("music_end_time"))) / 1000);
            videoVo.set("analysisPlatform","微视");
            videoVo.set("title",(String) bean.getJSONObject("data").getJSONArray("feeds").getJSONObject(0).get("feed_desc_withat"));
            videoVo.set("cover",(String) bean.getJSONObject("data").getJSONArray("feeds").getJSONObject(0).getJSONArray("images").getJSONObject(0).get("url"));
            videoVo.set("url",(String) bean.getJSONObject("data").getJSONArray("feeds").getJSONObject(0).get("video_url"));
        }catch (Exception e) {
            log.error("微视去水印异常", e);
            throw new CustomerException("提取失败！");
        }
        return videoVo;
    }

    /**
     * 微博去水印
     * @param url
     * @return
     */
    public static JSONObject weibo(String url) {
        JSONObject videoVo = new JSONObject();
        try {
            url = BogusUtils.commGetUrl(url);
            HttpResponse execute = HttpRequest.get(url).header(Header.USER_AGENT, BogusUtils.getUserAgent()).execute();
            //重定向地址去除videoId
            String locationUrl = execute.header("Location");
            String[] parts = locationUrl.split("/");
            int length = parts.length;
            String videoId = parts[length - 2];
            String mid = parts[length - 1];
            //请求视频详情
            //h5去除图片会打不开
//            String weiboDetail = HttpRequest.get("https://video.h5.weibo.cn/s/video/object")
//                    .form("object_id",videoId)
//                    .form("mid",mid)
//                    .header(Header.USER_AGENT, getUserAgent())
//                    .header(Header.ACCEPT_LANGUAGE,"zh-CN,zh;q=0.9")
//                    .execute().body();
//            JSONObject bean = JSONUtil.toBean(weiboDetail, JSONObject.class);
//            videoVo.set("author", (String) bean.getJSONObject("data").getJSONObject("object").getJSONObject("author").get("screen_name"));
//            videoVo.set("avatar",(String) bean.getJSONObject("data").getJSONObject("object").getJSONObject("author").get("profile_image_url"));
//            DateTime parse = DateUtil.parse((String) bean.getJSONObject("data").getJSONObject("object").get("created_at"), "yyyy-MM-dd");
//            videoVo.set("time",parse.getTime());
//            double ceil = Math.round(Float.parseFloat(String.valueOf(bean.getJSONObject("data").getJSONObject("object").getJSONObject("stream").get("duration"))));
//            videoVo.set("duration",(long) ceil);
//            videoVo.set("analysisPlatform","微博");
//            videoVo.set("title",(String) bean.getJSONObject("data").getJSONObject("object").get("summary"));
//            videoVo.set("cover",(String) bean.getJSONObject("data").getJSONObject("object").getJSONObject("image").get("url"));
//            videoVo.set("url",(String) bean.getJSONObject("data").getJSONObject("object").getJSONObject("stream").get("url"));
            String weiboDetail = HttpRequest.post("https://weibo.com/tv/api/component?page=/tv/show/"+videoId)
                    .body("data= {\"Component_Play_Playinfo\":{\"oid\":\"" + videoId + "\"}}")
                    .header("Cookie", "login_sid_t=6b652c77c1a4bc50cb9d06b24923210d; cross_origin_proto=SSL; WBStorage=2ceabba76d81138d|undefined; _s_tentry=passport.weibo.com; Apache=7330066378690.048.1625663522444; SINAGLOBAL=7330066378690.048.1625663522444; ULV=1625663522450:1:1:1:7330066378690.048.1625663522444:; TC-V-WEIBO-G0=35846f552801987f8c1e8f7cec0e2230; SUB=_2AkMXuScYf8NxqwJRmf8RzmnhaoxwzwDEieKh5dbDJRMxHRl-yT9jqhALtRB6PDkJ9w8OaqJAbsgjdEWtIcilcZxHG7rw; SUBP=0033WrSXqPxfM72-Ws9jqgMF55529P9D9W5Qx3Mf.RCfFAKC3smW0px0; XSRF-TOKEN=JQSK02Ijtm4Fri-YIRu0-vNj")
                    .header("Referer", "https://weibo.com/tv/show/" + videoId)
                    .execute().body();
            JSONObject bean = JSONUtil.toBean(weiboDetail, JSONObject.class);
            videoVo.set("author", (String) bean.getJSONObject("data").getJSONObject("Component_Play_Playinfo").get("author"));
            videoVo.set("avatar","https:"+bean.getJSONObject("data").getJSONObject("Component_Play_Playinfo").get("avatar"));
            videoVo.set("time",Long.valueOf(String.valueOf(bean.getJSONObject("data").getJSONObject("Component_Play_Playinfo").get("real_date"))));
            double ceil = Math.round(Float.parseFloat(String.valueOf(bean.getJSONObject("data").getJSONObject("Component_Play_Playinfo").get("duration_time"))));
            videoVo.set("duration",(long) ceil);
            videoVo.set("analysisPlatform","微博");
            videoVo.set("title",(String) bean.getJSONObject("data").getJSONObject("Component_Play_Playinfo").get("summary"));
            videoVo.set("cover","https:"+bean.getJSONObject("data").getJSONObject("Component_Play_Playinfo").get("cover_image"));
            videoVo.set("url","https:"+bean.getJSONObject("data").getJSONObject("Component_Play_Playinfo").getJSONObject("urls").get("高清 1080P"));
        }catch (Exception e) {
            log.error("微博去水印异常", e);
            throw new CustomerException("提取失败！");
        }
        return videoVo;
    }

    /**
     * 绿洲去水印
     * @param url
     * @return
     */
    public static JSONObject lvzhou(String url) {
        JSONObject videoVo = new JSONObject();
        try {
            url = BogusUtils.commGetUrl(url);
            String execute = HttpRequest.get(url).header(Header.USER_AGENT, BogusUtils.getUserAgent()).execute().body();
            videoVo.set("author", BogusUtils.commGetVideoId("<div class=\"nickname\">(.*)</div>", execute));
            videoVo.set("avatar",BogusUtils.commGetVideoId("<a class=\"avatar\"><img src=\"(.*)\"></a>", execute).replace("amp;",""));
            videoVo.set("analysisPlatform","绿洲");
            videoVo.set("title",BogusUtils.commGetVideoId("<div class=\"status-text\">(.*)</div>", execute));
            videoVo.set("cover",BogusUtils.commGetVideoId("<div style=\\\"background-image:url\\((.*)\\)", execute));
            videoVo.set("url",BogusUtils.commGetVideoId("<video src=\\\"([^\\\"]*)\\\"", execute).replace("amp;",""));
        }catch (Exception e) {
            log.error("绿洲去水印异常", e);
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
            Long videoDataId = (Long)bean.getJSONObject("data").getJSONObject("post").getJSONArray("imgs").getJSONObject(0).get("id");
            videoVo.set("author", (String) bean.getJSONObject("data").getJSONObject("post").getJSONObject("member").get("name"));
            videoVo.set("avatar",(String) bean.getJSONObject("data").getJSONObject("post").getJSONObject("member").getJSONObject("avatar_urls").getJSONObject("origin").getJSONArray("urls").get(0));
            videoVo.set("analysisPlatform","最右");
            videoVo.set("duration",Long.parseLong(String.valueOf(bean.getJSONObject("data").getJSONObject("post").getJSONObject("videos").getJSONObject(String.valueOf(videoDataId)).get("dur"))));
            videoVo.set("title",(String) bean.getJSONObject("data").getJSONObject("post").get("content"));
            videoVo.set("cover",(String) bean.getJSONObject("data").getJSONObject("post").getJSONObject("videos").getJSONObject(String.valueOf(videoDataId)).getJSONArray("cover_urls").get(0));
            videoVo.set("url",(String) bean.getJSONObject("data").getJSONObject("post").getJSONObject("videos").getJSONObject(String.valueOf(videoDataId)).get("url"));
        }catch (Exception e) {
            log.error("最右去水印异常", e);
            throw new CustomerException("提取失败！");
        }
        return videoVo;
    }

    /**
     * bilibili去水印
     * @param url
     * @return
     */
    public static JSONObject bilibili(String url) {
        JSONObject videoVo = new JSONObject();
        try {
            url = BogusUtils.commGetUrl(url);
            String bvid = null;
            if (url.contains("bilibili.com")) {
                bvid = BogusUtils.commGetVideoId("/video/(.*?)[/?&]", url);
            } else {
                HttpResponse execute = HttpRequest.get(url).execute();
                bvid = BogusUtils.commGetVideoId("/video/(.*?)[?&]", execute.header("Location"));
            }
            String cidData = HttpRequest.get("https://api.bilibili.com/x/player/pagelist?bvid="+bvid)
                    .header(Header.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:102.0) Gecko/20100101 Firefox/102.0")
                    .header(Header.REFERER, "https://www.bilibili.com/")
                    .execute()
                    .body();
            JSONObject cidBean = JSONUtil.toBean(cidData, JSONObject.class);
            Object cid = cidBean.getJSONArray("data").getJSONObject(0).get("cid");
            String videoUrl = HttpRequest.get("https://api.bilibili.com/x/player/playurl?cid="+cid+"&bvid="+bvid+"&qn=80&type=mp4&platform=html5&high_quality=1")
                    .header(Header.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:102.0) Gecko/20100101 Firefox/102.0")
                    .header(Header.REFERER, "https://www.bilibili.com/")
                    .header(Header.COOKIE,"SESSDATA=8ede3933%2C1708355230%2C7b8c1%2A823KMOiheJYc5wT11sPue493X6zqc60iVcCvvTAZr5JB-19ayWygcVc2MN10ZR5fov054XpQAAGQA;")
                    .execute()
                    .body();
            JSONObject videoBean = JSONUtil.toBean(videoUrl, JSONObject.class);
            String videoDetail = HttpRequest.get("https://api.bilibili.com/x/web-interface/view?cid="+cid+"&bvid="+bvid)
                    .header(Header.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:102.0) Gecko/20100101 Firefox/102.0")
                    .header(Header.REFERER, "https://www.bilibili.com/")
                    .execute()
                    .body();
            JSONObject videoDetailBean = JSONUtil.toBean(videoDetail, JSONObject.class);
            videoVo.set("author", (String) videoDetailBean.getJSONObject("data").getJSONObject("owner").get("name"));
            videoVo.set("avatar",(String) videoDetailBean.getJSONObject("data").getJSONObject("owner").get("face"));
            videoVo.set("analysisPlatform","bilibili");
            videoVo.set("time",Long.parseLong(String.valueOf(videoDetailBean.getJSONObject("data").get("pubdate"))));
            videoVo.set("duration",Long.parseLong(String.valueOf(videoDetailBean.getJSONObject("data").get("duration"))));
            videoVo.set("title",(String) videoDetailBean.getJSONObject("data").get("title"));
            videoVo.set("cover",(String) videoDetailBean.getJSONObject("data").get("pic"));
            videoVo.set("url",(String) videoBean.getJSONObject("data").getJSONArray("durl").getJSONObject(0).get("url"));
        }catch (Exception e) {
            log.error("bilibili去水印异常", e);
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
            videoVo.set("author", (String) bean.getJSONObject("photo").get("userName"));
            videoVo.set("avatar",(String) bean.getJSONObject("photo").get("headUrl"));
            videoVo.set("analysisPlatform","快手");
            videoVo.set("time",(Long) bean.getJSONObject("photo").get("timestamp"));
            videoVo.set("duration",Long.parseLong(String.valueOf(bean.getJSONObject("photo").get("duration")))/1000);
            videoVo.set("title",(String) bean.getJSONObject("photo").get("caption"));
            videoVo.set("cover",(String) bean.getJSONObject("photo").getJSONArray("coverUrls").getJSONObject(0).get("url"));
            videoVo.set("url",(String) bean.getJSONObject("photo").getJSONArray("mainMvUrls").getJSONObject(0).get("url"));
        }catch (Exception e) {
            log.error("快手去水印异常", e);
            throw new CustomerException("提取失败！");
        }
        return videoVo;
    }

    /**
     * 开眼去水印
     * @param url
     * @return
     */
    public static JSONObject kaiyan(String url) {
        JSONObject videoVo = new JSONObject();
        try {
            url = BogusUtils.commGetUrl(url);
            String videoId = BogusUtils.commGetVideoId("video_id=(\\d+)", url);
            String kuaishouDetail = HttpRequest.get("https://baobab.kaiyanapp.com/api/v1/video/" + videoId + "?f=web")
                    .header(Header.USER_AGENT, BogusUtils.getUserAgent())
                    .execute()
                    .body();
            JSONObject bean = JSONUtil.toBean(kuaishouDetail, JSONObject.class);
            videoVo.set("author", (String) bean.getJSONObject("author").get("name"));
            videoVo.set("avatar",(String) bean.getJSONObject("author").get("icon"));
            videoVo.set("analysisPlatform","开眼");
            videoVo.set("time",(Long) bean.get("date"));
            videoVo.set("duration",Long.parseLong(String.valueOf(bean.get("duration"))));
            videoVo.set("title",(String) bean.get("title"));
            videoVo.set("cover",(String) bean.get("coverForFeed"));
            videoVo.set("url","https://baobab.kaiyanapp.com/api/v1/playUrl?vid="+videoId+"&resourceType=video&editionType=default&source=aliyun&playUrlType=url_oss&ptl=true");
        }catch (Exception e) {
            log.error("开眼去水印异常", e);
            throw new CustomerException("提取失败！");
        }
        return videoVo;
    }

    /**
     * 皮皮搞笑去水印
     * @param url
     * @return
     */
    public static JSONObject pipigaoxiao(String url) {
        JSONObject videoVo = new JSONObject();
        try {
            url = BogusUtils.commGetUrl(url);
            String videoId = BogusUtils.commGetVideoId("/post/(\\d+)", url);
            String mId = BogusUtils.commGetVideoId("mid=(\\d+)", url);
            String kuaishouDetail = HttpRequest.post("http://share.ippzone.com/ppapi/share/fetch_content")
                    .header(Header.USER_AGENT, BogusUtils.getUserAgent())
                    .header(Header.REFERER,"http://share.ippzone.com/ppapi/share/fetch_content")
                    .body("{\"mid\":" + mId +",\"pid\":" +videoId +",\"type\":\"post\"}")
                    .execute()
                    .body();
            JSONObject bean = JSONUtil.toBean(kuaishouDetail, JSONObject.class);
            Long videoDataId = (Long) bean.getJSONObject("data").getJSONObject("post").getJSONArray("imgs").getJSONObject(0).get("id");
            videoVo.set("author", (String) bean.getJSONObject("data").getJSONObject("user").get("sign"));
            videoVo.set("avatar",(String) bean.getJSONObject("data").getJSONObject("user").getJSONObject("avatar_urls").getJSONObject("aspect_low").getJSONArray("urls").get(0));
            videoVo.set("analysisPlatform","皮皮搞笑");
            videoVo.set("time",Long.valueOf(String.valueOf(bean.getJSONObject("data").getJSONObject("post").get("review_time"))));
            videoVo.set("duration",Long.parseLong(String.valueOf(bean.getJSONObject("data").getJSONObject("post").getJSONObject("videos").getJSONObject(String.valueOf(videoDataId)).get("dur"))));
            videoVo.set("title",(String) bean.getJSONObject("data").getJSONObject("post").get("content"));
            videoVo.set("cover","https://file.ippzone.com/img/view/id/" + videoDataId);
            videoVo.set("url",(String) bean.getJSONObject("data").getJSONObject("post").getJSONObject("videos").getJSONObject(String.valueOf(videoDataId)).get("url"));
        }catch (Exception e) {
            log.error("皮皮搞笑去水印异常", e);
            throw new CustomerException("提取失败！");
        }
        return videoVo;
    }

    /**
     * 全民K歌去水印
     * @param url
     * @return
     */
    public static JSONObject quanminkge(String url) {
        JSONObject videoVo = new JSONObject();
        try {
            url = BogusUtils.commGetUrl(url);
            HttpResponse execute = HttpRequest.get(url).execute();
            String videoId = BogusUtils.commGetVideoId("s=([a-zA-Z0-9]+)", execute.header("Location"));
            String kuaishouDetail = HttpRequest.get("https://kg.qq.com/node/play?s=" + videoId)
                    .execute()
                    .body();
            String avatar = BogusUtils.commGetVideoId("<title>([^<]+)-", kuaishouDetail);
            String[] parts = avatar.split("-");
            DateTime parse = null;
            if (ObjectUtil.isNotEmpty(parts)) {
                videoVo.set("author", parts[0].trim());
                videoVo.set("title",parts[1].trim());
                parse = DateUtil.parse(BogusUtils.commGetVideoId("<p class=\"singer_more__time\">(.*)<\\/p>", kuaishouDetail), "MM-dd HH:mm");
            } else {
                videoVo.set("author", "暂无");
                videoVo.set("title","暂无");
                parse = DateUtil.parse(BogusUtils.commGetVideoId("<p class=\"singer_more__time\">(.*)<\\/p>", kuaishouDetail), "yyyy-MM-dd HH:mm");
            }
            videoVo.set("avatar",BogusUtils.commGetVideoId("\"activity_id\":0,\"avatar\":\"(http[^\"]+)\"",kuaishouDetail));
            videoVo.set("analysisPlatform","全民K歌");
            videoVo.set("time",parse.getTime());
            videoVo.set("cover",BogusUtils.commGetVideoId("\"cover\":\"(http[^\"]+)\"",kuaishouDetail));
            videoVo.set("url",BogusUtils.commGetVideoId("\"playurl\":\"\",\"playurl_video\":\"(http[^\"]+)\"",kuaishouDetail));
        }catch (Exception e) {
            log.error("全民K歌去水印异常", e);
            throw new CustomerException("提取失败！");
        }
        return videoVo;
    }

    /**
     * 西瓜去水印
     * @param url
     * @return
     */
    public static JSONObject xigua(String url) {
        JSONObject videoVo = new JSONObject();
        try {
            url = BogusUtils.commGetUrl(url);
            String kuaishouDetail = null;
            if (url.contains("v.ixigua.com")) {
                HttpResponse execute = HttpRequest.get(url).execute();
                String videoId = BogusUtils.commGetVideoId("/video/(.*)/", execute.header("Location"));
                kuaishouDetail = HttpRequest.get("https://www.ixigua.com/" + videoId )
                        .header(Header.USER_AGENT,"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.90 Safari/537.36 Edg/89.0.774.54")
                        .header(Header.COOKIE,"MONITOR_WEB_ID=7892c49b-296e-4499-8704-e47c1b150c18; ixigua-a-s=1; ttcid=af99669b6304453480454f150701d5c226; BD_REF=1; __ac_nonce=060d88ff000a75e8d17eb; __ac_signature=_02B4Z6wo00f01kX9ZpgAAIDAKIBBQUIPYT5F2WIAAPG2ad; ttwid=1%7CcIsVF_3vqSIk4XErhPB0H2VaTxT0tdsTMRbMjrJOPN8%7C1624806049%7C08ce7dd6f7d20506a41ba0a331ef96a6505d96731e6ad9f6c8c709f53f227ab1")
                        .execute()
                        .body();
            } else {
                kuaishouDetail = HttpRequest.get(url)
                        .header(Header.USER_AGENT,"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.90 Safari/537.36 Edg/89.0.774.54")
                        .header(Header.COOKIE,"MONITOR_WEB_ID=7892c49b-296e-4499-8704-e47c1b150c18; ixigua-a-s=1; ttcid=af99669b6304453480454f150701d5c226; BD_REF=1; __ac_nonce=060d88ff000a75e8d17eb; __ac_signature=_02B4Z6wo00f01kX9ZpgAAIDAKIBBQUIPYT5F2WIAAPG2ad; ttwid=1%7CcIsVF_3vqSIk4XErhPB0H2VaTxT0tdsTMRbMjrJOPN8%7C1624806049%7C08ce7dd6f7d20506a41ba0a331ef96a6505d96731e6ad9f6c8c709f53f227ab1")
                        .execute()
                        .body();
            }
            String data = BogusUtils.commGetVideoId("window._SSR_HYDRATED_DATA=(.*?)</script>", kuaishouDetail).replaceAll("undefined","null");
            JSONObject bean = JSONUtil.toBean(data, JSONObject.class);
            JSONObject detailData = bean.getJSONObject("anyVideo").getJSONObject("gidInformation").getJSONObject("packerData").getJSONObject("video");
            videoVo.set("author", (String) detailData.getJSONObject("user_info").get("name"));
            videoVo.set("avatar",detailData.getJSONObject("user_info").get("avatar_url").toString().replace("300x300.image", "300x300.jpg"));
            videoVo.set("analysisPlatform","西瓜");
            videoVo.set("time",Long.parseLong(String.valueOf(detailData.get("video_publish_time"))));
            videoVo.set("duration",Long.parseLong(String.valueOf(detailData.get("video_duration"))));
            videoVo.set("title",(String) detailData.get("title"));
            videoVo.set("cover",(String) detailData.get("poster_url"));
            String videoUrl;
            JSONObject jsonObject = detailData.getJSONObject("videoResource").getJSONObject("h265").getJSONObject("normal").getJSONObject("video_list").getJSONObject("video_4");
            if (ObjectUtil.isNotEmpty(jsonObject)) {
                videoUrl = Base64.decodeStr((CharSequence) jsonObject.get("backup_url_1"));
            } else {
                videoUrl = Base64.decodeStr((CharSequence) detailData.getJSONObject("videoResource").getJSONObject("h265").getJSONObject("normal").getJSONObject("video_list").getJSONObject("video_1").get("backup_url_1"));
            }
            videoVo.set("url",videoUrl);
        }catch (Exception e) {
            log.error("西瓜去水印异常", e);
            throw new CustomerException("提取失败！");
        }
        return videoVo;
    }

    /**
     * 虎牙去水印
     * @param url
     * @return
     */
    public static JSONObject huya(String url) {
        JSONObject videoVo = new JSONObject();
        try {
            url = BogusUtils.commGetUrl(url);
            String videoId = BogusUtils.commGetVideoId("/play/(.*).html", url);
            String huyaDetail = HttpRequest.get("https://liveapi.huya.com/moment/getMomentContent?videoId=" + videoId )
                    .header(Header.USER_AGENT,"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.90 Safari/537.36 Edg/89.0.774.54")
                    .header(Header.REFERER,"https://v.huya.com/")
                    .execute()
                    .body();
            JSONObject bean = JSONUtil.toBean(huyaDetail, JSONObject.class);
            JSONObject detailData = bean.getJSONObject("data").getJSONObject("moment");
            videoVo.set("author", (String) detailData.get("nickName"));
            videoVo.set("avatar",(String) detailData.get("iconUrl"));
            videoVo.set("analysisPlatform","西瓜");
            videoVo.set("time",DateUtil.parse((CharSequence) detailData.getJSONObject("videoInfo").get("videoUploadTime"),"yyyy-MM-dd HH:mm:ss").getTime());
            videoVo.set("duration",BogusUtils.durationConversion((String) detailData.getJSONObject("videoInfo").get("videoDuration")));
            videoVo.set("title",(String) detailData.getJSONObject("videoInfo").get("videoTitle"));
            videoVo.set("cover",(String) detailData.getJSONObject("videoInfo").get("videoCover"));
            videoVo.set("url",(String) detailData.getJSONObject("videoInfo").getJSONArray("definitions").getJSONObject(0).get("url"));
        }catch (Exception e) {
            log.error("虎牙去水印异常", e);
            throw new CustomerException("提取失败！");
        }
        return videoVo;
    }

    /**
     * 梨视频去水印
     * @param url
     * @return
     */
    public static JSONObject pear(String url) {
        JSONObject videoVo = new JSONObject();
        try {
            url = BogusUtils.commGetUrl(url);
            HttpResponse execute = HttpRequest.get(url).execute();
            String location = execute.header("Location");
            String titleText = HttpRequest.get("https:"+location).execute().body();
            String title = BogusUtils.commGetVideoId("<title>([^_]+)", titleText);
            String videoId = BogusUtils.commGetVideoId("/video_(.*)", location);
            String huyaDetail = HttpRequest.get("https://www.pearvideo.com/videoStatus.jsp?contId=" + videoId + "&mrd=" + System.currentTimeMillis())
                    .header(Header.USER_AGENT,"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36")
                    .header(Header.REFERER,location)
                    .execute()
                    .body();
            JSONObject bean = JSONUtil.toBean(huyaDetail, JSONObject.class);
            JSONObject detailData = bean.getJSONObject("videoInfo");
            videoVo.set("author", "暂无");
            videoVo.set("avatar","暂无");
            videoVo.set("analysisPlatform","梨视频");
            videoVo.set("time",Long.parseLong(String.valueOf(bean.get("systemTime"))));
            videoVo.set("title",title);
            videoVo.set("cover",(String) detailData.get("video_image"));
            videoVo.set("url",String.valueOf(detailData.getJSONObject("videos").get("srcUrl")).replace((CharSequence) bean.get("systemTime"), "cont-"+videoId));
        }catch (Exception e) {
            log.error("梨视频去水印异常", e);
            throw new CustomerException("提取失败！");
        }
        return videoVo;
    }

    /**
     * 新片场去水印
     * @param url
     * @return
     */
    public static JSONObject xinpianchang(String url) {
        JSONObject videoVo = new JSONObject();
        try {
            url = BogusUtils.commGetUrl(url);
            String oneDetailHtml = HttpRequest.get(url)
                    .header(Header.USER_AGENT,"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.125 Safari/537.36")
                    .header("upgrade-insecure-requests","1")
                    .execute().body();
            String appkey = BogusUtils.commGetVideoId("\"appKey\":\"([a-zA-Z0-9]+)\"", oneDetailHtml);
            String vid = BogusUtils.commGetVideoId("\"appKey\":\"[a-zA-Z0-9]+\",\"type\":\\d+,\"vid\":\"([a-zA-Z0-9]+)\"", oneDetailHtml);
            String xinpianchangDetail = HttpRequest.get("https://mod-api.xinpianchang.com/mod/api/v2/media/"+vid+"?appKey="+appkey+"&extend=userInfo,userStatus")
                    .header(Header.USER_AGENT,"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.125 Safari/537.36")
                    .header(Header.REFERER,url)
                    .header(Header.ORIGIN,"https://www.xinpianchang.com")
                    .header(Header.CONTENT_TYPE,"application/json")
                    .execute()
                    .body();
            JSONObject bean = JSONUtil.toBean(xinpianchangDetail, JSONObject.class);
            JSONObject detailData = bean.getJSONObject("data");
            videoVo.set("author", (String) detailData.getJSONObject("owner").get("username"));
            videoVo.set("avatar",(String) detailData.getJSONObject("owner").get("avatar"));
            videoVo.set("analysisPlatform","新片场视频");
            videoVo.set("duration",Long.parseLong(String.valueOf(detailData.get("duration"))));
            videoVo.set("time",System.currentTimeMillis());
            videoVo.set("title",(String) detailData.get("title"));
            videoVo.set("cover",(String) detailData.get("cover"));
            videoVo.set("url",(String) detailData.getJSONObject("resource").getJSONArray("progressive").getJSONObject(0).get("url"));
        }catch (Exception e) {
            log.error("新片场去水印异常", e);
            throw new CustomerException("提取失败！");
        }
        return videoVo;
    }

    /**
     * AcFun去水印
     * @param url
     * @return
     */
    public static JSONObject acfan(String url) {
        JSONObject videoVo = new JSONObject();
        try {
            url = BogusUtils.commGetUrl(url);
            if (url.contains("www.acfun.cn")) {
                String detailHtml = HttpRequest.get(url)
                        .execute().body();
                String detailJson = BogusUtils.commGetVideoId("videoInfo\\s*=\\s*(\\{.*\\})", detailHtml);
                JSONObject bean = JSONUtil.toBean(detailJson, JSONObject.class);
                Biz.check((Integer)bean.get("status") != 2,"提取失败！");
                videoVo.set("author", (String) bean.getJSONObject("user").get("name"));
                videoVo.set("avatar",(String) bean.getJSONObject("user").get("headUrl"));
                videoVo.set("analysisPlatform","AcFun");
                videoVo.set("duration",Long.parseLong(String.valueOf(bean.getJSONArray("videoList").getJSONObject(0).get("durationMillis")))/1000);
                videoVo.set("time",(Long) bean.getJSONArray("videoList").getJSONObject(0).get("uploadTime"));
                videoVo.set("title",(String) bean.get("title"));
                videoVo.set("cover",(String) bean.get("coverUrl"));
                videoVo.set("url",String.valueOf(bean.getJSONObject("currentVideoInfo").getJSONObject("ksPlayJson").getJSONArray("adaptationSet").getJSONObject(0).getJSONArray("representation").getJSONObject(0).getJSONArray("backupUrl").get(0)));
            } else {
                String detailHtml = HttpRequest.get(url)
                        .header(Header.USER_AGENT,"Mozilla/5.0 (iPhone; CPU iPhone OS 11_0 like Mac OS X) AppleWebKit/604.1.38 (KHTML, like Gecko) Version/11.0 Mobile/15A372 Safari/604.1")
                        .execute().body();
                String detailJson = BogusUtils.commGetVideoId("videoInfo\\s*=\\s*(\\{.*\\})", detailHtml);
                String playJson = BogusUtils.commGetVideoId("playInfo\\s*=\\s*(\\{.*\\})", detailHtml);
                String username = BogusUtils.commGetVideoId("<span class=\"up-name\">(.*)</span>",detailHtml);
                String avatar = BogusUtils.commGetVideoId("<span class=\"up-avatar\"><img src=\"(.*)\" /></span>",detailHtml);
                JSONObject bean = JSONUtil.toBean(detailJson, JSONObject.class);
                JSONObject playBean = JSONUtil.toBean(playJson, JSONObject.class);
                videoVo.set("author", username);
                videoVo.set("avatar",avatar);
                videoVo.set("analysisPlatform","AcFun");
                videoVo.set("duration",Long.parseLong(String.valueOf(bean.getJSONArray("videos").getJSONObject(0).get("durationMillis")))/1000);
                videoVo.set("time",(Long) bean.getJSONArray("videos").getJSONObject(0).get("uploadTime"));
                videoVo.set("title",(String) bean.get("title"));
                videoVo.set("cover",(String) bean.get("cover"));
                videoVo.set("url",(String) playBean.getJSONArray("streams").getJSONObject(0).getJSONArray("playUrls").get(0));
            }
        }catch (Exception e) {
            log.error("AcFun去水印异常", e);
            throw new CustomerException("提取失败！");
        }
        return videoVo;
    }

    /**
     * 美拍去水印
     * @param url
     * @return
     */
    public static JSONObject meipai(String url) {
        JSONObject videoVo = new JSONObject();
        try {
            url = BogusUtils.commGetUrl(url);
            HttpResponse execute = HttpRequest.get(url)
                    .header(Header.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36")
                    .execute();
            String detailHtml = HttpRequest.get(execute.header("Location"))
                    .header(Header.USER_AGENT,"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36")
                    .execute().body();
            String detailJson = BogusUtils.commGetVideoId("data-video=\"([^\"]+)\"", detailHtml);
            String titleJson = BogusUtils.commGetVideoId("<meta[^>]+content=\"([^\"]+)\"",detailHtml);
            String username = BogusUtils.commGetVideoId("【(.*?)】",titleJson);
            String coverUrl = BogusUtils.commGetVideoId("<div[^>]*?data-video=\"[^\"]+\"[^>]*?>\\s*<img[^>]*?src\\s*=\\s*\"([^\"]+)\"", detailHtml);
            String avatar = BogusUtils.commGetVideoId("<img[^>]*?src\\s*=\\s*\"([^\"]+)\"[^>]*?class\\s*=\\s*\"avatar pa detail-avatar\"[^>]*>", detailHtml);
            String time = BogusUtils.commGetVideoId("<div[^>]*?class\\s*=\\s*\"detail-time pa\"[^>]*?><strong>(.*?)</strong></div>", detailHtml);
            // 删除【】中的文本和后面的文本
            String title = titleJson.replaceAll("【.*?】.*?(,美拍高颜值频道推荐。)?", "").trim().replaceAll(",美拍高颜值频道推荐。","");
            videoVo.set("author", username.replace("美拍",""));
            videoVo.set("avatar","https:"+avatar.replace("!thumb160",""));
            videoVo.set("analysisPlatform","美拍");
            videoVo.set("time",BogusUtils.dateParse(time));
            videoVo.set("title",title);
            videoVo.set("cover",coverUrl);
            videoVo.set("url",BogusUtils.meipaiUrl(detailJson));
        }catch (Exception e) {
            log.error("美拍去水印异常", e);
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
            Biz.check(!detailJson.getJSONObject("note").get("type").equals("video"),"只提取无水印视频！");
            videoVo.set("author", String.valueOf(detailJson.getJSONObject("note").getJSONObject("user").get("nickname")));
            videoVo.set("avatar",String.valueOf(detailJson.getJSONObject("note").getJSONObject("user").get("avatar")));
            videoVo.set("analysisPlatform","小红书");
            videoVo.set("time",Long.parseLong(String.valueOf(detailJson.getJSONObject("note").get("time"))));
            videoVo.set("duration",Long.valueOf(String.valueOf(detailJson.getJSONObject("note").getJSONObject("video").getJSONObject("media").getJSONObject("video").get("duration"))));
            videoVo.set("title",(String) detailJson.getJSONObject("note").get("title"));
            videoVo.set("cover","https://ci.xiaohongshu.com/"+detailJson.getJSONObject("note").getJSONArray("imageList").getJSONObject(0).get("traceId")+"?imageView2/2/w/1080/format/jpg");
            videoVo.set("url",(String) detailJson.getJSONObject("note").getJSONObject("video").getJSONObject("media").getJSONObject("stream").getJSONArray("h264").getJSONObject(0).get("masterUrl"));
        }catch (Exception e) {
            log.error("小红书去水印异常", e);
            throw new CustomerException("提取失败！");
        }
        return videoVo;
    }

    /**
     * 知乎去水印
     * @param url
     * @return
     */
    public static JSONObject zhihu(String url) {
        JSONObject videoVo = new JSONObject();
        try {
            url = BogusUtils.commGetUrl(url);
            String videoId = BogusUtils.commGetVideoId("/zvideo/(.*)",url);
            String detailHtml = HttpRequest.get(url)
                    .execute().body();
            String detailJson = BogusUtils.commGetVideoId("<script id=\"js-initialData\" type=\"text/json\">(.*?)</script>", detailHtml);
            JSONObject detailBean = JSONUtil.toBean(detailJson, JSONObject.class);
            JSONObject detail = detailBean.getJSONObject("initialState").getJSONObject("entities");
            videoVo.set("author", detail.getJSONObject("users").getJSONObject((String) detail.getJSONObject("zvideos").getJSONObject(videoId).get("author")).get("name"));
            videoVo.set("avatar",detail.getJSONObject("users").getJSONObject((String) detail.getJSONObject("zvideos").getJSONObject(videoId).get("author")).get("avatarUrlTemplate"));
            videoVo.set("analysisPlatform","知乎");
            videoVo.set("time",detail.getJSONObject("zvideos").getJSONObject(videoId).get("updatedAt"));
            videoVo.set("duration",Math.round(Float.valueOf(String.valueOf(detail.getJSONObject("zvideos").getJSONObject(videoId).getJSONObject("video").get("duration")))));
            videoVo.set("title",detail.getJSONObject("zvideos").getJSONObject(videoId).get("title"));
            videoVo.set("cover",detail.getJSONObject("zvideos").getJSONObject(videoId).get("imageUrl"));
            videoVo.set("url",detail.getJSONObject("zvideos").getJSONObject(videoId).getJSONObject("video").getJSONObject("playlist").getJSONObject("hd").get("playUrl"));
        }catch (Exception e) {
            log.error("知乎去水印异常", e);
            throw new CustomerException("提取失败！");
        }
        return videoVo;
    }

    public static void main(String[] args) {
//        System.out.println(removeWatermark("如何评价电影《离秋》？ - 阿文影视的视频 - 知乎\n" +
//                "https://www.zhihu.com/zvideo/1675251141645565952"));
//        System.out.println(removeWatermark("这是唯一一部，所有视频网站，都不能播放的成龙电影 - joker剪辑的视频 - 知乎\n" +
//                "https://www.zhihu.com/zvideo/1617211526817030144"));
        //System.out.println(removeWatermark("https://www.zhihu.com/zvideo/1626511100869844992"));

//        System.out.println(removeWatermark("33 窝窝头emoji发布了一篇小红书笔记，快来看吧！ \uD83D\uDE06 GuXWsGleyrJMNw2 \uD83D\uDE06 http://xhslink.com/e2KbTt，复制本条信息，打开【小红书】App查看精彩内容！"));
//        System.out.println(removeWatermark("12 您真可爱.发布了一篇小红书笔记，快来看吧！ \uD83D\uDE06 RO7XX15fbCowWth \uD83D\uDE06 http://xhslink.com/CiEcTt，复制本条信息，打开【小红书】App查看精彩内容！"));
//        System.out.println(removeWatermark("81 窝窝头emoji发布了一篇小红书笔记，快来看吧！ \uD83D\uDE06 TxxaXl9hz3NNXAT \uD83D\uDE06 http://xhslink.com/SsU5St，复制本条信息，打开【小红书】App查看精彩内容！"));
//        System.out.println(removeWatermark("91 momo发布了一篇小红书笔记，快来看吧！ \uD83D\uDE06 fckFh7rRuXQXyvF \uD83D\uDE06 http://xhslink.com/ZijdUt，复制本条信息，打开【小红书】App查看精彩内容！"));

        //TODO 图片抖音链接
//        System.out.println(removeWatermark("7.92 DHI:/ J@V.Lw 02/28 复制打开抖音，看看# 背景图 # 欢迎取图 超好看的背景图推荐# 背景图 # 欢迎取图  https://v.douyin.com/iJEsJL8N/"));
//        System.out.println(removeWatermark("4.38 AgB:/ 复制打开抖音，看看入秋奖励自己一个大大大拿破仑  https://v.douyin.com/iJV8B7eT/"));
//        System.out.println(removeWatermark("8- 长按复制此条消息，打开抖音搜索，查看TA的更多作品。 https://v.douyin.com/iJCxMK9u/"));
//        System.out.println(removeWatermark("https://h5.pipix.com/s/iJV1a12W/"));
//        System.out.println(removeWatermark("7.15 jcA:/ 冰球制作教程# 冰天雪地# 舒畅身心# 沉浸式看雪# \uD83C\uDF28\uFE0Fasmr# 减压神器。  https://v.douyin.com/iJVLy8DN/ 复制此链接，打开Dou音火山版搜索，直接观看视频！"));
//        System.out.println(removeWatermark("说真话的人，为何越来越少了？#臧其超 #数字营销 #社会现象 >> https://video.weishi.qq.com/ONbRDNMr"));
        //TODO 头像有防盗链
//        System.out.println(removeWatermark("分享@黄小貓愛可樂 的@绿洲 动态:「80年代的表演」  https://m.oasis.weibo.cn/v1/h5/share?sid=4928994187413855"));
//        System.out.println(removeWatermark("#最右#分享一条有趣的内容给你，不好看算我输。请戳链接>>https://share.xiaochuankeji.cn/hybrid/share/post?pid=327417898&zy_to=applink&share_count=1&m=2823802580d139b049e4428f518d1e07&d=a878370439c26d1bca4a11b6de81335337cbb96c632bff34d691348a21d54e44450b3dda1428997d09503425ca29d0fd&app=zuiyou&recommend=r0&name=n0&title_type=t0"));
//        System.out.println(removeWatermark("【煮泡老陈皮水饮下-哔哩哔哩】 https://b23.tv/05Lhn0U"));
//        System.out.println(removeWatermark("【煮泡老陈皮水饮下】 https://www.bilibili.com/video/BV1su4y1R7aZ/?share_source=copy_web&vd_source=25372b8a5c7cbc4711f68350b3879402"));
//        System.out.println(removeWatermark("https://v.kuaishou.com/2pESOe 父母试探儿子的孝心，两个儿子的态度截然不同 \"情感动画 \"家庭百态 \"孝顺父母 该作品在快手被播放过915.5万次，点击链接，打开【快手】直接观看！"));
//        System.out.println(removeWatermark("https://m.eyepetizer.net/u1/video-detail?video_id=319457"));
//        System.out.println(removeWatermark("https://h5.pipigx.com/pp/post/750530000799?zy_to=copy_link&share_count=1&m=adc5da41833cb64bb13bea0857fa2abc&app=&type=post&did=7e3653bc8e96be3eba9284428b3d802a&mid=8237881659584&pid=750530000799"));
//        System.out.println(removeWatermark("https://static-play.kg.qq.com/node/5t99k2267F/play_v2?s=bf7NfZCbtE4FMCPg&shareuid=649a988d202f31&abtype=13&shareDescABType=1&topsource=a0_pn201001006_z1_u6758526_l0_t1692547879__&chain_share_id=-pAJUvHMv3NzF8EtpmfTagUmhuvh5z_ru0xdrws8zXc&pageId=feed"));
        //TODO 视频有时候打不开v9打不开
//        System.out.println(removeWatermark("https://www.ixigua.com/7266698324021871156"));
//        System.out.println(removeWatermark("https://v.huya.com/play/900323435.html?hyaction=immersepage&from_type=1&current_type=1&current_id=7262687276926392066&current_index=1"));
//        System.out.println(removeWatermark("https://www.pearvideo.com/detail_1679262"));
//        System.out.println(removeWatermark("https://www.xinpianchang.com/a12498778?from=IndexPick&part=%E7%BC%96%E8%BE%91%E7%B2%BE%E9%80%89&index=4"));
//        System.out.println(removeWatermark("https://m.acfun.cn/v/?ac=41253443&sid=d3027361769827a3"));
//        System.out.println(removeWatermark("https://www.acfun.cn/v/ac41253443"));
//        System.out.println(removeWatermark("没看懂，他们到底怎么和好的？ http://www.meipai.com/video/422/7086683181613598461?client_id=1089857299&utm_media_id=7086683181613598461&utm_source=meipai_share&utm_term=meipai_ios&gnum=&utm_content=9458&utm_share_type=3"));
//        System.out.println(removeWatermark("爱不是一味地索取，而是相互给予。#婚姻 #爱他美卓萃 #爱他美#吃秀##vlog# http://www.meipai.com/video/108/7084195679497644920?client_id=1089857299&utm_media_id=7084195679497644920&utm_source=meipai_share&utm_term=meipai_ios&gnum=2991006074&utm_content=9458&utm_share_type=3"));
//        System.out.println(removeWatermark("90年和97年的两个女孩在2023年顶峰相见！ http://www.meipai.com/video/507/7089433800888577141?client_id=1089857299&utm_media_id=7089433800888577141&utm_source=meipai_share&utm_term=meipai_ios&gnum=2991006074&utm_content=9458&utm_share_type=3"));

//        System.out.println(douyinAweme("https://v.douyin.com/iJPPm5C7/", null));

        System.out.println(douyinExclusive("1.05 YMw:/ 04/14 r@E.HV  复制打开抖音，看看【汽车周边 可能有关】开始感到幸福的时候 就知道是痛苦在倒计时了  https://v.douyin.com/idVVA8nk/"));
    }

    public static JSONObject removeWatermark(String url) {
        if (url.contains("pipix")){
            return JSONUtil.parseObj(pipixia(url));
        } else if (url.contains("douyin")){
            return JSONUtil.parseObj(douyin(url));
        } else if (url.contains("huoshan")){
            return JSONUtil.parseObj(huoshan(url));
        } else if (url.contains("h5.weishi")){
            return JSONUtil.parseObj(weishi(url));
        } else if (url.contains("isee.weishi")){
            return JSONUtil.parseObj(weishi(url));
        } else if (url.contains("video.weishi")) {
            return JSONUtil.parseObj(weishi(url));
        } else if (url.contains("weibo.com")){
            return JSONUtil.parseObj(weibo(url));
        } else if (url.contains("oasis.weibo")){
            return JSONUtil.parseObj(lvzhou(url));
        } else if (url.contains("zuiyou")){
            return JSONUtil.parseObj(zuiyou(url));
        } else if (url.contains("xiaochuankeji")){
            return JSONUtil.parseObj(zuiyou(url));
        } else if (url.contains("kuaishou")){
            return JSONUtil.parseObj(kuaishou(url));
        } else if (url.contains("eyepetizer")){
            return JSONUtil.parseObj(kaiyan(url));
        } else if (url.contains("pipigx")){
            return JSONUtil.parseObj(pipigaoxiao(url));
        } else if (url.contains("kg.qq.com")){
            return JSONUtil.parseObj(quanminkge(url));
        } else if (url.contains("ixigua.com")){
            return JSONUtil.parseObj(xigua(url));
        } else if(url.contains("huya.com/play/")){
            return JSONUtil.parseObj(huya(url));
        } else if(url.contains("pearvideo.com")){
            return JSONUtil.parseObj(pear(url));
        } else if(url.contains("xinpianchang.com")){
            return JSONUtil.parseObj(xinpianchang(url));
        } else if(url.contains("acfun.cn")){
            return JSONUtil.parseObj(acfan(url));
        } else if(url.contains("meipai.com")){
            return JSONUtil.parseObj(meipai(url));
        } else if (url.contains("b23.tv")) {
            return JSONUtil.parseObj(bilibili(url));
        } else if (url.contains("bilibili.com")) {
            return JSONUtil.parseObj(bilibili(url));
        } else if (url.contains("xhslink.com")) {
            return JSONUtil.parseObj(xiaohongshu(url));
        } else if (url.contains("zhihu.com")) {
            return JSONUtil.parseObj(zhihu(url));
        } else {
            JSONObject jsonObject = new JSONObject();
            jsonObject.set("msg", "不支持该链接");
            return jsonObject;
        }
    }
}

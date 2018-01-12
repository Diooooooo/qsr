package com.qsr.sdk.controller;

import com.qsr.sdk.controller.fetcher.Fetcher;
import com.qsr.sdk.lang.PageList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class TestController extends WebApiController {

    private final static Logger logger = LoggerFactory.getLogger(TestController.class);
    private final static String[] data = {"曼联", "曼城", "切尔西", "利物浦", "热刺", "阿森纳", "伯恩利", "水晶宫", "西汉姆", "伯恩茅", "哈德斯", "布莱顿", "纽卡", "南安普", "斯托克", "西部罗", "斯旺西", "沃特福"};

    public TestController() {
        super(logger);
    }

    public void picker() {
        try {
            List<Map<String, Object>> ls = new ArrayList<>();
            for(int i = 14 ; i < 18; i ++) {
                Map<String, Object> map = new HashMap<>();
                map.put("text", "20" + i + "/" + (i+1));
                map.put("value", i);
                ls.add(map);
            }
            Collections.reverse(ls);
            this.renderData(ls);
        } catch (Throwable t) {
            this.renderException("picker", t);
        }
    }

    public void tab(){
        try {
            List<Map<String, Object>> ls = new ArrayList<>();
            Map<String, Object> jf = new HashMap<>();
            jf.put("text", "积分榜");
            jf.put("value", 1);
            ls.add(jf);
            Map<String, Object> qy = new HashMap<>();
            qy.put("text", "球员榜");
            qy.put("value", 2);
            ls.add(qy);
            Map<String, Object> qd = new HashMap<>();
            qd.put("text", "球队榜");
            qd.put("value", 3);
            ls.add(qd);
            Map<String, Object> sc = new HashMap<>();
            sc.put("text", "赛程");
            sc.put("value", 4);
            ls.add(sc);
            this.renderData(ls);
        } catch (Throwable t) {
            this.renderException("tab", t);
        }
    }

    public void leftTab() {
        try {
            List<Map<String, Object>> ls = new ArrayList<>();
            String[] tab = {"进攻", "助攻", "射门", "射正", "解围", "抢断", "黄牌", "红牌", "任意球", "角球", "越位", "犯规", "控球率", "扑救", "传球"};
            for (int i = 0 ; i < 15; i ++) {
                Map<String, Object> jf = new HashMap<>();
                jf.put("text", tab[i]);
                jf.put("value", i+1);
                ls.add(jf);
            }
            this.renderData(ls);
        } catch (Throwable t) {
            this.renderException("leftTab", t);
        }
    }

    public void data() {
        try {
            Fetcher f = this.fetch();
            int pageNumber = f.i("pageNumber");
            int pageSize = f.i("pageSize");
            PageList<Map<String, Object>> m = getPageList(pageNumber, pageSize);
            this.renderData(m);
        } catch (Throwable t) {
            this.renderException("scoreData", t);
        }
    }

    private PageList<Map<String, Object>> getPageList(int pageNumber, int pageSize) {
        List<Map<String, Object>> ls = new ArrayList<>();
        getData(ls, data.length);
        return new PageList<>(ls.subList((pageNumber - 1) * pageSize, pageNumber * pageSize > ls.size() ? ls.size() : pageNumber * pageSize), pageNumber, pageSize);
    }

    private void getData(List<Map<String, Object>> ls, int lenght) {
        for(int i = 0 ; i < lenght; i ++) {
            Random r = new Random(90);
            Random r1 = new Random(60);
            Random r2 = new Random(10);
            Random r3 = new Random(20);
            Map<String, Object> m = new HashMap<>();
            m.put("id", i+1);
            m.put("name", data[i]);
            m.put("img", "");
            m.put("score", r.nextInt(90));
            m.put("win", r1.nextInt(60));
            m.put("deuce", r2.nextInt(10));
            m.put("lose", r3.nextInt(20));
            ls.add(m);
        }
    }

}

package com.chenmeng.project.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chenmeng.project.common.ErrorCode;
import com.chenmeng.project.exception.BusinessException;
import com.chenmeng.project.model.entity.Picture;
import com.chenmeng.project.service.PictureService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PictureServiceImpl implements PictureService {

    @Override
    public Page<Picture> searchPicture(String searchText, long pageNum, long pageSize) {
        long current = (pageNum - 1) * pageSize; // 偏移量
        String url = String.format("https://www.bing.com/images/search?q=%s&first=%s", searchText, current);
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get(); // 获取html页面
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据获取异常");
        }
        Elements elements = doc.select(".iuscp.isv"); // css标签
        // 图片类集合
        List<Picture> pictures = new ArrayList<>();
        for (Element element : elements) {
            // 取图片地址（murl）
            String m = element.select(".iusc").get(0).attr("m");
            Map<String, Object> map = JSONUtil.toBean(m, Map.class);
            String murl = (String) map.get("murl");
            // System.out.println(murl);
            // 取标题
            String title = element.select(".inflnk").get(0).attr("aria-label");
            // System.out.println(title);
            // 属性存入picture实体类
            Picture picture = new Picture();
            picture.setTitle(title);
            picture.setUrl(murl);
            // 添加到集合
            pictures.add(picture);
            if (pictures.size() >= pageSize) {
                break;
            }
        }
        // 创建一个新的分页对象，传入当前页码和每页大小
        Page<Picture> picturePage = new Page<>(pageNum, pageSize);
        // 将查询到的图片列表集合设置到分页对象中
        picturePage.setRecords(pictures);
        // 返回构建好的分页对象
        return picturePage;
    }

}

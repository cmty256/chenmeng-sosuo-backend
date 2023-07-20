package com.chenmeng.project.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chenmeng.project.common.BaseResponse;
import com.chenmeng.project.common.ErrorCode;
import com.chenmeng.project.common.ResultUtils;
import com.chenmeng.project.exception.ThrowUtils;
import com.chenmeng.project.model.dto.picture.PictureQueryRequest;
import com.chenmeng.project.model.entity.Picture;
import com.chenmeng.project.service.PictureService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/picture")
@Slf4j
public class PictureController {

    @Resource
    private PictureService pictureService;

    /**
     * 分页获取图片列表页面
     *
     * @param pictureQueryRequest 图片查询请求
     * @param request             请求
     * @return {@code BaseResponse<Page<Picture>>}
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<Picture>> listPictureByPage(@RequestBody PictureQueryRequest pictureQueryRequest, HttpServletRequest request) {
        long current = pictureQueryRequest.getCurrent();
        long pageSize = pictureQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(pageSize > 20, ErrorCode.PARAMS_ERROR);
        String searchText = pictureQueryRequest.getSearchText();
        Page<Picture> picturePage = pictureService.searchPicture(searchText, current, pageSize);
        return ResultUtils.success(picturePage);
    }

}

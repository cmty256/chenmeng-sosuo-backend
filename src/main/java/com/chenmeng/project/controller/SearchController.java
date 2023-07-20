package com.chenmeng.project.controller;

import com.chenmeng.project.common.BaseResponse;
import com.chenmeng.project.common.ResultUtils;
import com.chenmeng.project.manager.SearchFacade;
import com.chenmeng.project.model.dto.search.SearchRequest;
import com.chenmeng.project.model.vo.SearchVO;
import com.chenmeng.project.service.PictureService;
import com.chenmeng.project.service.PostService;
import com.chenmeng.project.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 聚合搜索控制器
 *
 */
@RestController
@RequestMapping("/search")
@Slf4j
public class SearchController {

    @Resource
    private UserService userService;

    @Resource
    private PostService postService;

    @Resource
    private PictureService pictureService;

    @Resource
    private SearchFacade searchFacade;

    @PostMapping("/all")
    public BaseResponse<SearchVO> searchAll(@RequestBody SearchRequest searchRequest, HttpServletRequest request) {
        return ResultUtils.success(searchFacade.searchAll(searchRequest, request));
    }
}
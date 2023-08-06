package com.chenmeng.project.manager;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chenmeng.project.common.ErrorCode;
import com.chenmeng.project.datasource.*;
import com.chenmeng.project.exception.BusinessException;
import com.chenmeng.project.exception.ThrowUtils;
import com.chenmeng.project.model.entity.Picture;
import com.chenmeng.project.model.enums.SearchTypeEnum;
import com.chenmeng.project.model.vo.PostVO;
import com.chenmeng.project.model.vo.SearchVO;
import com.chenmeng.project.model.vo.UserVO;
import com.chenmeng.project.model.dto.search.SearchRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.CompletableFuture;

/**
 * 搜索门面
 *
 */
@Component
@Slf4j
public class SearchFacade {

    @Resource
    private PostDataSource postDataSource;

    @Resource
    private UserDataSource userDataSource;

    @Resource
    private PictureDataSource pictureDataSource;

    @Resource
    private DataSourceRegistry dataSourceRegistry;

    public SearchVO searchAll(@RequestBody SearchRequest searchRequest, HttpServletRequest request) {
        String type = searchRequest.getType();
        SearchTypeEnum searchTypeEnum = SearchTypeEnum.getEnumByValue(type);
        ThrowUtils.throwIf(StringUtils.isBlank(type), ErrorCode.PARAMS_ERROR);
        String searchText = searchRequest.getSearchText();
        long current = searchRequest.getCurrent();
        long pageSize = searchRequest.getPageSize();
        // 搜索出所有数据
        if (searchTypeEnum == null) {
            CompletableFuture<Page<UserVO>> userTask = CompletableFuture.supplyAsync(() -> {
                Page<UserVO> userVOPage = userDataSource.doSearch(searchText, current, pageSize);
                return userVOPage;
            });
            CompletableFuture<Page<PostVO>> postTask = CompletableFuture.supplyAsync(() -> {
                Page<PostVO> postVOPage = postDataSource.doSearch(searchText, current, pageSize);
                return postVOPage;
            });
            CompletableFuture<Page<Picture>> pictureTask = CompletableFuture.supplyAsync(() -> {
                Page<Picture> picturePage = pictureDataSource.doSearch(searchText, 1, 10);
                return picturePage;
            });
            CompletableFuture.allOf(userTask, postTask, pictureTask).join();
            try {
                Page<UserVO> userVOPage = userTask.get();
                Page<PostVO> postVOPage = postTask.get();
                Page<Picture> picturePage = pictureTask.get();
                SearchVO searchVO = new SearchVO();
                searchVO.setUserList(userVOPage.getRecords());
                searchVO.setPostList(postVOPage.getRecords());
                searchVO.setPictureList(picturePage.getRecords());
                return searchVO;
            } catch (Exception e) {
                log.error("查询异常", e);
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "查询异常");
            }
        } else {
            SearchVO searchVO = new SearchVO();
            DataSource<?> dataSource = dataSourceRegistry.getDataSourceByType(type);
            Page<?> page = dataSource.doSearch(searchText, current, pageSize);
            searchVO.setDataList(page.getRecords());
            return searchVO;
            // Map<String, DataSource<T>> typeDataSourcesMap = new HashMap() {{
            //     put(SearchTypeEnum.POST.getValue(), postDataSource);
            //     put(SearchTypeEnum.USER.getValue(), userDataSource);
            //     put(SearchTypeEnum.PICTURE.getValue(), pictureDataSource);
            // }};
            // SearchVO searchVO = new SearchVO();
            // DataSource<?> dataSource = typeDataSourcesMap.get(type);
            // Page<?> page = dataSource.doSearch(searchText, current, pageSize);
            // searchVO.setDataList(page.getRecords());
            // return searchVO;
        }
    }
}
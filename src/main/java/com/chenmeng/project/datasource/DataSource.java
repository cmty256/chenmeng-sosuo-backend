package com.chenmeng.project.datasource;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface DataSource<T> {

    /**
     * 搜索
     *
     * @param searchText 搜索文本
     * @param pageNum    页码
     * @param pageSize   页面大小
     * @return {@code Page<T>}
     */
    Page<T> doSearch(String searchText, long pageNum, long pageSize);
}

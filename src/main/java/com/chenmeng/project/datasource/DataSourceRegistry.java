package com.chenmeng.project.datasource;

import com.chenmeng.project.model.enums.SearchTypeEnum;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据源注册器（管理器）
 *
 */
@Component
public class DataSourceRegistry {

    @Resource
    private PostDataSource postDataSource;

    @Resource
    private UserDataSource userDataSource;

    @Resource
    private PictureDataSource pictureDataSource;

    private Map<String, DataSource<T>> typeDataSourcesMap;

    /**
     * @PostConstruct 是Spring框架提供的一个用于标记初始化方法的注解，它在Bean实例化后执行
     */
    @PostConstruct
    public void doInit() {
        typeDataSourcesMap = new HashMap() {{
            put(SearchTypeEnum.POST.getValue(), postDataSource);
            put(SearchTypeEnum.USER.getValue(), userDataSource);
            put(SearchTypeEnum.PICTURE.getValue(), pictureDataSource);
        }};
    }

    /**
     * 获取数据源类型
     *
     * @param type 类型
     * @return {@code DataSource}
     */
    public DataSource getDataSourceByType(String type) {
        if (typeDataSourcesMap == null) {
            return null;
        }
        return typeDataSourcesMap.get(type);
    }

}
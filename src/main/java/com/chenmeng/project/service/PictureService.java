package com.chenmeng.project.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chenmeng.project.model.entity.Picture;

public interface PictureService {

    Page<Picture> searchPicture(String searchText, long pageNum, long pageSize);

}

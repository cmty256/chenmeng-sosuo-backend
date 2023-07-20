package com.chenmeng.project.job.once;

import com.chenmeng.project.model.dto.post.PostEsDTO;
import com.chenmeng.project.model.entity.Post;
import com.chenmeng.project.service.PostService;
import com.chenmeng.project.esdao.PostEsDao;

import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.boot.CommandLineRunner;

/**
 * 全量同步 MySQL 中的 Post 数据到 Elasticsearch 中
 *
 */
// todo 取消注释开启任务
// @Component
@Slf4j
public class FullSyncPostToEs implements CommandLineRunner {

    @Resource
    private PostService postService;

    @Resource
    private PostEsDao postEsDao;

    @Override
    public void run(String... args) {
        // 从 MySQL 数据库中获取所有的 Post 数据
        List<Post> postList = postService.list();
        if (CollectionUtils.isEmpty(postList)) {
            // 如果数据库中没有数据，直接返回
            return;
        }

        // 将获取的 Post 数据转换为 PostEsDTO 类型的列表，用于映射到 Elasticsearch 索引
        List<PostEsDTO> postEsDTOList = postList.stream().map(PostEsDTO::objToDto).collect(Collectors.toList());

        // 每次保存的批次大小
        final int pageSize = 500;
        int total = postEsDTOList.size();
        log.info("FullSyncPostToEs start, total {}", total);
        // 分批保存到 Elasticsearch 中
        for (int i = 0; i < total; i += pageSize) {
            int end = Math.min(i + pageSize, total);
            log.info("sync from {} to {}", i, end);
            // 将当前批次的数据保存到 Elasticsearch
            postEsDao.saveAll(postEsDTOList.subList(i, end));
        }
        log.info("FullSyncPostToEs end, total {}", total);
    }
}

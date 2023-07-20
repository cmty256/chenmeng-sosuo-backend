package com.chenmeng.project.job.cycle;

import com.chenmeng.project.mapper.PostMapper;
import com.chenmeng.project.model.dto.post.PostEsDTO;
import com.chenmeng.project.model.entity.Post;
import com.chenmeng.project.esdao.PostEsDao;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * 增量同步 MySQL 中的 Post 数据到 Elasticsearch 中
 *
 */
// todo 取消注释开启任务
// @Component
@Slf4j
public class IncSyncPostToEs {

    @Resource
    private PostMapper postMapper;

    @Resource
    private PostEsDao postEsDao;

    /**
     * 每分钟执行一次的定时任务，用于增量同步数据
     */
    @Scheduled(fixedRate = 60 * 1000)
    public void run() {
        // 查询近 5 分钟内的数据
        Date fiveMinutesAgoDate = new Date(new Date().getTime() - 5 * 60 * 1000L);
        List<Post> postList = postMapper.listPostWithDelete(fiveMinutesAgoDate);
        if (CollectionUtils.isEmpty(postList)) {
            // 如果没有增量数据需要同步，则直接返回
            log.info("no inc post");
            return;
        }

        // 将获取的增量 Post 数据转换为 PostEsDTO 类型的列表，用于映射到 Elasticsearch 索引
        List<PostEsDTO> postEsDTOList = postList.stream()
                .map(PostEsDTO::objToDto)
                .collect(Collectors.toList());

        // 每次保存的批次大小
        final int pageSize = 500;
        int total = postEsDTOList.size();
        log.info("IncSyncPostToEs start, total {}", total);
        // 分批保存到 Elasticsearch 中
        for (int i = 0; i < total; i += pageSize) {
            int end = Math.min(i + pageSize, total);
            log.info("sync from {} to {}", i, end);
            // 将当前批次的数据保存到 Elasticsearch
            postEsDao.saveAll(postEsDTOList.subList(i, end));
        }
        log.info("IncSyncPostToEs end, total {}", total);
    }
}

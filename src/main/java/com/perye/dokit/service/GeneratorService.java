package com.perye.dokit.service;

import com.perye.dokit.entity.ColumnInfo;
import com.perye.dokit.entity.GenConfig;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

public interface GeneratorService {

    /**
     * 查询数据库元数据
     * @param name 表名
     * @param startEnd 分页参数
     * @return /
     */
    Object getTables(String name, int[] startEnd);

    /**
     * 得到数据表的元数据
     * @param name 表名
     * @return /
     */
    List<ColumnInfo> getColumns(String name);

    /**
     * 同步表数据
     * @param columnInfos /
     */
    @Async
    void sync(List<ColumnInfo> columnInfos);

    /**
     * 保持数据
     *
     * @param columnInfos /
     */
    void save(List<ColumnInfo> columnInfos);

    /**
     * 获取所有table
     * @return /
     */
    Object getTables();

    /**
     * 代码生成
     * @param genConfig 配置信息
     * @param columns 字段信息
     * @return /
     */
    Object generator(GenConfig genConfig, List<ColumnInfo> columns);
}


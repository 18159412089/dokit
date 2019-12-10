package com.perye.dokit.service.impl;

import com.perye.dokit.entity.GenConfig;
import com.perye.dokit.repository.GenConfigRepository;
import com.perye.dokit.service.GenConfigService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Optional;

@Service
@CacheConfig(cacheNames = "genConfig")
public class GenConfigServiceImpl implements GenConfigService {

    private final GenConfigRepository genConfigRepository;

    public GenConfigServiceImpl(GenConfigRepository genConfigRepository) {
        this.genConfigRepository = genConfigRepository;
    }

    @Override
    @Cacheable(key = "#p0")
    public GenConfig find(String tableName) {
        GenConfig genConfig = genConfigRepository.findByTableName(tableName);
        if(genConfig == null){
            return new GenConfig(tableName);
        }
        return genConfig;
    }

    @Override
    @CachePut(key = "#p0")
    public GenConfig update(String tableName, GenConfig genConfig) {
        // 自动设置Api路径，注释掉前需要同步取消前端的注释
        String separator = File.separator;
        String[] paths;
        if (separator.equals("\\")) {
            paths = genConfig.getPath().split("\\\\");
        } else paths = genConfig.getPath().split(File.separator);
        StringBuilder api = new StringBuilder();
        for (String path : paths) {
            api.append(path);
            api.append(separator);
            if (path.equals("src")) {
                api.append("api");
                break;
            }
        }
        genConfig.setApiPath(api.toString());
        return genConfigRepository.save(genConfig);
    }
}


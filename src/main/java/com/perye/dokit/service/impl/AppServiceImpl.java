package com.perye.dokit.service.impl;

import cn.hutool.core.util.IdUtil;
import com.perye.dokit.dto.AppDTO;
import com.perye.dokit.dto.AppQueryCriteria;
import com.perye.dokit.entity.App;
import com.perye.dokit.mapper.AppMapper;
import com.perye.dokit.repository.AppRepository;
import com.perye.dokit.service.AppService;
import com.perye.dokit.utils.PageUtil;
import com.perye.dokit.utils.QueryHelp;
import com.perye.dokit.utils.ValidationUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * @author perye
 * @email peryedev@gmail.com
 * @date 2019/12/10
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class AppServiceImpl implements AppService {

    private AppRepository appRepository;

    private AppMapper appMapper;

    public AppServiceImpl(AppRepository appRepository, AppMapper appMapper) {
        this.appMapper = appMapper;
        this.appRepository = appRepository;
    }

    @Override
    public Object queryAll(AppQueryCriteria criteria, Pageable pageable){
        Page<App> page = appRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(appMapper::toDto));
    }

    @Override
    public Object queryAll(AppQueryCriteria criteria){
        return appMapper.toDto(appRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    public AppDTO findById(String id) {
        Optional<App> app = appRepository.findById(id);
        ValidationUtil.isNull(app,"App","id",id);
        return appMapper.toDto(app.get());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AppDTO create(App resources) {
        resources.setId(IdUtil.fastUUID());
        return appMapper.toDto(appRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(App resources) {
        Optional<App> optionalApp = appRepository.findById(resources.getId());
        ValidationUtil.isNull( optionalApp,"App","id",resources.getId());
        App App = optionalApp.get();
        App.copy(resources);
        appRepository.save(App);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String id) {
        appRepository.deleteById(id);
    }
}

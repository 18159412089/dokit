package com.perye.dokit.service;

import com.perye.dokit.dto.UserDto;
import com.perye.dokit.entity.Menu;
import com.perye.dokit.entity.Role;
import com.perye.dokit.repository.RoleRepository;
import com.perye.dokit.utils.StringUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@CacheConfig(cacheNames = "role")
public class JwtPermissionServiceImpl {

    private final RoleRepository roleRepository;

    public JwtPermissionServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }
    /**
     * key的名称如有修改，请同步修改 UserServiceImpl 中的 update 方法
     * @param user 用户信息
     * @return Collection
     */
    @Cacheable(key = "'loadPermissionByUser:' + #p0.username")
    public Collection<GrantedAuthority> mapToGrantedAuthorities(UserDto user) {

        System.out.println("--------------------loadPermissionByUser:" + user.getUsername() + "---------------------");

        Set<Role> roles = roleRepository.findByUsers_Id(user.getId());

        Set<String> permissions = roles.stream().filter(role -> StringUtils.isNotBlank(role.getPermission())).map(Role::getPermission).collect(Collectors.toSet());
        permissions.addAll(
                roles.stream().flatMap(role -> role.getMenus().stream())
                        .filter(menu -> StringUtils.isNotBlank(menu.getPermission()))
                        .map(Menu::getPermission).collect(Collectors.toSet())
        );
        return permissions.stream().map(permission -> new SimpleGrantedAuthority(permission))
                .collect(Collectors.toList());
    }
}

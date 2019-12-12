package ${package}.service;

import ${package}.entity.${className};
import ${package}.dto.${className}Dto;
import ${package}.dto.${className}QueryCriteria;
import org.springframework.data.domain.Pageable;
import java.util.Map;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public interface ${className}Service {

    /**
    * 查询数据分页
    * @param criteria 条件参数
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(${className}QueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<${className}Dto>
    */
    List<${className}Dto> queryAll(${className}QueryCriteria criteria);

    /**
    * 根据ID查询
    * @param ${pkChangeColName} ID
    * @return ${className}Dto
    */
    ${className}Dto findById(${pkColumnType} ${pkChangeColName});


    ${className}Dto create(${className} resources);


    void update(${className} resources);

    void delete(${pkColumnType} ${pkChangeColName});

    void download(List<${className}Dto> all, HttpServletResponse response) throws IOException;

    void deleteAll(${pkColumnType}[] ids);
}

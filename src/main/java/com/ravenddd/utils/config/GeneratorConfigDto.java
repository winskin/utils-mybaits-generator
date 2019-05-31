package com.ravenddd.utils.config;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zjh
 * @Date 2017/12/10
 * @Time 17:31
 * @ClassName GeneratorConfigDto
 * @Description 配置信息, 主要获取yml配置文件
 */
@Data
@Accessors(chain = true)
public class GeneratorConfigDto {

    private String[] tableNames;
    private String classPath;
    private String driverClass;
    private String url;
    private String user;
    private String password;
    private String schema;
    /**
     * 绝对路径项目根目录
     */
    private String projectPath;
    private String javaModelGeneratorPackage;
    private String javaModelGeneratorProject;
    private String javaClientGeneratorPackage;
    private String javaClientGeneratorProject;
    private String sqlMapGeneratorPackage;
    private String sqlMapGeneratorProject;
}

package com.ravenddd.utils.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.logging.Log;
import org.mybatis.generator.logging.LogFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zjh
 * @date 2019/5/31 9:39
 * @since 1.0
 */
public class ConfigHandle {

    private static Log logger = LogFactory.getLog(ConfigHandle.class);

    /**
     * 配置文件"generator.yml"
     *
     * @param yamlName
     * @return
     */
    public static GeneratorConfigDto handle(String yamlName) {

        // 配置对象
        GeneratorConfigDto generatorConfigDto = getConfig(yamlName);
        if (generatorConfigDto == null) {
            throw new IllegalArgumentException("获取配置错误");
        }

        // 获取驱动包
        getDriverClassPath(generatorConfigDto);

        // 获取项目路径
        getProjectPath(generatorConfigDto);

        // 创建文件夹
        mkdir(generatorConfigDto);

        logger.debug("entity path:" + generatorConfigDto.getJavaModelGeneratorProject());
        logger.debug("mapperJava path:" + generatorConfigDto.getJavaClientGeneratorProject());
        logger.debug("mapperXml path:" + generatorConfigDto.getSqlMapGeneratorProject());

        return generatorConfigDto;
    }

    /**
     * 获取yaml配置
     * @param yamlName
     * @return
     */
    private static GeneratorConfigDto getConfig(String yamlName) {
        Map<String, Object> map = new HashMap<>();
        Yaml yaml = new Yaml();
        InputStream is = ConfigHandle.class.getClassLoader().getResourceAsStream(yamlName);
        if (is != null) {
            Object obj = yaml.load(is);
            if (obj != null) {
                map = JSON.parseObject(JSON.toJSONString(obj), new TypeReference<Map<String, Object>>() {
                });
            }
        }

        return JSON.parseObject(JSON.toJSONString(map), GeneratorConfigDto.class);
    }

    /**
     * 获取驱动包
     * @param generatorConfigDto
     */
    private static void getDriverClassPath(GeneratorConfigDto generatorConfigDto) {
        if (StringUtils.isBlank(generatorConfigDto.getClassPath())) {
            try {
                String driverClassPath = Class.forName(generatorConfigDto.getDriverClass())
                        .getProtectionDomain()
                        .getCodeSource()
                        .getLocation()
                        .getPath()
                        .substring(1);
                generatorConfigDto.setClassPath(driverClassPath);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取项目路径
     * @param generatorConfigDto
     */
    private static void getProjectPath(GeneratorConfigDto generatorConfigDto) {
        if (StringUtils.isBlank(generatorConfigDto.getProjectPath())) {
            // 默认获取项目路径
            String projectPath = System.getProperty("user.dir") + File.separator + "mybatis-generator" + File.separator;
            // 处理中文路劲
            try {
                projectPath = java.net.URLDecoder.decode(projectPath, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            generatorConfigDto.setProjectPath(projectPath);
        }
    }

    /**
     * 创建文件夹
     * @param generatorConfigDto
     */
    private static void mkdir(GeneratorConfigDto generatorConfigDto) {
        String projectPath = generatorConfigDto.getProjectPath();
        generatorConfigDto.setJavaModelGeneratorProject(projectPath + generatorConfigDto.getJavaModelGeneratorProject().replace(".", File.separator))
                .setJavaClientGeneratorProject(projectPath + generatorConfigDto.getJavaClientGeneratorProject().replace(".", File.separator))
                .setSqlMapGeneratorProject(projectPath + generatorConfigDto.getSqlMapGeneratorProject().replace(".", File.separator));

        new File(generatorConfigDto.getProjectPath()).mkdirs();
        new File(generatorConfigDto.getJavaModelGeneratorProject()).mkdirs();
        new File(generatorConfigDto.getJavaClientGeneratorProject()).mkdirs();
        new File(generatorConfigDto.getSqlMapGeneratorProject()).mkdirs();
    }
}

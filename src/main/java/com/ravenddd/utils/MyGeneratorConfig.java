package com.ravenddd.utils;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zjh
 * @Date 2017/12/10
 * @Time 17:31
 * @ClassName MyGeneratorConfig
 * @Description 配置信息,主要获取yml配置文件
 */
@Data
@Accessors(chain = true)
public class MyGeneratorConfig {

    private  String[] tableNames;
    private  String classPath;
    private  String driverClass;
    private  String url;
    private  String user;
    private  String password;
    private  String schema;
    /**
     * 绝对路径项目根目录
     */
    private  String projectPath;
    private  String javaModelGeneratorPackage;
    private  String javaModelGeneratorProject;
    private  String javaClientGeneratorPackage;
    private  String javaClientGeneratorProject;
    private  String sqlMapGeneratorPackage;
    private  String sqlMapGeneratorProject;

    MyGeneratorConfig() {

    }

    /**
     * 配置文件"generator.yml"
     * @param ymlName
     * @return
     */
    public MyGeneratorConfig getConfig(String ymlName) {

        Map<String, Object> map = new HashMap<>();
        Yaml yaml = new Yaml();
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(ymlName);
        if (is != null) {
            Object obj = yaml.load(is);
            if (obj != null) {
                map = (Map<String, Object>) obj;
            }
        }

        MyGeneratorConfig myGeneratorConfig = JSON.parseObject(JSON.toJSONString(map), this.getClass());

        if (StringUtils.isBlank(myGeneratorConfig.getClassPath())) {
            // 获取驱动包路径
            try {
                String driverClassPath = Class.forName(myGeneratorConfig.getDriverClass())
                        .getProtectionDomain()
                        .getCodeSource()
                        .getLocation()
                        .getPath()
                        .replace("/", "\\").substring(1);
                myGeneratorConfig.setClassPath(driverClassPath);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        if (StringUtils.isBlank(myGeneratorConfig.getProjectPath())) {
            // 默认获取项目路径
            String[] projectPaths = MyGeneratorConfig.class.getResource("/").getPath().split("/target");
            projectPath = projectPaths[0].replace("/", "\\").substring(1) + "\\" + "mybatis-generator" + "\\";
            // 处理中文路劲
            try {
                projectPath = java.net.URLDecoder.decode(projectPath,"utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            myGeneratorConfig.setProjectPath(projectPath);
        }

        projectPath = myGeneratorConfig.getProjectPath();
        myGeneratorConfig.setJavaModelGeneratorProject(projectPath + myGeneratorConfig.getJavaModelGeneratorProject());
        myGeneratorConfig.setJavaClientGeneratorProject(projectPath + myGeneratorConfig.getJavaClientGeneratorProject());
        myGeneratorConfig.setSqlMapGeneratorProject(projectPath + myGeneratorConfig.getSqlMapGeneratorProject());

        // 创建文件夹
        new File(myGeneratorConfig.getProjectPath()).mkdirs();
        new File(myGeneratorConfig.getJavaModelGeneratorProject()).mkdirs();
        new File(myGeneratorConfig.getJavaClientGeneratorProject()).mkdirs();
        new File(myGeneratorConfig.getSqlMapGeneratorProject()).mkdirs();

        System.out.println("entity path:" + myGeneratorConfig.getJavaModelGeneratorProject());
        System.out.println("mapperJava path:" + myGeneratorConfig.getJavaClientGeneratorProject());
        System.out.println("mapperXml path:" + myGeneratorConfig.getSqlMapGeneratorProject());

        return myGeneratorConfig;
    }
}

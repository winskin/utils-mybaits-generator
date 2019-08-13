package com.ravenddd.utils;

import com.ravenddd.utils.config.ConfigHandle;
import com.ravenddd.utils.config.GeneratorConfigDto;
import com.ravenddd.utils.introspected.MyIntrospectedTableMyBatis3Impl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.*;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zjh
 * @Date 2017/11/28
 * @Time 14:44
 * @ClassName MybatisGenerator
 * @Description 用于生成库表代码mapper, 配置读取 generator.yml
 *              注意: Mybatis-Generator 1.3.5 xml默认追加合并且不支持修改, 1.3.6则支持追加
 */
@Slf4j
public class MybatisGeneratorMain {

    private Context context = new Context(ModelType.FLAT);
    private List<String> warnings = new ArrayList<>();
    /**
     * 获取配置
     */
    private GeneratorConfigDto generatorConfigDto;

    public MybatisGeneratorMain(String ymlPath) {

        // 设置配置文件
        this.generatorConfigDto = ConfigHandle.handle(ymlPath);
        // 生成
        generator();
    }

    /**
     * 生成代码主方法
     */
    private void generator() {

        if (StringUtils.isNotBlank(generatorConfigDto.getSchema())) {
            generatorConfigDto.setUrl(generatorConfigDto.getUrl() + "/" + generatorConfigDto.getSchema());
        }

        context.setId("prod");
//        context.setTargetRuntime("MyBatis3");
        context.setTargetRuntime(MyIntrospectedTableMyBatis3Impl.class.getName());

        // ---------- 配置信息 start ----------

        pluginBuilder(context, "org.mybatis.generator.plugins.ToStringPlugin");
        pluginBuilder(context, "org.mybatis.generator.plugins.FluentBuilderMethodsPlugin");
        pluginBuilder(context, "org.mybatis.generator.plugins.ToStringPlugin");
        pluginBuilder(context, "org.mybatis.generator.plugins.SerializablePlugin");

        commentGeneratorBuilder(context);

        jdbcConnectionBuilder(context);

        javaTypeResolverBuilder(context);

        javaModelGeneratorBuilder(context);

        sqlMapGeneratorBuilder(context);

        javaClientGeneratorBuilder(context);

        tableBuilder(context, generatorConfigDto.getSchema(), generatorConfigDto.getTableNames());

        // ---------- 配置信息 end ----------


        // --------- 校验,执行 ---------
        Configuration config = new Configuration();
        config.addClasspathEntry(generatorConfigDto.getClassPath());
        config.addContext(context);
        DefaultShellCallback callback = new DefaultShellCallback(true);

        try {
            MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
            myBatisGenerator.generate(null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 输出日志
        if (warnings != null) {
            warnings.forEach(warning -> log.warn("warnings : {}", warning));
        }
    }

    /**
     * plugin
     * @param context
     */
    private void pluginBuilder(Context context, String configurationType) {
        PluginConfiguration pluginConfiguration = new PluginConfiguration();
        pluginConfiguration.setConfigurationType(configurationType);
        context.addPluginConfiguration(pluginConfiguration);
    }

    /**
     * commentGenerator
     * @param context
     */
    private void commentGeneratorBuilder(Context context) {
        CommentGeneratorConfiguration commentGeneratorConfiguration = new CommentGeneratorConfiguration();
        commentGeneratorConfiguration.addProperty("suppressDate", "true");
        commentGeneratorConfiguration.addProperty("suppressAllComments", "false");
        commentGeneratorConfiguration.addProperty("addRemarkComments", "true");
        commentGeneratorConfiguration.addProperty("javaFileEncoding", "UTF-8");
        context.setCommentGeneratorConfiguration(commentGeneratorConfiguration);
    }

    /**
     * jdbcConnection
     * @param context
     */
    private void jdbcConnectionBuilder(Context context) {
        JDBCConnectionConfiguration jdbc = new JDBCConnectionConfiguration();
        jdbc.setConnectionURL(generatorConfigDto.getUrl());
        jdbc.setDriverClass(generatorConfigDto.getDriverClass());
        jdbc.setUserId(generatorConfigDto.getUser());
        jdbc.setPassword(generatorConfigDto.getPassword());
        context.setJdbcConnectionConfiguration(jdbc);
    }

    /**
     * javaTypeResolver
     * @param context
     */
    private void javaTypeResolverBuilder(Context context) {
        JavaTypeResolverConfiguration javaTypeResolverConfiguration = new JavaTypeResolverConfiguration();
        javaTypeResolverConfiguration.addProperty("forceBigDecimals", "true");
        context.setJavaTypeResolverConfiguration(javaTypeResolverConfiguration);
    }

    /**
     * javaModelGenerator
     * @param context
     */
    private void javaModelGeneratorBuilder(Context context) {
        JavaModelGeneratorConfiguration javaModel = new JavaModelGeneratorConfiguration();
        javaModel.setTargetPackage(generatorConfigDto.getJavaModelGeneratorPackage());
        javaModel.setTargetProject(generatorConfigDto.getJavaModelGeneratorProject());
        javaModel.addProperty("trimStrings", "true");
        javaModel.addProperty("enableSubPackages", "true");
        context.setJavaModelGeneratorConfiguration(javaModel);
    }

    /**
     * sqlMapGenerator
     * @param context
     */
    private void sqlMapGeneratorBuilder(Context context) {
        SqlMapGeneratorConfiguration sqlMapGeneratorConfiguration = new SqlMapGeneratorConfiguration();
        sqlMapGeneratorConfiguration.setTargetPackage(generatorConfigDto.getSqlMapGeneratorPackage());
        sqlMapGeneratorConfiguration.setTargetProject(generatorConfigDto.getSqlMapGeneratorProject());
        sqlMapGeneratorConfiguration.addProperty("enableSubPackages", "true");
        context.setSqlMapGeneratorConfiguration(sqlMapGeneratorConfiguration);
    }

    /**
     * javaClientGenerator
     * @param context
     */
    private void javaClientGeneratorBuilder(Context context) {
        JavaClientGeneratorConfiguration javaClientGeneratorConfiguration = new JavaClientGeneratorConfiguration();
        javaClientGeneratorConfiguration.setTargetPackage(generatorConfigDto.getJavaClientGeneratorPackage());
        javaClientGeneratorConfiguration.setTargetProject(generatorConfigDto.getJavaClientGeneratorProject());
        javaClientGeneratorConfiguration.setConfigurationType("XMLMAPPER");
        javaClientGeneratorConfiguration.addProperty("enableSubPackages", "true");
        context.setJavaClientGeneratorConfiguration(javaClientGeneratorConfiguration);

    }

    /**
     * table
     * @param context
     * @param schema        添加SQL表名前面的库名
     * @param tableName
     */
    private void tableBuilder(Context context, String schema, String...tableName) {
        for (String table : tableName) {
            TableConfiguration tableConfiguration = new TableConfiguration(context);
            tableConfiguration.setTableName(table);
            tableConfiguration.setCountByExampleStatementEnabled(false);
            tableConfiguration.setUpdateByExampleStatementEnabled(false);
            tableConfiguration.setDeleteByExampleStatementEnabled(false);
            tableConfiguration.setSelectByExampleStatementEnabled(false);
            if (StringUtils.isNotBlank(schema)) {
                tableConfiguration.setSchema(schema);
                tableConfiguration.addProperty("runtimeSchema", schema);
            }
            context.addTableConfiguration(tableConfiguration);
        }
    }
}

package com.ravenddd.utils;

/**
 * @author zjh
 * @Date 2017/11/28
 * @Time 14:44
 * @ClassName MybatisGenerator
 * @Description 用于生成库表代码mapper, 配置读取 generator.yml
 *              注意: Mybatis-Generator 1.3.5 xml默认追加合并且不支持修改, 1.3.6则支持追加
 */
public class MainGenerator {

    public static void main(String[] args) {
        new MybatisGeneratorMain("generator.yml");
    }
}

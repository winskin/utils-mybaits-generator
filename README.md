# utils-mybaits-generator
生成mybatis代码


### 使用说明(其他项目使用, 请先把本项目构建成jar)
1.引入maven包
```
<dependency>
    <groupId>com.ravenddd.utils</groupId>
    <artifactId>utils-mybaits-generator</artifactId>
    <version>${version}</version>
</dependency>
```

2.在resources目录下新建`generator.yml`配置文件(使用YAML,自行修改配置)
```
# ------------------------- 需要生成的表名 -----------------------------
tableNames :
   - user_account

# ------------------------- 数据库连接 --------------------------------

url :
user :
password :


# ------------------------- 项目路径和驱动包路径(绝对路径) ----------------------

# 项目所在的地址路径(若不设置,默认保存在target/所在根目录下新建mybatis-generator文件夹内)
#projectPath : D:\...\

# jar包的绝对路径(若不设置,默认根据driverClass包名自动获取)
# 已废弃
#classPath : D:\...\mysql-connector-java-5.1.40.jar

# ------------------------- 项目配置 ----------------------------------
# 驱动包名(默认使用mysql)
driverClass : com.mysql.jdbc.Driver

# entity生成
javaModelGeneratorPackage : com.cloud.utils.entity
javaModelGeneratorProject : src.main.java

# mapper生成
javaClientGeneratorPackage : com.cloud.utils.mapper
javaClientGeneratorProject : src.main.java

# xml生成
sqlMapGeneratorPackage : mapperXml
sqlMapGeneratorProject : src.main.resources

```

3.运行生成方法
```
public static void main(String[] args) {
    new MybatisGeneratorMain().generator();
}
```
package com.jycloud.tool.code.generator;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.TemplateType;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;

import java.util.*;
import java.util.stream.Collectors;

public class JyCloudCodeGenerator {

    public static void main(String[] args) {
        // ================= 1. 基础配置 =================
        String url = "jdbc:mysql://120.27.143.176:3306/jy_cloud?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai";
        String username = "root";
        String password = "Passw0rd@123!!";

        String rootPath = System.getProperty("user.dir");
        if (rootPath.endsWith("jy-code-generator")) {
            rootPath = rootPath.replace("\\jy-code-generator", "").replace("/jy-code-generator", "");
        }
        String projectRoot = rootPath;

        String bizModule = "user";
        String tablePrefix = "t_";

        // ---------- 模块源码根路径 ----------
        String apiJavaPath = projectRoot + "/api/jy-" + bizModule + "-api/src/main/java";
        String serviceJavaPath = projectRoot + "/service/jy-" + bizModule + "-service/src/main/java";
        String serviceResPath = projectRoot + "/service/jy-" + bizModule + "-service/src/main/resources";
        String webJavaPath = projectRoot + "/web/jy-" + bizModule + "/src/main/java";

        // ---------- 各层最终输出目录 (含包目录) ----------
        String entityOutPath = apiJavaPath + "/com/jycloud/api/" + bizModule + "/entity";
        String mapperOutPath = serviceJavaPath + "/com/jycloud/service/" + bizModule + "/repository/mapper";
        String serviceOutPath = serviceJavaPath + "/com/jycloud/service/" + bizModule + "/service";
        String serviceImplOutPath = serviceJavaPath + "/com/jycloud/service/" + bizModule + "/service/impl";
        String xmlOutPath = serviceResPath + "/mapper";
        String controllerOutPath = webJavaPath + "/com/jycloud/web/" + bizModule + "/controller";

        // ---------- 包名 (相对 com.jycloud) ----------
        String entityPkg = "api." + bizModule + ".entity";
        String mapperPkg = "service." + bizModule + ".repository.mapper";
        String servicePkg = "service." + bizModule + ".service";
        String serviceImplPkg = "service." + bizModule + ".service.impl";
        String controllerPkg = "web." + bizModule + ".controller";

        // ================= 2. 交互式输入表名 =================
        System.out.println("==================================================");
        System.out.println(" 欢迎使用 JyCloud 三段式代码生成器");
        System.out.println(" 请输入需要生成的表名 (多个表名请用逗号分隔)");
        System.out.println(" 示例: sys_user, sys_role");
        System.out.println("==================================================");
        System.out.print("请输入表名: ");

        Scanner scanner = new Scanner(System.in);
        String inputTables = scanner.nextLine().trim();

        List<String> tableList = Arrays.stream(inputTables.split("[,，]"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        if (tableList.isEmpty()) {
            System.out.println("❌ 未检测到有效的表名，程序已退出。");
            return;
        }
        System.out.println("✅ 即将为以下表生成代码: " + tableList);

        // ================= 3. 第一段：生成 Entity (到 api 模块) =================
        System.out.println("\n⏳ [1/3] 正在生成 Entity 到 api 模块...");
        FastAutoGenerator.create(url, username, password)
                .globalConfig(builder -> builder.author("jycloud").enableSpringdoc().disableOpenDir())
                .dataSourceConfig(builder -> builder.typeConvertHandler((globalConfig, typeRegistry, metaInfo) -> {
                    // tinyint(byte) → Integer，避免生成 Boolean 类型
                    String typeName = metaInfo.getTypeName();
                    if ("BIT".equalsIgnoreCase(typeName) || "TINYINT".equalsIgnoreCase(typeName)) {
                        return com.baomidou.mybatisplus.generator.config.rules.DbColumnType.INTEGER;
                    }
                    return typeRegistry.getColumnType(metaInfo);
                }))
                .packageConfig(builder -> builder.parent("com.jycloud")
                        .entity(entityPkg)
                        .pathInfo(Collections.singletonMap(OutputFile.entity, entityOutPath)))
                .strategyConfig(builder -> builder
                        .addInclude(tableList)
                        .addTablePrefix(tablePrefix)
                        .entityBuilder()
                        .enableLombok()
                        .enableTableFieldAnnotation()
                        .logicDeleteColumnName("deleted"))
                .templateConfig(builder -> builder
                        .disable(TemplateType.SERVICE, TemplateType.SERVICE_IMPL,
                                TemplateType.CONTROLLER, TemplateType.MAPPER, TemplateType.XML))
                .templateEngine(new VelocityTemplateEngine())
                .execute();

        // ================= 4. 第二段：生成 Mapper & Service & XML (到 service 模块) =================
        System.out.println("⏳ [2/3] 正在生成 Mapper/Service/XML 到 service 模块...");
        FastAutoGenerator.create(url, username, password)
                .globalConfig(builder -> builder.author("jycloud").enableSpringdoc().disableOpenDir())
                .packageConfig(builder -> builder.parent("com.jycloud")
                        .entity(entityPkg)
                        .mapper(mapperPkg)
                        .service(servicePkg)
                        .serviceImpl(serviceImplPkg)
                        .pathInfo(Map.of(
                                OutputFile.mapper, mapperOutPath,
                                OutputFile.service, serviceOutPath,
                                OutputFile.serviceImpl, serviceImplOutPath,
                                OutputFile.xml, xmlOutPath)))
                .strategyConfig(builder -> builder
                        .addInclude(tableList)
                        .addTablePrefix(tablePrefix)
                        .mapperBuilder()
                        .enableMapperAnnotation()
                        .enableBaseResultMap()
                        .enableBaseColumnList()
                        .serviceBuilder()
                        .formatServiceFileName("%sService")
                        .formatServiceImplFileName("%sServiceImpl"))
                .templateConfig(builder -> builder
                        .disable(TemplateType.ENTITY, TemplateType.CONTROLLER))
                .templateEngine(new VelocityTemplateEngine())
                .execute();

        // ================= 5. 第三段：生成 Controller (到 web 模块) =================
        System.out.println("⏳ [3/3] 正在生成 Controller 到 web 模块...");
        // FastAutoGenerator.create(url, username, password)
        //         .globalConfig(builder -> builder.author("jycloud").enableSpringdoc().disableOpenDir())
        //         .packageConfig(builder -> builder.parent("com.jycloud")
        //                 .controller(controllerPkg)
        //                 .pathInfo(Collections.singletonMap(OutputFile.controller, controllerOutPath)))
        //         .strategyConfig(builder -> builder
        //                 .addInclude(tableList)
        //                 .addTablePrefix(tablePrefix)
        //                 .controllerBuilder()
        //                 .enableRestStyle()
        //                 .enableHyphenStyle())
        //         .templateConfig(builder -> builder
        //                 .disable(TemplateType.ENTITY, TemplateType.MAPPER,
        //                         TemplateType.SERVICE, TemplateType.SERVICE_IMPL, TemplateType.XML))
        //         .templateEngine(new VelocityTemplateEngine())
        //         .execute();

        System.out.println("\n🎉 三段式代码生成完毕！请检查对应模块目录。");
    }
}
package com.rice.lfcdemo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import io.swagger.v3.oas.models.ExternalDocumentation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)  // HTTP 认证
                .in(SecurityScheme.In.HEADER)    // token 在 header 中
                .name("token");


        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("tokenAuth");
        // header 名称
        // 联系人信息
        Contact contact = new Contact()
                .name("LFC Team")  // 联系人名称
                .url("https://github.com/your-repo")  // 项目地址
                .email("support@lfc-demo.com");  // 邮箱

        // 许可证信息
        License license = new License()
                .name("Apache 2.0")
                .url("https://www.apache.org/licenses/LICENSE-2.0.html");

        // API 基础信息
        Info info = new Info()
                .title("LFC 博客系统 API 文档")
                .description("这是 LFC 博客系统的 RESTful API 文档，提供了文章管理、分类管理、用户认证等功能。")
                .version("v1.0.0")
                .contact(contact)
                .license(license)
                .termsOfService("https://your-website.com/terms");

        // 服务器配置（可以配置多个环境）
        Server devServer = new Server()
                .url("http://localhost:8080")
                .description("开发环境");

        Server testServer = new Server()
                .url("http://test.lfc-demo.com")
                .description("测试环境");

        Server prodServer = new Server()
                .url("https://api.lfc-demo.com")
                .description("生产环境");

        return new OpenAPI()
                .info(info)
                .servers(Arrays.asList(devServer, testServer, prodServer))
                .schemaRequirement("tokenAuth", securityScheme)
                .addSecurityItem(securityRequirement)
                .externalDocs(new ExternalDocumentation()
                        .description("项目文档")
                        .url("https://your-wiki.com"));
    }
}
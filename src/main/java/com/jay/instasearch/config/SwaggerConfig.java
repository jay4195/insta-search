package com.jay.instasearch.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
            //apiInfo指定测试文档基本信息，这部分将在页面展示
            .apiInfo(apiInfo())  //Api信息
            .select()  //选择器
            .apis(RequestHandlerSelectors.basePackage("com.jay.instasearch"))  //只有在这个包和子包下的接口被生成API文档
            .paths(PathSelectors.any())  //允许的路径,可以指定post
            .build();
    }

    //基本信息，页面展示
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
            .title("Instagram Search Service")
            .description("A Project of Instagram Clone.")
            //联系人实体类
            .contact(
                    new Contact("jay4195", "https://github.com/jay4195", "jay4195@qq.com")
            )
            //版本号
            .version("0.0.1")
            .build();
    }
}
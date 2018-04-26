package com.ugoodtech.umi.manager;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

//@Configuration
//@EnableAutoConfiguration
//@ComponentScan("com.ugoodtech.mdcc")
public class ServletInitializer extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(UmiManagerApplication.class);
    }

}

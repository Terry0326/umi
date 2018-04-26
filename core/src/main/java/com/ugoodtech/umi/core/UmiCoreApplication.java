package com.ugoodtech.umi.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@ComponentScan(basePackages = "com.ugoodtech.umi")
public class UmiCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(UmiCoreApplication.class, args);
    }
}

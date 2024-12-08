package com.system.assessment.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages = "com.system.assessment.mapper", sqlSessionFactoryRef = "mysqlSqlSessionFactory")
public class MysqlMyBatisConfig {
}

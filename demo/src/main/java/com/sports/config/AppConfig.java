package com.sports.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;

import java.time.LocalDate;
import java.util.Scanner;

@Configuration
public class AppConfig {

    @Bean
    public Scanner sc() {
        return new Scanner(System.in);
    }

    @Bean
    public String lineBreak() {
        return "************************************";
    }
}

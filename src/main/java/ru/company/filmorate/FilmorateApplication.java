package ru.company.filmorate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FilmorateApplication {

    public static final Logger log = LoggerFactory.getLogger(FilmorateApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(FilmorateApplication.class, args);
        log.info("Приложение запущено");
    }

}

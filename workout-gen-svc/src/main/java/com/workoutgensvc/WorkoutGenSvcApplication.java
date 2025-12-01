package com.workoutgensvc;

import com.google.gson.Gson;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class WorkoutGenSvcApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorkoutGenSvcApplication.class, args);
    }

    @Bean
    public Gson gson() {
        return new Gson();
    }
}

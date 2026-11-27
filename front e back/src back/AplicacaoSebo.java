package br.ufsc.sebo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class AplicacaoSebo {
    public static void main(String[] args) {
        SpringApplication.run(AplicacaoSebo.class, args);
    }
}

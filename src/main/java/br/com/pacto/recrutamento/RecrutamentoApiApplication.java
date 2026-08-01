package br.com.pacto.recrutamento;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RecrutamentoApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(RecrutamentoApiApplication.class, args);
    }
}

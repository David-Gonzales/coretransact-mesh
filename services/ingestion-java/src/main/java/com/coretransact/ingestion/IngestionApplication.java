package com.coretransact.ingestion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class IngestionApplication {

	public static void main(String[] args) {
        System.out.println(helloWorld());
        SpringApplication.run(IngestionApplication.class, args);
	}

    public static String helloWorld(){
        return "Hello World";
    }

}

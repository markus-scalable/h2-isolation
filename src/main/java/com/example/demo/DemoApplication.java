package com.example.demo;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@EnableRetry
@SpringBootApplication
public class DemoApplication {

    @Entity
    public static class Banana {
        @Id
        private int id;

        Banana() {
        }

        public Banana(int id) {
            this.id = id;
        }
    }

    @Service
    public static class BananaFactory {

        @Autowired
        private Bananas bananas;

        @Transactional(isolation = Isolation.SERIALIZABLE)
        @Retryable(maxAttempts = 100)
        public boolean createOnce(int id) {
            if (bananas.existsById(id)) {
                return false;
            }
            Banana banana = new Banana(id);
            bananas.save(banana);
            return true;
        }
    }

    public interface BananaRadioStation {
        void broadcastBirth(int id);
    }

    @Service
    public static class BananaBirthApplicationService {

        @Autowired
        private BananaFactory factory;

        @Autowired
        private BananaRadioStation radio;

        public void deliver(int id) {
            if (factory.createOnce(id)) {
                radio.broadcastBirth(id);
            }
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}

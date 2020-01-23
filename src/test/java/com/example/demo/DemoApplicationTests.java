package com.example.demo;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class DemoApplicationTests {

    @MockBean
    private DemoApplication.BananaRadioStation radio;

    @Autowired
    DemoApplication.BananaBirthApplicationService birthService;

    @Autowired
    Bananas bananas;

    @BeforeEach
    public void cleanUp() {
        bananas.deleteAll();
    }

    @RepeatedTest(500)
    public void shouldBroadCastOnlyOnce() {
        int id = 1;
        int concurrency = 50;

        runConcurrently(concurrency, () -> birthService.deliver(id));

        verify(radio).broadcastBirth(id);
        verify(radio, times(concurrency)).broadcastAlways(id);
    }

    private void runConcurrently(int concurrency, Runnable task) {
        CountDownLatch start = new CountDownLatch(concurrency);
        ExecutorService executor = Executors.newCachedThreadPool();

        for (int i = 0; i < concurrency; i++) {
            executor.execute(() -> {
                start.countDown();
                try {
                    start.await();
                } catch (InterruptedException e) {
                    fail(e.getMessage());
                }

                try {
                    task.run();
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            });
        }

        try {
            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            fail(e.getMessage());
        }
    }
}

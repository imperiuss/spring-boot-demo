package com.pcz.zookeeper;

import com.pcz.zookeeper.annotation.ZooLock;
import com.pcz.zookeeper.aspectj.ZooLockAspect;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class SpringBootZookeeperApplicationTest {
    public Integer count = 10000;
    private ExecutorService executorService = Executors.newFixedThreadPool(1000);

    @Autowired
    private CuratorFramework zkClient;

    public Integer getCount() {
        return count;
    }

    @Test
    public void test() throws InterruptedException {
        IntStream.range(1, 10000).forEach(i -> executorService.execute(this::doBuy));
        TimeUnit.MINUTES.sleep(1);
        log.error("count值为{}", count);
    }

    public void doBuy() {
        count--;
        log.info("count值为{}", count);
    }

    @Test
    public void testAopLock() throws InterruptedException {
        SpringBootZookeeperApplicationTest target = new SpringBootZookeeperApplicationTest();
        AspectJProxyFactory factory = new AspectJProxyFactory(target);
        ZooLockAspect aspect = new ZooLockAspect(zkClient);
        factory.addAspect(aspect);
        SpringBootZookeeperApplicationTest proxy = factory.getProxy();
        IntStream.range(1, 10000).forEach(i -> executorService.execute(() -> proxy.aopBuy(i)));
        TimeUnit.MINUTES.sleep(2);
        log.error("count值为{}", proxy.getCount());
    }

    @ZooLock(key = "buy", timeout = 1, timeUnit = TimeUnit.MINUTES)
    public void aopBuy(int userId) {
        log.info("{} 正在出库...", userId);
        doBuy();
        log.info("{} 扣库存成功...", userId);
    }

    @Test
    public void testManualLock() throws InterruptedException {
        IntStream.range(0, 10000).forEach(i -> executorService.execute(this::manualBuy));
        TimeUnit.MINUTES.sleep(2);
        log.error("count值为{}", count);
    }

    public void manualBuy() {
        String lockPath = "/buy";
        log.info("try to buy sth.");
        try {
            InterProcessMutex lock = new InterProcessMutex(zkClient, lockPath);
            try {
                if (lock.acquire(1, TimeUnit.MINUTES)) {
                    doBuy();
                    log.info("buy successfully!");
                }
            } finally {
                lock.release();
            }
        } catch (Exception e) {
            log.error("zk error");
        }
    }
}

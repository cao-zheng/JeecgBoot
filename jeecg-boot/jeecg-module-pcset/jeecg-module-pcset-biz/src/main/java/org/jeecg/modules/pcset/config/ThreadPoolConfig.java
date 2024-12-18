package org.jeecg.modules.pcset.config;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPoolConfig {
    public static ThreadPoolExecutor Download_Thread_Pool = new ThreadPoolExecutor(
                5, // 核心线程数
                10, // 最大线程数
                60L, // 空闲时间60秒
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(20), // 任务队列长度
            new CustomThreadFactory("downloadThreadPool-"), // 拒绝策略
            new ThreadPoolExecutor.CallerRunsPolicy()
        );
    static class CustomThreadFactory implements ThreadFactory {
        private final AtomicInteger threadNum = new AtomicInteger(1);
        private final String poolName;

        public CustomThreadFactory(String poolName) {
            this.poolName = poolName;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setName(poolName + "-thread-" + threadNum.getAndIncrement());
            return t;
        }
    }
}

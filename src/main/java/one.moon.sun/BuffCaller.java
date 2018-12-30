package one.moon.sun;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * buff method call arguments to a queue and then consume arguments in batch
 */
public class BuffCaller {
    private static Logger log = LoggerFactory.getLogger(BuffCaller.class);
    private BufWorker worker;


    public BuffCaller(long callInterval, int callOnEntryCount, Consumer<List<Object[]>> consumer) {
        if (callInterval <= 0) {
            throw new IllegalArgumentException("callInterval should gt 0");
        }
        if (consumer == null) {
            throw new IllegalArgumentException("consumer can not be null");
        }

        worker = new BufWorker(consumer, callInterval, callOnEntryCount);
        worker.start();
    }

    public void call(Object... args) {
        worker.offer(args);
        //TODO spawn new worker if the current worker is dead
    }

    public long size() {
        return worker.size.get();
    }

    public void close() {
        worker.stop = true;
        worker.interrupt();
    }

    private static class BufWorker extends Thread {

        private ConcurrentLinkedQueue<Object[]> buf = new ConcurrentLinkedQueue<>();
        private long lastCall;
        private boolean stop;
        private int callOnEntryCount;
        private long callInterval;
        private Consumer<List<Object[]>> consumer;

        BufWorker(Consumer<List<Object[]>> consumer, long callInterval, int callOnEntryCount) {
            this.consumer = consumer;
            this.callInterval = callInterval;
            this.callOnEntryCount = callOnEntryCount;

        }

        private AtomicLong size = new AtomicLong(0);

        void offer(Object[] e) {
            buf.offer(e);
            size.incrementAndGet();
        }

        public void run() {
            lastCall = System.currentTimeMillis();

            while (!stop) {

                boolean doCall = false;
                if ((System.currentTimeMillis() - lastCall) >= callInterval && !buf.isEmpty()) {
                    doCall = true;

                } else if (size.get() >= callOnEntryCount) {
                    doCall = true;
                }

                if (!doCall) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        break;
                    }
                    continue;
                }

                try {
                    //thread-safe remove
                    int count = 0;
                    List<Object[]> argsArray = new ArrayList<>();

                    while (count < callOnEntryCount) {
                        Object[] e = buf.poll();
                        if (e == null) break;
                        count++;
                        argsArray.add(e);
                    }

                    size.addAndGet(-count);
                    consumer.accept(argsArray);

                } catch (Exception e) {
                    log.error("accept buf error", e);
                }
                lastCall = System.currentTimeMillis();

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    break;
                }
            }

        }
    }
}

package app.streaming;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageQueue<T> {
    private final BlockingQueue<T> queue = new LinkedBlockingQueue<>();

    public void publish(T msg) throws InterruptedException {
        queue.put(msg);
    }

    public T take() throws InterruptedException {
        return queue.take();
    }
}


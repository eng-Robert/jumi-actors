// Copyright © 2011-2015, Esko Luontola <www.orfjackal.net>
// This software is released under the Apache License 2.0.
// The license text is at http://www.apache.org/licenses/LICENSE-2.0

package fi.jumi.actors.queue;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.util.concurrent.*;

/**
 * Asynchronous unbounded queue for message passing.
 */
@ThreadSafe
public class MessageQueue<T> implements MessageSender<T>, MessageReceiver<T> {

    private final BlockingQueue<T> queue = new LinkedBlockingQueue<>();

    @Override
    public void send(T message) {
        queue.add(message);
    }

    @Override
    public T take() throws InterruptedException {
        return queue.take();
    }

    @Nullable
    @Override
    public T poll() {
        return queue.poll();
    }
}

// Copyright © 2011-2012, Esko Luontola <www.orfjackal.net>
// This software is released under the Apache License 2.0.
// The license text is at http://www.apache.org/licenses/LICENSE-2.0

package fi.jumi.actors;

import org.junit.*;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class MultiThreadedActorsTest extends ActorsContract<MultiThreadedActors> {

    private final List<MultiThreadedActors> createdActorses = new ArrayList<MultiThreadedActors>();

    @Override
    protected MultiThreadedActors newActors(Eventizer<?>... factories) {
        MultiThreadedActors actors = new MultiThreadedActors(factories);
        createdActorses.add(actors);
        return actors;
    }

    @Override
    protected void processEvents() {
        // noop; background threads run automatically, rely on the timeouts in the contract tests for waiting
    }

    @After
    public void shutdown() throws InterruptedException {
        for (MultiThreadedActors actors : createdActorses) {
            actors.shutdown(TIMEOUT);
        }
    }


    // normal event-polling actors

    @Test
    public void target_is_invoked_in_its_own_actor_thread() throws InterruptedException {
        SpyDummyListener rawActor = new SpyDummyListener();

        ActorThread actorThread = actors.startActorThread("ActorName");
        ActorRef<DummyListener> actorRef = actorThread.bindActor(DummyListener.class, rawActor);
        actorRef.tell().onSomething("event");
        awaitEvents(1);

        assertThat(rawActor.thread.getName(), is("ActorName"));
    }


    // shutdown

    @Test
    public void stops_actor_threads_on_shutdown() throws InterruptedException {
        SpyDummyListener rawActor = new SpyDummyListener();
        ActorThread actorThread = actors.startActorThread("ActorName");
        ActorRef<DummyListener> actorRef = actorThread.bindActor(DummyListener.class, rawActor);
        actorRef.tell().onSomething("event");
        awaitEvents(1);

        assertThat("alive before shutdown", rawActor.thread.isAlive(), is(true));
        actors.shutdown(TIMEOUT);

        assertThat("alive after shutdown", rawActor.thread.isAlive(), is(false));
    }
}

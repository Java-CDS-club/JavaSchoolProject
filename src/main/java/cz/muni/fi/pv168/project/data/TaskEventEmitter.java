package cz.muni.fi.pv168.project.data;

import cz.muni.fi.pv168.project.model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

public class TaskEventEmitter {
    public enum Event {
        CREATED,
        DELETED,
    }

    private class Subscriber {
        Event event;
        Consumer<Task> callback;

        public Subscriber(Event event, Consumer<Task> callback) {
            this.event = event;
            this.callback = callback;
        }
    }

    private final List<Subscriber> subscribers = new ArrayList<>();

    public void emit(Task task, Event event) {
        for (var subscriber:
             subscribers) {
            if (subscriber.event == event)
                subscriber.callback.accept(task);
        }
    }

    public void subscribe(Consumer<Task> eventListener, Event event) {
        subscribers.add(new Subscriber(event, eventListener));
    }
}

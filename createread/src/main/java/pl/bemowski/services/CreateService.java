package pl.bemowski.services;

import pl.bemowski.Todo;

import io.vertx.core.Future;

/**
 * Created by Kamil Bemowski on 2017-09-01.
 */
public class CreateService extends BaseService {

    public Future<Long> createTodo(Todo todo) {
        return database.create(todo);
    }
}

package pl.bemowski.services;

import pl.bemowski.Todo;
import io.vertx.codegen.annotations.Nullable;

/**
 * Created by Kamil Bemowski on 2017-09-01.
 */
public class UpdateService extends BaseService {

    public void updateTodo(@Nullable String todoId, Todo todo) {
        database.update(todoId, todo);
    }
}

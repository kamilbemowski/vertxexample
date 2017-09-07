package pl.bemowski.services;

import io.vertx.codegen.annotations.Nullable;

/**
 * Created by Kamil Bemowski on 2017-08-31.
 */
public class DeleteService extends BaseService {


    public void deleteTodo(@Nullable String todoId) {
        database.delete(todoId);
    }
}

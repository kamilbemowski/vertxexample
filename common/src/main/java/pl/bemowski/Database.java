package pl.bemowski;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IdGenerator;

import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Vertx;

/**
 * Created by Kamil Bemowski on 2017-08-31.
 */
public class Database {
    private static Logger LOG = LogManager.getLogger(BaseStarter.class);
    IMap<Long, Todo> data;
    private static Database INSTANCE = new Database();
    private IdGenerator idGenerator;

    private Database() {
        Context context = Vertx.currentContext();
        context.executeBlocking(h -> {
            LOG.info("initializing hazelcast client");
            HazelcastInstance hazelcast = HazelcastClient.newHazelcastClient();
            idGenerator = hazelcast.getIdGenerator("todoId");
            data = hazelcast.getMap("todos");
            data.put(1L, new Todo());
            h.complete();
        }, res -> {
            if (res.succeeded()) {
                LOG.info("initializing hazelcast client has been finished");
            } else {
                LOG.error("Cannot connect to the hazelcast server");
            }
        });
    }

    public static Database instance() {
        return INSTANCE;
    }

    public Todo get(long id) {
        return data.get(id);
    }

    private long getId() {
        return idGenerator.newId();
    }

    public Future<Long> create(Todo todo) {
        Future<Long> future = Future.future();
        long id = getId();
        todo.setId(id);
        ExecutionCallback<Todo> callback = new ExecutionCallback<Todo>() {
            @Override
            public void onResponse(Todo response) {
                future.complete(id);
            }

            @Override
            public void onFailure(Throwable t) {
                future.fail(t);
            }
        };
        data.putAsync(id, todo).andThen(callback);
        return future;
    }

    public void update(@Nullable String todoId, Todo todo) {
        data.replace(Long.valueOf(todoId), todo);
    }

    public void delete(@Nullable String todoId) {
        data.delete(todoId);
    }
}

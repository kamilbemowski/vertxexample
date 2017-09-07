package pl.bemowski;

import pl.bemowski.services.CreateService;
import pl.bemowski.services.ReadService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Created by Kamil Bemowski on 2017-08-28.
 */
public class CreateReadStarter extends BaseStarter {

    private static Logger LOG = LogManager.getLogger(CreateReadStarter.class);
    private ReadService readService;
    private CreateService createService;

    public CreateReadStarter() {
    }

    @Override
    public void start(Future<Void> future) throws Exception {
        super.start(future);
        readService = new ReadService();
        createService = new CreateService();
        router.get(Constants.API_GET).handler(this::handlerGet);
        router.post(Constants.API_CREATE).handler(this::handlerCreate);
    }

    @Override
    protected String port() {
        return "7894";
    }

    private void handlerCreate(RoutingContext routingContext) {
        try {
            Todo todo = objectReader.readValue(routingContext.getBodyAsString());
            createService.createTodo(todo).setHandler(resultHandler(routingContext, res-> {
                if (res != null) {
                    try {
                        routingContext.response()
                                .setStatusCode(201)
                                .putHeader("content-type", "application/json")
                                .end(objectWriter.writeValueAsString(todo));
                    } catch (JsonProcessingException e) {
                        sendError(500, routingContext.response().write(e.getMessage()));
                    }
                } else {
                    serviceUnavailable(routingContext);
                }
            }));
        } catch (IOException e) {
            sendError(500, routingContext.response().write(e.getMessage()));
        }
    }

    private <T> Handler<AsyncResult<T>> resultHandler(RoutingContext context, Consumer<T> consumer) {
        return res -> {
            if (res.succeeded()) {
                consumer.accept(res.result());
            } else {
                serviceUnavailable(context);
            }
        };
    }

    private void handlerGet(RoutingContext routingContext) {
        LOG.info("get request");

        Todo todo = readService.read(Long.valueOf(routingContext.request().getParam("todoId")));
        Optional<String> todoJson;
        try {
            todoJson = Optional.ofNullable(objectWriter.writeValueAsString(todo));
            routingContext.response().putHeader("content-type", "application/json").end(todoJson.orElse("{}"));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() throws Exception {
        LOG.info("stop");
    }
}

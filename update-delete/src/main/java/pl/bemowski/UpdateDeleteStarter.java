package pl.bemowski;

import pl.bemowski.services.DeleteService;
import pl.bemowski.services.UpdateService;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;

import java.io.IOException;

/**
 * Created by Kamil Bemowski on 2017-08-28.
 */
public class UpdateDeleteStarter extends BaseStarter {

    private UpdateService updateService;
    private DeleteService deleteService;

    public UpdateDeleteStarter() {
    }

    @Override
    public void start(Future<Void> future) throws Exception {
        super.start(future);
        updateService = new UpdateService();
        deleteService = new DeleteService();
        router.delete(Constants.API_DELETE).handler(this::deleteHandler);
        router.put(Constants.API_UPDATE).handler(this::updateHandler);
    }

    private void updateHandler(RoutingContext routingContext) {
        try {
            @Nullable
            String todoJson = routingContext.getBodyAsString();
            @Nullable
            String todoId = routingContext.request().getParam("todoId");
            Todo todo = objectReader.readValue(todoJson);
            updateService.updateTodo(todoId, todo);
            routingContext.response().setStatusCode(200).end("ok");
        } catch (IOException e) {
            routingContext.response().setStatusCode(400).end(e.getLocalizedMessage());
        }
    }

    private void deleteHandler(RoutingContext routingContext) {
        @Nullable
        String todoId = routingContext.request().getParam("todoId");
        deleteService.deleteTodo(todoId);
        routingContext.response().setStatusCode(200).end("ok");
    }

    @Override
    protected String port() {
        return "7223";
    }
}

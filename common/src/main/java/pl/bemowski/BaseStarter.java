package pl.bemowski;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.hazelcast.config.Config;
import com.hazelcast.config.ManagementCenterConfig;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Created by Kamil Bemowski on 2017-09-04.
 */
public abstract class BaseStarter extends AbstractVerticle {
    private static Logger LOG = LogManager.getLogger(BaseStarter.class);
    Router router;
    final ObjectWriter objectWriter = new ObjectMapper().writer().forType(Todo.class).with(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS);
    final ObjectReader objectReader = new ObjectMapper().reader().forType(Todo.class);

    @Override
    public void start(Future<Void> future) throws Exception {
        super.start();
        init();
        router = Router.router(vertx);
        Set<String> allowHeaders = new HashSet<>();
        allowHeaders.add("x-requested-with");
        allowHeaders.add("Access-Control-Allow-Origin");
        allowHeaders.add("origin");
        allowHeaders.add("Content-Type");
        allowHeaders.add("accept");
        Set<HttpMethod> allowMethods = new HashSet<>();
        allowMethods.add(HttpMethod.GET);
        allowMethods.add(HttpMethod.POST);
        allowMethods.add(HttpMethod.DELETE);
        allowMethods.add(HttpMethod.PATCH);

        router.route().handler(CorsHandler.create("*").allowedHeaders(allowHeaders).allowedMethods(allowMethods));
        router.route().handler(BodyHandler.create());

        int port = portEnv();
        vertx.createHttpServer().requestHandler(router::accept).listen(port, "localhost", result -> {
            if (result.succeeded()) {
                LOG.info("Server started on port: " + port);
                future.complete();
            } else {
                future.fail("cannot start");
            }
        });
    }

    private int portEnv() {
        Optional<String> port = Optional.ofNullable(System.getProperty("port"));
        return Integer.valueOf(port.orElse(port()));
    }

    protected abstract String port();

    private void init() {
        LOG.info("Hazelcast server initializing");
        Config config = new Config();
        ManagementCenterConfig managementCenterConfig = new ManagementCenterConfig();
        managementCenterConfig.setEnabled(true);
        managementCenterConfig.setUrl("http://localhost:8080/mancenter");
        config.setManagementCenterConfig(managementCenterConfig);
        ClusterManager clusterManager = new HazelcastClusterManager(config);
        VertxOptions vertxOptions = new VertxOptions().setClusterManager(clusterManager);
        Vertx.clusteredVertx(vertxOptions, res -> {
            if (res.failed()) {
                LOG.error("cannot start hazelcast cluster");
            } else {
                LOG.info("Hazelcast started");
            }
        });
    }

    void sendError(int statusCode, HttpServerResponse response) {
        response.setStatusCode(statusCode).end();
    }

    void badRequest(RoutingContext context) {
        context.response().setStatusCode(400).end();
    }

    void notFound(RoutingContext context) {
        context.response().setStatusCode(404).end();
    }

    void serviceUnavailable(RoutingContext context) {
        context.response().setStatusCode(503).end();
    }


    @Override
    public void stop() throws Exception {
        LOG.info("stop");
    }
}

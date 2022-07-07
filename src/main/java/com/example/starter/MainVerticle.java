package com.example.starter;

import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Slf4JLoggerFactory;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.api.service.ServiceResponse;
import io.vertx.ext.web.openapi.RouterBuilder;
import io.vertx.serviceproxy.ServiceBinder;

import java.net.URISyntaxException;
import java.nio.file.Path;

import static io.vertx.core.Future.succeededFuture;

public class MainVerticle extends AbstractVerticle {

  private static final String LOG_DIR_PROPERTY = "LOG_DIR";

  public Future<Router> createEndpointRouter(Vertx vertx) throws URISyntaxException {
    Path contractPath = Path.of(ClassLoader.getSystemClassLoader().getResource("OpenAPI.yaml").toURI());

    return RouterBuilder.create(vertx, contractPath.toString())
      .map(routerBuilder -> routerBuilder.mountServicesFromExtensions().createRouter())
      .onFailure(err -> System.err.println("Error while initialize OpenAPI Endpoint"))
      .onSuccess(router -> System.out.println("Router initialized"));
  }

  @Override public void start(Promise<Void> startPromise) throws Exception {
    InternalLoggerFactory.setDefaultFactory(Slf4JLoggerFactory.INSTANCE);

    ServiceBinder transactionServiceBinder = new ServiceBinder(getVertx());
    transactionServiceBinder.setAddress(WebService.SERVICE_ADDRESS).registerLocal(WebService.class, (context, resultHandler) -> {
      JsonArray array = context.getParams().getJsonObject("query").getJsonArray("foo", new JsonArray());
      ServiceResponse resp = ServiceResponse.completedWithJson(array).setStatusCode(200);
      resultHandler.handle(succeededFuture(resp));
    });

    createEndpointRouter(vertx).map(
        router -> vertx.createHttpServer().requestHandler(router).listen(8080).onSuccess(http -> startPromise.complete()))
      .onFailure(startPromise::fail);
  }
}

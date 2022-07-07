package com.example.starter;

import com.google.common.truth.Truth;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.junit5.Timeout;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import static com.google.common.truth.Truth.assertThat;

@ExtendWith(VertxExtension.class)
public class TestMainVerticle {

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new MainVerticle(), testContext.succeeding(id -> testContext.completeNow()));
  }

  @Test
  @Timeout(value = 10, timeUnit = TimeUnit.HOURS)
  void verticle_deployed(Vertx vertx, VertxTestContext testContext) {
    JsonObject fooBar1 = new JsonObject().put("foo", "bar1");
    JsonObject fooBar2 = new JsonObject().put("foo", "bar2");
    JsonArray array = new JsonArray().add(fooBar1).add(fooBar2);

    WebClientOptions opts = new WebClientOptions().setDefaultHost("localhost").setDefaultPort(8080);
    HttpRequest<Buffer> request = WebClient.create(vertx, opts).request(HttpMethod.GET, "/foobar");
    request.addQueryParam("foo", fooBar1.encode());
    request.addQueryParam("foo", fooBar2.encode());

    request.send().onComplete(testContext.succeeding(resp -> testContext.verify(() -> {
      assertThat(resp.body().toJsonArray()).isEqualTo(array);
      testContext.completeNow();
    })));
  }
}

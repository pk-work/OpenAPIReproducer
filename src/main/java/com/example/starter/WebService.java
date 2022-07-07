package com.example.starter;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.ext.web.api.service.ServiceRequest;
import io.vertx.ext.web.api.service.ServiceResponse;
import io.vertx.ext.web.api.service.WebApiServiceGen;

@WebApiServiceGen
public interface WebService {
  String SERVICE_ADDRESS = "fooService";

  void getFoo(ServiceRequest context, Handler<AsyncResult<ServiceResponse>> resultHandler);
}

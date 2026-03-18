package client.server;

import java.net.http.HttpResponse;

import com.google.gson.Gson;

import datamodel.http.FailureResponse;

public class ServerResponseException extends Exception {

  public final HttpResponse<String> response;

  private ServerResponseException(String message, HttpResponse<String> response) {
    super(message);
    this.response = response;
  }

  public static ServerResponseException fromResponse(HttpResponse<String> response) {
    var err = new Gson().fromJson(response.body(), FailureResponse.class);
    return new ServerResponseException(err.message, response);
  }
}

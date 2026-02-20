package datamodel.http;

import datamodel.AuthData;

public class RegisterResponse {
  public String authToken;
  public String username;

  public RegisterResponse(String authToken, String username) {
    this.authToken = authToken;
    this.username = username;
  }

  public RegisterResponse(AuthData auth) {
    this.authToken = auth.authToken;
    this.username = auth.username;
  }
}

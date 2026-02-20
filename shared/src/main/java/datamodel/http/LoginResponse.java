package datamodel.http;

import datamodel.AuthData;

public class LoginResponse {
  public String authToken;
  public String username;

  public LoginResponse(String authToken, String username) {
    this.authToken = authToken;
    this.username = username;
  }

  public LoginResponse(AuthData auth) {
    this.authToken = auth.authToken;
    this.username = auth.username;
  }
}

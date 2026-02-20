package datamodel.http;

import datamodel.UserData;

public class RegisterRequest implements Request {
  public String username;
  public String password;
  public String email;

  public RegisterRequest() {}

  public RegisterRequest(String username, String password, String email) {
    this.username = username;
    this.password = password;
    this.email = email;
  }

  public UserData userData() {
    return new UserData(username, password, email);
  }

  @Override
  public void validate() throws InvalidRequestException {
    if (username == null) {
      throw new InvalidRequestException("username field is required");
    }

    if (password == null) {
      throw new InvalidRequestException("password field is required");
    }

    if (email == null) {
      throw new InvalidRequestException("email field is required");
    }
  }
}

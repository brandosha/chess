package datamodel;

import java.security.SecureRandom;
import java.util.Base64;

public class AuthData {
  public String authToken;
  public String username;

  public AuthData(String authToken, String username) {
    this.authToken = authToken;
    this.username = username;
  }

  public static AuthData generate(String username) {
    var random = new SecureRandom();
    byte[] bytes = new byte[16];
    random.nextBytes(bytes);

    var encoder = Base64.getEncoder().withoutPadding();
    String token = encoder.encodeToString(bytes);

    return new AuthData(token, username);
  }
}

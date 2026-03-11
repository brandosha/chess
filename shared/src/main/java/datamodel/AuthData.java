package datamodel;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Objects;

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

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 47 * hash + Objects.hashCode(this.authToken);
    hash = 47 * hash + Objects.hashCode(this.username);
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final AuthData other = (AuthData) obj;
    if (!Objects.equals(this.authToken, other.authToken)) {
      return false;
    }
    return Objects.equals(this.username, other.username);
  }
}

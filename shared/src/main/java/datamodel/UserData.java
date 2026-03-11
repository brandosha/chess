package datamodel;

import java.util.Objects;

public class UserData {
  public String username;
  public String password;
  public String email;

  public UserData(String username, String password, String email) {
    this.username = username;
    this.password = password;
    this.email = email;
  }

  @Override
public int hashCode() {
    int hash = 5;
    hash = 89 * hash + Objects.hashCode(this.username);
    hash = 89 * hash + Objects.hashCode(this.password);
    hash = 89 * hash + Objects.hashCode(this.email);
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
    final UserData other = (UserData) obj;
    if (!Objects.equals(this.username, other.username)) {
      return false;
    }
    if (!Objects.equals(this.password, other.password)) {
      return false;
    }
    return Objects.equals(this.email, other.email);
  }
}

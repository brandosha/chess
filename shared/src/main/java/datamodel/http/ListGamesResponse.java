package datamodel.http;

import java.util.Collection;

import datamodel.GameData;

public class ListGamesResponse {
  public Collection<GameData> games;

  public ListGamesResponse(Collection<GameData> games) {
    this.games = games;
  }
}

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import chess.ChessBoard;
import passoff.chess.TestUtilities;

public class BoardStringTests {
  @Test
  void defaultBoardToString() {
    ChessBoard board = TestUtilities.defaultBoard();
    String expected = """
                |r|n|b|q|k|b|n|r|
                |p|p|p|p|p|p|p|p|
                | | | | | | | | |
                | | | | | | | | |
                | | | | | | | | |
                | | | | | | | | |
                |P|P|P|P|P|P|P|P|
                |R|N|B|Q|K|B|N|R|
                """;
    
    assertEquals(expected.trim(), board.toString());
  }
}

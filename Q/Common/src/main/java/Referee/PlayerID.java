package Referee;

/**
 * Represents the pairing of a Player's name, and their Object identifier.
 */
public record PlayerID(Object id, String name) {
  public PlayerID(String name) {
    this(new Object(), name);
  }
}

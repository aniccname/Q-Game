package Action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Map.Coord;
import Map.Tile.ITile;
import Referee.IGameState;
import Referee.Visitor.IVisitor;

/**
 * A PlaceAction is a proposal to place a tile at a coordinate.
 */
public class PlaceAction implements IAction {
  private final List<Map.Entry<Coord, ITile>> placements;

  /**
   * Constructor to create proposal action to place tile at coordinate.
   *
   * @param placements the pairings of coordinates and tiles to place
   */
  public PlaceAction(List<Map.Entry<Coord, ITile>> placements) {
    if (placements == null) {
      throw new IllegalArgumentException("Placements cannot be null");
    }
    this.placements = new ArrayList<>(placements);
  }

  /**
   * Allows the visitor to specifically visit the place action by seeing the game state. This
   * action will mutate the game state if the visitor specifies that the action is valid.
   *
   * @param additionalArg additional argument to pass to the visitor
   * @param visitor entity being allowed in
   * @return <R> the return type of the Visitor
   * @param <R> of type R that specifies what the visitor returns when visiting
   */
  public <T, R> R accept(IVisitor<T, R> visitor, T additionalArg) {
    return visitor.visitPlace(this, additionalArg);
  }


  /**
   * Gets the tiles this move proposes to place and the coordinates to place them at.
   *
   * @return proposed tiles to place
   */
  public List<Map.Entry<Coord, ITile>> getPlacements() {
    return new ArrayList<>(placements);
  }
}

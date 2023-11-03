- Changing the bonus points again and allow more players to participate in a game
	Difficulty: 1. Bonus point values are stored as constants in the GameState class. Changing the point values again would simply require changing these constants. We do not impose a restriction on the number of players in the game, so allowing more players to participate would only require passing those players to the Referee.
- Adding wildcard tiles
	Difficulty: 2. We would need to add a new class for a wildcard tile, and update the validNeighbor method in the Tile class to allow placing by wildcard tiles.
- Imposing restrictions that enforce the rules of Qwirkle instead of Q
	Difficulty: 5. We would need to completely rewrite the checkNeighboringTiles method. Currently, it relies on calling validNeighbor in each of the four directions. Under the rules of Qwirkle, we would need a more complex validation method to confirm the entire line is valid.

The games server will make available a TCP port that potential players can connect to. To sign up for a game, a player sends a JSON string to this port consisting of their name. The server may then include them in an upcoming game.

When the server starts a game with remote players, it will create a proxy player implementation that will communicate with the remote players in the game.

When the referee calls a method on the proxy player (other than `name()`, which is already known), the proxy player will send the corresponding `JMethod` value to the remote player. For most methods, no response is required from the remote player.

For the `takeTurn` method, the remote player is expected to respond with a `JTurn` in a timely manner. If the remote player fails to do so, they are eliminated from this game.

Once a player has been eliminated from a game, or the game they are playing in has ended, they can sign up for a new game in the same manner as anyone else.

A `JMethod` is a union datatype; all specific instances will be subtypes of the following schema of an object with two fields:
```json
{ "method" : String,
  "args"   : [Any, ..., Any]
}
```
A `JMethod` is one of:
```json
{ "method" : "setup",
  "args"   : [JMap, [JTile, ..., JTile]]
}
```
```json
{ "method" : "takeTurn",
  "args"   : [JPub]
}
```
```json
{ "method" : "newTiles",
  "args"   : [[JTile, ..., JTile]]
}
```
```json
{ "method" : "win",
  "args"   : [Boolean]
}
```
A `JTurn` is one of: `"pass"`, `"replace"`, or a non-empty list of `1Placement`
## Protocol ##
This document is the specification of the protocol for messages between the client and server, based off of the specifications 
from CS4500 (with some minor alterations to make the communications more useful). 

#### ERRORS ####
Errors are specified with the JSON form of 
`["ERROR", message]` where  
  - **message** is the contents of the error message. 

### Server messages ###
Server requests are styled as remote method calls, sent with the form of `[method, [args... ]]`,
 where method is the name of the method being called, and args... the variable number of arguments being called. All methods called expect a return value. If no return value is sent back in a timely manner, the client is disconnected from the game. 
The server expects each request to be answered with an expected return value, specified below. 
  - setup(<a href=#jpub> JPub </a>, <a href=#jtile> JTile...</a>) -> "void"
  - take-turn(<a href=#jpub> JPub </a>) -> <a href=#jpub>JChoice</a>
  - new-tiles(<a href=#jtile> JTile...) -> "void"
  - win(boolean) -> "void"
  - watch-turn(<a href=#jpub>) -> "void"

### Data encodings ###
<h6 id=color>Color</h6>
One of:
    - "red"
    - "green"
    - "blue"
    - "yelow"
    - "orange"
    - "purple"
<h6 id =shape>Shape</h6>
One of:
    - "star"
    - "8star"
    - "square"
    - "circle"
    - "clover"
    - "diamond"
<h6 id=jtile>JTile</h6>
Is a {"color": <a href=#color>Color</a>, "shape": <a href=#shape>Shape</a>}
<h6 id=jcoordinate>JCoordinate</h6>
Is a {"row": Integer, "column": Integer}
<h6 id=jcell>JCell</h6>
Is a [Integer, <a href=#jtile> JTile </a>] representing a JTile at the specified column. Positive values go in the rightward direction, negative values go in the leftward direction.
<h6 id=jrow>JRow</h6>
Is a [Integer, JCell, ...] representing one non-empty row of the game board with 1 or more JCells. Positive values go in the downward direction, negative values go in the upward direction. 
<h6 id=jmap>JMap</h6>
Is a [JRow...] representing the complete game board with a non empty sequence of JMaps. 
<h6 id=jplayer>JPlayer</h6>
Is a {"score": PosInteger, "name": String, "tile*" <a href=#jtile> JTile... </a>} representing the player's score, name, and the player's 0 or more tiles. 
<h6 id=jopponent>JOpponent</h6>
Is a {"name": String, "tile*": PosInteger, "score": Integer} representing an opposing player's score, name, and number of tiles.
<h6 id=jpub>JPub</h6>
Is a {"map": <a href=#jmap> JMap </a>, "tile*": PosInteger, "players": [<a href=#jplayer>JPlayer</a>, <a href=#jopponent>JOpponent</a>, ...]}
<h6 id=1placement>1Placement</a>
Is a {"coordinate": <a href=#jcoordinate> JCoordinate </a>, "1tile": <a href=#jtile> JTile </a>} representing a tile to place at the given coordinate. 
<h6 id=jplacements>JPlacements</a>
Is a [<a href=#1placement> 1Placement </a>, ...] representing the placement of one or more tiles by the active player. 
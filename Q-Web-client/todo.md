### Q Game web client TODO ###
  * Is there duplicate information with activePlayerIndx and isTurn? I don't think there actually is, considering that the size of the players can change over the course of the game.

  * new-tiles and watch-turn are not changing the tiles that are being displayed (when clicking replace, even when the new-tiles method is sent, the new tiles are not showing up). 
    * I have no idea how to fix this. I need ot ask about this. 
    * Is it maybe that it's at the same position in the tree?
      * Was able to create a hack by having new tiles become a differnet node in the tree by just being the string "new-tiles". Need to connect with someone else who knows react to figure out how to fix this.
  
  * Figure out how to make it look good
    * graphics?
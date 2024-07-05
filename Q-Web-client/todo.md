### Q Game web client TODO ###
  * Consider splitting off Info into it's own self contained file?
  * Consider using a click away listener for the Paper Prop in Info so that you don't have to click the close button to close out.
    * This becomes a bit of a mess due to the click away listener also covering the opening and closing button. 
  * Make the QGameWebsocketServerMain program take in a proper config file (and create a config file myself), or add extra parameters.
    * Added extra parameters since I don't see the value of creating a config from scratch when we only really care about 4 different parameters. 

  * new-tiles and watch-turn are not changing the tiles that are being displayed (when clicking replace, even when the new-tiles method is sent, the new tiles are not showing up). 
    * I have no idea how to fix this. I need ot ask about this. 
    * Is it maybe that it's at the same position in the tree?
      * Was able to create a hack by having new tiles become a differnet node in the tree by just being the string "new-tiles". Need to connect with someone else who knows react to figure out how to fix this.

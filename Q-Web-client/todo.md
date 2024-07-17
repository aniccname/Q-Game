### Q Game web client TODO ###
  * Improve the wording and titles of the TabList in Info.
  * Figure out if the current manual opening and closing is clear to users.
  * On mobile, Game is really zoomed out. How can I make it so that this is better scaled to the appropriate screen size. 
    * It's something to do with teh stack, but I'm having an issue keeping the undo button the same height of the vertical stack of the board and player tiles. Ideally I'd like in mobile to have the undo as wide as the board/player tile container as well, or have it be the same width of the above component's if it's vertical.
  * Actually get SLF4J logging working...
  * Allow the IP address to be a console parameter (maybe this should be an optional parameter? Where if it's not supplied it's on local host?)
    * Actually that's just kind of annoying since that would only work for testing.
    * Probably allow for adjusting min and max players (min must be greater than 2)
    * And while we're add it add AI? Probably make it ldag. Maybe a boolean flag that determines whether to fill up the remaining slots with AI?


### Known Issues
  * new-tiles and watch-turn are not changing the tiles that are being displayed (when clicking replace, even when the new-tiles method is sent, the new tiles are not showing up). 
    * I have no idea how to fix this. I need ot ask about this. 
    * Is it maybe that it's at the same position in the tree?
      * Was able to create a hack by having new tiles become a differnet node in the tree by just being the string "new-tiles". Need to connect with someone else who knows react to figure out how to fix this.
      * Look into considering if it can be fixed using effects. 

### Consideration
  * See if I can make the background of StartPage into a button that sets info to false? Is that even possible since they are in separate files?
  * Change the Server to deal with name timeouts in a better way.
    * Maybe try passing in a Timer that will remove the player from the server's list of connections, and have the first name that is sent cancel that timer. 

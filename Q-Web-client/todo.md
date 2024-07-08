### Q Game web client TODO ###
  * Improve the wording and titles of the TabList in Info.
  * Figure out if the current manual opening and closing is clear to users.


### Known Issues
  * new-tiles and watch-turn are not changing the tiles that are being displayed (when clicking replace, even when the new-tiles method is sent, the new tiles are not showing up). 
    * I have no idea how to fix this. I need ot ask about this. 
    * Is it maybe that it's at the same position in the tree?
      * Was able to create a hack by having new tiles become a differnet node in the tree by just being the string "new-tiles". Need to connect with someone else who knows react to figure out how to fix this.

### Consideration
  * See if I can make the background of StartPage into a button that sets info to false? Is that even possible since they are in separate files?

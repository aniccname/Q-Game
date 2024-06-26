### Q Game web client TODO ###
  * For some reason the order of the player states isn't changing. It's staying constant. What should be done about this? Should the bold player be changed to the active player in the round, and the list shows the round order?

#### Nice to haves ####
  * Change *everything* to allow for duplicate names. This would mean new
    * PlayerNameAdapter in Referee.java
    * That's basically it.
  * Need a way to share a unique identifier between Player and PlayerInfo
  * Add an extra field called id() that's an Object that servers as the Object's identifier. Create a record that's PlayerID that is just the name of the player and the id() field. PlayerState will get created with this id inside of gamestate. 
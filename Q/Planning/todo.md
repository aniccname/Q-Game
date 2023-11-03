
# Completed

- refactor ActionScorer to not require access to the entire gamestate, just the map  
	[Refactor ActionScorer to only require an IMap, not an IGameState](https://github.khoury.northeastern.edu/CS4500-F23/spooky-wolves/commit/d20ce40efeae9a243f781bcaedd273562ccafc30)
- factor out point values as parameters  
	[Use parameters for bonus point values](https://github.khoury.northeastern.edu/CS4500-F23/spooky-wolves/commit/af73bd5542c784cb269e7fbc44f76246839bf4cb)
- refactor PlayerOrder so that currentPlayer is not necessary  
	[simplify player order and remove redundant fields in game state](https://github.khoury.northeastern.edu/CS4500-F23/spooky-wolves/commit/7603f6e85cbd392e7731425c54250e1a2e10eb0e)
- reconsider constructors of GameState  
	[Change testing constructor of gamestate to be a constructor to create a game from a starting position](https://github.khoury.northeastern.edu/CS4500-F23/spooky-wolves/commit/01994a97df00fcfc55d508a7e2f8cf570fda80b4)
- centralize serialization logic into one package  
	[Moved serialization logic to new package](https://github.khoury.northeastern.edu/CS4500-F23/spooky-wolves/commit/14f8558cfc7bafd748c7b8b8c56ae4a4ccd24c93)
- move game-end logic to referee  
	[Move game-end logic to referee](https://github.khoury.northeastern.edu/CS4500-F23/spooky-wolves/commit/c4f43228a0ae4e9d5adbbc5b6a45fc1da09dc94d) 
- added additional safety checking functionality to referee-player interaction  
	[player safety checks](https://github.khoury.northeastern.edu/CS4500-F23/spooky-wolves/commit/f318f637eb7db44848b2cf8eb8c8604d0a481ab0) 
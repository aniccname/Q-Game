TO: Q Game CEOs

FROM: Smit Patel and Matt Goldgirsh

DATE: 09/13/2023

SUBJECT: Planned Sprints

Sprints:
1. Develop the game state. Get started on the player, referee, and game pieces.
The state should be able to create the board with tiles, execute moves, place new
tiles, and get remaining tiles. Each game piece needs to be modelled with their
attributes; e.g. tiles should have their color and shape, the board has dimensions.
Observers will reveal game state. A player will have information such as name, age
and ID. A referee will be able to detect infractions and punish offending players.
2. Completing the first sprint will provide foundation to allow this sprint to be 
progressed. Complete the referee's behavior. Have the game set up to start playing. 
Enable all game rules and actions a user may take. Scoring for corresponding 
actions. End turns when appropriate. Detect game over conditions and winners.
3. Completing the second step will finalize a single game, and will enable the 
management of multiple games with remote communication features. Manage multiple 
games. Allow players to register and track relevant 
information such as age. Initialize communication for remote play. Implement
match-making. Generate and process JSON messages for game state, user registration,
and other player information. Receive and respond to JSON messages. Verify
communication over TCP.
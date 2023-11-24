The commit we tagged for your submission is 51095d98f64051fcab9c137126a885f07e4a66c9.
**If you use GitHub permalinks, they must refer to this commit or your self-eval will be rejected.**
Navigate to the URL below to create permalinks and check that the commit hash in the final permalink URL is correct:

https://github.khoury.northeastern.edu/CS4500-F23/plucky-monkeys/tree/51095d98f64051fcab9c137126a885f07e4a66c9

## Self-Evaluation Form for Milestone 9

Indicate below each bullet which file/unit takes care of each task.

For `Q/Server/player`,

- explain how it implements the exact same interface as `Q/Player/player`   
https://github.khoury.northeastern.edu/CS4500-F23/plucky-monkeys/blob/51095d98f64051fcab9c137126a885f07e4a66c9/Q/Common/src/main/java/Networking/ProxyPlayer.java#L28 Our Proxy player implements a common interface
with our AI player, the IPlayer interface.
- explain how it receives the TCP connection that enables it to communicate with a client
https://github.khoury.northeastern.edu/CS4500-F23/plucky-monkeys/blob/51095d98f64051fcab9c137126a885f07e4a66c9/Q/Common/src/main/java/Networking/ProxyPlayer.java#L29-L35. The proxy player stores the name of the
player and a client that connects to the remote client.   
https://github.khoury.northeastern.edu/CS4500-F23/plucky-monkeys/blob/51095d98f64051fcab9c137126a885f07e4a66c9/Q/Common/src/main/java/Networking/ProxyPlayer.java#L98-L107 For each method call, the invoke method 
is called which sends the JSON message to the player and returns the player's response. 
- point to unit tests that check whether it writes (proper) JSON to a mock output device
https://github.khoury.northeastern.edu/CS4500-F23/plucky-monkeys/blob/51095d98f64051fcab9c137126a885f07e4a66c9/Q/Common/src/test/java/Networking/ProxyPlayerTest.java#L150-L162 One test for brevity's sake. The rest of the tests are in the same file. 

For `Q/Client/referee`,

- explain how it implements the same interface as `Q/Referee/referee`
https://github.khoury.northeastern.edu/CS4500-F23/plucky-monkeys/blob/51095d98f64051fcab9c137126a885f07e4a66c9/Q/Common/src/main/java/Networking/ProxyReferee.java#L40 Both have the functionality to play a game. 
Since the proxy referee and the Server side referee interact with a different number of players an interface is not used. 
- explain how it receives the TCP connection that enables it to communicate with a server
https://github.khoury.northeastern.edu/CS4500-F23/plucky-monkeys/blob/51095d98f64051fcab9c137126a885f07e4a66c9/Q/Common/src/main/java/Networking/ProxyReferee.java#L41-L42 The playGame method is passed in a socket
connected to the server. An input and output stream are then generated from the socket for all reading and writing. 
- point to unit tests that check whether it reads (possibly broken) JSON from a mock input device
https://github.khoury.northeastern.edu/CS4500-F23/plucky-monkeys/blob/51095d98f64051fcab9c137126a885f07e4a66c9/Q/Common/src/test/java/Networking/ProxyRefereeTest.java#L135-L147 One test for brevity's sake. The 
rest of the tests are in the same file. 

For `Q/Client/client`, explain what happens when the client is started _before_ the server is up and running:

- does it wait until the server is up (best solution)
- does it shut down gracefully (acceptable now, but switch to the first option for 10)

For `Q/Server/server`, explain how the code implements the two waiting periods. 

The ideal feedback for each of these three points is a GitHub
perma-link to the range of lines in a specific file or a collection of
files.

A lesser alternative is to specify paths to files and, if files are
longer than a laptop screen, positions within files are appropriate
responses.

You may wish to add a sentence that explains how you think the
specified code snippets answer the request.

If you did *not* realize these pieces of functionality, say so.


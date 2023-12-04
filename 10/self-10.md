The commit we tagged for your submission is a69f093d84fc6d3588ad4b551bb2c81336e6e596.
**If you use GitHub permalinks, they must refer to this commit or your self-eval will be rejected.**
Navigate to the URL below to create permalinks and check that the commit hash in the final permalink URL is correct:

https://github.khoury.northeastern.edu/CS4500-F23/plucky-monkeys/tree/a69f093d84fc6d3588ad4b551bb2c81336e6e596

## Self-Evaluation Form for Milestone 10

Indicate below each bullet which file/unit takes care of each task.

The data representation of configurations clearly needs the following
pieces of functionality. Explain how your chosen data representation 

- implements creation within programs _and_ from JSON specs 

  Each config class has an associated builder class that can be used to create the config class within code. For example, here is the builder for the ServerConfig and its associated build method:

  https://github.khoury.northeastern.edu/CS4500-F23/plucky-monkeys/blob/a69f093d84fc6d3588ad4b551bb2c81336e6e596/Q/Common/src/main/java/Config/ServerConfig.java#L11-L29

  To create the config class from JSON, we use an intermediate class that our JSON library can directly unmarshal the JSON into. This class then has a convert() method that converts it to our config class. Here is the intermediate class for ServerConfig:

  https://github.khoury.northeastern.edu/CS4500-F23/plucky-monkeys/blob/a69f093d84fc6d3588ad4b551bb2c81336e6e596/Q/Common/src/main/java/Serialization/ServerConfig.java#L5-L32
  
  And here is an example of using Gson to unmarshal it:
  
  https://github.khoury.northeastern.edu/CS4500-F23/plucky-monkeys/blob/a69f093d84fc6d3588ad4b551bb2c81336e6e596/Q/Common/xserver/XServer.java#L28-L30

- enforces that each configuration specifies a fixed set of properties (no more, no less)

  The constructor of each config object require arguments corresponding to the properties. For example, here is the constructor for the ServerConfig:

  https://github.khoury.northeastern.edu/CS4500-F23/plucky-monkeys/blob/a69f093d84fc6d3588ad4b551bb2c81336e6e596/Q/Common/src/main/java/Config/ServerConfig.java#L3-L10

- supports the retrieval of properties 

  Because our configuration classes are Java records, all fields have corresponding public getter methods. Thus, retrieving a property is calling the corresponding method. Here's an example of accessing a property:

  https://github.khoury.northeastern.edu/CS4500-F23/plucky-monkeys/blob/a69f093d84fc6d3588ad4b551bb2c81336e6e596/Q/Common/src/main/java/Networking/Server.java#L63

- sets properties (what happens when the property shouldn't exist?) 

  Because our configuration classes are Java records, all fields are final. Thus, properties can only be set at creation through the constructor, or by using the builder. The constructor takes a specified number of arguments, and the builder provides only certain methods. Thus, it is impossible to set a property that shouldn't exist.
  
  Here is an example of providing properties when constructing a configuration class:
  
  https://github.khoury.northeastern.edu/CS4500-F23/plucky-monkeys/blob/a69f093d84fc6d3588ad4b551bb2c81336e6e596/Q/Common/src/main/java/Serialization/ServerConfig.java#L24-L31

- unit tests for these pieces of functionality

  We did not have unit tests for our configuration classes.

Explain how the server, referee, and scoring functionalities are abstracted
over their respective configurations.

The Server class takes in a ServerConfig in its constructor, or creates it from the defaults if not provided:

https://github.khoury.northeastern.edu/CS4500-F23/plucky-monkeys/blob/a69f093d84fc6d3588ad4b551bb2c81336e6e596/Q/Common/src/main/java/Networking/Server.java#L45-L52

The referee requires a RefereeConfig to be provided to its playGame method:

https://github.khoury.northeastern.edu/CS4500-F23/plucky-monkeys/blob/a69f093d84fc6d3588ad4b551bb2c81336e6e596/Q/Common/src/main/java/Referee/IReferee.java#L17

The ActionScorer takes in a ScoringConfig in its constructor:

https://github.khoury.northeastern.edu/CS4500-F23/plucky-monkeys/blob/a69f093d84fc6d3588ad4b551bb2c81336e6e596/Q/Common/src/main/java/Referee/Visitor/ActionScorer.java#L28-L31

Does the server touch the referee or scoring configuration, other than
passing it on?

No, the server simply retrieves the RefereeConfig to pass it to the referee:

https://github.khoury.northeastern.edu/CS4500-F23/plucky-monkeys/blob/a69f093d84fc6d3588ad4b551bb2c81336e6e596/Q/Common/src/main/java/Networking/Server.java#L69

Does the referee touch the scoring configuration, other than passing
it on?

The ScoringConfig is encapsulated within the GameState class and not visible to the referee.
The only time the referee touches a ScoringConfig is if no gamestate is provided in the RefereeConfig
and the referee must create a default one, in which case it creates the default scoring config:

https://github.khoury.northeastern.edu/CS4500-F23/plucky-monkeys/blob/a69f093d84fc6d3588ad4b551bb2c81336e6e596/Q/Common/src/main/java/Referee/Referee.java#L128-L130


The ideal feedback for each of these three points is a GitHub
perma-link to the range of lines in a specific file or a collection of
files.

A lesser alternative is to specify paths to files and, if files are
longer than a laptop screen, positions within files are appropriate
responses.

You may wish to add a sentence that explains how you think the
specified code snippets answer the request.

If you did *not* realize these pieces of functionality, say so.


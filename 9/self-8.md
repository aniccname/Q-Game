The commit we tagged for your submission is 3d10fd2be7483e2d0276f46b04a5f67b0bc72cf6.
**If you use GitHub permalinks, they must refer to this commit or your self-eval will be rejected.**
Navigate to the URL below to create permalinks and check that the commit hash in the final permalink URL is correct:

https://github.khoury.northeastern.edu/CS4500-F23/plucky-monkeys/tree/3d10fd2be7483e2d0276f46b04a5f67b0bc72cf6

## Self-Evaluation Form for Milestone 8

Indicate below each bullet which file/unit takes care of each task:

- concerning the modifications to the referee: 

  - is the referee programmed to the observer's interface
    or is it hardwired?  
    Programmed to IObserver interface.
https://github.khoury.northeastern.edu/CS4500-F23/plucky-monkeys/blob/3d10fd2be7483e2d0276f46b04a5f67b0bc72cf6/Q/Common/src/main/java/Referee/Referee.java#L65
https://github.khoury.northeastern.edu/CS4500-F23/plucky-monkeys/blob/3d10fd2be7483e2d0276f46b04a5f67b0bc72cf6/Q/Common/src/main/java/Observer/IObserver.java#L7-L21
  - if an observer is desired, is every state per player turn sent to
    the observer? Where?  
    Yes. After every turn is taken in a round, the observers are updated
    https://github.khoury.northeastern.edu/CS4500-F23/plucky-monkeys/blob/3d10fd2be7483e2d0276f46b04a5f67b0bc72cf6/Q/Common/src/main/java/Referee/Referee.java#L106-L117
    https://github.khoury.northeastern.edu/CS4500-F23/plucky-monkeys/blob/3d10fd2be7483e2d0276f46b04a5f67b0bc72cf6/Q/Common/src/main/java/Referee/Referee.java#L119-L128

  - if an observer is not desired, how does the referee avoid calls to
    the observer?  
    If no IObserver is desired, the passed in list of IObserver will be empty, and as such observers.foreach() will not run. 

- concerning the implementation of the observer:

  - does the purpose statement explain how to program to the
    observer's interface?   
    The interface covers the information about each new state, and that the referee is required to inform the observer that the game is over.
    https://github.khoury.northeastern.edu/CS4500-F23/plucky-monkeys/blob/3d10fd2be7483e2d0276f46b04a5f67b0bc72cf6/Q/Common/src/main/java/Observer/IObserver.java#L7-L21

  - does the purpose statement explain how a user would use the
    observer's view? Or is it explained elsewhere?  
    Our observer only states that this observer saves the observed game. The GUI the observer generates has self-explanatory buttons which 
    save the game, and change the observed state to the next or previous state (if one exists). 
    https://github.khoury.northeastern.edu/CS4500-F23/plucky-monkeys/blob/3d10fd2be7483e2d0276f46b04a5f67b0bc72cf6/Q/Common/src/main/java/Observer/Observer.java#L16-L19

The ideal feedback for each of these three points is a GitHub
perma-link to the range of lines in a specific file or a collection of
files.

A lesser alternative is to specify paths to files and, if files are
longer than a laptop screen, positions within files are appropriate
responses.

You may wish to add a sentence that explains how you think the
specified code snippets answer the request.

If you did *not* realize these pieces of functionality, say so.


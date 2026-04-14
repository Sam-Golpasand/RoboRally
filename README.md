# Group 16


### Implementation considerations

We have had a number of considerations when it comes to how the functionality should be. We can start with the movement cards, which we started the project with. To start, we did every function very imperatively, but then we realised that some movement cards can be boiled down to a combination of the same functions. For instance, `fast forward` is just 2 `forwards`, and one that we thought was really clever was `back`, since we can do a `U-turn`, `forward`, and then another ` U-turn`, where `U-turn` is ALSO a combination of 2 `right` functions. Programming is a really beautiful thing at times 🥺.

As for the `BoardFactory`, a subtle but yet quite interesting design choice, in my opinion, was moving the board creation into the `buildSimpleBoard()` instead of making a board and then passing that board to make it a simple/advanced board. One can argue that this is less configurable/dynamic since we can't have a simple board of size 16x16 or any arbitrary size, but i think that this solution is a lot cleaner. A simple switch statement for deciding which board and each board creation function handling the rest. You could easily implement this configurability back by passing in a size argument or similar.

The checkpoints aswell could be implemented differently. Another way of handling if a specific checkpoint is the last would be to make a function that takes all the checkpoints in a board and returns the checkpoint with the highest ID. This would both be more elegant and safer since, if you were adding another checkpoint to a board, you couldnt accidentally make checkpoint 2 a final checkpoint when there are 3 in total (Don't ask me how i thought of that example). But ultimately, we decided on the simple option and just adding a flag to say if this is the last checkpoint.

For the pushing of other players function, we specifically used recursion to make sure that even if we have a line of 50 players, all 50 players will be pushed (if there isn't something blocking of course).

And as for activating the field actions, we loop through all the players space to check if their currently occupying space is a conveyor belt or checkpoint and then performing their respective action.

### Things we a unsure of

There was a comment (deleted now) in the `executeCommand()` function that mentioned that the card handler could be implemented more elegantly. We couldn't really think of anything other than what was already there, so we just chalked it up to be a personal preference thing since the functionality would ultimately be the same.

Also, there is an `update()` function in `appController.java`. We are not sure what it should do tbh. It just ominously says "do nothing for now". In general, there are a lot of //XXX comments we didn't really know what to do with.

### Testing

We have reached 100% coverage of the `doAction()` methods in `ConveyorBelt.java` and `Checkpoint.java`. If you disregard the two assert false statements (they should never be reached) in `GameController.java`, we also reached 100% test coverage for `GameController.java`.

### Final notes

As you can read, i have been very liberal/personal with my writing in this README. This is mostly because i feel that personal vibe in the classroom, so i hope that translates to README's too. I hope that is fine for future projects; otherwise, i'll stop doing it lol.

Also, if you see any weird behaviour, know that it is intended functionality and not a bug. Let the record state that there is no such thing as bugs in our code.   
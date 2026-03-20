package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Space;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a checkpoint in the game, which extends the behavior of {@link FieldAction}.
 * A checkpoint is associated with a specific number, which can be used to identify it.
 * The class provides methods to retrieve and update this checkpoint number.
 */
public class CheckPoint extends FieldAction{

    /**
     * Each checkpoint should have a unique number to distinguish them in the game
     */
    private int number;

    /**
     * Retrieves the number associated with this checkpoint.
     *
     * @return the checkpoint number
     */
    public int getNumber() {
        return number;
    }

    /**
     * Sets the number associated with this checkpoint.
     *
     * @param number the new value to set for the checkpoint number
     */
    public void setNumber(int number) {
        this.number = number;
    }

    /**
     * Implementation of the action of a checkpoint.
     */
    @Override
    public boolean doAction(@NotNull GameController gameController, @NotNull Space space) {
        // TODO A6d: needs to be implemented
        // ...

        return false;
    }
}

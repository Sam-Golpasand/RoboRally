package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;

/**
 * This class is for the checkpoint system for the board
 * 
 * @author Sam Golpasand
 */
public class Checkpoint extends FieldAction {

    private int id;

    private boolean isLastCheckpoint = false;

    public int getId() {
        return id;
    }  

    public void setId(int id) {
        this.id = id;
    }

    public boolean getIsLastCheckpoint() {
        return isLastCheckpoint;
    }

    public void setIsLastCheckpoint(boolean lastCheckpoint) {
        isLastCheckpoint = lastCheckpoint;
    }

    /**
     * Executes the action associated with a specific checkpoint in the game.
     * This method checks whether the player has reached the next checkpoint in sequence
     * and updates the player's checkpoint count accordingly.
     *
     * @param gameController the controller managing the game logic and state
     * @param space the current space on the board where the action is being executed
     * @return true if the checkpoint count was successfully updated, false otherwise
     * @author Magnus Dragheim
     */
    @Override
    public boolean doAction(GameController gameController, Space space) {
        Player player = space.getPlayer();

        if (id == player.getCheckpointCount() + 1) {
            player.setCheckpointCount(player.getCheckpointCount() + 1);
            if (isLastCheckpoint) {
                player.setHasWon(true);
            }
            return true;
        }
        
        return false;
    }
}

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

    public int getId() {
        return id;
    }  

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean doAction(GameController gameController, Space space) {
        
        Player player = space.getPlayer();

        if (player == null) {
            return false;
        }

        // Check if the checkpoint reached is the one before this checkpoint.
        if (player.getCheckpointsReached() + 1 == this.id) {
            player.setCheckpointsReached(id);
        } else {
            return false;
        }
        
        return true;
    }
}

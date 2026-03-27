package dk.dtu.compute.se.pisd.roborally.controller;

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
        // TODO Auto-generated method stub
        if (id == space.getPlayer().getCheckpointCount() + 1) {
            space.getPlayer().setCheckpointCount(space.getPlayer().getCheckpointCount() + 1);
            return true;
        }
        return false;
    }
}

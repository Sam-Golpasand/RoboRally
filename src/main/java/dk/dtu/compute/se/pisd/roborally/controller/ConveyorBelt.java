/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import org.jetbrains.annotations.NotNull;

/**
 * This class represents a conveyor belt on a space.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */

public class ConveyorBelt extends FieldAction {

    private Heading heading;

    public Heading getHeading() {
        return heading;
    }

    public void setHeading(Heading heading) {
        this.heading = heading;
    }

    /**
     * Executes an action on the specified space using the provided game controller.
     * The action involves moving a player from the current space to a target space
     * determined by the conveyor belt's heading, if the target space is valid.
     *
     * @param gameController the game controller managing the game state
     * @param space the current space where the action is initiated
     * @return true if the player is successfully moved to the target space, false otherwise
     * @author Magnus Dragheim
     */
    @Override
    public boolean doAction(@NotNull GameController gameController, @NotNull Space space) {
        Space targetSpace = gameController.board.getNeighbour(space, heading);

        if (targetSpace == null || targetSpace.getPlayer() != null) {
            return false;
        }
        
        space.getPlayer().setSpace(targetSpace);
        
        return true;
    }

}

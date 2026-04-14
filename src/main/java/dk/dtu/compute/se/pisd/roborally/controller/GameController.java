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

import dk.dtu.compute.se.pisd.roborally.model.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class GameController {

    final public Board board;

    /**
     * Creates a controller for the given board instance.
     *
     * @param board the board whose game flow is managed by this controller
     */
    public GameController(@NotNull Board board) {
        this.board = board;
    }


    /**
     * Starts a new programming phase.
     * Resets the phase, active player, and register step, clears all programmed
     * registers, and deals new random command cards to each player.
     */
    public void startProgrammingPhase() {
        board.setPhase(Phase.PROGRAMMING);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);

        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            if (player != null) {
                for (int j = 0; j < Player.NO_REGISTERS; j++) {
                    CommandCardField field = player.getProgramField(j);
                    field.setCard(null);
                    field.setVisible(true);
                }
                for (int j = 0; j < Player.NO_CARDS; j++) {
                    CommandCardField field = player.getCardField(j);
                    field.setCard(generateRandomCommandCard());
                    field.setVisible(true);
                }
            }
        }
    }

    /**
     * Creates a random command card from all available command types.
     *
     * @return a randomly generated command card
     */
    private CommandCard generateRandomCommandCard() {
        Command[] commands = Command.values();
        int random = (int) (Math.random() * commands.length);
        return new CommandCard(commands[random]);
    }

    /**
     * Finishes the programming phase and enters activation.
     * Hides all program fields, reveals the first register, and resets the
     * current player and step for execution.
     */
    public void finishProgrammingPhase() {
        makeProgramFieldsInvisible();
        makeProgramFieldsVisible(0);
        board.setPhase(Phase.ACTIVATION);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);
    }

    /**
     * Makes one specific program register visible for all players.
     *
     * @param register the register index to reveal
     */
    private void makeProgramFieldsVisible(int register) {
        if (register >= 0 && register < Player.NO_REGISTERS) {
            for (int i = 0; i < board.getPlayersNumber(); i++) {
                Player player = board.getPlayer(i);
                CommandCardField field = player.getProgramField(register);
                field.setVisible(true);
            }
        }
    }

    /**
     * Hides all program register fields for all players.
     */
    private void makeProgramFieldsInvisible() {
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            for (int j = 0; j < Player.NO_REGISTERS; j++) {
                CommandCardField field = player.getProgramField(j);
                field.setVisible(false);
            }
        }
    }

    /**
     * Executes the full activation run (all possible steps) unless interaction
     * or phase changes interrupt the flow.
     */
    public void executePrograms() {
        board.setStepMode(false);
        continuePrograms();
    }

    /**
     * Executes exactly one activation step and then pauses.
     */
    public void executeStep() {
        board.setStepMode(true);
        continuePrograms();
    }

    /**
     * Handles the interaction phase by executing the next step based on the provided command
     * and continuing program execution if the system is not in step mode.
     *
     * @param choice the command representing the player's chosen action; must not be null
     */
    public void interact(Command choice) {
        executeNextStep(choice); // Pass the choice to the step execution
        if (!board.isStepMode()) {
            continuePrograms();  // Resume full execution if not in step mode
        }
    }

    /**
     * Continues program execution while activation is active and step mode is off.
     */
    private void continuePrograms() {
        do {
            executeNextStep(null);
        } while (board.getPhase() == Phase.ACTIVATION && !board.isStepMode());
    }

    /**
     * Executes the next activation step for the current player and current register.
     * Handles interactive cards by switching to PLAYER_INTERACTION when needed,
     * cycles through players, executes field actions after each register, and
     * starts a new programming phase after the last register.
     *
     * @param choice the player's selected command option for an interactive card,
     *               or {@code null} if no choice has been made yet
     */
    private void executeNextStep(Command choice) {
        Player currentPlayer = board.getCurrentPlayer();
        if ((board.getPhase() == Phase.ACTIVATION || board.getPhase() == Phase.PLAYER_INTERACTION) && currentPlayer != null) {
            int step = board.getStep();
            if (step >= 0 && step < Player.NO_REGISTERS) {
                CommandCard card = currentPlayer.getProgramField(step).getCard();
                if (card != null) {
                    Command command = card.command;
                    if (command == Command.LEFT_OR_RIGHT) {
                        if (choice == null) {
                            // there has been no choice made yet, change phase and stop the loop
                            board.setPhase(Phase.PLAYER_INTERACTION);
                            return;
                        } else {
                            // choice has been made, override the command and switch phase back
                            command = choice;
                            board.setPhase(Phase.ACTIVATION);
                        }
                    }
                    executeCommand(currentPlayer, command);
                }
                int nextPlayerNumber = board.getPlayerNumber(currentPlayer) + 1;
                if (nextPlayerNumber < board.getPlayersNumber()) {
                    board.setCurrentPlayer(board.getPlayer(nextPlayerNumber));
                } else {
                    executeFieldActions();

                    if (board.getPhase() != Phase.ACTIVATION) {
                        return;
                    }

                    step++;
                    if (step < Player.NO_REGISTERS) {
                        makeProgramFieldsVisible(step);
                        board.setStep(step);
                        board.setCurrentPlayer(board.getPlayer(0));
                    } else {
                        startProgrammingPhase();
                    }
                }
            } else {
                // this should not happen
                assert false;
            }
        } else {
            // this should not happen
            assert false;
        }
    }

    /**
     * Executes all field actions for the players on the board.
     *
     * @author Magnus Dragheim
     */
    private void executeFieldActions() {
       List<Player> players = board.getPlayers();
       
       for (int i = 0; i < players.size(); i++) {
           Player currentPlayer = players.get(i);
           Space currentSpace = currentPlayer.getSpace();
           
           for (FieldAction fieldAction: currentSpace.getActions()) {
               fieldAction.doAction(this, currentSpace);
               if (currentPlayer.getHasWon()) {
                   board.setPhase(Phase.FINISHED);
                   return;
               }
           }
        }
    }

    /**
     * Executes one concrete command for the given player.
     *
     * @param player the player whose robot executes the command
     * @param command the command to execute
     */
    private void executeCommand(@NotNull Player player, Command command) {
        if (player != null && player.board == board && command != null) {
            // XXX This is a very simplistic way of dealing with some basic cards and
            //     their execution. This should eventually be done in a more elegant way
            //     (this concerns the way cards are modelled as well as the way they are executed).

            switch (command) {
                case FORWARD:
                    this.moveForward(player);
                    break;
                case RIGHT:
                    this.turnRight(player);
                    break;
                case LEFT:
                    this.turnLeft(player);
                    break;
                case FAST_FORWARD:
                    this.fastForward(player);
                    break;
                case U_TURN:
                    this.uTurn(player);
                    break;
                case BACK:
                    this.back(player);
                    break;
                default:
                    // DO NOTHING (for now)//
            }
        }
    }

    // DONE A6c: implement this method

    /**
     * Moves the specified player one space forward in the direction they are currently facing.
     * If the target space is invalid (e.g., off the board), the player remains in their current position.
     * If the target space is unreachable due to an exceptional scenario, the move is aborted.
     *
     * @param player the player to move forward; must not be null
     */
    public void moveForward(@NotNull Player player) {
        Heading heading = player.getHeading();
        Space from = player.getSpace();
        Space to = board.getNeighbour(from, heading);

        if (to == null) {
            return;
        }


        try {
            moveToSpace(player, to, heading);
        } catch (ImpossibleMoveException e) {
            return;
        }

    }

    // DONE A6c: implement this method

    /**
     * Moves the specified player two spaces forward in their current heading
     * direction. This method calls the {@code moveForward} method twice to
     * achieve the effect of a double forward motion.
     *
     * @param player the player to move forward; must not be null
     */
    public void fastForward(@NotNull Player player) {
        moveForward(player);
        moveForward(player);

    }

    // DONE A6c: implement this method

    /**
     * Turns the specified player 90 degrees to the right, changing their heading
     * to the next value in the circular sequence of headings defined by the Heading enum.
     *
     * @param player the player to turn right; must not be null
     */
    public void turnRight(@NotNull Player player) {

        Heading playerHeading = player.getHeading();
        player.setHeading(playerHeading.next());
    }

    // DONE A6c: implement this method

    /**
     * Turns the specified player 90 degrees to the left, changing their heading
     * to the previous value in the circular sequence of headings defined by the Heading enum.
     *
     * @param player the player to turn left; must not be null
     */
    public void turnLeft(@NotNull Player player) {

        Heading playerHeading = player.getHeading();
        player.setHeading(playerHeading.prev());
    }

    // DONE A6c: Add two methods for the new commands BACK and UTURN here.

    /**
     * Executes a U-turn for the specified player. A U-turn involves the player
     * turning 180 degrees by performing two consecutive left turns.
     *
     * @param player the player who will perform the U-turn; must not be null
     */
    public void uTurn(@NotNull Player player) {
        turnLeft(player);
        turnLeft(player);
    }

    /**
     * Moves the given player backward by executing a sequence of operations
     * that simulates a reverse motion. The player performs a U-turn, moves
     * forward in the new direction, and performs a final U-turn to restore
     * their original heading.
     *
     * @param player the player to be moved backward; must not be null
     */
    public void back(@NotNull Player player) {
        uTurn(player);
        moveForward(player);
        uTurn(player);
    }

    /**
     * Moves a player to a specified space on the board. If the target space is
     * occupied by another player, the occupying player is pushed to an adjacent
     * space in the same direction. If pushing is not possible, an exception is thrown.
     *
     * @param pusher the player who is moving into the target space
     * @param space the target space to which the player is moving
     * @param heading the direction in which the player is pushing if the target space is occupied
     * @throws ImpossibleMoveException if the move or push cannot be completed
     */
    private void moveToSpace(@NotNull Player pusher, @NotNull Space space, @NotNull Heading heading) throws ImpossibleMoveException {
        if (space.getPlayer() != null) {
            Player beingPushed = space.getPlayer();
            Space to = board.getNeighbour(space, heading);

            if (to == null) {
                throw new ImpossibleMoveException();
            }
            
            moveToSpace(beingPushed, to, heading);
        }

        pusher.setSpace(space);
    }

    public class ImpossibleMoveException extends Exception {}

}

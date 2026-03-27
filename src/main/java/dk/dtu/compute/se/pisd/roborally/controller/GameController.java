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

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class GameController {

    final public Board board;

    public GameController(@NotNull Board board) {
        this.board = board;
    }

    /**
     * This is just some dummy controller operation to make a simple move to see something
     * happening on the board. This method should eventually be deleted!
     *
     * @param space the space to which the current player should move
     * @author Sam Golpasand
     */
    public void moveCurrentPlayerToSpace(@NotNull Space space)  {
        try {
            if (space.getPlayer() != null) {
                throw new IllegalAccessError("There is already a player in that space.");
            }

            if (board.getCurrentPlayer() == null) {
                throw new IllegalAccessError("The current player is not valid");
            }

            Player currentPlayer = board.getCurrentPlayer();
            Space oldSpace = currentPlayer.getSpace();

            oldSpace.setPlayer(null);

            space.setPlayer(currentPlayer);
            currentPlayer.setSpace(space);
            board.setCurrentPlayer(board.getPlayer((board.getPlayerNumber(currentPlayer) + 1) % board.getPlayersNumber()));

            board.incrementMoveCounter();
        } catch (Error e) {
            System.err.println(e);
        }
        
    }

    /**
     * This starts 
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

    // XXX A6c
    private CommandCard generateRandomCommandCard() {
        Command[] commands = Command.values();
        int random = (int) (Math.random() * commands.length);
        return new CommandCard(commands[random]);
    }

    // XXX A6c
    public void finishProgrammingPhase() {
        makeProgramFieldsInvisible();
        makeProgramFieldsVisible(0);
        board.setPhase(Phase.ACTIVATION);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);
    }

    // XXX A6c
    private void makeProgramFieldsVisible(int register) {
        if (register >= 0 && register < Player.NO_REGISTERS) {
            for (int i = 0; i < board.getPlayersNumber(); i++) {
                Player player = board.getPlayer(i);
                CommandCardField field = player.getProgramField(register);
                field.setVisible(true);
            }
        }
    }

    // XXX A6c
    private void makeProgramFieldsInvisible() {
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            for (int j = 0; j < Player.NO_REGISTERS; j++) {
                CommandCardField field = player.getProgramField(j);
                field.setVisible(false);
            }
        }
    }

    // XXX A6c
    public void executePrograms() {
        board.setStepMode(false);
        continuePrograms();
    }

    // XXX A6c
    public void executeStep() {
        board.setStepMode(true);
        continuePrograms();
    }

    // XXX A6c
    private void continuePrograms() {
        do {
            executeNextStep();
        } while (board.getPhase() == Phase.ACTIVATION && !board.isStepMode());
    }

    // XXX A6c
    // TODO A6d: add the execution of the field actions at the right
    //      place in this method
    // TODO A6e: implement the execution af an interactive card to
    //     this method (e.g. by switching to the PLAYER_INTERACTION phase
    //     at the right point)
    private void executeNextStep() {
        Player currentPlayer = board.getCurrentPlayer();
        if (board.getPhase() == Phase.ACTIVATION && currentPlayer != null) {
            int step = board.getStep();
            if (step >= 0 && step < Player.NO_REGISTERS) {
                CommandCard card = currentPlayer.getProgramField(step).getCard();
                if (card != null) {
                    Command command = card.command;
                    executeCommand(currentPlayer, command);
                }
                int nextPlayerNumber = board.getPlayerNumber(currentPlayer) + 1;
                if (nextPlayerNumber < board.getPlayersNumber()) {
                    board.setCurrentPlayer(board.getPlayer(nextPlayerNumber));
                } else {
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

    // XXX A6c
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
    public void moveForward(@NotNull Player player) {
        Heading heading = player.getHeading();
        Space from = player.getSpace();
        Space to = board.getNeighbour(from, heading);

        // off-board: do nothing (or handle as fall/death later)
        if (to == null) {
            return;
        }
        try {
            moveToSpace(player, to, heading);
        }
        catch (ImpossibleMoveException e) {
            return;
        }

    }

    // DONE A6c: implement this method
    public void fastForward(@NotNull Player player) {
        moveForward(player);
        moveForward(player);

    }

    // DONE A6c: implement this method
    public void turnRight(@NotNull Player player) {

        Heading playerHeading = player.getHeading();
        player.setHeading(playerHeading.next());
    }

    // DONE A6c: implement this method
    public void turnLeft(@NotNull Player player) {

        Heading playerHeading = player.getHeading();
        player.setHeading(playerHeading.prev());
    }

    // DONE A6c: Add two methods for the new commands BACK and UTURN here.

    public void uTurn(@NotNull Player player) {
        turnLeft(player);
        turnLeft(player);
    }

    public void back(@NotNull Player player) {
        uTurn(player);
        moveForward(player);
        uTurn(player);
    }

    private void moveToSpace(@NotNull Player pusher, @NotNull Space space, @NotNull Heading heading)
                                throws ImpossibleMoveException {
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

    /**
     * A method called when no corresponding controller operation is implemented yet.
     * This should eventually be removed.
     */
    public void notImplemented() {
        // XXX just for now to indicate that the actual method is not yet implemented
        assert false;
    }

    public class ImpossibleMoveException extends Exception {

    }

}

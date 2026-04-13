package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConveyorBeltTest {

    private GameController createControllerWithPlayers(int playerCount) {
        Board board = new Board(6, 6);
        GameController controller = new GameController(board);

        for (int i = 0; i < playerCount; i++) {
            Player player = new Player(board, null, "Player " + i);
            board.addPlayer(player);
            player.setSpace(board.getSpace(i, 0));
        }

        if (playerCount > 0) {
            board.setCurrentPlayer(board.getPlayer(0));
        }

        return controller;
    }

    @Test
    void doActionMovesPlayerWhenTargetIsFree() {
        GameController gameController = createControllerWithPlayers(1);
        Board board = gameController.board;
        Space source = board.getSpace(0, 0);

        ConveyorBelt conveyorBelt = new ConveyorBelt();
        conveyorBelt.setHeading(Heading.EAST);

        boolean moved = conveyorBelt.doAction(gameController, source);

        assertTrue(moved);
        assertSame(board.getSpace(1, 0), board.getPlayer(0).getSpace());
    }

    @Test
    void doActionReturnsFalseWhenNoNeighbourExists() {
        GameController gameController = createControllerWithPlayers(1);
        Board board = gameController.board;
        Space source = board.getSpace(0, 0);
        source.getWalls().add(Heading.EAST);

        ConveyorBelt conveyorBelt = new ConveyorBelt();
        conveyorBelt.setHeading(Heading.EAST);

        boolean moved = conveyorBelt.doAction(gameController, source);

        assertFalse(moved);
        assertSame(source, board.getPlayer(0).getSpace());
    }

    @Test
    void doActionReturnsFalseWhenTargetIsOccupied() {
        GameController gameController = createControllerWithPlayers(2);
        Board board = gameController.board;
        Space source = board.getSpace(0, 0);

        ConveyorBelt conveyorBelt = new ConveyorBelt();
        conveyorBelt.setHeading(Heading.EAST);

        boolean moved = conveyorBelt.doAction(gameController, source);

        assertFalse(moved);
        assertSame(board.getSpace(0, 0), board.getPlayer(0).getSpace());
        assertSame(board.getSpace(1, 0), board.getPlayer(1).getSpace());
    }
}

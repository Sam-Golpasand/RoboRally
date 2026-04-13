package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CheckpointTest {

    private GameController createControllerWithSinglePlayer() {
        Board board = new Board(6, 6);
        GameController controller = new GameController(board);
        Player player = new Player(board, null, "Player 0");
        board.addPlayer(player);
        player.setSpace(board.getSpace(0, 0));
        board.setCurrentPlayer(player);
        return controller;
    }

    @Test
    void doActionIncrementsCheckpointWhenReachedInOrder() {
        GameController gameController = createControllerWithSinglePlayer();
        Space space = gameController.board.getSpace(0, 0);
        Player player = space.getPlayer();

        Checkpoint checkpoint = new Checkpoint();
        checkpoint.setId(1);

        boolean activated = checkpoint.doAction(gameController, space);

        assertTrue(activated);
        assertEquals(1, player.getCheckpointCount());
        assertFalse(player.getHasWon());
    }

    @Test
    void doActionSetsWinnerAtLastCheckpoint() {
        GameController gameController = createControllerWithSinglePlayer();
        Space space = gameController.board.getSpace(0, 0);
        Player player = space.getPlayer();

        Checkpoint checkpoint = new Checkpoint();
        checkpoint.setId(1);
        checkpoint.setIsLastCheckpoint(true);

        boolean activated = checkpoint.doAction(gameController, space);

        assertTrue(activated);
        assertEquals(1, player.getCheckpointCount());
        assertTrue(player.getHasWon());
    }

    @Test
    void doActionReturnsFalseForOutOfOrderCheckpoint() {
        GameController gameController = createControllerWithSinglePlayer();
        Space space = gameController.board.getSpace(0, 0);
        Player player = space.getPlayer();

        Checkpoint checkpoint = new Checkpoint();
        checkpoint.setId(2);

        boolean activated = checkpoint.doAction(gameController, space);

        assertFalse(activated);
        assertEquals(0, player.getCheckpointCount());
        assertFalse(player.getHasWon());
    }
}

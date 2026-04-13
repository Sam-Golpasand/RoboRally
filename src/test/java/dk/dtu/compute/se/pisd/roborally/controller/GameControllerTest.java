package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Command;
import dk.dtu.compute.se.pisd.roborally.model.CommandCard;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Phase;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameControllerTest {

    private final int TEST_WIDTH = 8;
    private final int TEST_HEIGHT = 8;

    private GameController gameController;

    @BeforeEach
    void setUp() {
        Board board = new Board(TEST_WIDTH, TEST_HEIGHT);
        gameController = new GameController(board);
        for (int i = 0; i < 6; i++) {
            Player player = new Player(board, null, "Player " + i);
            board.addPlayer(player);
            player.setSpace(board.getSpace(i, i));
            player.setHeading(Heading.values()[i % Heading.values().length]);
        }

        board.setCurrentPlayer(board.getPlayer(0));
    }

    @AfterEach
    void tearDown() {
        gameController = null;
    }

    private void prepareActivation(Board board) {
        board.setPhase(Phase.ACTIVATION);
        board.setStep(0);
        board.setCurrentPlayer(board.getPlayer(0));
    }

    private void setRegister0(Player player, Command command) {
        player.getProgramField(0).setCard(new CommandCard(command));
    }

    @Test
    void startProgrammingPhaseInitializesProgramAndCardFields() {
        Board board = gameController.board;

        gameController.startProgrammingPhase();

        assertEquals(Phase.PROGRAMMING, board.getPhase());
        assertEquals(0, board.getStep());
        assertSame(board.getPlayer(0), board.getCurrentPlayer());

        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);

            for (int register = 0; register < Player.NO_REGISTERS; register++) {
                assertNull(player.getProgramField(register).getCard());
                assertTrue(player.getProgramField(register).isVisible());
            }

            for (int card = 0; card < Player.NO_CARDS; card++) {
                assertNotNull(player.getCardField(card).getCard());
                assertTrue(player.getCardField(card).isVisible());
            }
        }
    }

    @Test
    void finishProgrammingPhaseHidesRegistersExceptCurrent() {
        Board board = gameController.board;
        gameController.startProgrammingPhase();

        gameController.finishProgrammingPhase();

        assertEquals(Phase.ACTIVATION, board.getPhase());
        assertEquals(0, board.getStep());
        assertSame(board.getPlayer(0), board.getCurrentPlayer());

        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            assertTrue(player.getProgramField(0).isVisible());
            for (int register = 1; register < Player.NO_REGISTERS; register++) {
                assertFalse(player.getProgramField(register).isVisible());
            }
        }
    }

    @Test
    void moveForwardPushesPlayerWhenSpaceOccupied() {
        Board board = gameController.board;
        Player pusher = board.getPlayer(0);
        Player pushed = board.getPlayer(1);
        pusher.setSpace(board.getSpace(0, 0));
        pushed.setSpace(board.getSpace(1, 0));
        pusher.setHeading(Heading.EAST);

        gameController.moveForward(pusher);

        assertSame(board.getSpace(1, 0), pusher.getSpace());
        assertSame(board.getSpace(2, 0), pushed.getSpace());
    }

    @Test
    void moveForwardStopsWhenPushIsImpossible() {
        Board board = gameController.board;
        Player pusher = board.getPlayer(0);
        Player pushed = board.getPlayer(1);
        pusher.setSpace(board.getSpace(0, 0));
        pushed.setSpace(board.getSpace(1, 0));
        pusher.setHeading(Heading.EAST);
        board.getSpace(1, 0).getWalls().add(Heading.EAST);

        gameController.moveForward(pusher);

        assertSame(board.getSpace(0, 0), pusher.getSpace());
        assertSame(board.getSpace(1, 0), pushed.getSpace());
    }

    @Test
    void moveForwardStopsWhenWallBlocksDirectMove() {
        Board board = gameController.board;
        Player player = board.getPlayer(0);
        player.setSpace(board.getSpace(0, 0));
        player.setHeading(Heading.EAST);
        board.getSpace(0, 0).getWalls().add(Heading.EAST);

        gameController.moveForward(player);

        assertSame(board.getSpace(0, 0), player.getSpace());
        assertEquals(Heading.EAST, player.getHeading());
    }

    @Test
    void fastForwardStopsAtBlockingWallOnSecondMove() {
        Board board = gameController.board;
        Player player = board.getPlayer(0);
        player.setSpace(board.getSpace(0, 0));
        player.setHeading(Heading.EAST);
        board.getSpace(1, 0).getWalls().add(Heading.EAST);

        gameController.fastForward(player);

        assertSame(board.getSpace(1, 0), player.getSpace());
        assertEquals(Heading.EAST, player.getHeading());
    }

    @Test
    void backStopsWhenReverseDirectionIsBlocked() {
        Board board = gameController.board;
        Player player = board.getPlayer(0);
        player.setSpace(board.getSpace(0, 0));
        player.setHeading(Heading.EAST);
        board.getSpace(0, 0).getWalls().add(Heading.WEST);

        gameController.back(player);

        assertSame(board.getSpace(0, 0), player.getSpace());
        assertEquals(Heading.EAST, player.getHeading());
    }

    @Test
    void executeStepWithInteractiveCardEntersPlayerInteraction() {
        Board board = gameController.board;
        Player player = board.getPlayer(0);
        setRegister0(player, Command.LEFT_OR_RIGHT);

        prepareActivation(board);
        gameController.executeStep();

        assertTrue(board.isStepMode());
        assertEquals(Phase.PLAYER_INTERACTION, board.getPhase());
        assertEquals(0, board.getStep());
        assertSame(player, board.getCurrentPlayer());
    }

    @Test
    void interactWithChoiceExecutesAndSkipsContinuationInStepMode() {
        Board board = gameController.board;
        Player player = board.getPlayer(0);
        player.setHeading(Heading.NORTH);
        setRegister0(player, Command.LEFT_OR_RIGHT);

        prepareActivation(board);
        board.setStepMode(true);
        gameController.interact(Command.LEFT);

        assertEquals(Phase.ACTIVATION, board.getPhase());
        assertEquals(0, board.getStep());
        assertSame(board.getPlayer(1), board.getCurrentPlayer());
        assertEquals(Heading.WEST, player.getHeading());
    }

    @Test
    void interactWithChoiceContinuesWhenNotInStepMode() {
        Board board = gameController.board;
        Player player = board.getPlayer(0);
        player.setHeading(Heading.NORTH);
        setRegister0(player, Command.LEFT_OR_RIGHT);

        prepareActivation(board);
        gameController.interact(Command.RIGHT);

        assertEquals(Heading.EAST, player.getHeading());
        assertEquals(Phase.PROGRAMMING, board.getPhase());
        assertEquals(0, board.getStep());
    }

    @Test
    void executeProgramsRunsToNextProgrammingPhase() {
        Board board = gameController.board;

        prepareActivation(board);
        gameController.executePrograms();

        assertFalse(board.isStepMode());
        assertEquals(Phase.PROGRAMMING, board.getPhase());
        assertEquals(0, board.getStep());
        assertSame(board.getPlayer(0), board.getCurrentPlayer());

        for (int i = 0; i < Player.NO_CARDS; i++) {
            assertNotNull(board.getPlayer(0).getCardField(i).getCard());
        }
    }

    @Test
    void executeStepMovesControlToNextPlayer() {
        Board board = gameController.board;
        Player player0 = board.getPlayer(0);
        Player player1 = board.getPlayer(1);
        player0.setHeading(Heading.NORTH);
        setRegister0(player0, Command.RIGHT);

        prepareActivation(board);
        gameController.executeStep();

        assertSame(player1, board.getCurrentPlayer());
        assertEquals(0, board.getStep());
        assertEquals(Heading.EAST, player0.getHeading());
    }

    @Test
    void executeStepTriggersFinishedPhaseWhenFieldActionWins() {
        Board board = gameController.board;
        Player player = board.getPlayer(board.getPlayersNumber() - 1);
        Checkpoint checkpoint = new Checkpoint();
        checkpoint.setId(1);
        checkpoint.setIsLastCheckpoint(true);
        player.getSpace().getActions().add(checkpoint);

        prepareActivation(board);
        board.setCurrentPlayer(player);
        gameController.executeStep();

        assertEquals(Phase.FINISHED, board.getPhase());
        assertTrue(player.getHasWon());
        assertEquals(1, player.getCheckpointCount());
    }

    @Test
    void executeProgramsKeepsFinishedWhenWinningAtLastRegister() {
        Board board = gameController.board;
        Player player = board.getPlayer(0);
        Checkpoint checkpoint = new Checkpoint();
        checkpoint.setId(1);
        checkpoint.setIsLastCheckpoint(true);
        player.getSpace().getActions().add(checkpoint);

        board.setPhase(Phase.ACTIVATION);
        board.setStep(Player.NO_REGISTERS - 1);
        board.setCurrentPlayer(player);
        board.setStepMode(false);

        gameController.executePrograms();

        assertEquals(Phase.FINISHED, board.getPhase());
        assertTrue(player.getHasWon());
        assertEquals(1, player.getCheckpointCount());
    }

    @Test
    void executeStepCoversAllDirectCommandCards() {
        Board board = gameController.board;
        Command[] commands = {
                Command.FORWARD,
                Command.RIGHT,
                Command.LEFT,
                Command.FAST_FORWARD,
                Command.U_TURN,
                Command.BACK
        };

        for (Command command : commands) {
            Player player = board.getPlayer(0);
            for (int i = 0; i < board.getPlayersNumber(); i++) {
                board.getPlayer(i).getProgramField(0).setCard(null);
            }
            player.setSpace(board.getSpace(0, 0));
            player.setHeading(Heading.EAST);
            setRegister0(player, command);

            prepareActivation(board);
            gameController.executeStep();

            switch (command) {
                case FORWARD:
                    assertSame(board.getSpace(1, 0), player.getSpace());
                    assertEquals(Heading.EAST, player.getHeading());
                    break;
                case RIGHT:
                    assertSame(board.getSpace(0, 0), player.getSpace());
                    assertEquals(Heading.SOUTH, player.getHeading());
                    break;
                case LEFT:
                    assertSame(board.getSpace(0, 0), player.getSpace());
                    assertEquals(Heading.NORTH, player.getHeading());
                    break;
                case FAST_FORWARD:
                    assertSame(board.getSpace(2, 0), player.getSpace());
                    assertEquals(Heading.EAST, player.getHeading());
                    break;
                case U_TURN:
                    assertSame(board.getSpace(0, 0), player.getSpace());
                    assertEquals(Heading.WEST, player.getHeading());
                    break;
                case BACK:
                    assertSame(board.getSpace(7, 0), player.getSpace());
                    assertEquals(Heading.EAST, player.getHeading());
                    break;
                default:
                    break;
            }

            assertEquals(Phase.ACTIVATION, board.getPhase());
            assertEquals(0, board.getStep());
            assertSame(board.getPlayer(1), board.getCurrentPlayer());
        }
    }

}
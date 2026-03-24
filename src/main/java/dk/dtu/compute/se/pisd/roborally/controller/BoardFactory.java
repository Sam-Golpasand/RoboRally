package dk.dtu.compute.se.pisd.roborally.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Space;

/**
 * A factory for creating boards. The factory itself is implemented as a singleton.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
// XXX A3: might be used for creating a first slightly more interesting board.
public class BoardFactory {

    /**
     * The single instance of this class, which is lazily instantiated on demand.
     */
    static private BoardFactory instance = null;
    static private List<String> boardNames;
    /**
     * Constructor for BoardFactory. It is private in order to make the factory a singleton.
     */
    private BoardFactory() {
        List<String> availableBoards = new ArrayList<>();
        availableBoards.add("simple");
        availableBoards.add("advanced");

        boardNames = Collections.unmodifiableList(availableBoards);
    }

    /**
     * Returns the single instance of this factory. The instance is lazily
     * instantiated when requested for the first time.
     *
     * @return the single instance of the BoardFactory
     */
    public static BoardFactory getInstance() {
        if (instance == null) {
            instance = new BoardFactory();
        }
        return instance;
    }

    /**
     * Creates a new board of given name of a board, which indicates
     * which type of board should be created. For now the name is ignored.
     *
     * @param name the given name board
     * @return the new board corresponding to that name
     * 
     * @author Sam Golpasand
     */
    public Board createBoard(String name) {

        Board board;

        switch (name) {
            case "simple":
                board = buildSimpleBoard();
                break;
            case "advanced":
                board = buildAdvancedBoard();
                break;
            default:
                board = buildSimpleBoard();
                break;
        }

        return board;



    }

    /**
     * Configures a board by setting up walls and conveyor belts at specific spaces
     * to create a simple board structure.
     *
     * @param board the board to be configured as a simple board
     * @return the configured simple board
     */
    private Board buildSimpleBoard() {
        Board board = new Board(8, 8, "simple");

        
        // add some walls, actions and checkpoints to some spaces
        Space space = board.getSpace(0,0);
        space.getWalls().add(Heading.SOUTH);
        ConveyorBelt action  = new ConveyorBelt();
        action.setHeading(Heading.WEST);
        space.getActions().add(action);

        space = board.getSpace(1,0);
        space.getWalls().add(Heading.NORTH);
        action  = new ConveyorBelt();
        action.setHeading(Heading.WEST);
        space.getActions().add(action);

        space = board.getSpace(1,1);
        space.getWalls().add(Heading.WEST);
        action  = new ConveyorBelt();
        action.setHeading(Heading.NORTH);
        space.getActions().add(action);

        space = board.getSpace(5,5);
        space.getWalls().add(Heading.SOUTH);
        action  = new ConveyorBelt();
        action.setHeading(Heading.WEST);
        space.getActions().add(action);

        space = board.getSpace(6,5);
        action  = new ConveyorBelt();
        action.setHeading(Heading.WEST);
        space.getActions().add(action);

        return board;
    }

    /**
     * Constructs and configures an advanced board by setting up walls, conveyor belts,
     * and checkpoints on specific spaces of the provided board.
     *
     * @param board the board to be configured as an advanced board
     * @return the configured advanced board
     */
    private Board buildAdvancedBoard() {

        Board board = new Board(12, 12, "advanced");
        
        // add some walls, actions and checkpoints to some spaces
        Space space = board.getSpace(0,0);
        space.getWalls().add(Heading.SOUTH);
        ConveyorBelt action  = new ConveyorBelt();
        action.setHeading(Heading.WEST);
        space.getActions().add(action);

        space = board.getSpace(5,6);
        space.getWalls().add(Heading.NORTH);
        action  = new ConveyorBelt();
        action.setHeading(Heading.WEST);
        space.getActions().add(action);

        space = board.getSpace(6,3);
        space.getWalls().add(Heading.WEST);
        action  = new ConveyorBelt();
        action.setHeading(Heading.NORTH);
        space.getActions().add(action);

        space = board.getSpace(2,3);
        space.getWalls().add(Heading.SOUTH);
        action  = new ConveyorBelt();
        action.setHeading(Heading.WEST);
        space.getActions().add(action);

        space = board.getSpace(3,5);
        action  = new ConveyorBelt();
        action.setHeading(Heading.WEST);
        space.getActions().add(action);

        return board;
    }



    // TODO A6b: add a method that returns a list (of type List<String>)
    //     of all available board names. The corresponding method
    //     createBoard(String name) must return a board for any of the
    //     names in this list. Make sure that the new method that you create
    //     here has a proper JavaDoc documentation.
    //

    public List<String> getBoards() {
        return boardNames;
    }
    

}
package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Space;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    private List<String> boardNames;
    /**
     * Constructor for BoardFactory. It is private in order to make the factory a singleton.
     */
    private BoardFactory() {
        String SIMPLE_BOARD_NAME = "simple";
        String ADVANCED_BOARD_NAME = "advanced";
        List<String> underlyingList = new ArrayList();
        underlyingList.add(SIMPLE_BOARD_NAME);
        underlyingList.add(ADVANCED_BOARD_NAME);
        boardNames = Collections.unmodifiableList(underlyingList);
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
     */
    public Board createBoard(String name) {
        // TODO A6b: Implement this method properly as described in Assignment 6b.
        //     Dependent on the provided name, create a board accordingly and
        //     return it. In case the name is null, some default board should
        //     be returned (defensive programming).



        Board board;
        if (name == null) {
            name = "simple";
            board = new Board(8,8, name);
        } else {
            board = new Board(8,8, name);
        }

        if (name.equals("simple")) {
            board = buildSimpleBoard(board);
        }
        else if(name.equals("advanced")) {
            board = buildAdvancedBoard(board);
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
    private Board buildSimpleBoard(Board board) {
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
    private Board buildAdvancedBoard(Board board) {
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

        space = board.getSpace(3,7);
        CheckPoint actionCheckPoint = new CheckPoint();
        actionCheckPoint.setNumber(1);
        space.getActions().add(actionCheckPoint);

        space = board.getSpace(4,5);
        actionCheckPoint = new CheckPoint();
        actionCheckPoint.setNumber(2);
        space.getActions().add(actionCheckPoint);


        return board;
    }



    // TODO A6b: add a method that returns a list (of type List<String>)
    //     of all available board names. The corresponding method
    //     createBoard(String name) must return a board for any of the
    //     names in this list. Make sure that the new method that you create
    //     here has a proper JavaDoc documentation.
    //

    /**
     * Retrieves the list of available board names that can be created.
     * The board names represent the types of boards supported by this factory.
     *
     * @return a list of strings containing the names of the available boards
     */
    public List<String> getAvailableBoardNames() {
        return boardNames;
    }


}

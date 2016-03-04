package nl.tudelft.jpacman.level;

import nl.tudelft.jpacman.board.*;
import nl.tudelft.jpacman.npc.NPC;
import nl.tudelft.jpacman.npc.ghost.Navigation;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Angeall on 02/03/2016.
 */
public class InfiniteLevel extends Level {
    protected SquareLineGenerator generator;
    /**
     * Creates a new level for the board.
     *
     * @param b              The board for the level.
     * @param ghosts         The ghosts on the board.
     * @param startPositions The squares on which players start on this board.
     * @param collisionMap   The collection of collisions that should be handled.
     */
    public InfiniteLevel(InfiniteBoard b, List<NPC> ghosts, List<Square> startPositions, CollisionMap collisionMap) {
        super(b, ghosts, startPositions, collisionMap);
        this.generator = new SquareLineGenerator();
    }

    /**
     * Moves the unit into the given direction if possible and handles all
     * collisions.
     * Check at each move if the infinite board needs to be extended or not.
     *
     * @param unit
     *            The unit to move.
     * @param direction
     *            The direction to move the unit in.
     */
    @Override
    public void move(Unit unit, Direction direction) {
        super.move(unit, direction);
        // We check if the board must be extended or not.
        if(getInfiniteBoard().isToExtendBottom()){
            getInfiniteBoard().addLineBottom(generator.generateSquareLine(getInfiniteBoard().getBottomLine(), this));
        }
        if(getInfiniteBoard().isToExtendTop()){
            getInfiniteBoard().addLineTop(generator.generateSquareLine(getInfiniteBoard().getTopLine(), this));
        }
        if(getInfiniteBoard().isToExtendLeft()){
            getInfiniteBoard().addColumnLeft((generator.generateSquareLine(getInfiniteBoard().getLeftColumn(), this)));
        }
        if(getInfiniteBoard().isToExtendRight()){
            getInfiniteBoard().addColumnRight(generator.generateSquareLine(getInfiniteBoard().getRightColumn(), this));
        }
    }

    public void putNewGhosst(NPC ghost){
        ScheduledExecutorService service = Executors
                .newSingleThreadScheduledExecutor();
        service.schedule(new NpcMoveTask(service, ghost),
                ghost.getInterval() / 2, TimeUnit.MILLISECONDS);
        this.npcs.put(ghost, service);
    }

    public void removeGhost(NPC ghost){
        this.npcs.get(ghost).shutdownNow();
        this.npcs.remove(ghost);
    }

    @Override
    public void registerPlayer(Player p){
        super.registerPlayer(p);
        Navigation.playerList.add(p);
    }

    public InfiniteBoard getInfiniteBoard(){
        return (InfiniteBoard) super.getBoard();
    }
}

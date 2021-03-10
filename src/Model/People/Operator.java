package Model.People;

import Model.Blocks.Block;
import Model.Blocks.Game;
import Model.Coord;

public class Operator extends Employee {

    public Operator(Coord startingCoord, Block startingBlock, int salary)
    {
        super(startingCoord,startingBlock,salary);
    }

    public void operate(Game g )
    {
        this.setIsBusy(true);
        // TODO: this should be handled in the playground?
        this.setIsBusy(false);
    }
}
package Model.People;

import Model.Blocks.Block;
import Model.Position;

import java.awt.*;

public class Caterer extends Employee {
    //SericeArea workPlace;

    public Caterer(Position startingCoord, Block startingBlock, int salary)
    {
        super(startingCoord,startingBlock,salary);
    }

    public void serve(Visitor v )
    {
        this.setIsBusy(true);
        //v.eat(workPlace)
        // TODO: this should be handled in the playground?
        this.setIsBusy(false);
    }

    @Override
    protected Color getColor(){return Color.gray;};
}

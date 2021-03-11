package Model.Blocks;

import Model.Coord;
import View.IndexPair;

import java.awt.*;

public abstract class Block {
    private static final int MAX_CONDITION=100;

    protected BlockState state;
    protected int buildingCost;
    protected int upkeepCost;
    protected int condition;
    public IndexPair size; //3 blokk szeles es 2 blokk magas. Ez egy relative kicsi szam!
    public Coord pos; //bal felso eleme hol van
    protected double popularityIncrease;
    public Block()
    {
        state = BlockState.FREE;
        buildingCost=0;
        upkeepCost=0;
        condition=100;
        size=new IndexPair(1,1);
        popularityIncrease = 0;
    }

    public Block(int buildingCost, int upkeepCost, double popularityIncrease, BlockState state, IndexPair size, Coord pos) {
        this.buildingCost = buildingCost;
        this.upkeepCost = upkeepCost;
        this.popularityIncrease = popularityIncrease;
        this.state = state;
        this.size = size;
        this.pos = pos;
        condition=MAX_CONDITION;
    }

    public Block(int buildingCost, int upkeepCost, double popularityIncrease, BlockState state) {
        this.buildingCost = buildingCost;
        this.upkeepCost = upkeepCost;
        this.popularityIncrease = popularityIncrease;
        this.state = state;
        this.size = new IndexPair(1,1);
        this.pos = new Coord(0,0);
        condition=MAX_CONDITION;
    }

    //Methods:
    public void showInfoPanel(){}

    public BlockState getState() {
        return state;
    }

    public int getBuildingCost() {
        return buildingCost;
    }

    public int getUpkeepCost() {
        return upkeepCost;
    }

    public int getCondition() {
        return condition;
    }

    public double getPopularityIncrease() {
        return popularityIncrease;
    }

    abstract public Color getColor();

    public void setState(BlockState state) {
        this.state = state;
    }

    public void setBuildingCost(int buildingCost) {
        this.buildingCost = buildingCost;
    }

    public void setUpkeepCost(int upkeepCost) {
        this.upkeepCost = upkeepCost;
    }

    public void setCondition(int condition) {
        this.condition = condition;
    }

    public void setPos(Coord pos) {
        this.pos = pos;
    }

    public void setPopularityIncrease(double popularityIncrease) {
        this.popularityIncrease = popularityIncrease;
    }

        public String toString() {
        return "" +
                "state=" + state +
                ", buildingCost=" + buildingCost +
                ", upkeepCost=" + upkeepCost +
                ", condition=" + condition +
                ", size=" + size +
                ", pos=" + pos +
                ", popularityIncrease=" + popularityIncrease +
                '}';
    }
}

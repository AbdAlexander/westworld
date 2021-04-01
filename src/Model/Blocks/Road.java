package Model.Blocks;

import java.awt.*;

public class Road extends Block {
    private boolean hasGarbageCan;
    private boolean isEntrance;
    private int garbage;


    public Road(int buildingCost, int upkeepCost, double popularityIncrease, BlockState state, boolean hasGarbageCan, boolean isEntrance, int garbage) {
        super(buildingCost, upkeepCost, popularityIncrease, state);
        this.hasGarbageCan = hasGarbageCan;
        this.isEntrance = isEntrance;
        this.garbage = garbage;
        this.buildingCost = 100;
        this.upkeepCost = 0;

    }
    public Road(boolean isEntrance, int garbage) {
        super();
        this.hasGarbageCan = hasGarbageCan;
        this.isEntrance = isEntrance;
        this.garbage = garbage;
        this.buildingCost = 100;
        this.upkeepCost = 0;

    }

    //getters setters
    public boolean isHasGarbageCan() {
        return hasGarbageCan;
    }

    public void setHasGarbageCan(boolean hasGarbageCan) {
        this.hasGarbageCan = hasGarbageCan;
    }

    public boolean isEntrance() {
        return isEntrance;
    }

    public void setEntrance(boolean entrance) {
        isEntrance = entrance;
    }

    public int getGarbage() {
        return garbage;
    }

    public void setGarbage(int garbage) {
        this.garbage = garbage;
    }



    @Override
    public Color getColor() {
        return isEntrance ? Color.DARK_GRAY : Color.GRAY;
    }

    @Override
    public String toString() {
        return "Road{" +
                "hasGarbageCan=" + hasGarbageCan +
                ", isEntrance=" + isEntrance +
                ", garbage=" + garbage +
                " " + super.toString();
    }

    @Override
    public String getName(){return "Road"; }
}

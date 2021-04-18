package Model.Blocks;

import Model.GameEngine;
import Model.Position;
import View.GameField;
import View.spriteManagers.SpriteManager;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

public abstract class Block {
    protected static final Color DEFAULT_BACKGROUND_COLOR =new Color(52, 177, 52);
    protected static final int MAX_CONDITION=100;
    protected BlockState state;
    protected int buildingCost;
    protected int upkeepCost;
    protected int condition;
    public Position size;
    public  Position pos;
    protected double popularityIncrease;

    protected int buildingTime;
    protected int currentActivityTime;

    public Block()
    {
        state = BlockState.FREE;
        buildingCost=0;
        upkeepCost=0;
        condition=100;
        size=new Position(1,1,false);
        popularityIncrease = 0;
    }

    public Block(Position p){
        this();
        pos=p;
    }

    public Block(int buildingCost, int upkeepCost, double popularityIncrease, BlockState state, Position size, Position pos) {
        this.buildingCost = buildingCost;
        this.upkeepCost = upkeepCost;
        this.popularityIncrease = popularityIncrease;
        this.state = state;
        this.size = size;
        this.pos = pos;
        condition=MAX_CONDITION;
    }

    public Block(int buildingCost, int upkeepCost, double popularityIncrease, BlockState state) {
        this(buildingCost,upkeepCost,popularityIncrease,state,new Position(1,1,false),new Position(0,0,true));
    }


    public void build(){
        setupSprites();
        state=BlockState.UNDER_CONSTRUCTION;
        currentActivityTime= GameEngine.TIME_1x*5;
    }

    public void buildInstantly(){
        build();
        currentActivityTime=0;
        roundHasPassed(GameEngine.TIME_1x);
    }

    public void roundHasPassed(int minutesPerSecond){
        decreaseCurrentActivityTime(minutesPerSecond);

        //finished activity
        if(getState()==BlockState.UNDER_CONSTRUCTION && currentActivityTime==0){constructionFinished();}
        if(getState()==BlockState.UNDER_REPAIR && currentActivityTime==0){repairFinished();}
        if(getState()==BlockState.NOT_OPERABLE && !needRepair()){setState(BlockState.FREE);}

        //need to start activity
        if(getState()==BlockState.FREE && needRepair()){setState(BlockState.NOT_OPERABLE);}
    }

    protected void repairFinished(){
        condition=MAX_CONDITION;
        setState(BlockState.FREE);
        currentActivityTime=0;
    }

    protected void constructionFinished(){setState(BlockState.FREE);}

    protected void decreaseCurrentActivityTime(int value){
        if(value <=0){throw new IllegalArgumentException("pozitiv szam kene");}
        //if(currentActivityTime==0){throw new IllegalStateException("Nincsen folyamatban semmi. BlockState: " + getState().toString());}
        if(currentActivityTime>value){
            currentActivityTime-=value;
        }else{
            currentActivityTime=0;
        }
    }

    public void startDay(){}
    public void endDay(){}

    public Position getPos() {
        return pos;
    }

    public Position getSize(){return size;}

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

    public int getCurrentActivityTime() {
        return currentActivityTime;
    }

    public double getPopularityIncrease() {
        return popularityIncrease;
    }

    public void setState(BlockState state) { this.state = state; }

    public void setBuildingCost(int buildingCost) {
        this.buildingCost = buildingCost;
    }

    public void setUpkeepCost(int upkeepCost) {
        this.upkeepCost = upkeepCost;
    }

    public void setCondition(int condition) {
        this.condition = condition;
    }

    public void setPos(Position pos) {
        this.pos = pos;
    }

    public void setPopularityIncrease(double popularityIncrease) {
        this.popularityIncrease = popularityIncrease;
    }

    public boolean needRepair(){
        return (state==BlockState.NOT_OPERABLE || state==BlockState.FREE) && condition<20;
    }

    public void setCurrentActivityTime(int currentActivityTime) {
        this.currentActivityTime = currentActivityTime;
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

    public String getName(){
        return "Block";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Block)) return false;

        Block block = (Block) o;

        return this.state.equals(block.state)           &&
                this.buildingCost == block.buildingCost &&
                this.buildingTime == block.buildingTime &&
                this.upkeepCost == block.upkeepCost     &&
                this.condition == block.condition       &&
                this.size == block.size;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.buildingTime,this.buildingCost,this.size,this.pos,this.size,this.upkeepCost,this.condition,this.popularityIncrease,this.state);
    }

    //painting
    abstract public Color getColor();
    abstract protected SpriteManager getSpriteManager();

    private static BufferedImage workingPic;
    private static final HashMap<Position,BufferedImage> workingPicMap =new HashMap<>();

    static{
        try {
            workingPic=ImageIO.read(new File("graphics/work.png"));
        } catch (IOException e) {
            System.err.println("graphics/work.png not found");
        }

    }

    private void setupSprites(){
        if(!workingPicMap.containsKey(getSize()) && !Objects.isNull(workingPic)){
            workingPicMap.put(getSize(),SpriteManager.resize(workingPic,getSize()));}
    }

    public void paint(Graphics2D gr){
        switch (state){
            default:
                gr.drawImage(getSpriteManager().nextSprite(),pos.getX_asPixel(),pos.getY_asPixel(),null);
                break;
            case UNDER_CONSTRUCTION:
            case UNDER_REPAIR:
                gr.drawImage(workingPicMap.get(getSize()),pos.getX_asPixel(),pos.getY_asPixel(),null);
                break;
            case USED:
                getSpriteManager().start();
                gr.drawImage(getSpriteManager().nextSprite(),pos.getX_asPixel(),pos.getY_asPixel(),null);
                break;
            case FREE:
                getSpriteManager().stop();
                gr.drawImage(getSpriteManager().nextSprite(),pos.getX_asPixel(),pos.getY_asPixel(),null);
                break;
            case NOT_OPERABLE:
                getSpriteManager().stop();
                gr.drawImage(getSpriteManager().nextSprite(),pos.getX_asPixel(),pos.getY_asPixel(),null);
                GameField.centerString(gr,GameField.getBlockAsRectangle(this),"Not operable");
                break;
        }
        drawBorder(gr);
    }

    protected void drawBorder(Graphics2D gr){
        gr.setColor(Color.BLACK);
        gr.drawRect(pos.getX_asPixel(),pos.getY_asPixel(),size.getX_asPixel(),size.getY_asPixel());
    }
}

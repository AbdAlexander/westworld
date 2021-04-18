package Model.Blocks;

import Model.People.Operator;
import Model.People.Repairman;
import Model.People.Visitor;
import Model.Position;
import View.spriteManagers.DynamicSpriteManager;
import View.spriteManagers.OneColorSpriteManager;
import View.spriteManagers.OnePicDynamicSpriteManager;
import View.spriteManagers.SpriteManager;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;


public class Game extends Block implements Queueable{
    private SpriteManager spriteManager;
    private static final int MAX_QUEUE_LENGTH=100;
    private int ticketCost;
    private final ArrayBlockingQueue<Visitor> playingVisitors;
    private final ArrayBlockingQueue<Visitor> queue;
    private ArrayList<Operator> workers;
    private final int capacity;
    private final int cooldownTime;
    public Repairman repairer;
    public GameType type;
    private static final int MIN_VISITOR_TO_START=2;

    // Implemented preset types of games
    public Game(GameType type,Position pos) {
        this.type = type;
        queue=new ArrayBlockingQueue<>(MAX_QUEUE_LENGTH);
        if (type == GameType.DODGEM) {
            this.buildingCost = 300;
            this.upkeepCost = 50;
            this.popularityIncrease=1.4;
            this.state=BlockState.UNDER_CONSTRUCTION;
            this.ticketCost=25;
            this.capacity=20;
            this.size= new Position(2, 2,false);
            this.pos = pos;
            this.cooldownTime=10;
            this.buildingTime = 5 * cooldownTime;
            playingVisitors = new ArrayBlockingQueue<>(this.capacity);
            this.workers=new ArrayList<Operator>();
        } else if (type == GameType.FERRISWHEEL) {
            this.buildingCost = 600;
            this.upkeepCost = 150;
            this.popularityIncrease=1.7;
            this.state=BlockState.UNDER_CONSTRUCTION;
            this.ticketCost=40;
            this.capacity=20;
            this.size= new Position(2, 2,false);
            this.pos = pos;
            this.cooldownTime = 5;
            this.buildingTime = 5 * cooldownTime;
            playingVisitors = new ArrayBlockingQueue<>(this.capacity);
            this.workers=new ArrayList<Operator>();
        } else if (type == GameType.RODEO)
        {
            this.buildingCost = 270;
            this.upkeepCost = 40;
            this.popularityIncrease=1.3;
            this.state=BlockState.UNDER_CONSTRUCTION;
            this.ticketCost=30;
            this.capacity=3;
            this.size= new Position(1, 1,false);
            this.pos = pos;
            this.cooldownTime = 4;
            this.buildingTime = 5 * cooldownTime;
            playingVisitors = new ArrayBlockingQueue<>(this.capacity);
            this.workers=new ArrayList<Operator>();
        } else if( type == GameType.ROLLERCOASTER) {
            this.buildingCost = 800;
            this.upkeepCost = 200;
            this.popularityIncrease=2.1;
            this.state=BlockState.UNDER_CONSTRUCTION;
            this.ticketCost=60;
            this.capacity=15;
            this.size= new Position(2, 3,false);
            this.pos = pos;
            this.cooldownTime = 5;
            this.buildingTime = 5 * cooldownTime;
            playingVisitors = new ArrayBlockingQueue<>(this.capacity);
            this.workers=new ArrayList<Operator>();
        } else if(type == GameType.SHOOTINGGALLERY) {
            this.buildingCost = 200;
            this.upkeepCost = 30;
            this.popularityIncrease=1.1;
            this.state=BlockState.UNDER_CONSTRUCTION;
            this.ticketCost=20;
            this.capacity=5;
            this.size= new Position(2, 2,false);
            this.pos = pos;
            this.cooldownTime = 3;
            this.buildingTime = 5 * cooldownTime;
            playingVisitors = new ArrayBlockingQueue<>(this.capacity);
            this.workers=new ArrayList<Operator>();
        }
        else throw new RuntimeException("Gametype not found at creating game, or not yet implemented");

        setupImage();
    }

    @Override
    public Color getColor() {
        return Color.red;
    }

    public void addWorker(Operator o){workers.add(o);}
    public void addVisitor(Visitor v){
        queue.add(v);
    }

    public int getCooldownTime() {
        return cooldownTime;
    }

    /*
    public void run(){
        if(this.state.equals(BlockState.FREE))
        {
            queue.clear();
            this.setState(BlockState.USED);
            System.out.println("Game is running...");
            currentActivityTime = cooldownTime;
            this.setCondition(this.getCondition()-2);
        }
        else throw new RuntimeException("You can't run the game cause it's not free!");
    }*/

    public void fillWithWorkers()
    {
        workers.add(new Operator(new Position(this.getPos().getX_asIndex(),this.getPos().getY_asIndex(),true),25,this));
        workers.add(new Operator(new Position(this.getPos().getX_asIndex(),this.getPos().getY_asIndex(),true),25,this));
    }
    private boolean isOperable(){
        return workers.size()>0;
    }

    @Override
    public void roundHasPassed(int minutesPerSecond)
    {
        decreaseCurrentActivityTime(minutesPerSecond);

        //finished activity
        if(getState()==BlockState.UNDER_CONSTRUCTION && currentActivityTime==0){fillWithWorkers();constructionFinished();}
        if(getState()==BlockState.UNDER_REPAIR && currentActivityTime==0){
            repairFinished();
        }
        if(getState()==BlockState.NOT_OPERABLE && isOperable() && !needRepair()){setState(BlockState.FREE);}
        if (getState()==BlockState.USED && currentActivityTime==0){playingFinished();}

        //need to start activity
        if(getState()==BlockState.FREE && needRepair()){setState(BlockState.NOT_OPERABLE);}
        if(getState()==BlockState.FREE && !isOperable()){setState(BlockState.NOT_OPERABLE);}

        if (getState()==BlockState.FREE && isOperable() && !needRepair() && queue.size()>0 && playingVisitors.size()<capacity){
            startPlaying();
        }
    }

    private void startPlaying(){
        if(queue.size() >= MIN_VISITOR_TO_START){
            setState(BlockState.USED);
            for(int i = 0; i<=getCapacity() && !queue.isEmpty(); ++i){
                Visitor v= queue.poll();
                playingVisitors.add(v);
                v.startPlaying();
            }
            currentActivityTime=getCooldownTime();
        }
    }

    private void playingFinished(){
        setState(BlockState.FREE);
        condition-=2;
        while(!playingVisitors.isEmpty()){
            Visitor v=playingVisitors.poll();
            v.finishedPlaying();
        }
    }

    public int getTicketCost() {
        return ticketCost;
    }

    public int getCapacity() {
        return capacity;
    }

    public ArrayBlockingQueue<Visitor> getQueue() {
        return queue;
    }

    public ArrayList<Operator> getWorkers() {
        return workers;
    }

    public int getBuildingTime() {
        return buildingTime;
    }

    public void setTicketCost(int ticketCost) {
        this.ticketCost = ticketCost;
    }

    public void setWorkers(ArrayList<Operator> workers) {
        this.workers = workers;
    }

    public void setBuildingTime(int buildingTime) {
        this.buildingTime = buildingTime;
    }

    public ArrayBlockingQueue<Visitor> getPlayingVisitors() {
        return playingVisitors;
    }

    @Override
    public String toString() {
        return "Game{" +
                "Type=" + type +
                "ticketCost=" + ticketCost +
                ", queue=" + queue.size() +
                ", workers=" + workers +
                ", capacity=" + capacity +
                ", cooldownTime=" + cooldownTime +
                '}' + super.toString();
    }

    @Override
    public String getName()
    {
        switch(this.type)
        {
            case FERRISWHEEL: return "Ferris Wheel";
            case RODEO: return "Rodeo";
            case ROLLERCOASTER: return "Roller Coaster";
            case DODGEM: return "Dodgem";
            case SHOOTINGGALLERY: return "Shooting Gallery";
            default : return "undefined";
        }
    }


    private void setupImage(){
        if(Objects.isNull(type)){throw new IllegalStateException("unknown type");}
        switch (type){
            case ROLLERCOASTER:
                List<Rectangle> rectangles= Arrays.asList(
                        new Rectangle(0,0,150,227),
                        new Rectangle(150,0,150,227),
                        new Rectangle(300,0,150,227)
                );
                spriteManager=new OnePicDynamicSpriteManager("graphics/rollercoaster.png",size,rectangles,10);
                break;
            case FERRISWHEEL:
                List<String> imgPaths = Arrays.asList("graphics/ferriswheel1.png", "graphics/ferriswheel2.png");
                spriteManager=new DynamicSpriteManager(imgPaths,size,5);
                break;
            case RODEO:
                imgPaths = Arrays.asList("graphics/rodeo1.png", "graphics/rodeo2.png");
                spriteManager=new DynamicSpriteManager(imgPaths,size,5);
                break;
            case SHOOTINGGALLERY:
                imgPaths = Arrays.asList("graphics/shooting_1.png", "graphics/shooting_2.png");
                spriteManager=new DynamicSpriteManager(imgPaths,size,5);
                break;
            default:
                spriteManager=new OneColorSpriteManager(getColor(),getSize()); break;
        }
    }

    @Override
    protected SpriteManager getSpriteManager() {
        return spriteManager;
    }
}

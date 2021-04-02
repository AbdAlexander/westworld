package Model.Blocks;

import Model.People.Operator;
import Model.People.Visitor;
import Model.Position;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;



public class Game extends Block implements Queueable{
    private static final HashMap<GameType,BufferedImage> imgMap=new HashMap<>();
    private int ticketCost;
    private final ArrayBlockingQueue<Visitor> queue;
    private ArrayList<Operator> workers;
    private int capacity;
    private int cooldownTime;
    private int buildingTime;
    private int currentActivityTime;
    public GameType type;

    @Deprecated
    public Game(int buildingCost, int upkeepCost, double popularityIncrease, BlockState state, int ticketCost, int capacity, Position size, Position pos, int cooldownTime) {
        super(buildingCost, upkeepCost, popularityIncrease, state, size, pos);
        this.ticketCost = ticketCost;
        this.capacity = capacity;
        this.queue = new ArrayBlockingQueue<>(capacity);
        this.cooldownTime = cooldownTime;
        this.buildingTime = 5 * cooldownTime;
        this.workers=new ArrayList<Operator>();
        setupImage();
    }
    @Deprecated
    public Game(Position size, Position pos) {
        super(0, 0, 0, BlockState.FREE, size, pos);
        this.ticketCost = 0;
        this.capacity = 0;
        this.queue = new ArrayBlockingQueue<>(capacity);
        setupImage();
    }
    // Implemented preset types of games
    public Game(GameType type,Position pos) {
        this.type = type;
        if (type == GameType.DODGEM) {
            this.buildingCost = 300;
            this.upkeepCost = 50;
            this.popularityIncrease=1.4;
            this.state=BlockState.UNDER_CONSTRUCTION;
            this.ticketCost=25;
            this.capacity=20;
            this.size= new Position(2, 2,false);
            this.pos = pos;
            this.cooldownTime=5;
            this.buildingTime = 5 * cooldownTime;
            queue = new ArrayBlockingQueue<>(this.capacity);
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
            this.cooldownTime = 3;
            this.buildingTime = 5 * cooldownTime;
            queue = new ArrayBlockingQueue<>(this.capacity);
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
            this.cooldownTime = 2;
            this.buildingTime = 5 * cooldownTime;
            queue = new ArrayBlockingQueue<>(this.capacity);
            this.workers=new ArrayList<Operator>();
        } else if( type == GameType.ROLLERCOASTER) {
            this.buildingCost = 800;
            this.upkeepCost = 200;
            this.popularityIncrease=2.1;
            this.state=BlockState.UNDER_CONSTRUCTION;
            this.ticketCost=60;
            this.capacity=15;
            this.size= new Position(3, 2,false);
            this.pos = pos;
            this.cooldownTime = 5;
            this.buildingTime = 5 * cooldownTime;
            queue = new ArrayBlockingQueue<>(this.capacity);
            this.workers=new ArrayList<Operator>();
        } else if(type == GameType.SHOOTINGGALLERY) {
            this.buildingCost = 200;
            this.upkeepCost = 30;
            this.popularityIncrease=1.1;
            this.state=BlockState.UNDER_CONSTRUCTION;
            this.ticketCost=20;
            this.capacity=5;
            this.size= new Position(1, 1,false);
            this.pos = pos;
            this.cooldownTime = 2;
            this.buildingTime = 5 * cooldownTime;
            queue = new ArrayBlockingQueue<>(this.capacity);
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
        if( this.getState() == BlockState.FREE ) queue.add(v);
        else throw new RuntimeException("Visitor tried to get into queue, but state of Game wasn't 'FREE' ");
    }

    public int getCooldownTime() {
        return cooldownTime;
    }

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
    }

    public void roundHasPassed(int minutesPerSecond)
    {
        if(workers.size() <= 1 )
        {
            state = BlockState.NOT_OPERABLE;
            return;
        }
        if(state.equals(BlockState.UNDER_CONSTRUCTION))
        {
            buildingTime-=minutesPerSecond;
        }
        if(state.equals(BlockState.USED))
        {
            currentActivityTime-=minutesPerSecond;
        }
        else if(buildingTime == 0 && !(state.equals(BlockState.USED))) {
            state = BlockState.FREE;
        }
        else if(state.equals(BlockState.FREE) && queue.remainingCapacity()==0)
        {
            workers.get(0).operate();
        }
        if(state.equals(BlockState.UNDER_REPAIR))
        {
            currentActivityTime -= minutesPerSecond;
        }
        if(state.equals(BlockState.UNDER_REPAIR) && currentActivityTime <= 0 )
        {
            state = BlockState.FREE;
            currentActivityTime = 0;
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

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setCooldownTime(int cooldownTime) {
        this.cooldownTime = cooldownTime;
    }

    public void setBuildingTime(int buildingTime) {
        this.buildingTime = buildingTime;
    }


    @Override
    public String toString() {
        return "Game{" +
                "Type=" + type +
                "ticketCost=" + ticketCost +
                ", queue=" + queue +
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

    private String getImagePath(){
        switch (this.type) {
            case FERRISWHEEL:
                return "graphics/ferriswheel2.png";
            case ROLLERCOASTER:
                return "graphics/ROLLERCOASTER.png";
            default:
                return "undefined";
        }
    }

    private void setupImage(){
        String imgPath=getImagePath();
        try {
            if(!imgMap.containsKey(type) && !imgPath.equals("undefined")){
                BufferedImage i= ImageIO.read(new File(imgPath));
                BufferedImage img=resize(i, size.getX_asPixel(),size.getY_asPixel());
                imgMap.put(type,img);
            }
        } catch (IOException e) {
            System.err.println(imgPath+" not found");
        }
    }

    @Override
    public void paint(Graphics2D gr) {
        if(!imgMap.containsKey(type)){
            gr.setColor(getColor());
            gr.fillRect(pos.getX_asPixel(),pos.getY_asPixel(),size.getX_asPixel(),size.getY_asPixel());
        }else{
            gr.drawImage(imgMap.get(type),pos.getX_asPixel(),pos.getY_asPixel(),DEFAULT_BACKGROUNG_COLOR,null);
        }
        gr.setColor(Color.BLACK);
        gr.drawRect(pos.getX_asPixel(),pos.getY_asPixel(),size.getX_asPixel(),size.getY_asPixel());
    }
}

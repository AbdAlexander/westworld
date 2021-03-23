package Model.Blocks;

import Model.People.Employee;
import Model.People.Operator;
import Model.People.Visitor;
import Model.Position;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

public class ServiceArea extends Block {
    private int menuCost;
    private ArrayBlockingQueue<Visitor> queue;
    private ArrayList<Employee> workers;
    private int capacity;
    private ServiceType type;

    // TODO: service area-ba is kéne valami kis cooldown, hogy a sorban állásnak legyen értelme, és ne daráljuk le őket egyből.
    public ServiceArea(int buildingCost, int upkeepCost, double popularityIncrease, BlockState state, int menuCost, int capacity) {
        super(buildingCost, upkeepCost, popularityIncrease, state);
        this.menuCost = menuCost;
        this.capacity = capacity;
        workers = new ArrayList<>();
        queue = new ArrayBlockingQueue<>(capacity);
    }

    public ServiceArea(ServiceType type, Position pos) {
        this.type=type;
        if(type==ServiceType.BUFFET)
        {
            buildingCost = 100;
            upkeepCost = 10;
            popularityIncrease = 1.0;
            state = BlockState.UNDER_CONSTRUCTION;
            this.menuCost = 15;
            this.capacity = 50;
            this.size=new Position(3,1,false);
            workers = new ArrayList<>();
            queue = new ArrayBlockingQueue<>(capacity);
        }
        if(type==ServiceType.TOILET)
        {
            buildingCost = 75;
            upkeepCost = 10;
            popularityIncrease = 1.0;
            state = BlockState.UNDER_CONSTRUCTION;
            this.menuCost = 3;
            this.capacity = 25;
            this.size=new Position(1,2,false);
            workers = new ArrayList<>();
            queue = new ArrayBlockingQueue<>(this.capacity);
        }

    }

    public void addWorker(Operator o){workers.add(o);}
    public void addVisitor(Visitor v){
        if( this.getState() == BlockState.FREE ) queue.add(v);
        else throw new RuntimeException("Visitor tried to get into queue, but state of Service Area wasn't 'FREE' ");
        }

    @Override
    public Color getColor() {
        return Color.blue;
    }



    public int getTicketCost() {
        return menuCost;
    }

    public int getCapacity() {
        return capacity;
    }

    @Override
    public String toString() {
        return "ServiceArea type : " + type + " " +
                "menuCost=" + menuCost +
                ", queue=" + queue +
                ", workers=" + workers + super.toString();

    }
}

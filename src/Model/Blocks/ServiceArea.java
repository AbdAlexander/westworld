package Model.Blocks;

import Model.People.Employee;
import Model.People.Operator;
import Model.People.Visitor;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

public class ServiceArea extends Block {
    private int menuCost;
    private ArrayBlockingQueue<Visitor> queue;
    private ArrayList<Employee> workers;
    private int capacity;

    public ServiceArea(int buildingCost, int upkeepCost, double popularityIncrease, BlockState state, int menuCost, int capacity) {
        super(buildingCost, upkeepCost, popularityIncrease, state);
        this.menuCost = menuCost;
        this.capacity = capacity;
        workers = new ArrayList<>();
        queue = new ArrayBlockingQueue<>(capacity);
    }

    public void addWorker(Operator o){workers.add(o);}
    public void addVisitor(Visitor v){queue.add(v);}

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
}
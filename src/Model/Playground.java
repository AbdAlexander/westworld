package Model;

import Model.Blocks.*;
import Model.People.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;

import static View.MainWindow2.NUM_OF_COLS;
import static View.MainWindow2.NUM_OF_ROWS;

//TODO: Visitor happiness, befolyásolja a Popularity-t, amikor elmegy a parkból a visitor
//TODO: Visitor teleportáljon a játékokhoz

public class Playground {

    /* Adattagok */
    public Block[][] blocks;
    private ArrayList<Block> buildedObjects;
    private ArrayList<Game> builedGames;
    private ArrayList<ServiceArea> builededServices;

    private ArrayList<Visitor> visitors;

    private ArrayList<Caterer> cateres;
    private ArrayList<Cleaner> cleaners;
    private ArrayList<Operator> operators;
    private ArrayList<Repairman> repairmen;

    private int money;
    private int days, hours, minutes;
    private double popularity;

    Position entrancePosition;

    /* Konstruktor */
    public Playground() {
        blocks              = new Block[NUM_OF_COLS][NUM_OF_ROWS];
        buildedObjects      = new ArrayList<>();
        builedGames         = new ArrayList<>();
        builededServices    = new ArrayList<>();
        visitors            = new ArrayList<>();
        cateres             = new ArrayList<>();
        cleaners            = new ArrayList<>();
        operators           = new ArrayList<>();
        repairmen           = new ArrayList<>();



        money = 100_000;
        days = 1; hours = 8; minutes = 0;
        popularity = 0;

    }

    /* Metódusok */

    /**
     * Metódus segítségével X,Y pozícióba lehet egy objektumot építeni
     * @param block : Megépítendő block
     * @param posX
     * @param posY
     * @return  false: HA adott blockon már van valami építve
     *          true: Ha adott blockon nincs még semmi építve
     */
    public boolean buildBlock(Block block, int posX, int posY) {
        if(!(blocks[posX][posY] instanceof FreePlace) && !Objects.isNull(blocks[posX][posY])) return false;
        blocks[posX][posY] = block;
        return true;
    }
    public void demolishBlock(FreePlace block, int posX, int posY) {
        blocks[posX][posY] = block;
    }

    /**
     * Megkapjuk a blockot, annak a pozíció és size alapján kiszámítjuk mettől meddig tart, majd végigmegyünk
     * a blockokon, ha valahol nem freeplace van visszatérünk false-szal
     * @param block Megépítendő block
     * @return  False egyből, ha már az első helyen nem freeplace van
     *          False literálisan, ha valamelyik helyen nem freeplace van
     *          True ha az összesen helyen freeplace van
     */
    public boolean isBuildable(Block block){ //todo implement for Entrance
        int blockFromX = block.getPos().getX_asIndex();
        int blockFromY = block.getPos().getY_asIndex();


        if(blockFromX<0 || blockFromX >=NUM_OF_COLS ||blockFromY<0 || blockFromY >=NUM_OF_ROWS ){return false;} //Nincs a fieldben

        if(Objects.isNull(blocks[block.getPos().getX_asIndex()][block.getPos().getY_asIndex()])){return true;}

        if(block instanceof GarbageCan){
            if(blocks[block.getPos().getX_asIndex()][block.getPos().getY_asIndex()] instanceof Road){
                return !((Road) blocks[block.getPos().getX_asIndex()][block.getPos().getY_asIndex()]).isHasGarbageCan();
            }
            return false;
        }
        if(!(blocks[block.getPos().getX_asIndex()][block.getPos().getY_asIndex()] instanceof FreePlace) ) return false;


        int blockMaxX = blockFromX + block.getSize().getX_asIndex();
        int blockMaxY = blockFromY + block.getSize().getY_asIndex();
        if(blockMaxX > NUM_OF_COLS || blockMaxY >NUM_OF_ROWS){return false;}// Ha kilóg, akkor nyilván nem építhető

        for(int x=blockFromX; x<blockMaxX; ++x)
            for(int y=blockFromY; y<blockMaxY; ++y)
                if(!(blocks[x][y] instanceof FreePlace)) return false;
        return true;
    }

    public boolean hire(Employee e) {
        int salary = e.getSalary();
        if(money < salary) return false;

        if(e instanceof Caterer) {
            cateres.add((Caterer) e);
            ((Caterer) e).workPlace.addWorker((Caterer) e);
            return true;
        } else if(e instanceof Cleaner) {
            cleaners.add(new Cleaner(new Position(0,0,false), salary));
            return true;
        }
        else if(e instanceof Operator) {
            operators.add(new Operator(new Position(0,0,false), salary));
            return true;
        }
        else if(e instanceof Repairman) {
            repairmen.add(new Repairman(new Position(0,0,false), salary));
            return true;
        }
        else return false;
    }

    public boolean fire(Employee e){
        if(e instanceof Caterer) {
            if (cateres.size() > 0) {
                cateres.remove(cateres.size() - 1);
                ((Caterer) e).workPlace.getWorkers().remove(e);
                return true;
            } else { return false; }
        }
        else if(e instanceof Cleaner) {
            if(cleaners.size() > 0) {
                cleaners.remove(cleaners.size()-1);
                return true;
            } else { return false; }
        }
        else if(e instanceof Operator) {
            if(operators.size() > 0) {
                operators.remove(operators.size()-1);
                return true;
            } else { return false; }
        }
        else if(e instanceof Repairman) {
            if(repairmen.size() > 0) {
                repairmen.remove(repairmen.size()-1);
                return true;
            } else { return false; }
        }
        return false;
    }

    public boolean findRoute(Visitor visitor, Position start, Position destination) {
        boolean visited[][] = new boolean[NUM_OF_COLS][NUM_OF_ROWS];

        for (int i = 0; i < NUM_OF_COLS; i++)
            for (int j = 0; j < NUM_OF_ROWS; j++)
                if (i == start.getX_asIndex() && j == start.getY_asIndex() && !visited[i][j])
                    if (isPath(i, j, visited, destination, visitor)) {
                        visitor.getPathPositionList().add(new Position(i,j,false));
                        return true;
                    }

        return false;
    }
    public boolean isSafe(int i, int j) {
        if (i >= 0 && i < blocks.length && j >= 0 && j < blocks[0].length)
            return true;

        return false;
    }

    public boolean isPath(int i, int j, boolean visited[][], Position destination, Visitor visitor) {
        if (isSafe(i, j) &&
                (blocks[i][j] instanceof Road ||
                        blocks[i][j] instanceof Game
                                && blocks[i][j].getPos().getX_asIndex() == i && blocks[i][j].getPos().getY_asIndex() == j  ||
                        blocks[i][j] instanceof ServiceArea
                                && blocks[i][j].getPos().getX_asIndex() == i && blocks[i][j].getPos().getY_asIndex() == j) &&
                !visited[i][j]) {

            visited[i][j] = true;

            if (i == destination.getX_asIndex() && j == destination.getY_asIndex()){
                visitor.getPathPositionList().add(new Position(i,j,false));
                return true;
            }

            boolean down = isPath(i + 1, j, visited, destination, visitor);
            if (down){
                visitor.getPathPositionList().add(new Position(i,j,false));
                return true;
            }

            boolean up = isPath(i - 1, j, visited, destination, visitor);
            if (up){
                visitor.getPathPositionList().add(new Position(i,j,false));
                return true;
            }

            boolean right = isPath(i, j + 1, visited, destination, visitor);
            if (right) {
                visitor.getPathPositionList().add(new Position(i,j,false));
                return true;
            }

            boolean left = isPath(i, j - 1, visited, destination, visitor);
            if (left){
                visitor.getPathPositionList().add(new Position(i,j,false));
                return true;
            }

        }
        return false;
    }

    public ArrayList getEmployeesLike(Employee e){
        if(e instanceof Caterer){return getCateres();}
        if(e instanceof Cleaner){return getCleaners();}
        if(e instanceof Operator){return getOperators();}
        if(e instanceof Repairman){return getRepairmen();}

        return null;
    }


    /* Getterek / Setterek */
    public int getMoney()                                   { return money; }
    public int getHours()                                   { return hours; }
    public int getMinutes()                                 { return minutes; }
    public int getDays()                                    { return days; }
    public double getPopularity()                           { return popularity; }
    public Position getEntrancePosition()                   { return entrancePosition; }
    public Block getBlockByPosition(Position pos)           { return blocks[pos.getX_asIndex()][pos.getY_asIndex()]; }

    public BlockState getBlockState(Block block)            { return block.getState(); }
    public Color getColor(Block block)                      { return block.getColor(); }
    public Block[][] getBlocks()                            { return blocks; }
    public ArrayList<Block> getBuildedObjectList()          { return buildedObjects; }
    public ArrayList<Game> getBuildedGameList()             { return builedGames; }
    public ArrayList<ServiceArea> getBuildedServiceList()   { return builededServices; }

    public ArrayList<Visitor> getVisitors()                 { return visitors; }
    public ArrayList<Caterer> getCateres()                  { return cateres; }
    public ArrayList<Cleaner> getCleaners()                 { return cleaners; }
    public ArrayList<Operator> getOperators()               { return operators; }
    public ArrayList<Repairman> getRepairmen()              { return repairmen; }

    public void setMoney(int money)                         { this.money = money; }
    public void setDays(int days)                           { this.days = days; }
    public void setHours(int hours)                         { this.hours = hours; }
    public void setMinutes(int minutes)                     { this.minutes = minutes; }
    public void setPopularity(double popularity)            { this.popularity = popularity; }
    public void setBuildedObjects(ArrayList<Block> buildedObjects) { this.buildedObjects = buildedObjects; }

    public String dateToString() {
        return  " Day: " + this.days +
                " Hour: " + this.hours +
                " Minutes: " + this.minutes;
    }
}

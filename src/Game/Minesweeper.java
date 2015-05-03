package Game;

import PathFinding.GameMap;
import java.awt.Point;

public class Minesweeper {

    public  enum Personality {
        CALM, CLEVER, MAD
    }
    private Point pos;
    private int angle=0;
    private int health=100;
    private int fuel=100;
    private int exp=0;
    private Personality personality=Personality.CLEVER;
    
    public Minesweeper(Point poz) {this.pos=poz;}
    
    public Point getPos(){return pos;}
    public int getAngle(){return angle;}
    public int getHealth(){return health;}
    public int getFuel(){return fuel;}
    public int getExp(){return exp;}
    public Personality getPersonality(){return personality;}
    
    public void setPos(int x, int y){pos.x=x; pos.y=y;}
    public void setPos(Point pos){this.pos=pos;}
    public void setAngle(int angle){this.angle=angle;}
    public void setHealth(int health){this.health=health;}
    public void setFuel(int fuel){this.fuel=fuel;}
    public void setExp(int exp){this.exp=exp;}
    public void setPersonality(Personality personality){this.personality=personality;}
    
    public void move(int x, int y){pos.x+=x; pos.y+=y;}
    public void move(Point offset){pos.x+=offset.x; pos.y+=offset.y;}
    public void rotate(int angle){this.angle+=angle; this.angle %= 360;}
    public void decreaseFuel(int amount){if(fuel>amount) fuel-=amount; else fuel=0;}
    public void increaseExp (int amount){exp+=amount;}
    public void harm(int bomb){
        int amount = 0;
        if (bomb == GameMap.EASY_BOMB) 
            amount = 20;
        else if (bomb == GameMap.MEDIUM_BOMB) 
            amount = 40;
        else if (bomb == GameMap.HARD_BOMB) 
            amount = 60;
        
        if(health > amount) 
            health -= amount; 
        else 
            health = 0;
    }
}
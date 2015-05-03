package PathFinding;

import Game.Minesweeper;
import PathFindingFramework.Mover;

public class UnitMover implements Mover {

    private Minesweeper.Personality personality;

    public UnitMover(Minesweeper.Personality personality) {
        this.personality=personality;
    }
    
    public Minesweeper.Personality getPersonality()
    {
        return personality;
    }
}


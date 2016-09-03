
package kouyoum.mccrario;


import java.awt.Color;
import java.awt.Graphics2D;


public class GoalTile extends Tile
{
  public void render (Graphics2D g)
  {
    if (DebugInfo.debug) {
      g.setColor(Color.BLUE);
      g.fillRect(0, 0, 1, 1);
    }
  }
  
  
  public boolean isSolid ()
  {
    return false;
  }
}

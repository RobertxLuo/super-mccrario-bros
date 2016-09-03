
package kouyoum.mccrario;


import java.awt.Graphics2D;
import java.awt.Image;


public abstract class Tile
{
  public static final Class[] subclasses = {
    Air.class, SurfaceBlock.class, UndergroundBrick.class, GoalTile.class,
    BrokenBrick.class, GrassBlock.class, BossBrick.class, BossStop.class
  };
  
  
  private Image image;
  
  
  public void render (Graphics2D g)
  {
    g.drawImage(image, 0, 1, 1, -1, null);
  }
  
  
  public boolean isSolid ()
  {
    return true;
  }
  
  
  public void setImage (Image image)
  {
    this.image = image;
  }
}

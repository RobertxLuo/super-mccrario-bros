
package kouyoum.mccrario;


import java.awt.Graphics2D;


public class Checkpoint extends Entity
{
  public Checkpoint (Vector pos)
  {
    super(pos, new Size(1.0f, 2.0f), new Size(1.0f, 2.0f));
    setImage(Resources.CHECKPOINT);
  }
  
  
  public void update (float dt)
  {
  }
  
  
  public void render (Graphics2D g)
  {
    if (DebugInfo.debug)
      super.render(g);
  }
  
  
  public boolean collidesWith (Entity ent)
  {
    return false;
  }
  
  
  public boolean collidesWith (Tile tile)
  {
    return false;
  }
  
  
  public boolean isSolid ()
  {
    return false;
  }
}

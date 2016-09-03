
package kouyoum.mccrario;


import java.awt.Graphics2D;


public class Stopper extends Entity
{
  public Stopper (Vector pos)
  {
    super(pos, new Size(1.0f, 0.3f), new Size(1.0f, 1.0f));
    setImage(Resources.STOPPER);
    setDistFromFloor(0.5f);
  }
  
  
  public void update (float dt)
  {
  }
  
  
  public void render (Graphics2D g)
  {
    if (DebugInfo.debug)
      super.render(g);
  }
  
  
  public void collideEntity (Entity ent, Direction side, float dist)
  {
  }
  
  
  public boolean isSolid ()
  {
    return false;
  }
}

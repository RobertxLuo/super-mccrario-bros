
package kouyoum.mccrario;


import java.awt.Graphics2D;


public class BrokenBrick extends Tile
{
  private final float BREAKTIME = 0.75f;
  private float broken = 0.0f;
  private boolean breaking = false;
  private boolean halfway = false;
  
  
  public BrokenBrick ()
  {
    setImage(Resources.BROKENBRICK0);
  }
  
  
  public void startBreaking ()
  {
    if (!breaking)
      setImage(Resources.BROKENBRICK1);
    breaking = true;
  }
  
  
  public void update (float dt)
  {
    if (breaking)
      broken += dt;
    
    if (broken > BREAKTIME/2.0f && !halfway) {
      setImage(Resources.BROKENBRICK2);
      halfway = true;
    }
  }
  
  
  public void render (Graphics2D g)
  {
    if (DebugInfo.debug || broken < BREAKTIME)
      super.render(g);
  }
  
  
  public boolean isSolid ()
  {
    return broken < BREAKTIME;
  }
}

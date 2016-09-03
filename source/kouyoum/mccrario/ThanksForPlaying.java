
package kouyoum.mccrario;


import java.awt.Graphics2D;


public class ThanksForPlaying extends Entity
{
  public ThanksForPlaying (Vector pos)
  {
    super(pos, new Size(1.0f, 1.0f), new Size(10.0f, 1.0f));
    setImage(Resources.THANKS);
  }
  
  
  public void update (float dt)
  {
  }
  
  
  public void collideEntity (Entity ent, Direction side, float dist)
  {
  }
  
  
  public boolean isSolid ()
  {
    return false;
  }
}


package kouyoum.mccrario;


import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;


public class BossBrick extends Tile
{
  private boolean exists = false;
  
  
  public BossBrick ()
  {
    setImage(Resources.SURFACEBLOCK);
  }
  
  
  public void setExists (boolean exists)
  {
    this.exists = exists;
  }
  
  
  public void render (Graphics2D g)
  {
    if (exists)
      super.render(g);
    
    if (DebugInfo.debug) {
      super.render(g);
      
      g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
      g.setColor(Color.RED);
      g.fillRect(0, 0, 1, 1);
      g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
    }
  }
  
  
  public boolean isSolid ()
  {
    return exists;
  }
}

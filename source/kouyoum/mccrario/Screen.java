
package kouyoum.mccrario;


import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import java.awt.geom.AffineTransform;

import javax.swing.JPanel;


public class Screen extends JPanel
{
  private static final float PIXELS_IN_WORLD = 50.0f;
  public static int SCREEN_WIDTH = (int) (650.0f/43.0f * PIXELS_IN_WORLD);
  public static int SCREEN_HEIGHT = (int) (450.0f/650.0f * SCREEN_WIDTH);
  
  private Level level;
  private boolean paused = false;
  private Image bgImage = null;
  private boolean logoScreen = false;
  
  
  public static Vector screenToWorld (int x, int y, Vector camera)
  {
    Vector world = new Vector(x, y);
    world.sub(new Vector(SCREEN_WIDTH/2, SCREEN_HEIGHT/2));
    world.x /= PIXELS_IN_WORLD;
    world.y /= -PIXELS_IN_WORLD;
    world.sub(new Vector(-0.5f, -0.5f));
    world.add(camera);
    
    return world; 
  }
  
  
  public Screen ()
  {
    setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
    setBackground(Color.WHITE);
    setDoubleBuffered(true);
    setFocusable(true);
  }
  
  
  public void paint (Graphics g)
  { 
    super.paint(g);
    Graphics2D g2d = (Graphics2D) g;
    
    SCREEN_WIDTH = getSize().width;
    SCREEN_HEIGHT = getSize().height;
    
    if (logoScreen) {
      bgImage = Resources.LOGO;
      int width = SCREEN_WIDTH;
      int height = bgImage.getHeight(null) * width / bgImage.getWidth(null);
      g2d.drawImage(bgImage, 0, SCREEN_HEIGHT/2-height/2, width, height, null);
      return;
    }
    
    if (bgImage != null) {
      int width = SCREEN_WIDTH;
      int height = bgImage.getHeight(null) * width / bgImage.getWidth(null);
      if (height < SCREEN_HEIGHT) {
        height = SCREEN_HEIGHT;
        width = bgImage.getWidth(null) * height / bgImage.getHeight(null);
      }
      g2d.drawImage(bgImage, 0, 0, width, height, null);
    }
    
    AffineTransform defaultTransform = g2d.getTransform();
    g2d.translate(SCREEN_WIDTH/2, SCREEN_HEIGHT/2);
    g2d.scale(PIXELS_IN_WORLD, -PIXELS_IN_WORLD);
    g2d.translate(-0.5f, -0.5f);
    
    g2d.setStroke(new BasicStroke(1.0f/PIXELS_IN_WORLD));
    
    if (level != null)
      level.render(g2d);
      
    g2d.setTransform(defaultTransform);
    
    if (paused) {
      g2d.setColor(Color.GRAY);
      g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 
                                                  0.5f));
      g2d.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
    }
  }
  
  
  public void setLevel (Level level)
  {
    this.level = level;
    
    String name = level.getName();
    if (name.equals("1-1"))
      bgImage = Resources.BG0;
    else if (name.equals("1-2"))
      bgImage = Resources.BG1;
    else if (name.equals("1-3"))
      bgImage = Resources.BG2;
    else if (name.equals("1-4"))
      bgImage = Resources.BG3;
    else if (name.equals("1-B"))
      bgImage = Resources.BG4;
    else
      bgImage = null;
  }
  
  
  public void setPaused (boolean paused)
  {
    this.paused = paused;
  }
  
  
  public void showLogo (boolean logo)
  {
    logoScreen = logo;
  }
}

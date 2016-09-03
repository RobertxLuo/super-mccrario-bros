
package kouyoum.mccrario;


import java.awt.Image;

import java.io.File;

import javax.imageio.ImageIO;


public class Resources
{
  public static Image BAAL;
  public static Image MCCRARIO0;
  public static Image SPIKEBAAL;
  public static Image BAALOON;
  public static Image STOPPER;
  public static Image CHECKPOINT;
  public static Image BOSSBAAL;
  public static Image THANKS;
  
  public static Image SURFACEBLOCK;
  public static Image UNDERGROUNDBRICK;
  public static Image BROKENBRICK0, BROKENBRICK1, BROKENBRICK2;
  public static Image GRASSBLOCK;
  
  public static Image BG0, BG1, BG2, BG3, BG4;
  
  public static Image LOGO;
  
  
  public static void load ()
  {
    BAAL = loadImage("baal.png");
    
    MCCRARIO0 = loadImage("mccrario0.png");
    
    SPIKEBAAL = loadImage("spikebaal.png");
    BAALOON = loadImage("baaloon.png");
    STOPPER = loadImage("stopper.png");
    CHECKPOINT = loadImage("checkpoint.png");
    BOSSBAAL = loadImage("bossbaal.png");
    THANKS = loadImage("thanks.png");
    
    SURFACEBLOCK = loadImage("surfaceblock.jpg");
    UNDERGROUNDBRICK = loadImage("undergroundbrick.jpg");
    
    BROKENBRICK0 = loadImage("brokenbrick0.png");
    BROKENBRICK1 = loadImage("brokenbrick1.png");
    BROKENBRICK2 = loadImage("brokenbrick2.png");
    
    GRASSBLOCK = loadImage("grassblock.jpg");
    
    BG0 = loadImage("bg0.jpg");
    BG1 = loadImage("bg1.jpg");
    BG2 = loadImage("bg2.jpg");
    BG3 = loadImage("bg3.jpg");
    BG4 = loadImage("bg4.jpg");
    
    LOGO = loadImage("logo.png");
  }
  
  
  private static Image loadImage (String filename)
  {
    filename = "resources/textures/" + filename;
    try {
      File file = new File(filename);
      return ImageIO.read(file);
    } catch (Exception e) {
      System.out.println("Unable to load resource: " + filename);
      return null;
    }
  }
}

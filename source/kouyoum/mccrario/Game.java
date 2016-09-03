
package kouyoum.mccrario;


import java.awt.Dimension;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;


public class Game
{
  private JFrame window;
  private Screen screen;
  private GameLoop loop;
  
  
  public Game ()
  {
    Resources.load();
    Audio.load();
    
    screen = new Screen();
    
    window = new JFrame("Super McCrario Bros.");
    window.addWindowListener(new WindowAdapter() {
        public void windowClosing (WindowEvent e)
        {
          Game.stop();
        }
      });
    window.add(screen);
    window.pack();
    window.setLocationRelativeTo(null);
    window.setResizable(false);
    
    Input input = new Input();
    screen.addKeyListener(input);
    screen.addMouseListener(input);
    screen.addMouseMotionListener(input);
    
    loop = new GameLoop(screen);
  }
  
  
  public void start ()
  {
    window.setVisible(true);
    screen.showLogo(true);
    screen.repaint();
    try {
      Thread.sleep(3000);
    } catch (Exception e) {}
    screen.showLogo(false);
    
    int nLevel = 1;
    int nWorld = 1;
    
    Level level = null;
    int checkpoint = 0;
    
    while (true) {
      String levelName = nWorld + "-";
      if (nLevel == 5)
        levelName += 'B';
      else
        levelName += nLevel;
      
      if (DebugInfo.debug) {
        levelName = (JOptionPane.showInputDialog(
                       "Enter the level name to load." + 
                       "\nIt should be in the form \"world-level\"."));
        
        if (levelName != null) {
          levelName = levelName.toUpperCase();
          
          nWorld = levelName.charAt(0) - '0';
          nLevel = levelName.charAt(2) == 'B'? 5 : levelName.charAt(2) - '0';
        }
      }
      
      DebugInfo.debug = false;
      
      if (levelName == null && level == null)
        break;
      if (levelName == null)
        level.continueRunning();
      else
        level = new Level(levelName, checkpoint);
      loop.play(level);
      
      if (level.didWin()) {
        ++nLevel;
        checkpoint = 0;
      } else {
        checkpoint = level.getCheckpoint();
      }
      if (nLevel > 5)
        break;
    }
    
    Audio.stop();
    screen.showLogo(true);
    screen.repaint();
    try {
      Thread.sleep(3000);
    } catch (Exception e) {}
    screen.showLogo(false);
    
    stop();
  }
  
  
  public static void stop ()
  {
    Audio.stop();
    
    System.exit(0);
  }
}

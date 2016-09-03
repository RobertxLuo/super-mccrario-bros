
package kouyoum.mccrario;


import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import java.util.List;
import java.util.ArrayList;


public class Input implements KeyListener, MouseListener, MouseMotionListener
{
  private static List<Integer> keysDown = new ArrayList<Integer>();
  private static List<Integer> keysOvrd = new ArrayList<Integer>();
  private static boolean[] mouseButtons = new boolean[3];
  private static int mouseX = 0, mouseY = 0;
  
  
  public static boolean isKeyDown (int key)
  {
    return !keysOvrd.contains(key) && keysDown.contains(key);
  }
  
  
  public static void overrideKey (int key)
  {
    keysOvrd.add(new Integer(key));
  }
  
  
  public static void manualKeyRelease (int key)
  {
    keysDown.remove(new Integer(key));
    keysOvrd.remove(new Integer(key));
  }
  
  
  public static boolean isMouseDown (int button)
  {
    switch (button) {
      case MouseEvent.BUTTON1:
        return mouseButtons[0];
      case MouseEvent.BUTTON2:
        return mouseButtons[1];
      case MouseEvent.BUTTON3:
        return mouseButtons[2];
      default:
        return false;
    }
  }
  
  
  public static int getMouseX ()
  {
    return mouseX;
  }
  
  
  public static int getMouseY ()
  {
    return mouseY;
  }
  
  
  public void keyPressed (KeyEvent e)
  {
    if (!keysDown.contains(e.getKeyCode()))
      keysDown.add(e.getKeyCode());
  }
  
  
  public void keyReleased (KeyEvent e)
  {
    keysDown.remove(new Integer(e.getKeyCode()));
    keysOvrd.remove(new Integer(e.getKeyCode()));
  }
  
  
  public void keyTyped (KeyEvent e)
  {
  }
  
  
  public void mouseClicked (MouseEvent e)
  {
  }
  
  
  public void mouseEntered (MouseEvent e)
  {
  }
  
  
  public void mouseExited (MouseEvent e)
  {
  }
  
  
  public void mousePressed (MouseEvent e)
  {
    switch (e.getButton()) {
      case MouseEvent.BUTTON1:
        mouseButtons[0] = true;
        break;
      case MouseEvent.BUTTON2:
        mouseButtons[1] = true;
        break;
      case MouseEvent.BUTTON3:
        mouseButtons[2] = true;
        break;
    }
  }
  
  
  public void mouseReleased (MouseEvent e)
  {
    switch (e.getButton()) {
      case MouseEvent.BUTTON1:
        mouseButtons[0] = false;
        break;
      case MouseEvent.BUTTON2:
        mouseButtons[1] = false;
        break;
      case MouseEvent.BUTTON3:
        mouseButtons[2] = false;
        break;
    }
  }
  
  
  public void mouseDragged (MouseEvent e)
  {
    mouseX = e.getX();
    mouseY = e.getY();
  }
  
  
  public void mouseMoved (MouseEvent e)
  {
    mouseX = e.getX();
    mouseY = e.getY();
  }
}

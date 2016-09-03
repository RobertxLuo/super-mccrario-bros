
package kouyoum.mccrario;


import java.awt.event.KeyEvent;


public class GameLoop
{
  private Screen screen;
  
  
  public GameLoop (Screen screen)
  {
    this.screen = screen;
  }
  
  
  public void play (Level level)
  {
    final long NANOS_IN_MIL = 1000000;
    final long NANOS_IN_SEC = NANOS_IN_MIL * 1000;
    final float DELTA_TIME = 1.0f/120.0f;
    
    screen.setLevel(level);
    level.update(DELTA_TIME);
    
    boolean paused = false;
    
    long lastTime = System.nanoTime();
    float accumTime = 0.0f;
    long secondTime = 0;
    long frames = 0;
    
    while (level.isRunning()) {
      if (Input.isKeyDown(KeyEvent.VK_ESCAPE) && !DebugInfo.debug)
        Game.stop();
      if (Input.isKeyDown(KeyEvent.VK_BACK_QUOTE)) {
        Input.manualKeyRelease(KeyEvent.VK_BACK_QUOTE);
        DebugInfo.debug = !DebugInfo.debug;
        level.switchDebugMode();
      }
      if (Input.isKeyDown(KeyEvent.VK_ENTER)) {
        Input.overrideKey(KeyEvent.VK_ENTER);
        paused = !paused;
        screen.setPaused(paused);
      }
      Audio.setPaused(paused);
      
      long currentTime = System.nanoTime();
      long elapsedTime = currentTime - lastTime;
      lastTime = currentTime;
      
      accumTime += (float) elapsedTime/NANOS_IN_SEC;
      while (accumTime >= DELTA_TIME) {
        if (!paused)
          level.update(DELTA_TIME);
        accumTime -= DELTA_TIME;
      }
      
      screen.repaint();
      Audio.loop(Audio.getLevelMusic(level.getName()));
      
      ++frames;
      secondTime += elapsedTime;
      if (secondTime >= NANOS_IN_SEC) {
        DebugInfo.fps = frames;
        
        frames = 0;
        secondTime = 0;
      }
      
      try {
        long sleeptime = NANOS_IN_SEC/60 - (System.nanoTime() - lastTime);
        Thread.sleep(sleeptime/NANOS_IN_MIL, (int) (sleeptime%NANOS_IN_MIL));
      } catch (Exception e) {}
    }
  }
}


package kouyoum.mccrario;


import java.io.File;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;


public class Audio
{
  public static Sequence MUSIC11;
  public static Sequence MUSIC12;
  public static Sequence MUSIC13;
  public static Sequence MUSIC14;
  public static Sequence MUSIC1B;
  
  private static Sequencer player;
  private static boolean paused = false;
  private static boolean playing = false;
  
  
  public static void load ()
  {
    MUSIC11 = loadMidi("1-1.mid");
    MUSIC12 = loadMidi("1-2.mid");
    MUSIC13 = loadMidi("1-3.mid");
    MUSIC14 = loadMidi("1-4.mid");
    MUSIC1B = loadMidi("1-B.mid");
    
    try {
      player = MidiSystem.getSequencer();
      player.open();
    } catch (Exception e) {}
  }
  
  
  private static Sequence loadMidi (String filename)
  {
    filename = "resources/sound/" + filename;
    try {
      File file = new File(filename);
      return MidiSystem.getSequence(file);
    } catch (Exception e) {
      System.out.println("Unable to load resource: " + filename);
      return null;
    }
  }
  
  
  public static void loop (Sequence seq)
  {
    if (seq == null)
      return;
    
    if (player.getSequence() != null && player.getSequence().equals(seq) && 
        player.isRunning() || paused || playing) {
      playing = false;
      return;
    }
    
    try {
      player.stop();
      player.setTickPosition(0);
      
      player.setSequence(seq);
      
      player.start();
    } catch (Exception e) {}
  }
  
  
  public static void play (Sequence seq)
  {
    if (seq == null)
      return;
    
    if (player.getSequence() != null && player.getSequence().equals(seq)) {
      if (player.isRunning())
        playing = true;
    }
    
    try {
      player.setSequence(seq);
      player.start();
    } catch (Exception e) {}
  }
  
  
  public static Sequence getLevelMusic (String level)
  {
    if (level.equals("1-1"))
      return MUSIC11;
    else if (level.equals("1-2"))
      return MUSIC12;
    else if (level.equals("1-3"))
      return MUSIC13;
    else if (level.equals("1-4"))
      return MUSIC14;
    else if (level.equals("1-B"))
      return MUSIC1B;
    
    return null;
  }
  
  
  public static void setPaused (boolean paused)
  {
    Audio.paused = paused;
    if (paused)
      player.stop();
    else if (player.getSequence() != null)
      player.start();
  }
  
  
  public static void stop ()
  {
    if (player.isRunning())
      player.stop();
  }
}

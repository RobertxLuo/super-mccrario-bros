
package kouyoum.mccrario;


import java.awt.Color;
import java.awt.Graphics2D;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import java.awt.geom.AffineTransform;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.nio.ByteBuffer;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.swing.JOptionPane;


public class Level
{
  private String name;
  
  private boolean running = true;
  private boolean won = false;
  
  private List<Entity> entities;
  private Tile[][] tiles;
  private Vector camera;
  
  private List<EntitySeed> entitySeeds;
  private List<EntitySeed> toSpawn;
  private int selectedClass = 0;
  private int selectedTile = 1;
  private boolean entMode = false;
  private boolean delMode = false;
  
  private boolean contMode = false;
  private float contTime = 0;
  
  private int checkpoint;
  
  
  public Level (String level, int checkpoint)
  {
    name = level;
    loadLevel(level);
    
    this.checkpoint = checkpoint;
    Vector point = null;
    for (EntitySeed seed : entitySeeds) {
      if (seed.type.equals(Player.class)) {
        point = seed.pos.clone();
        break;
      }
    }
    int check = 0;
    for (EntitySeed seed : entitySeeds) {
      if (check >= checkpoint)
        break;
      
      if (seed.type.equals(Checkpoint.class)) {
        point = seed.pos.clone();
        ++check;
      }
    }
    int k = 0;
    for (int i = 0; i < toSpawn.size() && k < check; ++i) {
      if (toSpawn.get(i).type.equals(Checkpoint.class)) {
        toSpawn.remove(i--);
        ++k;
      }
    }
    for (int i = 0; i < toSpawn.size(); ++i) {
      if (toSpawn.get(i).type.equals(Player.class))
        toSpawn.set(i, new EntitySeed(Player.class, point));
    }
    camera = point.clone();
  }
  
  
  public void update (float dt)
  {
    if (Input.isKeyDown(KeyEvent.VK_O)) {
      Input.overrideKey(KeyEvent.VK_O);
      for (Entity ent : entities) {
        if (ent instanceof Player)
          ent.die();
      }
    }
    if (DebugInfo.debug && Input.isKeyDown(KeyEvent.VK_BACK_SPACE)) {
      Input.overrideKey(KeyEvent.VK_BACK_SPACE);
      checkpoint = 0;
    }
    
    // continue mode
    if (contMode && !DebugInfo.debug) {
      for (Entity ent : entities) {
        ent.move(dt);
      }
      
      contTime += dt;
      if (contTime > 1.0f)
        running = false;
    }
    
    // update level editor and load
    if (DebugInfo.debug) {
      updateLevelEdit();
    
      if (Input.isKeyDown(KeyEvent.VK_F9) || Input.isKeyDown(KeyEvent.VK_L)) {
        running = false;
        Input.manualKeyRelease(KeyEvent.VK_F9);
        Input.manualKeyRelease(KeyEvent.VK_L);
      }
    }
    
    for (Entity ent : entities) {
      
      // update entities
      if (ent.isDying()) {
        ent.playDeathAnimation(dt);
        continue;
      }
      if (!contMode)
        ent.update(dt);
      
      // get entity bounds
      int leftX = (int) Math.floor(ent.getPos().x);
      int rightX = (int) Math.floor(ent.getPos().x + ent.getHitSize().w);
      int downY = (int) Math.floor(ent.getPos().y);
      int upY = (int) Math.floor(ent.getPos().y + ent.getHitSize().h);
      
      if (leftX < 0) {
        leftX = 0;
        if (!contMode)
      	  ent.collideTile(Direction.LEFT, -ent.getPos().x);
      }
      if (rightX >= tiles[0].length) {
        rightX = tiles[0].length - 1;
        if (!contMode)
          ent.collideTile(Direction.RIGHT, 
                         tiles[0].length - ent.getHitSize().w - ent.getPos().x);
      }
      if (downY < 0)
        downY = 0;
      if (upY >= tiles.length)
        upY = tiles.length - 1;
      
      // check if walking off lethal cliff
      if (ent.getVel().x < 0.0f && leftX < tiles[0].length) {
        boolean isAir = true;
        for (int r = 0; r <= upY && r < tiles.length; ++r) {
          if (ent.collidesWith(tiles[r][leftX])) {
            isAir = false;
            break;
          }
        }
        if (isAir)
          ent.lethalCliff(Direction.LEFT, leftX + 1 - ent.getPos().x);
      } else if (ent.getVel().x > 0.0f && rightX >= 0) {
        boolean isAir = true;
        for (int r = 0; r <= upY && r < tiles.length; ++r) {
          if (ent.collidesWith(tiles[r][rightX])) {
            isAir = false;
            break;
          }
        }
        if (isAir)
          ent.lethalCliff(Direction.RIGHT, 
                          rightX - ent.getHitSize().w - ent.getPos().x);
      }
      
      // check other entities
      if (!contMode) {
        boolean toCheck = false;
        for (Entity entB : entities) {
          if (toCheck && !entB.isDying())
            Physics.check(ent, entB);
        
          if (ent instanceof Player && entB instanceof Checkpoint &&
              ent.getPos().x + ent.getHitSize().w > entB.getPos().x &&
              ent.getPos().x < entB.getPos().x + entB.getHitSize().w) {
            ++checkpoint;
            entB.die();
          }
        
          if (ent == entB)
            toCheck = true;
        }
      }
      
      // check tiles
      for (int r = downY; r <= upY; ++r) {
        for (int c = leftX; c <= rightX; ++c) {
          Physics.check(ent, tiles, c, r);
          
          if (ent instanceof Player && tiles[r][c] instanceof GoalTile && 
              !DebugInfo.debug) {
            contMode = true;
            won = true;
          } else if (ent instanceof Player && tiles[r][c] instanceof BossStop &&
                     !DebugInfo.debug) {
            won = true;
          }
        }
      }
    }
    
    for (int i = 0; i < entities.size(); ++i) {
      Entity ent = entities.get(i);
      
      // check if fallen
      if (ent.getPos().y + ent.getImageSize().h < -0.5f &&
          !((DebugInfo.debug || contMode) && ent instanceof Player) &&
          !(ent instanceof BossBaal))
        ent.die();
      
      // remove if dead
      if (!ent.isAlive()) {
        if (ent instanceof Player)
          running = false;
        entities.remove(i--);
      }
    }
    
    // position camera
    for (Entity ent : entities) {
      if (ent instanceof Player && !ent.isDying()) {
        final float HBUFF = 0.4f;
        final float VBUFF = 0.3f;
        int l = (int) (Screen.SCREEN_WIDTH * HBUFF);
        int r = (int) (Screen.SCREEN_WIDTH * (1.0f - HBUFF));
        int d = (int) (Screen.SCREEN_HEIGHT * (1.0f - VBUFF));
        int u = (int) (Screen.SCREEN_HEIGHT * VBUFF);
        
        Vector ru = Screen.screenToWorld(r, u, camera);
        if (ent.getPos().x + ent.getHitSize().w > ru.x)
          camera.x += ent.getPos().x + ent.getHitSize().w - ru.x;
        if (ent.getPos().y + ent.getHitSize().h > ru.y)
          camera.y += ent.getPos().y + ent.getHitSize().h - ru.y;
        
        Vector dl = Screen.screenToWorld(l, d, camera);
        if (ent.getPos().x < dl.x)
          camera.x += ent.getPos().x - dl.x;
        if (ent.getPos().y < dl.y)
          camera.y += ent.getPos().y - dl.y;
      }
    }
    
    // restrict camera
    if (!DebugInfo.debug) {
      Vector ru = Screen.screenToWorld(Screen.SCREEN_WIDTH, 0, camera);
      if (ru.x >= tiles[0].length)
        camera.x -= ru.x - tiles[0].length;
      if (ru.y >= tiles.length)
        camera.y -= ru.y - tiles.length;
        
      Vector ld = Screen.screenToWorld(0, Screen.SCREEN_HEIGHT, camera);
      if (ld.x < 0.0f)
        camera.x -= ld.x;
      if (ld.y < 0.0f)
        camera.y -= ld.y;
    }
    
    // spawn entities
    for (int i = 0; i < toSpawn.size(); ++i) {
      Entity ent = toSpawn.get(i).instantiate();
      if (isOnScreen(ent.getPos(), 
                     Vector.add(ent.getPos(), ent.getImageSize().toVector())) ||
          ent instanceof Player || ent instanceof Stopper) {
        entities.add(toSpawn.get(i).instantiate());
        toSpawn.remove(i--);
      }
    }
    
    // check for broken bricks
    for (Tile[] row : tiles) {
      for (Tile tile : row) {
        if (tile instanceof BrokenBrick)
          ((BrokenBrick) tile).update(dt);
      }
    }
    
    // if boss exists, set bossbricks to exist
    boolean bossExists = false;
    for (Entity ent : entities) {
      if (ent instanceof BossBaal) {
        bossExists = true;
        break;
      }
    }
    for (Tile[] row : tiles) {
      for (Tile tile : row) {
        if (tile instanceof BossBrick)
          ((BossBrick) tile).setExists(bossExists);
      }
    }
    
    // give player pos to bossbaal
    for (Entity player : entities) {
      if (!(player instanceof Player))
        continue;
      
      for (Entity boss : entities) {
        if (!(boss instanceof BossBaal))
          continue;
        
        ((BossBaal) boss).setPlayerPos(player.getPos());
      }
    }
  }
  
  
  public void render (Graphics2D g)
  {
    Stack<AffineTransform> stack = new Stack<AffineTransform>();
    
    if (camera != null)
      g.translate(-camera.x, -camera.y);
  
    Vector ld = Screen.screenToWorld(0, Screen.SCREEN_HEIGHT, camera);
    Vector ru = Screen.screenToWorld(Screen.SCREEN_WIDTH, 0, camera);
    int rTile = Math.min((int) Math.floor(ru.x) + 1, tiles[0].length);
    int lTile = Math.max((int) Math.floor(ld.x), 0);
    int uTile = Math.min((int) Math.floor(ru.y) + 1, tiles.length);
    int dTile = Math.max((int) Math.floor(ld.y), 0);
    
    stack.push(g.getTransform());
    g.translate(lTile, dTile);
    for (int r = dTile; r < uTile; ++r) {
      stack.push(g.getTransform());
      for (int c = lTile; c < rTile; ++c) {
        tiles[r][c].render(g);
        g.translate(1.0, 0.0);
      }
      g.setTransform(stack.pop());
      g.translate(0.0f, 1.0);
    }
    g.setTransform(stack.pop());
    
    if (DebugInfo.debug) {
      g.setColor(Color.LIGHT_GRAY);
      for (int i = 0; i <= tiles.length; ++i) {
        g.drawLine(0, i, tiles[0].length, i);
      }
      for (int i = 0; i <= tiles[0].length; ++i) {
        g.drawLine(i, 0, i, tiles.length);
      }
      g.setColor(Color.BLACK);
      g.drawLine(0, 0, tiles[0].length, 0);
      g.drawLine(0, tiles.length, tiles[0].length, tiles.length);
      g.drawLine(0, 0, 0, tiles.length);
      g.drawLine(tiles[0].length, 0, tiles[0].length, tiles.length);
    }
    
    if (DebugInfo.debug) {
      g.setXORMode(Color.LIGHT_GRAY);
      for (EntitySeed seed : entitySeeds) {
        stack.push(g.getTransform());
        seed.instantiate().render(g);
        g.setTransform(stack.pop());
      }
      g.setPaintMode();
    }
    
    for (Entity ent : entities) {
      if (!isOnScreen(ent.getPos(),
                      Vector.add(ent.getPos(), ent.getImageSize().toVector())))
        continue;
      
      stack.push(g.getTransform());
      ent.render(g);
      g.setTransform(stack.pop());
      
      if (DebugInfo.debug) {
        stack.push(g.getTransform());
        ent.renderHitBox(g);
        g.setTransform(stack.pop());
      }
    }
    
    if (DebugInfo.debug) {
      if (entMode && !delMode) {
        stack.push(g.getTransform());
        EntitySeed seed = new EntitySeed(Entity.subclasses[selectedClass],
                                         Screen.screenToWorld(
                                                  Input.getMouseX(),
                                                  Input.getMouseY(),
                                                  camera));
        seed.pos.sub(new Vector(0.5f, 0.5f));
        seed.instantiate().render(g);
        g.setTransform(stack.pop());
      }
      
      if (!entMode && !delMode) {
        stack.push(g.getTransform());
        Vector mouse = Screen.screenToWorld(Input.getMouseX(), 
                                            Input.getMouseY(), camera);
        g.translate(mouse.x - 0.5f, mouse.y - 0.5f);
        new EntitySeed(
                   Tile.subclasses[selectedTile]).instantiateAsTile().render(g);
        
        g.setTransform(stack.pop());
      }
    }
  }
  
  
  private boolean isOnScreen (Vector entLD, Vector entRU)
  {
    Vector ld = Screen.screenToWorld(0, Screen.SCREEN_HEIGHT, camera);
    Vector ru = Screen.screenToWorld(Screen.SCREEN_WIDTH, 0, camera);
    return entRU.x > ld.x && 
           entLD.x < ru.x &&
           entRU.y > ld.y && 
           entLD.y < ru.y;
  }
  
  
  public void add (Entity ent)
  {
    entities.add(ent);
  }
  
  
  public String getName ()
  {
    return name;
  }
  
  
  public int getCheckpoint ()
  {
    return checkpoint;
  }
  
  
  private void loadLevel (String level)
  {
    if (level == null) {
      running = false;
      return;
    }
    
    try {
      String filename = "resources/levels/" + level + ".lvl";
      FileInputStream in = new FileInputStream(new File(filename));
      
      byte[] bytes = new byte[4];
      in.read(bytes);
      int rows = ByteBuffer.wrap(bytes).getInt();
      in.read(bytes);
      int cols = ByteBuffer.wrap(bytes).getInt();
      
      tiles = new Tile[rows][cols];
      
      for (int r = 0; r < tiles.length; ++r) {
        for (int c = 0; c < tiles[r].length; ++c) {
          Class<?> tileClass = Tile.subclasses[in.read()];
          tiles[r][c] = (Tile) tileClass.getConstructor(
                                  new Class[0]).newInstance(new Object[0]);
        }
      }
      
      entitySeeds = new ArrayList<EntitySeed>();
      
      while (true) {
        byte b = (byte) in.read();
        if (b == -1)
          break;
        
        Class<?> entClass = Entity.subclasses[b];
        Vector pos = new Vector(0.0f, 0.0f);
        in.read(bytes);
        pos.x = ByteBuffer.wrap(bytes).getFloat();
        in.read(bytes);
        pos.y = ByteBuffer.wrap(bytes).getFloat();
        entitySeeds.add(new EntitySeed(entClass, pos));
      }
      
      in.close();
    } catch (Exception e) {
      tiles = new Tile[4][4];
      
      entitySeeds = new ArrayList<EntitySeed>();
      
      for (int r = 0; r < tiles.length; ++r) {
        for (int c = 0; c < tiles[r].length; ++c) {
          tiles[r][c] = new Air();
        }
      }
    }
    
    entities = new ArrayList<Entity>();
    toSpawn = new ArrayList<EntitySeed>();
    for (EntitySeed seed : entitySeeds) {
      toSpawn.add(seed);
    }
    camera = new Vector(0.0f, 0.0f);
  }
  
  
  public boolean isRunning ()
  {
    return running;
  }
  
  
  public boolean didWin ()
  {
    return won;
  }
  
  
  public void continueRunning ()
  {
    running = true;
  }
  
  
  public void switchDebugMode ()
  {
    for (int i = 0; i < entities.size(); ++i) {
      if (DebugInfo.debug && entities.get(i) instanceof Player) {
        Vector pos = entities.get(i).getPos();
        entities.set(i, new FlyingPlayer(pos));
      } else if (entities.get(i) instanceof Player) {
        Vector pos = entities.get(i).getPos();
        entities.set(i, new Player(pos));
      }
    }
  }
  
  //
  //
  //   LEVEL EDITOR
  //
  //
  
  public void saveLevel (String level)
  {
    if (level == null)
      return;
    
    try {
      String filename = "resources/levels/" + level + ".lvl";
      FileOutputStream out = new FileOutputStream(new File(filename));
      
      out.write(ByteBuffer.allocate(4).putInt(tiles.length).array());
      out.write(ByteBuffer.allocate(4).putInt(tiles[0].length).array());
      
      for (Tile[] row : tiles) {
        for (Tile tile : row) {
          boolean foundClass = false;
          for (byte b = 0; b < Tile.subclasses.length; ++b) {
            if (Tile.subclasses[b].equals(tile.getClass())) {
              out.write(b);
              foundClass = true;
              break;
            }
          }
          if (!foundClass) {
            System.out.println("Could not find " + tile.getClass().toString());
            System.exit(0);
          }
        }
      }
      
      List<EntitySeed> checks = new ArrayList<EntitySeed>();
      for (int i = 0; i < entitySeeds.size(); ++i) {
        if (!entitySeeds.get(i).type.equals(Checkpoint.class))
          continue;
        
        boolean found = false;
        for (int j = 0; j < checks.size(); ++j) {
          if (entitySeeds.get(i).pos.x < checks.get(j).pos.x) {
            checks.add(j, entitySeeds.get(i));
            found = true;
            break;
          }
        }
        if (!found)
          checks.add(entitySeeds.get(i));
        entitySeeds.remove(i--);
      }
      for (EntitySeed check : checks) {
        entitySeeds.add(check);
      }
      
      for (EntitySeed seed : entitySeeds) {
        boolean foundClass = false;
        for (byte b = 0; b < Entity.subclasses.length; ++b) {
          if (Entity.subclasses[b].equals(seed.type)) {
            out.write(b);
            foundClass = true;
            break;
          }
        }
        if (!foundClass) {
          System.out.println("Could not find " + seed.type.toString());
          System.exit(0);
        }
        
        out.write(ByteBuffer.allocate(4).putFloat(seed.pos.x).array());
        out.write(ByteBuffer.allocate(4).putFloat(seed.pos.y).array());
      }
      
      out.write(-1);
      
      out.close();
    } catch (IOException e) {
      System.out.println("Unable to save level " + level);
    }
  }
  
  
  private boolean wasClick = false;
  private boolean wasMidClick = false;
  public void updateLevelEdit ()
  {
    delMode = false;
    
    Vector mouse = Screen.screenToWorld(Input.getMouseX(), 
                                        Input.getMouseY(),
                                        camera);
    
    if (Input.isKeyDown(KeyEvent.VK_R)) {
      entMode = !entMode;
      Input.overrideKey(KeyEvent.VK_R);
    }
    
    if (Input.isKeyDown(KeyEvent.VK_E)) {
      Input.overrideKey(KeyEvent.VK_E);
      if (entMode) {
        ++selectedClass;
        if (selectedClass == Entity.subclasses.length)
          selectedClass = 0;
      } else {
        ++selectedTile;
        if (selectedTile == Tile.subclasses.length)
          selectedTile = 1;
      }
    }
    if (Input.isKeyDown(KeyEvent.VK_Q)) {
      Input.overrideKey(KeyEvent.VK_Q);
      if (entMode) {
        --selectedClass;
        if (selectedClass == -1)
          selectedClass = Entity.subclasses.length - 1;
      } else {
        --selectedTile;
        if (selectedTile == 0)
          selectedTile = Tile.subclasses.length - 1;
      }
    }
    
    if (entMode) {
      if (Input.isMouseDown(MouseEvent.BUTTON3)) {
        delMode = true;
        for (int i = 0; i < entitySeeds.size(); ++i) {
          if (Physics.testPoint(mouse, entitySeeds.get(i).instantiate())) {
            entitySeeds.remove(i--);
          }
        }
      } else if (Input.isMouseDown(MouseEvent.BUTTON1)) {
        if (!wasClick)
          entitySeeds.add(new EntitySeed(Entity.subclasses[selectedClass], 
                                         Vector.sub(mouse, 
                                                    new Vector(0.5f, 0.5f))));
        wasClick = true;
      } else {
        wasClick = false;
      }
      if (Input.isMouseDown(MouseEvent.BUTTON2)) {
        if (!wasMidClick) {
          delMode = true;
          for (EntitySeed seed : entitySeeds) {
            if (Physics.testPoint(mouse, seed.instantiate()))
              entities.add(seed.instantiate());
          }
        }
        wasMidClick = true;
      } else {
        wasMidClick = false;
      }
      
      saveIfNeeded();
      return;
    }
    
    if (Input.isMouseDown(MouseEvent.BUTTON2))
      delMode = true;
    
    int row = (int) Math.floor(mouse.y);
    int col = (int) Math.floor(mouse.x);
    if (row >= 0 && row < tiles.length &&
        col >= 0 && col < tiles[0].length) {
          
      if (Input.isMouseDown(MouseEvent.BUTTON1)) {
        tiles[row][col] = new EntitySeed(
                            Tile.subclasses[selectedTile]).instantiateAsTile();
      } else if (Input.isMouseDown(MouseEvent.BUTTON3)) {
        delMode = true;
        tiles[row][col] = new Air();
      } else if (Input.isMouseDown(MouseEvent.BUTTON2) &&
        ((row != 0 && row != tiles.length-1) || 
         (col != 0 && col != tiles[0].length-1)) && tiles.length != 1) {
        
        if (row == tiles.length-1 || col == tiles[0].length-1) {
          int rs = row == tiles.length-1? tiles.length-1 : tiles.length;
          int cs = col == tiles[0].length-1? tiles[0].length-1 : 
                                                            tiles[0].length;
          Tile[][] newGrid = new Tile[rs][cs];
          for (int r = 0; r < newGrid.length; ++r) {
            for (int c = 0; c < newGrid[r].length; ++c) {
              newGrid[r][c] = tiles[r][c];
            }
          }
          tiles = newGrid;
        } else if (row == 0 || col == 0) {
          int rs = row == 0? tiles.length-1 : tiles.length;
          int cs = col == 0? tiles[0].length-1 : tiles[0].length;
          
          Tile[][] newGrid = new Tile[rs][cs];
          for (int r = 0; r < newGrid.length; ++r) {
            for (int c = 0; c < newGrid[r].length; ++c) {
              int r1 = row == 0? r+1 : r;
              int c1 = col == 0? c+1 : c;
              newGrid[r][c] = tiles[r1][c1];
            }
          }
          tiles = newGrid;
          
          for (Entity ent : entities) {
            ent.displace(new Vector(col == 0? -1 : 0, row == 0? -1 : 0));
          }
          
          for (EntitySeed seed : entitySeeds) {
            seed.pos.x += col == 0? -1 : 0;
            seed.pos.y += row == 0? -1 : 0;
          }
        }
      }
    } else {
      
      if (Input.isMouseDown(MouseEvent.BUTTON1) || 
          Input.isMouseDown(MouseEvent.BUTTON3)) {
        
        Tile[][] newGrid;
        if (row >= tiles.length || col >= tiles[0].length) {
          newGrid = new Tile[Math.max(row+1, tiles.length)]
                            [Math.max(col+1, tiles[0].length)];
          for (int r = 0; r < newGrid.length; ++r) {
            for (int c = 0; c < newGrid[r].length; ++c) {
              if (r >= tiles.length || c >= tiles[r].length)
                newGrid[r][c] = new Air();
              else
                newGrid[r][c] = tiles[r][c];
            }
          }
        } else {
          newGrid = new Tile[Math.max(tiles.length - row, tiles.length)]
                            [Math.max(tiles[0].length - col, tiles[0].length)];
          for (int r = 0; r < newGrid.length; ++r) {
            for (int c = 0; c < newGrid[r].length; ++c) {
              if (r + row < 0 || c + col < 0) {
                newGrid[r][c] = new Air();
              } else {
                int r1 = row < 0 ? r + row : r;
                int c1 = col < 0 ? c + col : c;
                newGrid[r][c] = tiles[r1][c1];
              }
            }
          }
          
          for (Entity ent : entities) {
            ent.displace(new Vector(col < 0.0f? -col : 0.0f, 
                                    row < 0.0f? -row : 0.0f));
          }
          
          for (EntitySeed seed : entitySeeds) {
            seed.pos.x += col < 0? -col : 0;
            seed.pos.y += row < 0? -row : 0;
          }
        }
        tiles = newGrid;
      }
    }
    
    saveIfNeeded();
  }
  
  
  private void saveIfNeeded ()
  {
    if (Input.isKeyDown(KeyEvent.VK_F5) || 
        Input.isKeyDown(KeyEvent.VK_ESCAPE)) {
      saveLevel(JOptionPane.showInputDialog("Enter the level name to save." +
        "\nIt should be in the form \"world-level\"."));
      Input.manualKeyRelease(KeyEvent.VK_F5);
      Input.manualKeyRelease(KeyEvent.VK_ESCAPE);
    }
  }
  
  
  private class EntitySeed
  {
    public Class<?> type;
    public Vector pos;
    
    
    public EntitySeed (Class<?> type, Vector pos)
    {
      this.type = type;
      this.pos = pos;
    }
    
    
    public EntitySeed (Class<?> type)
    {
      this(type, new Vector(0.0f, 0.0f));
    }
    
    
    public Entity instantiate ()
    {
      try {
        Entity ent = (Entity) type.getConstructor(
                                  new Class[] {Vector.class}).newInstance(pos);
        if (DebugInfo.debug && ent instanceof Player)
          return new FlyingPlayer(pos);
        return ent;
      } catch (Exception e) {
        System.out.println("Unable to instantiate " + type.toString());
        System.exit(0);
        return null;
      }
    }
    
    
    public Tile instantiateAsTile ()
    {
      try {
        return (Tile) type.getConstructor(new Class[0]).newInstance(
                                                                new Object[0]);
      } catch (Exception e) {
        System.out.println("Unable to instantiate " + type.toString());
        System.exit(0);
        return null;
      }
    }
  }
}

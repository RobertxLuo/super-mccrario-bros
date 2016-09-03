
package kouyoum.mccrario;


public class Physics
{
  public static final float GRAVITY = 50.0f;
  public static final float TERM_VEL = -50.0f;
  
  
  public static void check (Entity ent, Tile[][] tiles, int tileL, int tileD)
  {
    int r = tileD;
    int c = tileL;
    
    if (!ent.collidesWith(tiles[r][c]))
      return;
    
    float bufL = tileL - ent.getHitSize().w - ent.getPos().x;
    float bufR = tileL + 1 - ent.getPos().x;
    float bufD = tileD - ent.getHitSize().h - ent.getPos().y;
    float bufU = tileD + 1 - ent.getPos().y;
    
    Direction side = Direction.LEFT;
    float dist = bufR;
    if (Math.abs(bufL) < Math.abs(dist)) {
      side = Direction.RIGHT;
      dist = bufL;
    }
    if (Math.abs(bufD) < Math.abs(dist)) {
      side = Direction.UP;
      dist = bufD;
    }
    if (Math.abs(bufU) < Math.abs(dist)) {
      side = Direction.DOWN;
      dist = bufU;
    }
    
    if (side == Direction.LEFT && c < tiles[0].length - 1 && 
        ent.collidesWith(tiles[r][c+1]))
      return;
    if (side == Direction.RIGHT && c > 0 && 
        ent.collidesWith(tiles[r][c-1]))
      return;
    if (side == Direction.DOWN && r < tiles.length - 1 && 
        ent.collidesWith(tiles[r+1][c]))
      return;
    if (side == Direction.UP && r > 0 && 
        ent.collidesWith(tiles[r-1][c]))
      return;
    
    ent.collideTile(side, dist);
    if (tiles[r][c] instanceof BrokenBrick && ent instanceof Player && 
        side == Direction.DOWN) {
      ((BrokenBrick) tiles[r][c]).startBreaking();
    }
  }
  
  
  public static boolean testPoint (Vector p, Entity ent)
  {
    return p.x >= ent.getPos().x &&
           p.x <= ent.getPos().x + ent.getHitSize().w &&
           p.y >= ent.getPos().y &&
           p.y <= ent.getPos().y + ent.getHitSize().h;
  }
  
  
  public static void check (Entity entA, Entity entB)
  {
    if (!entA.collidesWith(entB) || !entB.collidesWith(entA))
      return;
    
    if (entA.getPos().x + entA.getHitSize().w <= entB.getPos().x)
      return;
    if (entB.getPos().x + entB.getHitSize().w <= entA.getPos().x)
      return;
    if (entA.getPos().y + entA.getHitSize().h <= entB.getPos().y)
      return;
    if (entB.getPos().y + entB.getHitSize().h <= entA.getPos().y)
      return;
    
    float bufL = entB.getPos().x - entA.getHitSize().w - entA.getPos().x;
    float bufR = entB.getPos().x + entB.getHitSize().w - entA.getPos().x;
    float bufD = entB.getPos().y - entA.getHitSize().h - entA.getPos().y;
    float bufU = entB.getPos().y + entB.getHitSize().h - entA.getPos().y;
    
    Direction side = Direction.LEFT;
    float dist = bufR;
    if (Math.abs(bufL) < Math.abs(dist)) {
      side = Direction.RIGHT;
      dist = bufL;
    }
    if (Math.abs(bufD) < Math.abs(dist)) {
      side = Direction.UP;
      dist = bufD;
    }
    if (Math.abs(bufU) < Math.abs(dist)) {
      side = Direction.DOWN;
      dist = bufU;
    }
    
    entA.collideEntity(entB, side, dist/2.0f);
    entB.collideEntity(entA, side.opposite(), -dist/2.0f);
  }
}

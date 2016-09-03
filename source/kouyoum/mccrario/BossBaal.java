
package kouyoum.mccrario;


public class BossBaal extends Entity
{
  private Vector playerPos;
  
  
  public BossBaal (Vector pos)
  {
    super(pos.clone(), new Size(1.0f, 2.8f), new Size(1.5f, 3.0f));
    setImage(Resources.BOSSBAAL);
    setDistFromFloor(0.1f);
    
    setFaceDirection(-1);
    
    playerPos = pos;
  }
  
  
  public void update (float dt)
  {
    Vector dir = Vector.unit(Vector.sub(playerPos, getPos()));
    
    Vector mov = Vector.mul(dir, 16.0f);
    mov.sub(Vector.mul(getVel(), 1.5f));
    accel(mov);
    
    fly();
    super.update(dt);
  }
  
  
  public void setPlayerPos (Vector pos)
  {
    playerPos = pos;
    
    if (playerPos.x - getPos().x < 0.0f)
      setFaceDirection(-1);
    else
      setFaceDirection(1);
  }
  
  
  public void collideEntity (Entity ent, Direction side, float dist)
  {
    ent.maybeDie(this, side.opposite());
  }
  
  
  public void maybeDie (Entity ent, Direction side)
  {
  }
  
  
  public boolean collidesWith (Tile tile)
  {
    return tile instanceof BossStop;
  }
}

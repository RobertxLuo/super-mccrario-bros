
package kouyoum.mccrario;


public class Baaloon extends Entity
{
  private int direction = -1;
  
  
  public Baaloon (Vector pos)
  {
    super(pos.clone(), new Size(0.9f, 1.0f), new Size(1.0f, 1.3f));
    setImage(Resources.BAALOON);
    setDistFromFloor(0.2f);
    
    setFaceDirection(-1);
  }
  
  
  public void update (float dt)
  {
    final float FLY_SPEED = 1.0f;
    final float FLY_ACCEL = 64.0f;
    
    if (direction < 0) {
      if (getVel().y > -FLY_SPEED)
        accel(new Vector(0.0f, -FLY_ACCEL));
    } else {
      if (getVel().y < FLY_SPEED)
        accel(new Vector(0.0f, FLY_ACCEL));
    }
    
    fly();
    super.update(dt);
  }
  
  
  public void collideTile (Direction side, float dist)
  {
    if (side == Direction.DOWN)
      direction = 1;
    else if (side == Direction.UP)
      direction = -1;
    
    super.collideTile(side, dist);
  }
  
  
  public void collideEntity (Entity ent, Direction side, float dist)
  {
    if (super.collidesWith(ent))
      super.collideEntity(ent, side, dist);
    
    if (ent instanceof Player && side != Direction.UP)
      ent.maybeDie(this, side.opposite());
    
    if (ent instanceof Stopper) {
      if (side == Direction.DOWN)
        direction = 1;
      else if (side == Direction.UP)
        direction = -1;
    }
  }
  
  
  public boolean collidesWith (Entity ent)
  {
    return super.collidesWith(ent) || ent instanceof Stopper;
  }
  
  
  public void maybeDie (Entity ent, Direction side)
  {
    if (side == Direction.UP)
      die();
    else
      ent.maybeDie(this, side.opposite());
  }
}

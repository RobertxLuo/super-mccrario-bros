
package kouyoum.mccrario;


public class Baal extends Entity
{
  public Baal (Vector pos)
  {
    super(pos.clone(), new Size(0.9f, 0.9f), new Size(1.0f, 1.0f));
    setImage(Resources.BAAL);
    
    setFaceDirection(-1);
  }
  
  
  public void update (float dt)
  {
    final float ROLL_SPEED = 0.4f;
    final float ROLL_ACCEL = 64.0f;
    
    if (getFaceDirection() < 0) {
      if (getVel().x > -ROLL_SPEED)
        accel(new Vector(-ROLL_ACCEL, 0.0f));
    } else {
      if (getVel().x < ROLL_SPEED)
        accel(new Vector(ROLL_ACCEL, 0.0f));
    }
    
    super.update(dt);
  }
  
  
  public void collideTile (Direction side, float dist)
  {
    if (side == Direction.LEFT)
      setFaceDirection(1);
    else if (side == Direction.RIGHT)
      setFaceDirection(-1);
    
    super.collideTile(side, dist);
  }
  
  
  public void collideEntity (Entity ent, Direction side, float dist)
  {
    super.collideEntity(ent, side, dist);
    if (ent instanceof Player && side != Direction.UP)
      ent.maybeDie(this, side.opposite());
  }
  
  
  public void maybeDie (Entity ent, Direction side)
  {
    if (side == Direction.UP)
      die();
    else
      ent.maybeDie(this, side.opposite());
  }
  
  
  public void lethalCliff (Direction side, float dist)
  {
    collideTile(side, dist);
  }
}

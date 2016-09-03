
package kouyoum.mccrario;


import java.awt.event.KeyEvent;


public class FlyingPlayer extends Player
{
  boolean fly = false;
  boolean flyDown = false;
  
  
  public FlyingPlayer (Vector pos)
  {
    super(pos.clone());
  }
  
  
  public void update (float dt)
  {
    if (Input.isKeyDown(KeyEvent.VK_F)) {
      if (!flyDown)
        fly = !fly;
      flyDown = true;
    } else {
      flyDown = false;
    }
    if (!fly) {
      super.update(dt);
      return;
    }
    
    final float FLY_SPEED = 10.0f;
    final float FLY_ACCEL = 100.0f;
    
    Vector dir = new Vector(0.0f, 0.0f);
    if (Input.isKeyDown(KeyEvent.VK_A) || Input.isKeyDown(KeyEvent.VK_LEFT))
      dir.x -= 1.0f;
    if (Input.isKeyDown(KeyEvent.VK_D) || Input.isKeyDown(KeyEvent.VK_RIGHT))
      dir.x += 1.0f;
    if (Input.isKeyDown(KeyEvent.VK_W) || Input.isKeyDown(KeyEvent.VK_UP))
      dir.y += 1.0f;
    if (Input.isKeyDown(KeyEvent.VK_S) || Input.isKeyDown(KeyEvent.VK_DOWN))
      dir.y -= 1.0f;
    if (Math.abs(getVel().x) >= FLY_SPEED && dir.x > 0.0f == getVel().x > 0.0f)
      dir.x = 0.0f;
    if (Math.abs(getVel().y) >= FLY_SPEED && dir.y > 0.0f == getVel().y > 0.0f)
      dir.y = 0.0f;
    accel(Vector.mul(dir, FLY_ACCEL));
    
    if (dir.x != 0.0f)
      setFaceDirection((int) dir.x);
    
    fly();
    super.update(dt);
  }
  
  
  public void collideTile (Direction side, float dist)
  {
    if (!fly)
      super.collideTile(side, dist);
  }
  
  
  public boolean collidesWith (Entity ent)
  {
    if (fly)
      return false;
    return super.collidesWith(ent);
  }
  
  
  public void playDeathAnimation (float dt)
  {
    miraculouslyRecover();
  }
}

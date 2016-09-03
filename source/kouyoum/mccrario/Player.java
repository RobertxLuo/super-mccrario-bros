
package kouyoum.mccrario;


import java.awt.Color;
import java.awt.Graphics2D;

import java.awt.event.KeyEvent;


public class Player extends Entity
{
  private boolean jumping = false;
  private float jumpTime = 0.0f;
  private boolean bouncing = false;
  private float bounceTime = 0.0f;
  private float deathAnim = 0.0f;
  
  
  public Player (Vector pos)
  {
    super(pos.clone(), new Size(0.5f, 0.8f), new Size(0.8f, 1.0f));
    setImage(Resources.MCCRARIO0);
    
    setDistFromLeft(-0.05f);
  }
  
  
  public void update (float dt)
  {
    if (isDying()) {
      playDeathAnimation(dt);
      return;
    }
    
    final float WALK_SPEED = 7.0f;
    final float WALK_ACCEL = 32.0f;
    
    final float JUMP_TIME = 0.15f;
    final float JUMP_ACCEL = 165.0f;
    
    final float BOUNCE_TIME = 0.08f;
    
    int dir = 0;
    if (Input.isKeyDown(KeyEvent.VK_A) || Input.isKeyDown(KeyEvent.VK_LEFT))
      dir -= 1;
    if (Input.isKeyDown(KeyEvent.VK_D) || Input.isKeyDown(KeyEvent.VK_RIGHT))
      dir += 1;
    if (Math.abs(getVel().x) > WALK_SPEED && dir > 0 == getVel().x > 0)
      dir = 0;
    accel(new Vector(dir * WALK_ACCEL, 0.0f));
    
    if (dir != 0)
      setFaceDirection(dir);
    
    boolean jumpKey = Input.isKeyDown(KeyEvent.VK_W) || 
                      Input.isKeyDown(KeyEvent.VK_UP);
    
    if (jumpKey && isOnGround()) {
      bouncing = false;
      jumping = true;
      jumpTime = 0.0f;
    }
    if (jumpKey && bouncing) {
      jumping = true;
    }
    if (bouncing && bounceTime > BOUNCE_TIME) {
      bouncing = false;
    }
    if (jumping && (!jumpKey || jumpTime > JUMP_TIME)) {
      jumping = false;
    }
    
    if (jumping || bouncing) {
      accel(new Vector(0.0f, JUMP_ACCEL));
      jumpTime += dt;
      bounceTime += dt;
    }
    
    super.update(dt);
  }
  
  
  public void collideEntity (Entity ent, Direction side, float dist)
  {
    super.collideEntity(ent, side, dist);
    
    if (side == Direction.DOWN) {
      bouncing = true;
      bounceTime = 0.0f;
      ent.maybeDie(this, Direction.UP);
    }
  }
  
  
  public void playDeathAnimation (float dt)
  {
    super.update(dt);
    
    deathAnim += dt;
    if (deathAnim == dt) {
      stop();
    } else if (deathAnim < 0.3f) {
      fly();
      accel(new Vector(0.0f, 40.0f));
    } else if (deathAnim > 2.0f) {
      dieInstantly();
    }
  }
}

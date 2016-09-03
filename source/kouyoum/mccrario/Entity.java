
package kouyoum.mccrario;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;


public abstract class Entity
{
  public static final Class[] subclasses = {
    Player.class, Baal.class, SpikeBaal.class, Baaloon.class, Stopper.class,
    Checkpoint.class, BossBaal.class, ThanksForPlaying.class
  };
  
  private Vector pos;
  private Vector vel;
  private Vector accel;
  private Size hitSize;
  private Size imageSize;
  private Image image;
  private float distFromFloor = 0.0f;
  private float distFromLeft = 0.0f;
  
  private int facing = 1;
  private boolean onGround = false;
  private boolean alive = true;
  private boolean dying = false;
  
  
  public Entity (Vector pos, Size hitSize, Size imageSize, Vector vel)
  {
    this.pos = pos.clone();
    this.hitSize = hitSize.clone();
    this.imageSize = imageSize.clone();
    this.vel = vel;
    
    accel = new Vector(0.0f, 0.0f);
  }
  
  
  public Entity (Vector pos, Size hitSize, Size imageSize)
  {
    this(pos, hitSize, imageSize, new Vector(0.0f, 0.0f));
  }
  
  
  public void update (float dt)
  { 
    accel.y -= Physics.GRAVITY;
    
    if (accel.x == 0.0f)
      vel.x -= vel.x * dt/0.1f;
    if (accel.y == 0.0f)
      vel.y -= vel.y * dt/0.1f;
    
    vel.add(Vector.mul(accel, dt));
    if (vel.y < Physics.TERM_VEL)
      vel.y = Physics.TERM_VEL;
    
    pos.add(Vector.mul(vel, dt));
    
    accel.set(0.0f, 0.0f);
    onGround = false;
  }
  
  
  public void move (float dt)
  {
    pos.add(Vector.mul(vel, dt));
  }
  
  
  public void render (Graphics2D g)
  {
    if (image == null)
      return;
    
    g.translate(pos.x, pos.y);
    g.translate(hitSize.w/2.0f, 0.0f);
    g.translate(-imageSize.w/2.0f, imageSize.h);
    g.translate(0.0f, -distFromFloor);
    g.translate(-distFromLeft * facing, 0.0f);
    if (facing < 0) {
      g.translate(imageSize.w, 0.0f);
      g.scale(-imageSize.w, -imageSize.h);
    } else {
      g.scale(imageSize.w, -imageSize.h);
    }
    g.drawImage(image, 0, 0, 1, 1, null);
  }
  
  
  public void renderHitBox (Graphics2D g)
  {
    g.setColor(Color.RED);
    g.translate(pos.x, pos.y);
    g.scale(hitSize.w, hitSize.h);
    g.drawRect(0, 0, 1, 1);
  }
  
  
  public void collideTile (Direction side, float dist)
  {
    switch (side) {
      case UP:
        pos.y += dist;
        if (vel.y > 0.0f)
          vel.y = 0.0f;
        break;
      
      case DOWN:
        pos.y += dist;
        if (vel.y < 0.0f)
          vel.y = 0.0f;
        break;
      
      case LEFT:
        pos.x += dist;
        if (vel.x < 0.0f)
          vel.x = 0.0f;
        break;
      
      case RIGHT:
        pos.x += dist;
        if (vel.x > 0.0f)
          vel.x = 0.0f;
        break;
    }
    
    if (side == Direction.DOWN)
      onGround = true;
  }
  
  
  public void collideEntity (Entity ent, Direction side, float dist)
  {
    collideTile(side, dist);
  }
  
  
  public void maybeDie (Entity ent, Direction side)
  {
    die();
  }
  
  
  public void die ()
  {
    dying = true;
  }
  
  
  public void dieInstantly ()
  {
    dying = false;
    alive = false;
  }
  
  
  public void playDeathAnimation (float dt)
  {
    dieInstantly();
  }
  
  
  public void miraculouslyRecover ()
  {
    alive = true;
    dying = false;
  }
  
  
  public void lethalCliff (Direction side, float dist)
  {
  }
  
  
  public void fly ()
  {
    accel(new Vector(0.0f, Physics.GRAVITY));
  }
  
  
  public void accel (Vector amt)
  {
    accel.add(amt);
  }
  
  
  public boolean isOnGround ()
  {
    return onGround;
  }
  
  
  public boolean collidesWith (Tile tile)
  {
    return tile.isSolid();
  }
  
  
  public boolean collidesWith (Entity ent)
  {
    return ent.isSolid();
  }
  
  
  public boolean isSolid ()
  {
    return true;
  }
  
  
  public void setFaceDirection (int facing)
  {
    this.facing = facing;
  }
  
  
  public Vector getPos ()
  {
    return pos.clone();
  }
  
  
  public Vector getVel ()
  {
    return vel.clone();
  }
  
  
  public void stop ()
  {
    vel.set(0.0f, 0.0f);
  }
  
  
  public Size getHitSize ()
  {
    return hitSize.clone();
  }
  
  
  public Size getImageSize ()
  {
    return imageSize.clone();
  }
  
  
  public int getFaceDirection ()
  {
    return facing;
  }
  
  
  public void setImage (Image img)
  {
    image = img;
  }
  
  
  public void setHitSize (Size size)
  {
    hitSize = size;
  }
  
  
  public void setImageSize (Size size)
  {
    imageSize = size;
  }
  
  
  public void setDistFromFloor (float d)
  {
    distFromFloor = d;
  }
  
  
  public void setDistFromLeft (float d)
  {
    distFromLeft = d;
  }
  
  
  public void displace (Vector v)
  {
    pos.add(v);
  }
  
  
  public boolean isAlive ()
  {
    return alive;
  }
  
  
  public boolean isDying ()
  {
    return dying;
  }
}

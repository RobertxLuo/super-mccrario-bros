
package kouyoum.mccrario;


public class SpikeBaal extends Baal
{
  public SpikeBaal (Vector pos)
  {
    super(pos);
    setImage(Resources.SPIKEBAAL);
    setImageSize(new Size(1.2f, 1.2f));
    setHitSize(new Size(0.8f, 0.8f));
    setDistFromFloor(0.1f);
  }
  
  
  public void collideEntity (Entity ent, Direction side, float dist)
  {
    super.collideEntity(ent, side, dist);
    if (ent instanceof Player)
      ent.maybeDie(this, side.opposite());
  }
  
  
  public void maybeDie (Entity ent, Direction side)
  {
    ent.maybeDie(this, side.opposite());
  }
}

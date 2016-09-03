
package kouyoum.mccrario;


public class Size
{
  public float w;
  public float h;
  
  
  public Size (float w, float h)
  {
    this.w = w;
    this.h = h;
  }
  
  
  public Size clone ()
  {
    return new Size(w, h);
  }
  
  
  public boolean equals (Size s)
  {
    return w == s.w && h == s.h;
  }
  
  
  public Vector toVector ()
  {
    return new Vector(w, h);
  }
}


package kouyoum.mccrario;


public class Vector
{
  public float x;
  public float y;
  
  
  public Vector (float x, float y)
  {
    this.x = x;
    this.y = y;
  }
  
  
  public Vector clone ()
  {
    return new Vector(x, y);
  }
  
  
  public boolean equals (Vector v)
  {
    return x == v.x && y == v.y;
  }
  
  
  public String toString ()
  {
    return "<" + x + ", " + y + ">";
  }
  
  
  public void set (float x, float y)
  {
    this.x = x;
    this.y = y;
  }
  
  
  public static Vector add (Vector a, Vector b)
  {
    return new Vector(a.x + b.x, a.y + b.y);
  }
  
  
  public static Vector sub (Vector a, Vector b)
  {
    return new Vector(a.x - b.x, a.y - b.y);
  }
  
  
  public static Vector mul (Vector a, float b)
  {
    return new Vector(a.x * b, a.y * b);
  }
  
  
  public static Vector div (Vector a, float b)
  {
    return new Vector(a.x / b, a.y / b);
  }
  
  
  public void add (Vector v)
  {
    x += v.x;
    y += v.y;
  }
  
  
  public void sub (Vector v)
  {
    x -= v.x;
    y -= v.y;
  }
  
  
  public void mul (float s)
  {
    x *= s;
    y *= s;
  }
  
  
  public void div (float s)
  {
    x /= s;
    y /= s;
  }
  
  
  public static float dot (Vector a, Vector b)
  {
    return a.x * b.x + a.y * b.y;
  }
  
  
  public float mag ()
  {
    return (float) Math.sqrt(x*x + y*y);
  }
  
  
  public float sqrMag ()
  {
    return x*x + y*y;
  }
  
  
  public static Vector unit (Vector v)
  {
    float mag = v.mag();
    if (mag == 0.0f)
      return new Vector(0.0f, 0.0f);
    return Vector.div(v, mag);
  }
  
  
  public void normalize ()
  {
    float mag = mag();
    if (mag != 0.0f)
      this.div(mag());
  }
  
  
  public static Vector perpL (Vector v)
  {
    return new Vector(-v.y, v.x);
  }
  
  
  public static Vector perpR (Vector v)
  {
    return new Vector(v.y, -v.x);
  }
  
  
  public static Vector neg (Vector v)
  {
    return new Vector(-v.x, -v.y);
  }
  
  
  public void negate ()
  {
    x = -x;
    y = -y;
  }
}

/* 31Oct00
*/

import Drawable;
import java.awt.Color;
 
 public class Vertex3D implements Drawable {
     public float x, y, z;           // coordinate of vertex
     public int nfaces;
     public float nx, ny, nz;        // vertex normal
     public int argb;                // color of vertex
 
     public Vertex3D()
     {
         argb = 0;
     }
 
     public Vertex3D(float xval, float yval, float zval)
     {
         x = xval;
         y = yval;
         z = zval;
         nx = ny = nz = 0;
         nfaces = 0;
         argb = 0;
     }
 
     public void setNormal(float xval, float yval, float zval)
     {
         nx = xval;
         ny = yval;
         nz = zval;
         nfaces = 0;
     }
 
     public void addNormal(float xval, float yval, float zval)
     {
         float l = (float) Math.sqrt(xval*xval + yval*yval + zval*zval);
         xval = xval / l;
         yval = yval / l;
         zval = zval / l;
         nx += xval;
         ny += yval;
         nz += zval;
         nfaces += 1;
     }
 
     public void averageNormals()
     {
         float l = (float) Math.sqrt(nx*nx + ny*ny + nz*nz);
         nx = nx / l;
         ny = ny / l;
         nz = nz / l;
     }
 
     public Color getColor()
     {
         return new Color(argb);
     }
 
     public void setColor(Color color)
     {
         argb = color.getRGB();
     }
 
     public void Draw(Raster r)
     {
         int ix = (int) (x + 0.5f);
         int iy = (int) (y + 0.5f);
         if ((ix < 0) || (ix >= r.width)) return;
         if ((iy < 0) || (iy >= r.height)) return;
         r.setPixel(argb, ix, iy);
     }
 
     public String toString()
     {
         return new String(" ["+x+", "+y+", "+z+"]");
     }

     public void Illuminate(Light l[], int lights)
     {
     }

 }
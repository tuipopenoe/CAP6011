// Tui Popenoe
// Vertex3D.java

// Vertex class, stores vertex coordinates and vertex normals
public class Vertex3D {

    // Coordinates
    protected float x=0, y=0, z=0;
    // Vertex Normals
    protected float nx=0, ny=0, nz=0;

    public Vertex3D(){
        x=y=z=nx =ny=nz= 0;
    }

    // Constructor
    public Vertex3D(float xval, float yval, float zval){
        x = xval;
        y = yval;
        z = zval;
        nx = ny = nz = 0;
    }

    //Set methods
    public void setNormal(float xval, float yval, float zval){
        nx = xval;
        ny = yval;
        nz = zval;
    }

    public void setVertex(float xval, float yval, float zval){
        x = xval; y = yval; z = zval;   
        nx =ny=nz=0;
    }

    public void setVertex(float xval, float yval, float zval, float anx,
        float any,float anz){
        x = xval; y = yval; z = zval;
        nx=anx; ny=any; nz=anz;
    }

    // Add a normal to the Vertex
    public void addNormal(float xval, float yval, float zval){
        float l = (float) Math.sqrt(xval*xval + yval*yval + zval*zval);
        xval /=  l;
        yval /=  l;
        zval /=  l;
        nx += xval;
        ny += yval;
        nz += zval;
    }

    // Average the normals
    public void averageNormals(){
        float l = (float) Math.sqrt(nx*nx + ny*ny + nz*nz);
        nx /= l;
        ny /= l;
        nz /= l;
    }

    // Get Methods
    public float getX(){
        return x;
    }
    public float getY(){
        return y;
    }
    public float getZ(){
        return z;
    }
    public float getNX(){
        return nx;
    }
    public float getNY(){
        return ny;
    }
    public float getNZ(){
        return nz;
    }

    // Draw the vertex
    public void Draw(Raster r, int acolor){
        int ix = (int) (x + 0.5f);
        int iy = (int) (y + 0.5f);
        if ((ix < 0) || (ix >= r.width)){
            return;
        }
        if ((iy < 0) || (iy >= r.height)){
            return;
        }
        r.setPixel(acolor, ix, iy);
    }

    // Get a string of the vertex's values
    public String toString(){
        return new String(" ["+x+", "+y+", "+z+"]");
    }
}
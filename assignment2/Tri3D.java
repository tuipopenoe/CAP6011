import java.applet.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class Tri3D extends Applet {
    final static int CHUNKSIZE = 100;
    Raster raster;
    Image screen;
    Vertex3D vertList[];
    Vertex3D worldList[];
    Vertex3D tranList[];
    int vertices;
    Drawable triList[];
    int triangles;
    Matrix4x4 view;
    Matrix4x4 model;
    float xmin, xmax;
    float ymin, ymax;
    float zmin, zmax;

    Light lightList[];
    int lights;
    Surface surfaceList[];
    int surfaces;

    float eyex, eyey, eyez;
    float lookatx, lookaty, lookatz;
    float upx, upy, upz;
    float fov;

    public void init( )
    {
        raster = new Raster(size().width, size().height);
        raster.fill(getBackground());
        screen = raster.toImage(this);

        eyex = 0;        eyey = 0;        eyez = -10;
        lookatx = 0;     lookaty = 0;     lookatz = 0;
        upx = 0;         upy = 1;         upz = 0;
        fov = 30;

        vertList = new Vertex3D[CHUNKSIZE];
        vertices = 0;
        triList = new FlatTri[CHUNKSIZE];
        triangles = 0;
        lightList = new Light[CHUNKSIZE];
        lights = 0;
        surfaceList = new Surface[CHUNKSIZE];
        surfaces = 0;

        xmin = 1000; xmax = -1000;
        ymin = 1000; ymax = -1000;
        zmin = 1000; zmax = -1000;
        /*                  NOTE to run as an applet outside of Eclipse .. IE a webpage, use the code below in an HTML page AND change the filename assignment on line 121 to what is on line 118                  <applet code="Tri3D.class" width="256" height="256">		<param name="datafile" value="object3.obj">		</applet>        
        String filename = getParameter("cow.obj");                */        String filename = "cow.obj";
        showStatus("Reading "+filename);
        InputStream is = null;
        try {
            is = new URL(getDocumentBase(), filename).openStream();
            ReadInput(is);
            is.close();
        } catch (IOException e) {
            showStatus("Error reading "+filename);
        }

        System.out.println("x range = ["+xmin+", "+xmax+"]");
        System.out.println("y range = ["+ymin+", "+ymax+"]");
        System.out.println("z range = ["+zmin+", "+zmax+"]");

        float xscale = (-xmin > xmax) ? -xmin : xmax;
        float yscale = (-ymin > ymax) ? -ymin : ymax;
        float scale = (xscale > yscale) ? xscale : yscale;

        tranList = new Vertex3D[vertList.length];
        worldList = new Vertex3D[vertList.length];
        for (int i = 0; i < vertices; i++) {
            tranList[i] = new Vertex3D();
            worldList[i] = new Vertex3D();
            vertList[i].averageNormals();
        }
        view = new Matrix4x4(raster);
        float t = (float) Math.sin(Math.PI*(fov/2)/180);
        float s = (t*size().height)/size().width;
        view.frustum(-t, t, -s, s, -1, -200);
        view.lookAt(eyex, eyey, eyez, lookatx, lookaty, lookatz, upx, upy, upz);
        model = new Matrix4x4();

        long time = System.currentTimeMillis();
        showStatus("Drawing "+triangles+" triangles ...");
        model.transform(vertList, worldList, vertices);
        ((FlatTri) triList[0]).setVertexList(worldList);
        for (int i = 0; i < triangles; i++) {
            triList[i].Illuminate(lightList, lights);
        }
        view.transform(worldList, tranList, vertices);
        DrawObject();
        time = System.currentTimeMillis() - time;
        showStatus("Time = "+time+" ms");
        screen = raster.toImage(this);
    }

    private double getNumber(StreamTokenizer st) throws IOException
    {
        if (st.nextToken() != StreamTokenizer.TT_NUMBER) {
            throw new IOException(st.toString());
        }
        return st.nval;
    }

    private void growList()
    {
        Drawable newList[] = new Drawable[triList.length+CHUNKSIZE];
        System.arraycopy(triList, 0, newList, 0, triList.length);
        triList = newList;
    }

    public void ReadInput(InputStream is) throws IOException
    {
	    StreamTokenizer st = new StreamTokenizer(is);
    	st.eolIsSignificant(true);
    	st.commentChar('#');
        scan: while (true) {
	        switch (st.nextToken()) {
	          default:
		        break scan;
	          case StreamTokenizer.TT_EOL:
		        break;
	          case StreamTokenizer.TT_WORD:
		        if (st.sval.equals("v")) {
		            float x = (float) getNumber(st);
		            float y = (float) getNumber(st);
		            float z = (float) getNumber(st);
		            if (vertices == vertList.length) {
		                Vertex3D newList[] = new Vertex3D[vertList.length+CHUNKSIZE];
                        System.arraycopy(vertList, 0, newList, 0, vertList.length);
		                vertList = newList;
		            }
		            if (x < xmin) xmin = x; else if (x > xmax) xmax = x;
		            if (y < ymin) ymin = y; else if (y > ymax) ymax = y;
		            if (z < zmin) zmin = z; else if (z > zmax) zmax = z;
		            vertList[vertices++] = new Vertex3D(x, y, z);
		        } else
		        if (st.sval.equals("f")) {
		            int faceTris = 0;
                    int v0 = (int) getNumber(st);
		            int v1 = (int) getNumber(st);
		            while (st.nextToken() == StreamTokenizer.TT_NUMBER) {
		                st.pushBack();
		                int v2 = (int) getNumber(st);
		                if (triangles == triList.length) growList();
		                triList[triangles] = new FlatTri(v0, v1, v2);
		                float nx = (vertList[v1].z - vertList[v0].z) * (vertList[v2].y - vertList[v1].y)
                                 - (vertList[v1].y - vertList[v0].y) * (vertList[v2].z - vertList[v1].z);
                        float ny = (vertList[v1].x - vertList[v0].x) * (vertList[v2].z - vertList[v1].z)
                                 - (vertList[v1].z - vertList[v0].z) * (vertList[v2].x - vertList[v1].x);
                        float nz = (vertList[v1].y - vertList[v0].y) * (vertList[v2].x - vertList[v1].x)
                                 - (vertList[v1].x - vertList[v0].x) * (vertList[v2].y - vertList[v1].y);
                        if (faceTris == 0) {
                            vertList[v0].addNormal(nx, ny, nz);
                            vertList[v1].addNormal(nx, ny, nz);
                        }
                        if (surfaces == 0) {
                            surfaceList[surfaces] = new Surface(0.5f, 0.5f, 0.5f, 1.0f, 1.0f, 1.0f, 5.0f);
		                    surfaces += 1;
                        }
                        ((FlatTri) triList[triangles]).setSurface(surfaceList[surfaces-1]);
                        vertList[v2].addNormal(nx, ny, nz);
		                v1 = v2;
		                faceTris += 1;
                        triangles += 1;
		            }
			    } else
			    if (st.sval.equals("eye")) {
                    eyex = (float) getNumber(st);
                    eyey = (float) getNumber(st);
		            eyez = (float) getNumber(st);
			    } else
			    if (st.sval.equals("look")) {
                    lookatx = (float) getNumber(st);
                    lookaty = (float) getNumber(st);
		            lookatz = (float) getNumber(st);
			    } else
			    if (st.sval.equals("up")) {
                    upx = (float) getNumber(st);
                    upy = (float) getNumber(st);
		            upz = (float) getNumber(st);
			    } else
			    if (st.sval.equals("fov")) {
                    fov = (float) getNumber(st);
			    } else
			    if (st.sval.equals("la")) {
                    float r = (float) getNumber(st);
                    float g = (float) getNumber(st);
		            float b = (float) getNumber(st);
		            lightList[lights] = new Light(Light.AMBIENT, 0, 0, 0, r, g, b);
		            lights += 1;
			    } else
			    if (st.sval.equals("ld")) {
                    float r = (float) getNumber(st);
                    float g = (float) getNumber(st);
		            float b = (float) getNumber(st);
		            float x = (float) getNumber(st);
		            float y = (float) getNumber(st);
		            float z = (float) getNumber(st);
		            lightList[lights] = new Light(Light.DIRECTIONAL, x, y, z, r, g, b);
		            lights += 1;
			    } else
			    if (st.sval.equals("lp")) {
                    float r = (float) getNumber(st);
                    float g = (float) getNumber(st);
		            float b = (float) getNumber(st);
		            float x = (float) getNumber(st);
		            float y = (float) getNumber(st);
		            float z = (float) getNumber(st);
		            lightList[lights] = new Light(Light.POINT, x, y, z, r, g, b);
		            lights += 1;
			    } else
			    if (st.sval.equals("surf")) {
                    float r = (float) getNumber(st);
                    float g = (float) getNumber(st);
		            float b = (float) getNumber(st);
		            float ka = (float) getNumber(st);
		            float kd = (float) getNumber(st);
		            float ks = (float) getNumber(st);
		            float ns = (float) getNumber(st);
		            surfaceList[surfaces] = new Surface(r, g, b, ka, kd, ks, ns);
		            surfaces += 1;
			    }
			    break;
	        }
	        if (triangles % 100 == 0) showStatus("triangles = "+triangles);
	    }
        is.close();
	    if (st.ttype != StreamTokenizer.TT_EOF)
	        throw new IOException(st.toString());
	}

    public void paint(Graphics g)
    {
        g.drawImage(screen, 0, 0, this);
    }

    public void update(Graphics g)
    {
        paint(g);
    }

    float v0x, v0y, v0z;

    public boolean mouseDown(Event e, int x, int y)
    {
        if (e.metaDown()) {
            showStatus("Resetting model matrix");
            model.loadIdentity();
        }
        v0x = (float) (x - (size().width / 2));
        v0y = (float) ((size().height / 2) - y);
        v0z = (float) size().width;
        float l0 = (float) (1 / Math.sqrt(v0x*v0x + v0y*v0y + v0z*v0z));
        v0x *= l0;
        v0y *= l0;
        v0z *= l0;
	    return true;
    }

    public boolean mouseDrag(Event e, int x, int y)
    {
        if (e.metaDown()) {
            showStatus("Resetting model matrix");
        } else {
            float v1x = (float) (x - (size().width / 2));
            float v1y = (float) ((size().height / 2) - y);
            float v1z = (float) size().width;
            float l = (float) (1 / Math.sqrt(v1x*v1x + v1y*v1y + v1z*v1z));
            v1x *= l;
            v1y *= l;
            v1z *= l;

            float ax = v0y*v1z - v0z*v1y;
            float ay = v0z*v1x - v0x*v1z;
            float az = v0x*v1y - v0y*v1x;
            l = (float) Math.sqrt(ax*ax + ay*ay + az*az);
            float theta = (float) Math.asin(l);
            if (v0x*v1x + v0y*v1y + v0z*v1z < 0)
                theta += (float) Math.PI / 2;
            model.rotate(ax, ay, az, theta);
            v0x = v1x;
            v0y = v1y;
            v0z = v1z;
            model.transform(vertList, worldList, vertices);
            ((FlatTri) triList[0]).setVertexList(worldList);
            for (int i = 0; i < triangles; i++) {
                triList[i].Illuminate(lightList, lights);
            }
            view.transform(worldList, tranList, vertices);
            DrawObject();
        }
        screen = raster.toImage(this);
        repaint();
        return true;
    }

    void DrawObject()
    {
        ((FlatTri) triList[0]).setVertexList(tranList);
        raster.fill(getBackground());
        raster.resetz();
        for (int i = 0; i < triangles; i++) {
            triList[i].Draw(raster);
        }
    }

}
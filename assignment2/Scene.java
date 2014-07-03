
import java.awt.*;
import java.awt.event.*;
import java.net.*; 
import java.io.*;

public class Scene extends java.awt.Canvas 
{
    final protected static int CHUNKSIZE=100;
    protected Surface surfaceList[];
    protected int nsurfaces;
    protected int rending_mode;
    protected Light lightList[];
    protected int lights;
    protected float eyex, eyey, eyez;
    protected float lookatx, lookaty, lookatz;
    protected float upx, upy, upz;
    protected float fov;
    protected Raster raster;
    protected Image screen;
    protected TriDObject m3DObject;
    protected boolean reflect_mode=true;


    
    public Scene()
    {
        setSize(256,256);
        Reset();
    }
    void Reset()
    {
        lightList = new Light[CHUNKSIZE];
        lights = 0;
        reflect_mode=true;
        surfaceList=new Surface[CHUNKSIZE];
        surfaceList[0] = new Surface(0.5f, 0.5f, 0.5f, 1.0f, 1.0f, 1.0f, 5.0f);
        nsurfaces =1;
        raster = new Raster(size().width,size().height);
        raster.fill(Color.gray);/*getBackground());*/
        
        screen = raster.toImage(this);
        
        eyex = 0;        eyey = 0;        eyez = -10;
        lookatx = 0;     lookaty = 0;     lookatz = 0;
        upx = 0;         upy = 1;         upz = 0;
        fov = 30;
        rending_mode=TriDObject.GouraudReflect;
        m3DObject=new TriDObject(this);
 
    }
    
    public boolean FileOpen(URL turl,String filename)
    {
           
        InputStream  is;
        try {
            is = new URL(turl, filename).openStream();
            ReadInput(is);
            is.close();
        } catch (IOException e)
        {

            return false;
        }
        m3DObject.AverageNormal();
        Draw();
        return true;
    }
    
    
    public void Draw()
    {
        float t = (float) Math.sin(Math.PI*(fov/2)/180);
        float s = (t* size().height)/ size().width;
        Matrix4x4 view =new Matrix4x4(raster);
        view.frustum(-t, t, -s, s, -1, -200);
        view.lookAt(eyex, eyey, eyez
                    , lookatx, lookaty, lookatz
                    , upx, upy, upz);
        raster.resetz();
        m3DObject.Draw(lightList,lights,raster,view,rending_mode,reflect_mode,raster);
        screen=raster.toImage(this);
        repaint();
    }
    
    private double getNumber(StreamTokenizer st) throws IOException
    {
        if (st.nextToken() != StreamTokenizer.TT_NUMBER) {
            throw new IOException(st.toString());
        }
        return st.nval;
    }
    public void ReadInput(InputStream is) throws IOException
    {
    	StreamTokenizer st = new StreamTokenizer(is);
    	st.eolIsSignificant(true);
    	st.commentChar('#');
        scan: while (true)
        {
	        switch (st.nextToken())
	        {
    	        default:
		            break scan;
	            case StreamTokenizer.TT_EOL:
		            break;
	            case StreamTokenizer.TT_WORD:
		            if (st.sval.equals("eye"))
		            {
                        eyex = (float) getNumber(st);
                        eyey = (float) getNumber(st);
	    	            eyez = (float) getNumber(st);
		    	        break;
		    	    } 
			        if (st.sval.equals("look"))
			        {
                        lookatx = (float) getNumber(st);
                        lookaty = (float) getNumber(st);
	    	            lookatz = (float) getNumber(st);
		    	        break;
		    	    } 
        		    if (st.sval.equals("up"))
        		    {
                        upx = (float) getNumber(st);
                        upy = (float) getNumber(st);
		                upz = (float) getNumber(st);
                        break;
    			    } 
	                if (st.sval.equals("fov"))
	                {
                        fov = (float) getNumber(st);
                        break;
    			    } 
	        	    if (st.sval.equals("la"))
	        	    {
                        float r = (float) getNumber(st);
                        float g = (float) getNumber(st);
	    	            float b = (float) getNumber(st);
                        Light l=new Light(Light.AMBIENT, 0, 0, 0, r, g, b);
		                lightList[lights] = new Light(Light.AMBIENT, 0, 0, 0, r, g, b);
		                lights ++;
                        break;
			        } 
    			    if (st.sval.equals("ld"))
    			    {
                        float r = (float) getNumber(st);
                        float g = (float) getNumber(st);
		                float b = (float) getNumber(st);
		                float x = (float) getNumber(st);
    		            float y = (float) getNumber(st);
	    	            float z = (float) getNumber(st);
		                lightList[lights] = new Light(Light.DIRECTIONAL, x, y, z, r, g, b);
		                lights ++;
			            break;
			        } 
    			    if (st.sval.equals("lp"))
    			    {
                        float r = (float) getNumber(st);
                        float g = (float) getNumber(st);
		                float b = (float) getNumber(st);
    		            float x = (float) getNumber(st);
	    	            float y = (float) getNumber(st);
		                float z = (float) getNumber(st);
		                lightList[lights] = new Light(Light.POINT, x, y, z, r, g, b);
	    	            lights += 1;
	    	            break;
		    	    }
    		        if (st.sval.equals("v")) 
    		        {

		                float x = (float) getNumber(st);
    		            float y = (float) getNumber(st);
    		            float z = (float) getNumber(st);
    		    	    m3DObject.AddVertix(x,y,z);
    		    	    break;
    		    	}   
    		    	if (st.sval.equals("f"))  
    		    	{
/*	    	             int v1 = (int) getNumber(st);
    		             int v2 = (int) getNumber(st); 
    		             int v3 = (int) getNumber(st); 
                         int v4 =-1;
                         if(st.nextToken() == StreamTokenizer.TT_NUMBER)
                         {
		                    st.pushBack();
    		                v4 = (int) getNumber(st); 
    		             }
    		             m3DObject.AddTriFace(v1,v2, v3,v4
    		                    ,surfaceList[nsurfaces-1]);

    		    	    */
    		            int faceTris = 0;
                        int v0 = (int) getNumber(st);
		                int v1 = (int) getNumber(st);
                        float nx[]=new float[1]
                             ,ny[]=new float[1]
                             ,nz[]=new float[1]
                             ;
	    	            while (st.nextToken() == StreamTokenizer.TT_NUMBER) 
		                {
    		                st.pushBack();
	    	                int v2 = (int) getNumber(st);
                            if (faceTris == 0) 
                            {
            		            m3DObject.GetNormalVertor(v0, v1, v2
                                                        ,nx, ny, nz);
                                m3DObject.addNormal(v0,nx[0],ny[0],nz[0]);
                                m3DObject.addNormal(v1,nx[0],ny[0],nz[0]);
                            }
                            m3DObject.addNormal(v2,nx[0],ny[0],nz[0]);
        		             m3DObject.AddTriFace(v0,v1, v2
    	    	                    ,surfaceList[nsurfaces-1]);
    		                v1 = v2;
	    	                faceTris += 1;
     		         	}
       		            break;
       		       } 	
    		       if (st.sval.equals("surf")) 
    		       {
                        float r = (float) getNumber(st);
                        float g = (float) getNumber(st);
    		            float b = (float) getNumber(st);
    		            float ka = (float) getNumber(st);
    		            float kd = (float) getNumber(st);
    		            float ks = (float) getNumber(st);
    		            float ns = (float) getNumber(st);
    		            surfaceList[nsurfaces] = new Surface(r, g, b, ka, kd, ks, ns);
                        nsurfaces ++;
    			        break;
    			    }
	            }
    	    }
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
    public void setRenderMode(int render_mode)
    {
          rending_mode=render_mode;
    }
    public void setReflect(boolean bReflect)
    {
          reflect_mode= bReflect;
    }
    public void Operation(Matrix4x4 m) 
    {
         m3DObject.Operation( m);
    }

    float v0x, v0y, v0z;
    Matrix4x4 model=new Matrix4x4();

    public boolean mouseDown(Event e, int x, int y)
    {
     if (e.metaDown()) {
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
        if (e.metaDown())
              return true;
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

        Operation(model); 
        Draw();
        model.loadIdentity();
        return true;
    }



}
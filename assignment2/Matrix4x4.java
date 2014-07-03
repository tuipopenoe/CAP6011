
public class Matrix4x4 {
    private float m[];

    public Matrix4x4()      // null constructor allows for extension
    {
        m = new float[16];
        loadIdentity();
    }

    public Matrix4x4(Raster r)
    {
        m = new float[16];
        float w = r.width / 2;
        float h = r.height / 2;
        float d = Raster.MAXZ / 2;
        m[ 0] = w;  m[ 1] = 0;  m[ 2] = 0;  m[ 3] = w;
        m[ 4] = 0;  m[ 5] = h;  m[ 6] = 0;  m[ 7] = h;
        m[ 8] = 0;  m[ 9] = 0;  m[10] = d;  m[11] = d;
        m[12] = 0;  m[13] = 0;  m[14] = 0;  m[15] = 1;
    }

    public Matrix4x4(Matrix4x4 copy)    // makes a copy of the matrix
    {
        m = new float[16];
        for(int i=0;i<16;i++)
            m[i]=copy.get(i);
    }


    /*
        ... Methods for setting and getting matrix elements ...
    */
    public final void set(int j, int i, float val)
    {
        m[4*j+i-5] = val;
    }

    public final void set(int i, float val)
    {
        m[i] = val;
    }

    public final float get(int j, int i)
    {
        return m[4*j+i-5];
    }

    public final float get(int i)
    {
        return m[i];
    }


    public final void copy(Matrix4x4 src)
    {
        for(int i=0;i<16;i++)
            m[i]=src.get(i);
    }
    public void transform(float ix,float iy, float iz
                          ,float ox[],float oy[], float oz[])
    {
         float x, y, z, w;
         x = m[0]*ix + m[1]*iy + m[2]*iz + m[3];
         y = m[4]*ix + m[5]*iy + m[6]*iz + m[7];
         z = m[8]*ix + m[9]*iy + m[10]*iz + m[11];
         w = m[12]*ix + m[13]*iy + m[14]*iz + m[15];
         w = 1f / w;

          ox[0] = x*w;
          oy[0] = y*w;
          oz[0] = z*w;
        
        
    }
    public void transform(Vertex3D in[], Vertex3D out[], int vertices)
    {
        float x, y, z, w;
        for (int i = 0; i < vertices; i++) 
        {
            x = m[0]*in[i].x + m[1]*in[i].y + m[2]*in[i].z + m[3];
            y = m[4]*in[i].x + m[5]*in[i].y + m[6]*in[i].z + m[7];
            z = m[8]*in[i].x + m[9]*in[i].y + m[10]*in[i].z + m[11];
            w = m[12]*in[i].x + m[13]*in[i].y + m[14]*in[i].z + m[15];
            w = 1f / w;

            out[i].x = x*w;
            out[i].y = y*w;
            out[i].z = z*w;

            x = m[0]*in[i].nx + m[1]*in[i].ny + m[2]*in[i].nz;
            y = m[4]*in[i].nx + m[5]*in[i].ny + m[6]*in[i].nz;
            z = m[8]*in[i].nx + m[9]*in[i].ny + m[10]*in[i].nz;

            w = (float) Math.sqrt(x*x + y*y + z*z);
            w = 1f / w;
            out[i].nx = w*x;
            out[i].ny = w*y;
            out[i].nz = w*z;
        }
    }

    public final void compose(Matrix4x4 s)
    {
        float t0, t1, t2, t3;
        for (int i = 0; i < 16; i += 4) {
            t0 = m[i  ];
            t1 = m[i+1];
            t2 = m[i+2];
            t3 = m[i+3];
            m[i  ] = t0*s.get(0) + t1*s.get(4) + t2*s.get( 8) + t3*s.get(12);
            m[i+1] = t0*s.get(1) + t1*s.get(5) + t2*s.get( 9) + t3*s.get(13);
            m[i+2] = t0*s.get(2) + t1*s.get(6) + t2*s.get(10) + t3*s.get(14);
            m[i+3] = t0*s.get(3) + t1*s.get(7) + t2*s.get(11) + t3*s.get(15);
        }
    }

    public void loadIdentity()
    {
        for (int i = 0; i < 16; i++)
            if ((i >> 2) == (i & 3))
                m[i] = 1;
            else
                m[i] = 0;
    }

    public void mult(float r[])    // Note: r is in row major order
    {
        float t0, t1, t2, t3;

        for (int i = 0; i < 16; i += 4) {
            t0 = m[i];
            t1 = m[i+1];
            t2 = m[i+2];
            t3 = m[i+3];
            m[i  ] = t0*r[ 0] + t1*r[ 1] + t2*r[ 2] + t3*r[ 3];
            m[i+1] = t0*r[ 4] + t1*r[ 5] + t2*r[ 6] + t3*r[ 7];
            m[i+2] = t0*r[ 8] + t1*r[ 9] + t2*r[10] + t3*r[11];
            m[i+3] = t0*r[12] + t1*r[13] + t2*r[14] + t3*r[15];
        }
    }

    public void translate(float tx, float ty, float tz)
    {
        m[ 3] += m[ 0]*tx + m[ 1]*ty + m[ 2]*tz;
        m[ 7] += m[ 4]*tx + m[ 5]*ty + m[ 6]*tz;
        m[11] += m[ 8]*tx + m[ 9]*ty + m[10]*tz;
        m[15] += m[12]*tx + m[13]*ty + m[14]*tz;
    }

    public void scale(float sx, float sy, float sz)
    {
        m[ 0] *= sx; m[ 1] *= sy; m[ 2] *= sz;
        m[ 4] *= sx; m[ 5] *= sy; m[ 6] *= sz;
        m[ 8] *= sx; m[ 9] *= sy; m[10] *= sz;
        m[12] *= sx; m[13] *= sy; m[14] *= sz;
    }

    public void rotate2(float ax, float ay, float az, float angle)
    {
        float t0, t1, t2;
        if (Math.abs(angle)<0.00000001) return;
        t0 = ax*ax + ay*ay + az*az;
        if (Math.abs(t0)<0.00000001) return;
        float s=(float)Math.cos(angle*.5f)
            , t=(float)Math.sin(angle*.5f)
            ;
        t0 = 1f / ((float) Math.sqrt(t0))*t;
        ax *= t0;
        ay *= t0;
        az *= t0;
        float aa=ax*ax,ab=ax*ay,ac=ax*az,bb=ay*ay,bc=ay*az,cc=az*az 
              ,sc=s*az,sb=s*ay,sa=s*ax;    
        m[0]=2f*(0.5f-bb-cc); m[1]=2f*(ab-sc); m[2]=2f*(ac+sb); m[3]=0; 
        m[4]=2f*(ab+sc);  m[5]=2f*(0.5f-aa-cc);m[6]=2f*(bc-sa); m[7]=0;
        m[8]=2f*(ac-sb);  m[9]=2f*(bc+sa);  m[10]=2f*(0.5f-aa-bb);m[11]=0;
        m[12]=0;         m[13]=0;         m[14]=0;          m[15]=1;
    }
    
    public void rotate(float ax, float ay, float az, float angle)
    {
        Matrix4x4 t=new Matrix4x4();
        t.rotate2(ax, ay,az,angle);
        compose(t);
    }

    public void lookAt(float eyex, float eyey, float eyez,
                       float atx,  float aty,  float atz,
                       float upx,  float upy,  float upz)
    {
        float t0, t1, t2;

        /*
            .... a unit vector along the line of sight ....
        */
        atx -= eyex;
        aty -= eyey;
        atz -= eyez;

        t0 = atx*atx + aty*aty + atz*atz;
        if (t0 == 0) return;                // at and eye at same point
        t0 = (float) (1 / Math.sqrt(t0));
        atx *= t0;
        aty *= t0;
        atz *= t0;

        /*
            .... a unit vector to the right ....
        */
        float rightx, righty, rightz;
        rightx = aty*upz - atz*upy;
        righty = atz*upx - atx*upz;
        rightz = atx*upy - aty*upx;
        t0 = rightx*rightx + righty*righty + rightz*rightz;
        if (t0 == 0) return;                // up is the same as at
        t0 = (float) (1 / Math.sqrt(t0));
        rightx *= t0;
        righty *= t0;
        rightz *= t0;


        /*
            .... a unit up vector ....
        */
        upx = righty*atz - rightz*aty;
        upy = rightz*atx - rightx*atz;
        upz = rightx*aty - righty*atx;


        /*
            .... find camera translation ....
        */
        float tx, ty, tz;
        tx = rightx*eyex + righty*eyey + rightz*eyez;
        ty = upx*eyex + upy*eyey + upz*eyez;
        tz = atx*eyex + aty*eyey + atz*eyez;

        /*
            .... do transform ....
        */
        for (int i = 0; i < 16; i += 4) {
            t0 = m[i];
            t1 = m[i+1];
            t2 = m[i+2];
            m[i  ] = t0*rightx + t1*upx - t2*atx;
            m[i+1] = t0*righty + t1*upy - t2*aty;
            m[i+2] = t0*rightz + t1*upz - t2*atz;
            m[i+3] -= t0*tx + t1*ty - t2*tz;
        }
    }

    public void frustum(float left, float right,
                        float bottom, float top,
                        float near, float far)
    {
        float t0, t1, t2, t3;

        t0 = 1f / (right - left);
        t1 = 1f / (bottom - top);
        t2 = 1f / (far - near);

        float m13 = -t0*(right + left);
        float m23 = -t1*(bottom + top);
        float m33 = t2*(far + near);

        near *= 2;
        float m11 = t0*near;
        float m22 = t1*near;
        float m34 = -t2*far*near;

        for (int i = 0; i < 16; i += 4) {
            t0 = m[i];
            t1 = m[i+1];
            t2 = m[i+2];
            m[i  ] = t0*m11;
            m[i+1] = t1*m22;
            m[i+2] = t0*m13 + t1*m23 + t2*m33 + m[i+3];
            m[i+3] = t2*m34;
        }
    }

    public String toString()
    {
        return ("[ ["+m[ 0]+", "+m[ 1]+", "+m[ 2]+", "+m[ 3]+" ], ["+
                      m[ 4]+", "+m[ 5]+", "+m[ 6]+", "+m[ 7]+" ], ["+
                      m[ 8]+", "+m[ 9]+", "+m[10]+", "+m[11]+" ], ["+
                      m[12]+", "+m[13]+", "+m[14]+", "+m[15]+" ] ]");
    }
}

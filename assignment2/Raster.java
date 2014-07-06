// Tui Popenoe
// Render3D.java

import java.awt.*;
import java.awt.image.*;
import java.util.*;

class Raster implements ImageObserver{
    public final static int MAXZ =  2147483647;
    public int width, height;
    public int pixel[];
    public int zbuff[];
    private ImageProducer producer;
    boolean available;

    // Constructor
    public Raster(){
    }

    // Constructor initialized to width w and height h
    public Raster(int w, int h){
        width = w;
        height = h;
        pixel = new int[w*h];
        zbuff = new int[w*h];
    }


    // Raster initialized with contents of the image
    public Raster(Image img){
        width = img.getWidth(this);
        height = img.getHeight(this);

        try {
            if ((width < 0) || (height < 0)) {
                available = false;
                while (!available) {
                    Thread.sleep((long) 100);
                }
            }
            pixel = new int[width*height];
            zbuff = new int[width*height];
            PixelGrabber  pg = new PixelGrabber(img,0,0,width,height,pixel,0,width);
            pg.grabPixels();
        } catch (InterruptedException e) {
            width = 0;
            height = 0;
            pixel = null;
            return;
        }
    }

    public boolean imageUpdate(Image img, int flags, int x,int y,int w,int h){
        if ((flags & WIDTH) !=0){
            width = w;
        }
        if ((flags & HEIGHT) !=0){
            height = h;
        }
        available = ((width >= 0) && (height >= 0));
        return !available;
    }

    // Return the number of pixels in the Raster
    public final int size( ){
        return width*height;
    }

    // Fill the Raster with the color c
    public final void fill(Color c){
        int s = size();
        int rgb = c.getRGB();
        for (int i = 0; i < s; i++)
            pixel[i] = rgb;
    }

    // Set all z values in the buffer to the maximum value
    public final void resetz(){
        for (int i = 0; i < zbuff.length; i++){
            zbuff[i] = MAXZ;
        }
    }

    // Converts Rasters to Images
    public Image toImage(Component root){
        return root.createImage(new MemoryImageSource(width, height, pixel, 0, width));
    }

    // Get a pixel from the Raster
    public final int getPixel(int x, int y){
        return pixel[y*width+x];
    }

    // Get a color from the Raster
    public final Color getColor(int x, int y){
        return new Color(pixel[y*width+x]);
    }

    // Sets a pixel to the given value
    public final boolean setPixel(int pix, int x, int y){
        pixel[y*width+x] = pix;
        return true;
    }

    // Sets a pixel to the given value if it is closer than the given value
    public final boolean zBufferPixel(int pix, int x, int y, int z){
        int i = y*width+x;
        if (z <= zbuff[i]) pixel[i] = pix;
        return true;
    }

    // Set a pixel to the given color
     public final boolean setColor(Color c, int x, int y){
        pixel[y*width+x] = c.getRGB();
        return true;
    }
}
public abstract interface Drawable 
{
    public abstract void Draw(Raster r);
    public abstract void Illuminate(Light l[], int lights);
}

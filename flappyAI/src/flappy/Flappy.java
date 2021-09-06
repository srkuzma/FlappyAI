package flappy;

import java.awt.Image;

public class Flappy implements Cloneable {
	int x, y, w,h;
	Image im;
	double g = 0.2;
	double v;
	int numOfFrames;
	boolean isAlive;
	final static int numOfBirds = 50;
	public int canFlap = 0;
	Parameters params;
	
	public static final int numHidden = 2;
	Parameters[] hiddenLayer = new Parameters[numHidden];
	public static int numOfAlive = numOfBirds;
	public Flappy(Image im,int x,int y, int w, int h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.im = im;
		this.numOfFrames = 0;
		isAlive = true;
		params = new Parameters(numHidden + 1);
		for(int i = 0; i<numHidden; i++)
			hiddenLayer[i] = new Parameters(3);
		
	}
	public Flappy clone() {
		try {
			Flappy f = (Flappy)super.clone();
			f.params = (Parameters) params.clone();
			return f;
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
}

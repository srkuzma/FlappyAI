package flappy;

import java.awt.Image;

public class Pillar {
	int x;
	int y;
	int w;
	int emptySize;
	
	Image low;
	Image high;
	public Pillar(int x, int y, int w, int es, Image imgDown, Image imgUp) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.emptySize = es;
		low = imgDown;
		high = imgUp;
	}
}

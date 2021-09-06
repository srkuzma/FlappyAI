package flappy;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;

import javax.swing.JTabbedPane;

public class ScoreBoard {
	int score;
	int highScore;
	
	private void transformAndDraw(Graphics2D g, String toDraw, int w, int h, boolean offset, int fontSize) {
		Font f = new Font("Comic Sans MS", Font.BOLD, fontSize);
		g.setFont(f);
		FontMetrics metrics = g.getFontMetrics(g.getFont());
		GlyphVector glyphVector = f.createGlyphVector(g.getFontRenderContext(), toDraw);
        // get the shape object
        Shape textShape = glyphVector.getOutline();
        //glyphVector.t
        // activate anti aliasing for text rendering (if you want it to look nice)
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);

        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(2));
        AffineTransform tx = new AffineTransform();
		tx.translate(w/2 - metrics.stringWidth(toDraw)/2, h/3 - (offset ? 1.3 * metrics.getAscent() : 0));
        textShape = tx.createTransformedShape(textShape);
        //g.setClip(50, 50, 50, 50);
        g.draw(textShape); // draw outline
        g.setColor(Color.WHITE);
        g.fill(textShape); // fill the shape
	}
	
	public void draw(Graphics2D g, int w, int h, boolean gameOver) {
			
			String toDraw;
			if (gameOver) {
				toDraw = "Score: " + score;
				transformAndDraw(g, toDraw, w, h, true, 18);
				toDraw = "Highscore: " + highScore;
				transformAndDraw(g, toDraw, w, h, false, 18);
			}
			else {
				toDraw = ((Integer)score).toString(); 
				transformAndDraw(g, toDraw, w, h, true, 24);
			}
			
			
	}
	
}

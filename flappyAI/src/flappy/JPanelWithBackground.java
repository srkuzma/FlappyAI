package flappy;



import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class JPanelWithBackground extends JPanel {
	public static final int INF = 1000000000;
	
	Image flappyImage;
	Image backgroundImage;
	Image pillarDownImage;
	Image pillarUpImage;
	int FLAPPY_WIDTH;
	int FLAPPY_HEIGHT;
	int BOOST = 4;
	int OFFSET = 200;
	int EMPTY_SIZE = 90;
	int PILLAR_WIDTH = 40;
	ArrayList<Flappy> birds = new ArrayList<Flappy>();
	Timer fps;
	boolean gameOver = false;
	boolean started = false;
	ScoreBoard sb = new ScoreBoard();
	int pos = 0;
	
	boolean spaceDisabled = false;
	int opacityChange = 0;
	int currPillar = 0;
	BufferedImage biDown = null;
	BufferedImage biUp = null;
	ArrayList<Pillar> pillars = new ArrayList<>();
	//ArrayList<Image> pillarImages = new ArrayList<>();
	
	public JPanelWithBackground(String flp, String bck, String pillDown, String pillUp) {
		flappyImage = new ImageIcon(getClass().getClassLoader().getResource(flp)).getImage();
		backgroundImage = new ImageIcon(getClass().getClassLoader().getResource(bck)).getImage();
		FLAPPY_HEIGHT = flappyImage.getHeight(null);
		FLAPPY_WIDTH = flappyImage.getWidth(null);
		pillarDownImage = new ImageIcon(getClass().getClassLoader().getResource(pillDown)).getImage();
		pillarUpImage = new ImageIcon(getClass().getClassLoader().getResource(pillUp)).getImage();
		//pillarImage = new ImageIcon(getClass().getClassLoader().getResource(pillDown)).getImage();
		
		URL urlDown = this.getClass().getClassLoader().getResource(pillDown);
		URL urlUp = this.getClass().getClassLoader().getResource(pillUp);
		try {
			biDown = ImageIO.read(urlDown);
			biUp = ImageIO.read(urlUp);
			
			/*pillarDownImage = pillarDownImage.getScaledInstance(PILLAR_WIDTH, 1000, Image.SCALE_DEFAULT);
			pillarUpImage = pillarUpImage.getScaledInstance(PILLAR_WIDTH, 1000, Image.SCALE_DEFAULT);
			
		    biDown = new BufferedImage(biDown.getWidth(), biDown.getHeight(), Image.SCALE_DEFAULT);
			biDown.getGraphics().drawImage(pillarDownImage, 0, 0 , null);
			
			biUp = new BufferedImage(biUp.getWidth(), biUp.getHeight(), Image.SCALE_DEFAULT);
			biUp.getGraphics().drawImage(pillarUpImage, 0, 0 , null);*/
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		createBirds();
		
		fps = new Timer(10, (ae) -> {
			
			//adding new pillar if necessary
			if (pillars.isEmpty() || pillars.get(pillars.size()-1).x < getWidth()) {
				
				Pillar p = new Pillar(getWidth() + OFFSET, 40 + (int)(Math.random()*(getHeight() - EMPTY_SIZE - 80)), PILLAR_WIDTH, EMPTY_SIZE, pillarDownImage, pillarUpImage); 
				
				p.low = biDown.getSubimage(0,biDown.getHeight()- p.y, p.w, p.y);
				p.high = biUp.getSubimage(0,  0, p.w, getHeight() - p.y - EMPTY_SIZE);
				pillars.add(p);
				
			}
			
			//checking if first pillar is out of bounds
			if (pillars.get(0).x + PILLAR_WIDTH < 0) {
				pillars.remove(0);
				currPillar--;
			}
			
			//checking if we should update the score
			boolean shouldUpdate = true;
			for(Flappy bird:birds) {
				if (bird.isAlive && bird.x < pillars.get(currPillar).x + PILLAR_WIDTH) {
					shouldUpdate = false;
					
				}
			}
			
			if (shouldUpdate) {
				sb.score++;
				currPillar++;
				if (sb.score > sb.highScore)
					sb.highScore = sb.score;
			}
			
			//moving pillars to the left
			for(Pillar p:pillars) {
				p.x -= 1;
			}
			
			for(Flappy bird:birds)
				if (bird.isAlive && started) {
					
					bird.numOfFrames++;
					
					bird.canFlap--;
					if (bird.canFlap < 0)
						bird.canFlap = 0;
					//checking if the bird should flap and doing so if yes
					if (shouldFlap(bird)) {
						bird.v = BOOST;
						bird.canFlap  = 20;
					}
					
					//moving the bird and checking if it hit the ground
					bird.v = bird.v - bird.g;
					bird.y = (int)(bird.y - bird.v);
					if (bird.y + FLAPPY_HEIGHT > getHeight()) {
						removeBird(bird);
						bird.numOfFrames = 1;
					}
						
					
				}
			checkCollisions();
			if (Flappy.numOfAlive == 0) {
				restartGame();
			}
			repaint();
		});
		fps.start();

		
	}
	
	private void createBirds() {
		//creating birds at start and setting their weights to random numbers
		for(int i = 0; i<Flappy.numOfBirds; i++) {
			
			Flappy newFlappy = new Flappy(flappyImage, 0,0, FLAPPY_WIDTH, FLAPPY_HEIGHT);
			birds.add(newFlappy);
		}
	}

	private void checkCollisions() {
		//checking if birds hit some pillar
		Pillar p = pillars.get(currPillar);
		for(Flappy bird:birds)
			if (bird.isAlive)
				if (bird.y < 0 || ((p.x < bird.x + FLAPPY_WIDTH && p.x + PILLAR_WIDTH > bird.x) && !(bird.y > p.y && bird.y + FLAPPY_HEIGHT < p.y + EMPTY_SIZE))) {
					if (bird.y < 0)
						bird.numOfFrames = 1;
					else if (sb.score > 0) {
						bird.numOfFrames *= 500;
					}
					removeBird(bird);
				}
		
	}

	private void removeBird(Flappy bird) {
		bird.isAlive = false;
		Flappy.numOfAlive--;

	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);	
		
		if (!started) {
			started = true;
			
			for(Flappy bird:birds) {
				bird.x = getWidth()/3;
				bird.y = (int)(Math.random() * getHeight());
			}
				
		}
		Graphics2D g2D = (Graphics2D) g;
		
		g2D.drawImage(backgroundImage, 0, 0, null);
		
		for(Pillar p:pillars) {

			g2D.drawImage(p.low, p.x, 0, null);
			g2D.drawImage(p.high, p.x, p.y + EMPTY_SIZE, null);

		}
		
		for(Flappy bird:birds)
			if (bird.isAlive)
				g2D.drawImage(flappyImage, bird.x, bird.y, null);
		
		sb.draw(g2D, getWidth(), getHeight(), gameOver);
			
	}
	
	
	         
	        
	void recombine(Flappy bird1, Flappy bird2) {
		double shouldChange;
		Parameters par1 = bird1.params;
		Parameters par2 = bird2.params;
		
		for(int i = 0; i<= Flappy.numHidden; i++) {
			shouldChange = Math.random();
			if (shouldChange < 0.002) {
				double tmp = par1.pars[i];
				par1.pars[i] = par2.pars[i];
				par2.pars[i] = tmp;
			}
		}
		
		
		for(int i = 0; i< Flappy.numHidden; i++) {
			par1 = bird1.hiddenLayer[i];
			par2 = bird2.hiddenLayer[i];
			for(int j = 0; j<= 2; j++) {
				shouldChange = Math.random();
				if (shouldChange < 0.002) {
					double tmp = par1.pars[j];
					par1.pars[j] = par2.pars[j];
					par2.pars[j] = tmp;
				}
			}
		}
		
		
	}
	
	void mutate(Flappy bird) {
		int arg = 0;
		if (bird.numOfFrames < 10) {
			arg = 1;
			System.out.println("BAD");
		}
			
		mutateParams(bird.params, arg);
		
			
		for(int i  = 0; i<Flappy.numHidden; i++)
			mutateParams(bird.hiddenLayer[i], arg);
	}
	
	void mutateParams(Parameters par, int arg) {
		double mutationFactor = 0.01;
		
		double mutationVariation = mutationFactor * Math.random();
		int mutationDirection = (Math.random() < 0.5) ? -1 : 1;
		
		
		for(int i = 0; i< par.num; i++) {
			mutationVariation = mutationFactor * Math.random();
			mutationDirection = (Math.random() < 0.5) ? -1 : 1;
			if (arg > 0) {
				par.pars[i] = Parameters.random();
				
			}
				
			else 
				par.pars[i] += (mutationVariation * mutationDirection * par.pars[i]);
		}
		
	}
	
	Comparator<Flappy> compareBirds = (Flappy f1, Flappy f2) ->{
		if (f1.numOfFrames >f2.numOfFrames) 
			return -1;
		else if (f1.numOfFrames < f2.numOfFrames)
			return 1;
		else 
			return 0;
	};
	
	private void reproduce() {
		
		//double bestPercentage = 0.01;
		//int numOfFit = (int)(bestPercentage * birds.size());
		ArrayList<Flappy> fitBirds = new ArrayList<Flappy>();
		
		Collections.sort(birds, compareBirds);
		
		//for(Flappy bird:birds)
			//System.out.println(bird.numOfFrames);
		
		for(int i = 0; i < Flappy.numOfBirds; i++) {
			fitBirds.add(birds.get(i));
		}
		birds = new ArrayList<Flappy>();
		
		for(int i = 0; i<Flappy.numOfBirds/2; i++) {
			int pos1 = selectFlappy(fitBirds);
			int pos2 = selectFlappy(fitBirds);
			Flappy bird1 = fitBirds.get(pos1).clone();
			System.out.println(bird1.params.pars[0]);
			Flappy bird2 = fitBirds.get(pos2).clone();
			for(int j = 0; j < Flappy.numHidden; j++)
				System.out.print(bird1.hiddenLayer[j].pars[0] + " ");
			recombine(bird1, bird2);
			mutate(bird1);
			mutate(bird2);
			System.out.println();
			for(int j = 0; j < Flappy.numHidden; j++)
				System.out.print(bird1.hiddenLayer[j].pars[0] + " ");
			System.out.println();
			birds.add(bird1);
			birds.add(bird2);
		}
		
	}
	
	private int selectFlappy(ArrayList<Flappy> fitBirds) {
		int sum = 0;
		ArrayList<Double> probs = new ArrayList<>();
		ArrayList<Double> pref = new ArrayList<>();
		for(int i = 0; i<Flappy.numOfBirds; i++)
			sum += fitBirds.get(i).numOfFrames;
		for(int i = 0; i<Flappy.numOfBirds; i++)
			probs.add(1.0 * fitBirds.get(i).numOfFrames / sum);
		
		pref.add(probs.get(0));
		for(int i = 1; i<Flappy.numOfBirds; i++)
			pref.add(pref.get(i-1) + probs.get(i));
		
		int loc = 0;
		double p = Math.random();
		for(int i = 0; i<Flappy.numOfBirds; i++)
			if (p < pref.get(i)) {
				loc = i;
				break;
			}
		//System.out.println(loc + "L");
		/*int x = (int)(Math.random() * Flappy.numOfBirds * Flappy.numOfBirds);
		int loc = Flappy.numOfBirds - 1 - (int)(Math.sqrt(x));*/
		return loc;
	}

	private void restartGame() {
		pillars = new ArrayList<>();
		sb.score = 0;
		pos = 0;
		started = false;
		Flappy.numOfAlive = Flappy.numOfBirds;
		currPillar = 0;
		int max = 0;
		for(Flappy bird: birds)
			max = Math.max(max, bird.numOfFrames);
		System.out.println(max);
		reproduce();
		
		for(Flappy bird: birds) {
			bird.isAlive = true;
			bird.numOfFrames = 0;
		}
		repaint();

	}
	
	private double xDistance(Flappy bird) {
		
		Pillar p = pillars.get(currPillar);
		return p.x + PILLAR_WIDTH - bird.x;
		
	}
	
	private double yDistance(Flappy bird) {
		
		Pillar p = pillars.get(currPillar);
		return p.y + EMPTY_SIZE/2 - bird.y;
		
	}
	
	private boolean shouldFlap(Flappy bird) {
		Parameters p = bird.params;
		double xDist = xDistance(bird);
		double yDist = yDistance(bird);
		
		double[] input = new double[Flappy.numHidden + 1];
		
		for(int i = 0; i< Flappy.numHidden; i++)
			input[i] = sigmoid(xDist * bird.hiddenLayer[i].pars[0] + yDist * bird.hiddenLayer[i].pars[1] + bird.hiddenLayer[i].pars[2]);
		
		double sum =  0;
		
		for(int i = 0; i<Flappy.numHidden; i++)
			sum += input[i] * bird.params.pars[i];
		
		sum += bird.params.pars[Flappy.numHidden];
		
		
		double sig = sigmoid(sum);
		
		return (sig > 0.5);
	}
	
	private double sigmoid(double x) {
		
		return 1/(1 + Math.exp(-x));
	}
}

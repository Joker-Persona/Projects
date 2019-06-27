package gameplatform;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;

import gameplatform.display.Display;


public abstract class Game
implements Runnable, KeyListener {
	
	//My lovely thread
	private Thread thread;
	//So that the thread doesn't break when started or stopped
	private boolean running = false;
	//screen stuff
	private int height, width;
	private String title;
	protected Display disp;
	//fps
	private int fps = 60;
	
	//draw stuff
	private Graphics g;
	private BufferStrategy bs;
	
	// initialize everything
	
	private void hiddenInit() {
		
		//required stuff
		disp = new Display(title, width, height);
		disp.getFrame().addKeyListener(this);
		
		//extras
		init();
		
	}
	
	// Setup the draw
	
	private void hiddenDraw() {
		
		bs = disp.getCanvas().getBufferStrategy();
		if(bs == null) {
			disp.getCanvas().createBufferStrategy(3);
			return;
		}
		
		g = bs.getDrawGraphics();
		
		g.clearRect(0, 0, width, height);
		
		//Draw
		
		draw(g);
		
		//End
		
		bs.show();
		g.dispose();
		
	}
	
	// To call tests
	
	private void hiddenTest() {
		test();
	}
	
	// for the tests
	
	protected abstract void test();
	
	// I don't know why but run() doesn't like abstract methods
	// So hiddenUpdate is simply so that it doesn't have to call one
 	
	private void hiddenUpdate() {
		update();
	}
	
	// update all variables
	
	protected abstract void update();
	
	// draw without needing to clear canvas and setup buffer strategy
	
	protected abstract void draw(Graphics g);
	
	// Initialize game objects other than the required screen and input handler
	
	protected abstract void init();
	
	// Method called when the thread is started
	
	public void run() {
		
		// Initialize the program
		
		hiddenInit();
		
		// More complex FPS stuff
		
		double timeTicks = 1000000000 / fps;
		double delta = 0;
		long now;
		long lastTime = System.nanoTime();
		
		
		while(running) {
			
			// Complex FPS stuff
			
			now = System.nanoTime();
			delta += (now - lastTime) / timeTicks;
			lastTime = now;
			
			// Keep it under the said fps
			
			if (delta >= 1) {
				
				// Methods that happen during the said time
				
				hiddenUpdate();
				hiddenDraw();
				
				delta -= 1;
				
				// In case tests are required, such as counting FPS
				hiddenTest();
				
			}
			
		}
		
	}
	
	// Start the thread
	
	public synchronized void start() {
		
		if (running)
			return;
		
		running = true;
		thread = new Thread(this);
		thread.start();
		
	}
	
	//Stop the thread
	
	public synchronized void stop() {
		
		if (!running)
			return;
		
		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}

	// This makes the method not required by inherited classes
	// However it can still be overwritten if the situation calls for it
	@Override
	public void keyTyped(KeyEvent e) {
		
	}
	
	// Initiate the variables that will later be used to create the screen
	
	public Game(String t, int w, int h) {
		
		title = t;
		width = w;
		height = h;
		
	}
	
	// Manipulate fps
	
	public void setFps(int n) {
		fps = n;
	}
	
	// Get the height of the game screen
	
	public int getHeight() {
		return height;
	}
	
	// Get the width of the game screen
	
	public int getWidth() {
		return width;
	}
	
	
}
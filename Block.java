import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;

import javax.swing.JComponent;

public class Block extends JComponent {

	//static variables
	private static final long serialVersionUID = 1L;
	private static final int MAX_A = 6; //maximum acceleration allowed for a block
	private static final int MAX_V = 60; //maximum velocity allowed for a block
	public static final double CHARGE_RANGE = 4; //how many times larger than the size of the block that electrical charge has an effect
	public static final double CHARGE_FACTOR = 0.005; //a factor of how much velocity is to be added
	//instance variables
	public int size;
	public int mass;
	public int x;
    public int y;
    public int xv = 0; //x velocity
    public int yv = 0; //y velocity
    public int time = 0; //the time elapsed
    public int xa = 0; //x acceleration
    public int ya = 0; //y acceleration
    public int xgoal = x; //stays still at the beginning
    public int ygoal = y; //stays still at the beginning
    public int charge;
    public Color color;
    public ArrayList<Point> goals = new ArrayList<Point>(); //ArrayList<Point> acts a queue for goals
	private int oldXError = 0; //used for gravitation toward a goal
	private int oldYError = 0; //used for gravitation toward a goal

	/**
	 * Default constructor for a block
	 */
	Block(){
		 //position to middle of screen and color it red with charge 0
		this(MainFrame.WIDTH / 2, MainFrame.HEIGHT / 2, 30, Color.RED, 0);
	}
	
	/**
	 * Parameterized constructor for a block
	 * @param x the x position of the center of the block
	 * @param y the y position of the center of the block
	 * @param size the side length of the block
	 */
	Block(int x, int y, int size, Color color, int charge){
		this.x = x;
		this.y = y;
		this.size = size;
		this.color = color;
		this.charge = charge;
		mass = size * size;
		
	}
	
	/**
	 * Paints the actual block
	 */
    public void paintComponent(Graphics g) {
    	//increment the time
    	time++;
    	super.paintComponent(g);
        g.setColor(color);
        g.fillRect(x - size / 2, y - size / 2, size, size); //block is centered around x and y
        bounce();
        updateAcc();
        checkCollide();
        checkCharge();
        pause();
        repaint();
    }
    
    /**
     * Adds the given point to ArrayList<Point> goals to gravitate toward
     * @param point The point for the block to gravitate to
     */
    public void updateGoal(Point point) {
		goals.add(point);
	}

	/**
     * Ensures that blocks stay in the window
     */
    private void bounce() {
		if(x - size / 2 < 0) { //if too far down
			xv = -xv;
			x = 0 + size / 2;
		}
		else if(x + size / 2 > MainFrame.WIDTH) { //if too far right
			xv = -xv;
			x = MainFrame.WIDTH - size / 2;
		}
		else if(y - size / 2 < 0) { //if too far left
			yv = -yv;
			y = 0 + size / 2;
		}
		else if(y + size / 2 + MainFrame.BANNER_HEIGHT > MainFrame.HEIGHT) { //if too far down
			yv = -yv;
			y = MainFrame.HEIGHT - size / 2 - MainFrame.BANNER_HEIGHT;
		}
	}

    /**
     * Updates velocities of the block
     */
    private void updateVel() {
		x += xv;
		y += yv;
		//ensure that acceleration never goes over maximum
		if(xv < -MAX_V) xv = -MAX_V;
		if(xv > MAX_V) xv = MAX_V;
		if(yv < -MAX_V) yv = -MAX_V;
		if(yv > MAX_V) yv = MAX_V;
    }
    
    /**
     * Updates all of the acceleration values of the block basd on x goals and y goals
     */
	private void updateAcc() {
		//add acceleration to velocity
		xv += xa;
		yv += ya;
		updateVel();
		//make a point goal for the block to gravitate to
		Point goal = new Point(x, y);
		//if there is a goal for the block to move to
		if(goals.size() != 0) {
			//update goal
			goal = goals.get(0);
			xgoal = goal.x;
			ygoal = goal.y;
			//calculate current error
			int xerror = xgoal - x;
			int yerror = ygoal - y;
			//change of error = current error - old error
			int dxerror = xerror - oldXError;
			int dyerror = yerror - oldYError;
			//acceleration compensates based on error and change of error
			xa = xerror + dxerror;
			ya = yerror + dyerror;
			//reset old errors
			oldXError = xerror;
			oldYError = yerror;
			//removes point from list of goals if it has stopped there
			if(xa == 0 && ya == 0 && xv == 0 && yv == 0) {
				goals.remove(0);
			}
		}
		//ensure that acceleration never goes over maximum
		if(xa < -MAX_A) xa = -MAX_A;
		if(xa > MAX_A) xa = MAX_A;
		if(ya < -MAX_A) ya = -MAX_A;
		if(ya > MAX_A) ya = MAX_A;
	}
	
	/**
     * Checks if blocks collide and conserves momentum in collision
     */
	private void checkCollide() {
		for(Block el: MainFrame.blocks) {
			for(Block el2: MainFrame.blocks) {
				if(el != el2 && el.doesCollide(el2)) { //if they collide
					el.collide(el2); //calculate their collisions
				}
			}
		}
	}

	/**
	 * Checks if two blocks are overlapping
	 * @param block2 the non-player block
	 * @return true if two blocks are overlapping/colliding; false if they are not
	 */
	private boolean doesCollide(Block block2) {
		if(this.x + this.size/2 >= block2.x - block2.size/2 //if block right edge overlaps with block2 left edge
				&& this.x - this.size/2 <= block2.x + block2.size/2 //if block left edge overlaps with block2 right edge
				&& this.y + this.size/2 >= block2.y - block2.size/2 //if block bottom edge overlaps with block2 top edge
				&& this.y - this.size/2 <= block2.y + block2.size/2) { //if block top edge overlaps with block2 bottom edge
			return true;
		}
		return false;
	}
	
    /**
     * Computes the amount of momenutum needed to transfer to another block
     * @param block2 the other block that user-controlled block collides with 
     */
	private void collide(Block block2) {
		//perfect elastic collisions with different masses (this = 1 and block2 = 2)
		//conservation of momentum formula: m1v1 + m2v2 = m1'v1' + m2'v2' or for graphing: m_1v_1+m_2v_2=m_1x+m_2y
		//conservation of kinetic energy formla: m1v1^2 + m2v2^2 = m1'v1'^2 + m2'v2'^2 or for graphing: m_1v_1^2+m_2v_2^2=m_1x^2+m_2y^2
		//store block2v as temporary values
		int tempxv = block2.xv;
		int tempyv = block2.yv;
		//store quo as a variable because of repetition
		int quo = this.mass + block2.mass;
		//solve for v1'
		block2.xv = (block2.mass * block2.xv + 2 * this.mass * this.xv - this.mass * block2.xv) / quo;
		block2.yv = (block2.mass * block2.yv + 2 * this.mass * this.yv - this.mass * block2.yv) / quo;
		//solve for v2' 
		this.xv = (this.mass * this.xv + 2 * block2.mass * tempxv - block2.mass * this.xv) / quo;
		this.yv = (this.mass * this.yv + 2 * block2.mass * tempyv - block2.mass * this.yv) / quo;
		
		//perfectly elastic collisions assuming same mass (simply swaps velocities and is more efficient)
//		int tempxv = block2.xv;
//		int tempyv = block2.yv;
//		block2.xv = this.xv;
//		block2.yv = this.yv;
//		this.xv = tempxv;
//		this.yv = tempyv;
		//account for lag and prevent blocks from sticking together or gliding through eachother
		this.updateVel();
		block2.updateVel();
		this.updateVel();
		block2.updateVel();
	}

	/**
     * Ensure that repulsion or attraction occurs according to charge rules
     */
    private void checkCharge() {
    	for(Block el: MainFrame.blocks) { //iterate through array of blocks
			for(Block el2: MainFrame.blocks) {
				if(el != el2) { //if they are different blocks
					el.calculateCharge(el2);
				}
			}
		}
	}

    /**
     * Calculates how much a block has to move according to charge
     * @param el2 the other block to calculate with
     */
	private void calculateCharge(Block el2) {
		if(this.charge == 0 || el2.charge == 0) { //if their charges are neutral
			return; //charge will have no effect
		}
		int dx = this.x - el2.x; //the x difference between two blocks
		int dy = this.y - el2.y; //the y difference between two blocks
		int hyp = (int) Math.hypot(dx, dy); //the hypotenuse distance between two blocks
		if(hyp < CHARGE_RANGE * (this.size + el2.size)) { //if in range
			if(this.charge == el2.charge) { //if their charges are equal, repel
				this.x -= dx; //filler
				el2.x += dx; //filler
				this.y -= dy; //filler
				el2.y += dy; //filler
			}
			else{ //their charges are opposite, attract
				this.x -= dx; //filler
				el2.x += dx; //filler
				this.y -= dy; //filler
				el2.y += dy; //filler
			}
		}
	}
	
	/**
	 * Determines the amount of time between each refresh or repaint
	 */
	private void pause() {
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		};
	}

	/**
	 * Move the block right by increasing the x velocity
	 */
	public void moveRight() {
        xv += 7;
    }

	/**
	 * Move the block left by reducing the x velocity
	 */
    public void moveLeft() {
        xv -= 7;
    }

    /**
     * Move the block down by increasing the y velocity
     */
    public void moveDown() {
        yv += 7;
    }

    /**
     * Move the block up by decreasing the y velocity
     */
    public void moveUp() {
        yv -= 7;
    }

    /**
     * Stops the block in its position
     */
	public void stop() {
		xa = 0;
		ya = 0;
		xv = 0;
		yv = 0;
		goals.clear();
	}
}
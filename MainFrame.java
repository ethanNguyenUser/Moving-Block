import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JFrame;

public class MainFrame extends JFrame implements KeyListener, MouseListener {
	
	private static final long serialVersionUID = 1L;
	public static ArrayList<Block> blocks = new ArrayList<Block>(); //blocks.get(0) is the user-controllable block object
    public static final int HEIGHT = 1080;
    public static final int WIDTH = 1920;
    public static final int BANNER_HEIGHT = 37;
    
    /**
     * main method
     * @param args
     */
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                MainFrame frame = new MainFrame();
                frame.setTitle("Ethan Nguyen's Moving Blocks");
                frame.setResizable(false);
                frame.setSize(WIDTH, HEIGHT);
                frame.setMinimumSize(new Dimension(WIDTH, HEIGHT));
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                //add all of the blocks
                for(Block el: blocks) {
                	frame.getContentPane().add(el);
                	frame.pack();
                    frame.setVisible(true);
                }
            }
        });
    }
    
    /**
     * Constructor
     */
    public MainFrame(){
    	//default user-controlled block
        blocks.add(new Block());
        //second block
        blocks.add(new Block(WIDTH / 4, HEIGHT / 2, 100, Color.GRAY, -1));
        //third block
        blocks.add(new Block(3 * WIDTH / 4, HEIGHT / 2, 20, Color.BLACK, 1));
        //fourth block
        
        //add listeners and initialize
        addKeyListener(this);
        this.getContentPane().addMouseListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
    }
    
    public void keyPressed(KeyEvent e) {
    	// TODO Auto-generated method stub
    }

    /**
     * Moves the block directionally according to arrow input. Also moves
     */
    public void keyReleased(KeyEvent e) {
    	Block block = blocks.get(0);
        if(e.getKeyCode() == KeyEvent.VK_RIGHT)
            block.moveRight();
        else if(e.getKeyCode() == KeyEvent.VK_LEFT)
            block.moveLeft();
        else if(e.getKeyCode() == KeyEvent.VK_DOWN)
            block.moveDown();
        else if(e.getKeyCode() == KeyEvent.VK_UP)
            block.moveUp();
        else if(e.getKeyCode() == KeyEvent.VK_S) {
    		for(Block block2: blocks)
    			block2.stop();
    	}
        else if(e.getKeyCode() == KeyEvent.VK_Q) //quit program
        	System.exit(0);
    }

	public void keyTyped(KeyEvent e) {
    }
	
	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	 /**
     * Updates goal of block to wherever the mouse clicks
     */
	@Override
	public void mouseReleased(MouseEvent e) {
		blocks.get(0).updateGoal(e.getPoint());
	}
}
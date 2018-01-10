package mandlebrot;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
//import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

public class Mandlebrot extends JFrame {

	public static void main(String[] args) {
		new Mandlebrot();
	}

	// size of window to be rendered
	public final int render_WIDTH = 600;
	public final int render_HEIGHT = 400;

	// size of frame
	public final int WIDTH = 800;
	public final int HEIGHT = 600;

	// offset of render
	public int X_OFFSET = 0;
	public int Y_OFFSET = 0;

	// amount of details
	public int ITERATIONS = 30;
	// zoom
	public float SCALE = 200;

	// values for sliders
	public final int MAX_ITERATIONS = 75;
	public final int MAX_SCALE = 1000;

	// detect if is first click
	public boolean firstClick = false;
	// detect if mouse down
	public boolean mousePressed = false;

	// last location of mouse
	public int MOUSE_X = 0;
	public int MOUSE_Y = 0;

	// panel for render to be on
	public JPanel panel = new JPanel();

	// buffer to render on
	private BufferedImage buffer;// create buffer, then assign to frame

	public Mandlebrot() {
		// Initialize buffer
		buffer = new BufferedImage(render_WIDTH, render_HEIGHT, BufferedImage.TYPE_INT_RGB);

		// Initialize iteration slider
		JSlider iterations = new JSlider(JSlider.HORIZONTAL, 0, MAX_ITERATIONS, 10);
		iterations.setMajorTickSpacing(MAX_ITERATIONS / 10);
		iterations.setMinorTickSpacing(MAX_ITERATIONS / 100);
		iterations.setPaintTicks(true);
		iterations.setPaintLabels(true);
		iterations.setVisible(true);
		iterations.setValue(ITERATIONS);

		// Initialize scale slider
		JSlider scale = new JSlider(JSlider.HORIZONTAL, 0, MAX_SCALE, 10);
		scale.setMajorTickSpacing(MAX_SCALE / 10);
		scale.setMinorTickSpacing(MAX_SCALE / 100);
		scale.setPaintTicks(true);
		scale.setPaintLabels(true);
		scale.setVisible(true);
		scale.setValue((int) SCALE);

		// organize components
		pack();

		// frame setup
		setVisible(true);
		setPreferredSize(new java.awt.Dimension(WIDTH, HEIGHT));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(true);
		getContentPane().setLayout(new FlowLayout());

		// organize sliders
		JPanel sliderList = new JPanel();
		sliderList.setLayout(new BoxLayout(sliderList, BoxLayout.Y_AXIS));
		sliderList.add(iterations);
		sliderList.add(scale);

		// setup panel to contain components
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		JLabel image = new JLabel(new ImageIcon(buffer));
		panel.add(image);

		MouseListener ml = new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
				mousePressed = true;
				firstClick = true;
				MOUSE_X = e.getX() - render_WIDTH / 2;
				MOUSE_Y = render_HEIGHT / 2 - e.getY();
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				mousePressed = false;
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
			}
		};

		image.addMouseListener(ml);
		panel.add(sliderList);

		add(panel);

		renderMandelbrotSet();

		while (true) {
			// if mouse is down update render with new offset
			if (mousePressed) {
				Point mousePos = image.getMousePosition();
				if (mousePos != null) {
					int x = (int) (mousePos.getX() - render_WIDTH / 2);
					int y = (int) (render_HEIGHT / 2 - mousePos.getY());

					System.out.println((x - MOUSE_X) + ", " + (y - MOUSE_Y));

					if (!firstClick) {
						X_OFFSET += x - MOUSE_X;
						Y_OFFSET += y - MOUSE_Y;
					}

					firstClick = false;

					MOUSE_X = x;
					MOUSE_Y = y;
				}
				SCALE = (int) Math.pow(scale.getValue(), 1.1);
				ITERATIONS = iterations.getValue();

				// redraw the Mandlebrot Set onto a BufferedImage
				renderMandelbrotSet();
				pack();// resizes it to preferred size and layout of
				// subcomponents
				repaint();
			}
		}

	}

	public void renderMandelbrotSet() {
		for (int x = 0; x < render_WIDTH; x++) {
			for (int y = 0; y < render_HEIGHT; y++) {
				// convert to Cartesian plane coordinates & factor
				// transformations
				int color = calculatePoint(((x - X_OFFSET) - (render_WIDTH) / 2f) / (SCALE),
						((y + Y_OFFSET) - (render_HEIGHT) / 2f) / (SCALE));
				buffer.setRGB(x, y, color);
			}
		}
	}

	public int calculatePoint(float x, float y) {
		float cx = x;
		float cy = y;
		int i = 0;
		for (; i < ITERATIONS; i++) {
			// squaring a complex number can be interpreted as below
			// where x = a and y = b
			// (a + bi)
			float nx = x * x - y * y + cx;
			float ny = 2 * x * y + cy;
			x = nx;
			y = ny;
			if (x * x + y * y > 4)
				break;
		}

		// if (x, y) is part of Mandelbrot, color it black
		if (i == ITERATIONS)
			return 0x000000;//
		// or make its hue based on how many iterations it took for it to
		// diverge
		return Color.HSBtoRGB((float) i / ITERATIONS, 0.5f, 1);
	}
}

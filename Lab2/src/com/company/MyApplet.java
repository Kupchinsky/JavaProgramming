package com.company;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MyApplet extends JApplet implements Runnable
{
	private static final String DATAFILE = "X:\\Java Programming\\Lab2\\datafile.txt";

	@ToString
	public static class RectCoords implements Serializable
	{
		@Getter
		@Setter
		public int x = -1, y = -1;
	}

	private Image    offScreenImage;
	private Graphics offScreenGraphics;
	private int      offScreenWidth, offScreenHeight;

	private Map<DrawableRect, RectCoords> rects = new HashMap<>();
	private Thread thread;
	private Random rand = new Random();

	private boolean working        = false;
	private boolean isDrawing      = true;
	private boolean onStartRepaint = true;

	private DrawableRect movingRect    = null;
	private MouseEvent   prevMoveEvent = null;

	private JButton button1 = new JButton("Create ColoredRect");
	private JButton button2 = new JButton("Save to file");
	private JButton button3 = new JButton("Load from file");
	private JButton button4 = new JButton("Clear");

	private Color getRandomColor()
	{
		float r = this.rand.nextFloat();
		float g = this.rand.nextFloat();
		float b = this.rand.nextFloat();

		return new Color(r, g, b);
	}

	private void recreateImage()
	{
		this.offScreenWidth = this.getSize().width;
		this.offScreenHeight = this.getSize().height - 25;

		this.offScreenImage = this.createImage(this.offScreenWidth, this.offScreenHeight);
		this.offScreenGraphics = this.offScreenImage.getGraphics();
	}

	private void saveData()
	{
		try
		{
			FileOutputStream fos = new FileOutputStream(MyApplet.DATAFILE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);

			oos.writeInt(this.rects.size());

			for (Map.Entry<DrawableRect, RectCoords> entry : this.rects.entrySet())
			{
				oos.writeObject(entry.getKey());
				oos.writeObject(entry.getValue());
			}

			oos.close();
			fos.close();
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
	}

	private void loadData()
	{
		try
		{
			FileInputStream fis = new FileInputStream(MyApplet.DATAFILE);
			ObjectInputStream ois = new ObjectInputStream(fis);

			int count = ois.readInt();
			for (int i = 1; i <= count; i++)
			{
				DrawableRect obj1 = (DrawableRect) ois.readObject();
				RectCoords obj2 = (RectCoords) ois.readObject();

				System.out.println("OBJ1: " + obj1);
				System.out.println("OBJ2: " + obj2);

				this.rects.put(obj1, obj2);
			}

			System.out.println("Read " + count + " rects");

			ois.close();
			fis.close();
		}
		catch (IOException | ClassNotFoundException ex)
		{
			ex.printStackTrace();
		}
	}

	@Override
	public void init()
	{
		this.recreateImage();
		this.addMouseMotionListener(new MouseMotionListener()
		{
			@Override
			public void mouseDragged(MouseEvent e)
			{
				if (MyApplet.this.movingRect != null)
				{
					if (MyApplet.this.prevMoveEvent != null)
						MyApplet.this.movingRect.move(e.getX() - MyApplet.this.prevMoveEvent.getX(),
													  e.getY() - MyApplet.this.prevMoveEvent.getY());

					MyApplet.this.prevMoveEvent = e;
				}
			}

			@Override
			public void mouseMoved(MouseEvent e)
			{
			}
		});

		this.addMouseListener(new MouseListener()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) != 0)
				{
					for (DrawableRect rect : MyApplet.this.rects.keySet())
					{
						if (rect instanceof ColoredRect
							&& rect.getX1() < e.getX()
							&& rect.getX2() > e.getX()
							&& rect.getY1() < e.getY() - 25
							&& rect.getY2() > e.getY() - 25)
						{
							rect.setColorGrani(MyApplet.this.getRandomColor());
							((ColoredRect) rect).setColorFull(MyApplet.this.getRandomColor());
						}
					}

					MyApplet.this.repaint();
				}
			}

			@Override
			public void mousePressed(MouseEvent e)
			{
				if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0)
				{
					for (DrawableRect rect : MyApplet.this.rects.keySet())
					{
						if (rect.getX1() < e.getX()
							&& rect.getX2() > e.getX()
							&& rect.getY1() < e.getY() - 25
							&& rect.getY2() > e.getY() - 25)
						{
							MyApplet.this.movingRect = rect;
							break;
						}
					}
				}
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				MyApplet.this.movingRect = null;
				MyApplet.this.prevMoveEvent = null;
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
			}
		});

		this.addComponentListener(new ComponentListener()
		{
			@Override
			public void componentResized(ComponentEvent e)
			{
				MyApplet.this.onStartRepaint = true;
				MyApplet.this.recreateImage();
			}

			@Override
			public void componentMoved(ComponentEvent e)
			{
			}

			@Override
			public void componentShown(ComponentEvent e)
			{
			}

			@Override
			public void componentHidden(ComponentEvent e)
			{
			}
		});

		this.button1.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				ColoredRect newRect = new ColoredRect(MyApplet.this.rand.nextInt(100), MyApplet.this.rand.nextInt(100));
				newRect.move(MyApplet.this.rand.nextInt(MyApplet.this.offScreenWidth), MyApplet.this.rand.nextInt(
						MyApplet.this.offScreenHeight) + 25);

				newRect.setColorGrani(MyApplet.this.getRandomColor());
				newRect.setColorFull(MyApplet.this.getRandomColor());

				MyApplet.this.rects.put(newRect, new RectCoords());
				MyApplet.this.repaint();
			}
		});

		this.button2.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				// Save
				MyApplet.this.saveData();
			}
		});

		this.button3.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				// Load
				MyApplet.this.loadData();
				MyApplet.this.repaint();
			}
		});

		this.button4.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				MyApplet.this.rects.clear();
				MyApplet.this.repaint();
			}
		});

		this.setLayout(new FlowLayout());

		this.add(this.button1);
		this.add(this.button2);
		this.add(this.button3);
		this.add(this.button4);
	}

	@Override
	public void start()
	{
		this.working = true;
		this.thread = new Thread(this);
		this.thread.start();
	}

	@Override
	public void stop()
	{
		this.working = false;
	}

	@Override
	public void paint(Graphics g)
	{
		if (this.onStartRepaint)
		{
			this.onStartRepaint = false;
			super.paint(g);
		}

		this.offScreenGraphics.clearRect(0, 0, this.offScreenWidth, this.offScreenHeight);

		for (DrawableRect rect : this.rects.keySet())
			rect.draw(this.offScreenGraphics);

		g.drawImage(this.offScreenImage, 0, 25, this);
	}

	@Override
	public void update(Graphics g)
	{
		super.update(g);
		this.paint(g);
	}

	void animate()
	{
		Rectangle bounds = this.getBounds();

		for (Map.Entry<DrawableRect, RectCoords> entry : this.rects.entrySet())
		{
			DrawableRect rect = entry.getKey();
			RectCoords coords = entry.getValue();

			if (rect == movingRect)
				continue;

			if (rect.getX1() + coords.getX() + rect.getW() < 0
				|| rect.getX1() + coords.getX() < 0
				|| rect.getX1() + coords.getX() + rect.getW() > bounds.width
				|| rect.getX1() + coords.getX() > bounds.width)
				coords.setX(-coords.getX());

			if (rect.getY1() + coords.getY() + rect.getH() < 0
				|| rect.getY1() + coords.getY() < 0
				|| rect.getY1() + coords.getY() + rect.getH() > bounds.height - 25
				|| rect.getY1() + coords.getY() > bounds.height - 25)
				coords.setY(-coords.getY());

			rect.move(coords.getX(), coords.getY());
		}
	}

	@Override
	public void run()
	{
		while (this.working)
		{
			if (this.isDrawing)
			{
				this.animate();
				this.repaint();
			}

			try
			{
				Thread.sleep(10);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}

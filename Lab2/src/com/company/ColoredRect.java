package com.company;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;

/**
 * Created by killer on 10.09.2015.
 */
public class ColoredRect extends DrawableRect
{
	@Getter(AccessLevel.PACKAGE)
	@Setter(AccessLevel.PACKAGE)
	private Color colorFull = null;

	ColoredRect(int width, int height)
	{
		super(width, height);
	}

	@Override
	void draw(Graphics gr)
	{
		gr.setColor(this.colorFull);
		gr.fillRect(this.getX1(),
					this.getY1(),
					this.getX2() - this.getX1(),
					this.getY2() - this.getY1());

		super.draw(gr);

		//System.out.println("ColoredRect.draw (full: " + colorFull + ")");
	}
}

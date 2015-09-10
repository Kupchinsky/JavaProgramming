package com.company;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;

/**
 * Created by killer on 10.09.2015.
 */
class DrawableRect extends Rect
{
	@Getter(AccessLevel.PROTECTED)
	@Setter(AccessLevel.PROTECTED)
	private Color colorGrani = null;

	void draw(Graphics gr)
	{
		gr.setColor(this.colorGrani);
		gr.drawRect(this.getX1(), this.getY1(), this.getX2() - this.getX1(), this.getY2() - this.getY1());

		System.out.println("DrawableRect.draw (" + this.colorGrani + ")");
	}
}

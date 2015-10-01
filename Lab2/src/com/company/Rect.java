package com.company;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by killer on 10.09.2015.
 */
@AllArgsConstructor
class Rect
{
	@Getter(AccessLevel.PACKAGE)
	@Setter(AccessLevel.PACKAGE)
	private int x1 = 0, y1 = 0, x2 = 0, y2 = 0, w, h;

	Rect(int width, int height)
	{
		this.x1 = 0;
		this.y1 = 0;
		this.x2 = width;
		this.y2 = height;
		this.w = width;
		this.h = height;
	}

	void move(int delta_x, int delta_y)
	{
		this.x1 += delta_x;
		this.x2 += delta_x;
		this.y1 += delta_y;
		this.y2 += delta_y;
	}

	Rect union(Rect rect2)
	{
		return new Rect(this.x1, this.y1, rect2.getX2(), rect2.getY2(), rect2.getW(), rect2.getH());
	}

	Rect()
	{
	}

	@Override
	public String toString()
	{
		return "x1: "
			   + this.x1
			   + ", y1: "
			   + this.y1
			   + "; x2: "
			   + this.x2
			   + ", y2: "
			   + this.y2;
	}
}

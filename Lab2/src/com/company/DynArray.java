package com.company;

import lombok.AccessLevel;
import lombok.Getter;

import java.util.Arrays;

/**
 * Created by killer on 10.09.2015.
 */
class DynArray
{
	@Getter(AccessLevel.PACKAGE)
	private Object[] array = null;

	DynArray()
	{
	}

	DynArray(int size)
	{
		this.array = new Object[size];
	}

	DynArray(int size, Object[] initial)
	{
		this.array = Arrays.copyOf(initial, size);
	}

	int getLength()
	{
		return this.array == null ? 0 : this.array.length;
	}

	void setLength(int newLength)
	{
		this.array = this.array != null ? Arrays.copyOf(this.array, newLength) : new Object[newLength];
	}

	@Override
	public String toString()
	{
		String out = "";

		if (this.array != null)
		{
			out += "length: " + this.array.length + "\n";
			int index = 0;
			for (Object obj : this.array)
			{
				out += index + ": " + obj + "\n";
				index++;
			}
		}
		else
			out += "Array is null\n";

		return out;
	}
}

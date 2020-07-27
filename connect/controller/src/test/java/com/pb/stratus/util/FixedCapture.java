package com.pb.stratus.util;

import org.easymock.Capture;

public class FixedCapture<T> extends Capture<T>
{

	private static final long serialVersionUID = -5240664131485496758L;

	@Override
	public void setValue(T value)
	{
		if (!hasCaptured())
		{
			super.setValue(value);
		}
	}

}

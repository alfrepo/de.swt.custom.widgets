package de.swt.custom.cropimage;

import org.eclipse.core.databinding.conversion.IConverter;


public class TestIconverter implements IConverter{

	@Override
	public Bean getFromType() {
		return null;
	}

	@Override
	public Bean getToType() {
		return null;
	}

	@Override
	public Object convert(Object fromObject) {
		return fromObject;
	}

}

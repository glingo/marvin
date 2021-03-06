package com.marvin.component.parser.support;

import com.marvin.component.parser.Parser;

/**
 * Convert to a byte by parsing the value as a string
 *
 */
public class ByteParser implements Parser<Byte> {

    @Override
    public Object[] getTypeKeys() {
        return new Object[]{
            Byte.class,
            Byte.TYPE,
            Byte.class.getName()
        };
    }

    @Override
    public Byte parse(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Byte) {
            return (Byte) value;
        } else {
            String v = value.toString();
            if (v.trim().length() != 0) {
                return Byte.parseByte(v);
            }
        }

        return null;
    }
}

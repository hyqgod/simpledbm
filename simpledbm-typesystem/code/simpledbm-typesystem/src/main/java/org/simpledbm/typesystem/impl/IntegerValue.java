/***
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *    
 *    Linking this library statically or dynamically with other modules 
 *    is making a combined work based on this library. Thus, the terms and
 *    conditions of the GNU General Public License cover the whole
 *    combination.
 *    
 *    As a special exception, the copyright holders of this library give 
 *    you permission to link this library with independent modules to 
 *    produce an executable, regardless of the license terms of these 
 *    independent modules, and to copy and distribute the resulting 
 *    executable under terms of your choice, provided that you also meet, 
 *    for each linked independent module, the terms and conditions of the 
 *    license of that module.  An independent module is a module which 
 *    is not derived from or based on this library.  If you modify this 
 *    library, you may extend this exception to your version of the 
 *    library, but you are not obligated to do so.  If you do not wish 
 *    to do so, delete this exception statement from your version.
 *
 *    Project: www.simpledbm.org
 *    Author : Dibyendu Majumdar
 *    Email  : d dot majumdar at gmail dot com ignore
 */
package org.simpledbm.typesystem.impl;

import java.nio.ByteBuffer;

import org.simpledbm.common.util.TypeSize;
import org.simpledbm.typesystem.api.DataValue;
import org.simpledbm.typesystem.api.TypeDescriptor;

public class IntegerValue extends BaseDataValue {

    int i;

    IntegerValue(IntegerValue other) {
        super(other);
        this.i = other.i;
    }

    public IntegerValue(TypeDescriptor typeDesc) {
        super(typeDesc);
    }

    public IntegerValue(TypeDescriptor typeDesc, ByteBuffer bb) {
        super(typeDesc, bb);
        if (isValue()) {
            i = bb.getInt();
        }
    }

    @Override
    public String toString() {
        return getString();
    }

    @Override
    public int getStoredLength() {
        int n = super.getStoredLength();
        if (isValue()) {
            n += TypeSize.INTEGER;
        }
        return n;
    }

    @Override
    public void store(ByteBuffer bb) {
        super.store(bb);
        if (isValue()) {
            bb.putInt(i);
        }
    }

    //    @Override
    //    public void retrieve(ByteBuffer bb) {
    //    	super.retrieve(bb);
    //    	if (isValue()) {
    //    		i = bb.getInt();
    //    	}
    //    }

    @Override
    public int getInt() {
        if (!isValue()) {
            return 0;
        }
        return i;
    }

    @Override
    public String getString() {
        if (isValue()) {
            return Integer.toString(i);
        }
        return super.toString();
    }

    @Override
    public void setInt(Integer integer) {
        i = integer;
        setValue();
    }

    @Override
    public void setString(String string) {
        setInt(Integer.parseInt(string));
    }

    protected int compare(IntegerValue o) {
        int comp = super.compare(o);
        if (comp != 0 || !isValue()) {
            return comp;
        }
        return i - o.i;

    }

    @Override
    public int compareTo(DataValue o) {
        if (this == o) {
            return 0;
        }
        if (o == null) {
            throw new IllegalArgumentException();
        }
        if (!(o instanceof IntegerValue)) {
            throw new ClassCastException("Cannot cast " + o.getClass() + " to "
                    + this.getClass());
        }
        return compare((IntegerValue) o);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            throw new IllegalArgumentException();
        }
        if (!(o instanceof IntegerValue)) {
            throw new ClassCastException("Cannot cast " + o.getClass() + " to "
                    + this.getClass());
        }
        return compare((IntegerValue) o) == 0;
    }

    public DataValue cloneMe() {
        return new IntegerValue(this);
    }

    @Override
    public StringBuilder appendTo(StringBuilder sb) {
        if (isValue()) {
            return sb.append(i);
        }
        return super.appendTo(sb);
    }

}

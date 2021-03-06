/**
 * DO NOT REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Contributor(s):
 *
 * The Original Software is SimpleDBM (www.simpledbm.org).
 * The Initial Developer of the Original Software is Dibyendu Majumdar.
 *
 * Portions Copyright 2005-2014 Dibyendu Majumdar. All Rights Reserved.
 *
 * The contents of this file are subject to the terms of the
 * Apache License Version 2 (the "APL"). You may not use this
 * file except in compliance with the License. A copy of the
 * APL may be obtained from:
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the APL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the APL, the GPL or the LGPL.
 *
 * Copies of GPL and LGPL may be obtained from:
 * http://www.gnu.org/licenses/license-list.html
 */
package org.simpledbm.network.common.api;

import java.nio.ByteBuffer;

import org.simpledbm.common.api.registry.Storable;
import org.simpledbm.common.util.TypeSize;
import org.simpledbm.typesystem.api.Row;
import org.simpledbm.typesystem.api.RowFactory;

public class FetchNextRowReply implements Storable {

    final int containerId;
    final boolean eof;
    final Row row;

    public FetchNextRowReply(int containerId, boolean eof, Row row) {
        this.containerId = containerId;
        this.eof = eof;
        this.row = row;
    }

    public FetchNextRowReply(RowFactory rowFactory, ByteBuffer bb) {
        eof = (bb.get() == 1 ? true : false);
        if (!eof) {
            containerId = bb.getInt();
            row = rowFactory.newRow(containerId, bb);
        } else {
            containerId = -1;
            row = null;
        }
    }

    public int getStoredLength() {
        return TypeSize.BYTE
                + (!eof ? (row.getStoredLength() + TypeSize.INTEGER) : 0);
    }

    public void store(ByteBuffer bb) {
        bb.put((byte) (eof ? 1 : 0));
        if (!eof) {
            bb.putInt(containerId);
            row.store(bb);
        }
    }

    public Row getRow() {
        return row;
    }

    public boolean isEof() {
        return eof;
    }

    @Override
    public String toString() {
        return "FetchNextRowReply [containerId=" + containerId + ", eof=" + eof
                + ", row=" + row + ", storedLength=" + getStoredLength() + "]";
    }
}

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
 *    Project: www.simpledbm.org
 *    Author : Dibyendu Majumdar
 *    Email  : dibyendu@mazumdar.demon.co.uk
 */
package org.simpledbm.database;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import org.simpledbm.rss.api.st.Storable;
import org.simpledbm.rss.util.ByteString;
import org.simpledbm.rss.util.TypeSize;
import org.simpledbm.typesystem.api.Field;
import org.simpledbm.typesystem.api.Row;
import org.simpledbm.typesystem.api.RowFactory;
import org.simpledbm.typesystem.api.TypeDescriptor;

/**
 * Encapsulates a table definition and provides methods to work with table and
 * indexes associated with the table.
 * 
 * @author dibyendumajumdar
 * @since 7 Oct 2007
 */
public class TableDefinition implements Storable {

    Database database;
    int containerId;
    String name;
    TypeDescriptor[] rowType;
    ArrayList<IndexDefinition> indexes = new ArrayList<IndexDefinition>();

    TableDefinition(Database database) {
        this.database = database;
    }

    public TableDefinition(Database database, int containerId, String name,
            TypeDescriptor[] rowType) {
        this.database = database;
        this.containerId = containerId;
        this.name = name;
        this.rowType = rowType;

        database.getRowFactory().registerRowType(containerId, rowType);
        database.tables.add(this);
    }

    public void addIndex(int containerId, String name, int[] columns,
            boolean primary, boolean unique) {
        if (!primary && indexes.size() == 0) {
            throw new IllegalArgumentException(
                    "First index must be the primary");
        }
        new IndexDefinition(this, containerId, name, columns, primary, unique);
    }

    public Database getDatabase() {
        return database;
    }

    public int getContainerId() {
        return containerId;
    }

    public String getName() {
        return name;
    }

    public TypeDescriptor[] getRowType() {
        return rowType;
    }

    public ArrayList<IndexDefinition> getIndexes() {
        return indexes;
    }

    public int getStoredLength() {
        int n = 0;
        ByteString s = new ByteString(name);
        n += TypeSize.INTEGER;
        n += s.getStoredLength();
        n += database.getFieldFactory().getStoredLength(rowType);
        n += TypeSize.SHORT;
        for (int i = 0; i < indexes.size(); i++) {
            n += indexes.get(i).getStoredLength();
        }
        return n;
    }

    public void retrieve(ByteBuffer bb) {
        containerId = bb.getInt();
        ByteString s = new ByteString();
        s.retrieve(bb);
        name = s.toString();
        rowType = database.getFieldFactory().retrieve(bb);
        int n = bb.getShort();
        indexes = new ArrayList<IndexDefinition>();
        for (int i = 0; i < n; i++) {
            IndexDefinition idx = new IndexDefinition(this);
            idx.retrieve(bb);
        }
        if (!database.tables.contains(this)) {
            database.getRowFactory().registerRowType(containerId, rowType);
            database.tables.add(this);
        }
    }

    public void store(ByteBuffer bb) {
        bb.putInt(containerId);
        ByteString s = new ByteString(name);
        s.store(bb);
        database.getFieldFactory().store(rowType, bb);
        bb.putShort((short) indexes.size());
        for (int i = 0; i < indexes.size(); i++) {
            IndexDefinition idx = indexes.get(i);
            idx.store(bb);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + containerId;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TableDefinition other = (TableDefinition) obj;
        if (containerId != other.containerId) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }

    public Row getRow() {
        RowFactory rowFactory = database.getRowFactory();
        return rowFactory.newRow(containerId);
    }

    Row getIndexRow(IndexDefinition index, Row tableRow) {
        Row indexRow = index.getRow();
        for (int i = 0; i < index.columns.length; i++) {
            indexRow.set(i, (Field) tableRow.get(index.columns[i]).cloneMe());
        }
        return indexRow;
    }
}
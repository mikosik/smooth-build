package org.smoothbuild.db.object.obj.val;

import static java.util.Objects.checkIndex;

import org.smoothbuild.db.object.obj.ObjectDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.db.object.obj.exc.UnexpectedObjNodeException;
import org.smoothbuild.db.object.type.base.ObjType;
import org.smoothbuild.db.object.type.base.ValType;
import org.smoothbuild.db.object.type.val.StructOType;
import org.smoothbuild.util.collect.NamedList;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class Struc_ extends Val {
  // Should be accessed only through synchronized items() method.
  private ImmutableList<Val> items;

  public Struc_(MerkleRoot merkleRoot, ObjectDb objectDb) {
    super(merkleRoot, objectDb);
  }

  @Override
  public StructOType type() {
    return (StructOType) super.type();
  }

  public Val get(int index) {
    ImmutableList<Val> items = items();
    checkIndex(index, items.size());
    return items.get(index);
  }

  public synchronized ImmutableList<Val> items() {
    if (items == null) {
      items = instantiateItems();
    }
    return items;
  }

  private ImmutableList<Val> instantiateItems() {
    NamedList<ValType> fields = this.type().fields();
    var objs = readSequenceObjs(DATA_PATH, dataHash(), fields.size(), Val.class);
    for (int i = 0; i < fields.size(); i++) {
      ObjType expectedType = fields.getObject(i);
      ObjType actualType = objs.get(i).type();
      if (!expectedType.equals(actualType)) {
        throw new UnexpectedObjNodeException(
            hash(), this.type(), DATA_PATH, i, expectedType, actualType);
      }
    }
    return objs;
  }

  @Override
  public String valueToString() {
    StringBuilder builder = new StringBuilder("{");
    var typeFields = this.type().fields().list();
    ImmutableList<Val> values = items();
    for (int i = 0; i < values.size(); i++) {
      builder.append(typeFields.get(i).saneName());
      builder.append("=");
      builder.append(values.get(i).valueToString());
      if (i != values.size() - 1) {
        builder.append(",");
      }
    }
    builder.append("}");
    return builder.toString();
  }
}

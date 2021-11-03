package org.smoothbuild.db.object.obj.val;

import static java.util.Objects.checkIndex;

import org.smoothbuild.db.object.obj.ObjectDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.db.object.obj.exc.UnexpectedObjNodeException;
import org.smoothbuild.db.object.type.base.ObjType;
import org.smoothbuild.db.object.type.val.TupleOType;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class Tuple extends Val {
  private ImmutableList<Val> items;

  public Tuple(MerkleRoot merkleRoot, ObjectDb objectDb) {
    super(merkleRoot, objectDb);
  }

  @Override
  public TupleOType type() {
    return (TupleOType) super.type();
  }

  public Val get(int index) {
    ImmutableList<Val> items = items();
    checkIndex(index, items.size());
    return items.get(index);
  }

  public Val superObject() {
    ImmutableList<Val> items = items();
    return items.size() == 0 ? null : items.iterator().next();
  }

  public ImmutableList<Val> items() {
    if (items == null) {
      items = instantiateItems();
    }
    return items;
  }

  private ImmutableList<Val> instantiateItems() {
    var itemTypes = this.type().items();
    var objs = readSequenceObjs(DATA_PATH, dataHash(), itemTypes.size(), Val.class);
    for (int i = 0; i < itemTypes.size(); i++) {
      Val obj = objs.get(i);
      ObjType expectedType = itemTypes.get(i);
      ObjType actualType = obj.type();
      if (!expectedType.equals(actualType)) {
        throw new UnexpectedObjNodeException(
            hash(), this.type(), DATA_PATH, i, expectedType, actualType);
      }
    }
    return objs;
  }

  @Override
  public String valueToString() {
    return "{" + sequenceToString(items()) + '}';
  }
}

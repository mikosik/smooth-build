package org.smoothbuild.db.object.obj.val;

import static java.util.Objects.checkIndex;

import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.ValueH;
import org.smoothbuild.db.object.obj.exc.UnexpectedObjNodeException;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.db.object.type.val.TupleTypeH;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class TupleH extends ValueH {
  private ImmutableList<ValueH> items;

  public TupleH(MerkleRoot merkleRoot, ObjectHDb objectHDb) {
    super(merkleRoot, objectHDb);
  }

  @Override
  public TupleTypeH type() {
    return (TupleTypeH) super.type();
  }

  public ValueH get(int index) {
    ImmutableList<ValueH> items = items();
    checkIndex(index, items.size());
    return items.get(index);
  }

  public ValueH superObject() {
    ImmutableList<ValueH> items = items();
    return items.size() == 0 ? null : items.iterator().next();
  }

  public ImmutableList<ValueH> items() {
    if (items == null) {
      items = instantiateItems();
    }
    return items;
  }

  private ImmutableList<ValueH> instantiateItems() {
    var itemTypes = this.type().items();
    var objs = readSequenceObjs(DATA_PATH, dataHash(), itemTypes.size(), ValueH.class);
    for (int i = 0; i < itemTypes.size(); i++) {
      ValueH obj = objs.get(i);
      TypeH expectedType = itemTypes.get(i);
      TypeH actualType = obj.type();
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

package org.smoothbuild.db.object.obj.val;

import static java.util.Objects.checkIndex;

import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.exc.UnexpectedObjNodeExc;
import org.smoothbuild.db.object.type.base.SpecH;
import org.smoothbuild.db.object.type.val.TupleTypeH;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public final class TupleH extends ValH {
  private ImmutableList<ValH> items;

  public TupleH(MerkleRoot merkleRoot, ObjDb objDb) {
    super(merkleRoot, objDb);
  }

  @Override
  public TupleTypeH spec() {
    return (TupleTypeH) super.spec();
  }

  public ValH get(int index) {
    ImmutableList<ValH> items = items();
    checkIndex(index, items.size());
    return items.get(index);
  }

  public ValH superObject() {
    ImmutableList<ValH> items = items();
    return items.size() == 0 ? null : items.iterator().next();
  }

  public ImmutableList<ValH> items() {
    if (items == null) {
      items = instantiateItems();
    }
    return items;
  }

  private ImmutableList<ValH> instantiateItems() {
    var itemTypes = spec().items();
    var objs = readSeqObjs(DATA_PATH, dataHash(), itemTypes.size(), ValH.class);
    for (int i = 0; i < itemTypes.size(); i++) {
      ValH val = objs.get(i);
      SpecH expectedType = itemTypes.get(i);
      SpecH actualType = val.spec();
      if (!expectedType.equals(actualType)) {
        throw new UnexpectedObjNodeExc(hash(), spec(), DATA_PATH, i, expectedType, actualType);
      }
    }
    return objs;
  }

  @Override
  public String valToString() {
    return "{" + seqToString(items()) + '}';
  }
}

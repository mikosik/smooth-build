package org.smoothbuild.db.object.obj.val;

import static java.util.Objects.checkIndex;

import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.exc.UnexpectedObjNodeExc;
import org.smoothbuild.db.object.type.val.TupleTH;

import com.google.common.collect.ImmutableList;

/**
 * This class is thread-safe.
 */
public final class TupleH extends ValH {
  private ImmutableList<ValH> items;

  public TupleH(MerkleRoot merkleRoot, ObjDb objDb) {
    super(merkleRoot, objDb);
  }

  @Override
  public TupleTH cat() {
    return (TupleTH) super.cat();
  }

  @Override
  public TupleTH type() {
    return cat();
  }

  public ValH get(int index) {
    ImmutableList<ValH> items = items();
    checkIndex(index, items.size());
    return items.get(index);
  }

  public ImmutableList<ValH> items() {
    if (items == null) {
      items = instantiateItems();
    }
    return items;
  }

  private ImmutableList<ValH> instantiateItems() {
    var itemTs = type().items();
    var objs = readSeqObjs(DATA_PATH, dataHash(), itemTs.size(), ValH.class);
    for (int i = 0; i < itemTs.size(); i++) {
      var val = objs.get(i);
      var expectedT = itemTs.get(i);
      var actualT = val.cat();
      if (!expectedT.equals(actualT)) {
        throw new UnexpectedObjNodeExc(hash(), cat(), DATA_PATH, i, expectedT, actualT);
      }
    }
    return objs;
  }

  @Override
  public String objToString() {
    return "{" + seqToString(items()) + '}';
  }
}

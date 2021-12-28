package org.smoothbuild.db.bytecode.obj.val;

import static com.google.common.base.Suppliers.memoize;
import static java.util.Objects.checkIndex;

import org.smoothbuild.db.bytecode.obj.ByteDbImpl;
import org.smoothbuild.db.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.db.bytecode.obj.exc.DecodeObjWrongNodeCatExc;
import org.smoothbuild.db.bytecode.type.val.TupleTB;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;

/**
 * This class is thread-safe.
 */
public final class TupleB extends ValB {
  private final Supplier<ImmutableList<ValB>> itemsSupplier;

  public TupleB(MerkleRoot merkleRoot, ByteDbImpl byteDb) {
    super(merkleRoot, byteDb);
    this.itemsSupplier = memoize(this::instantiateItems);
  }

  @Override
  public TupleTB cat() {
    return (TupleTB) super.cat();
  }

  @Override
  public TupleTB type() {
    return cat();
  }

  public ValB get(int index) {
    ImmutableList<ValB> items = items();
    checkIndex(index, items.size());
    return items.get(index);
  }

  public ImmutableList<ValB> items() {
    return itemsSupplier.get();
  }

  private ImmutableList<ValB> instantiateItems() {
    var itemTs = type().items();
    var objs = readSeqObjs(DATA_PATH, dataHash(), itemTs.size(), ValB.class);
    for (int i = 0; i < itemTs.size(); i++) {
      var val = objs.get(i);
      var expectedT = itemTs.get(i);
      var actualT = val.cat();
      if (!expectedT.equals(actualT)) {
        throw new DecodeObjWrongNodeCatExc(hash(), cat(), DATA_PATH, i, expectedT, actualT);
      }
    }
    return objs;
  }

  @Override
  public String objToString() {
    return "{" + seqToString(items()) + '}';
  }
}

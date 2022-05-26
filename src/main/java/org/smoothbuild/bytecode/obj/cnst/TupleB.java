package org.smoothbuild.bytecode.obj.cnst;

import static com.google.common.base.Suppliers.memoize;
import static java.util.Objects.checkIndex;

import org.smoothbuild.bytecode.obj.ObjDbImpl;
import org.smoothbuild.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.bytecode.obj.exc.DecodeObjWrongNodeTypeExc;
import org.smoothbuild.bytecode.type.cnst.TupleTB;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;

/**
 * This class is thread-safe.
 */
public final class TupleB extends CnstB {
  private final Supplier<ImmutableList<CnstB>> itemsSupplier;

  public TupleB(MerkleRoot merkleRoot, ObjDbImpl objDb) {
    super(merkleRoot, objDb);
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

  public CnstB get(int index) {
    ImmutableList<CnstB> items = items();
    checkIndex(index, items.size());
    return items.get(index);
  }

  public ImmutableList<CnstB> items() {
    return itemsSupplier.get();
  }

  private ImmutableList<CnstB> instantiateItems() {
    var itemTs = type().items();
    var objs = readSeqObjs(DATA_PATH, dataHash(), itemTs.size(), CnstB.class);
    for (int i = 0; i < itemTs.size(); i++) {
      var val = objs.get(i);
      var expectedT = itemTs.get(i);
      var actualT = val.cat();
      if (!expectedT.equals(actualT)) {
        throw new DecodeObjWrongNodeTypeExc(hash(), cat(), DATA_PATH, i, expectedT, actualT);
      }
    }
    return objs;
  }

  @Override
  public String objToString() {
    return "{" + objsToString(items()) + '}';
  }
}

package org.smoothbuild.bytecode.expr.val;

import static com.google.common.base.Suppliers.memoize;
import static java.util.Objects.checkIndex;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.bytecode.expr.exc.DecodeExprWrongNodeTypeExc;
import org.smoothbuild.bytecode.type.val.TupleTB;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;

/**
 * This class is thread-safe.
 */
public final class TupleB extends ValB {
  private final Supplier<ImmutableList<ValB>> itemsSupplier;

  public TupleB(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    super(merkleRoot, bytecodeDb);
    this.itemsSupplier = memoize(this::instantiateItems);
  }

  @Override
  public TupleTB type() {
    return (TupleTB) super.cat();
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
    var objs = readSeqExprs(DATA_PATH, dataHash(), itemTs.size(), ValB.class);
    for (int i = 0; i < itemTs.size(); i++) {
      var val = objs.get(i);
      var expectedT = itemTs.get(i);
      var actualT = val.type();
      if (!expectedT.equals(actualT)) {
        throw new DecodeExprWrongNodeTypeExc(hash(), cat(), DATA_PATH, i, expectedT, actualT);
      }
    }
    return objs;
  }

  @Override
  public String exprToString() {
    return "{" + exprsToString(items()) + '}';
  }
}

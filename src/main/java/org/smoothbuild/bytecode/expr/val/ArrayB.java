package org.smoothbuild.bytecode.expr.val;

import static com.google.common.base.Suppliers.memoize;

import java.util.function.Supplier;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.bytecode.expr.exc.DecodeExprWrongNodeTypeExc;
import org.smoothbuild.bytecode.type.val.ArrayTB;

import com.google.common.collect.ImmutableList;

/**
 * This class is thread-safe.
 */
public final class ArrayB extends ValB {
  private final Supplier<ImmutableList<ValB>> elemsSupplier;

  public ArrayB(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    super(merkleRoot, bytecodeDb);
    this.elemsSupplier = memoize(this::instantiateElems);
  }

  @Override
  public ArrayTB type() {
    return (ArrayTB) super.cat();
  }

  public <T extends ValB> ImmutableList<T> elems(Class<T> elemTJ) {
    assertIsIterableAs(elemTJ);
    return (ImmutableList<T>) elemsSupplier.get();
  }

  private <T extends ValB> void assertIsIterableAs(Class<T> clazz) {
    var elemT = type().elem();
    if (!clazz.isAssignableFrom(elemT.typeJ())) {
      throw new IllegalArgumentException(cat().name() + " cannot be viewed as Iterable of "
          + clazz.getCanonicalName() + ".");
    }
  }

  private ImmutableList<ValB> instantiateElems() {
    var elems = readElemVals();
    var expectedElemT = type().elem();
    for (int i = 0; i < elems.size(); i++) {
      var elemT = elems.get(i).type();
      if (!expectedElemT.equals(elemT)) {
        throw new DecodeExprWrongNodeTypeExc(hash(), cat(), DATA_PATH, i, expectedElemT, elemT);
      }
    }
    return elems;
  }

  private ImmutableList<ValB> readElemVals() {
    return readSeqExprs(DATA_PATH, dataHash(), ValB.class);
  }

  @Override
  public String exprToString() {
    return "[" + exprsToString(readElemVals()) + ']';
  }
}

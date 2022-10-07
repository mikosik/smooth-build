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
public final class ArrayB extends InstB {
  private final Supplier<ImmutableList<InstB>> elemsSupplier;

  public ArrayB(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    super(merkleRoot, bytecodeDb);
    this.elemsSupplier = memoize(this::instantiateElems);
  }

  @Override
  public ArrayTB evalT() {
    return type();
  }

  @Override
  public ArrayTB type() {
    return (ArrayTB) super.category();
  }

  public long size() {
    return readDataSeqSize();
  }

  public <T extends InstB> ImmutableList<T> elems(Class<T> elemTJ) {
    assertIsIterableAs(elemTJ);
    return (ImmutableList<T>) elemsSupplier.get();
  }

  private <T extends InstB> void assertIsIterableAs(Class<T> clazz) {
    var elemT = type().elem();
    if (!clazz.isAssignableFrom(elemT.typeJ())) {
      throw new IllegalArgumentException(category().name() + " cannot be viewed as Iterable of "
          + clazz.getCanonicalName() + ".");
    }
  }

  private ImmutableList<InstB> instantiateElems() {
    var elems = readElems();
    var expectedElemT = type().elem();
    for (int i = 0; i < elems.size(); i++) {
      var elemT = elems.get(i).type();
      if (!expectedElemT.equals(elemT)) {
        throw new DecodeExprWrongNodeTypeExc(
            hash(), category(), DATA_PATH, i, expectedElemT, elemT);
      }
    }
    return elems;
  }

  private ImmutableList<InstB> readElems() {
    return readDataSeqElems(InstB.class);
  }

  @Override
  public String exprToString() {
    return "[" + exprsToString(readElems()) + ']';
  }
}

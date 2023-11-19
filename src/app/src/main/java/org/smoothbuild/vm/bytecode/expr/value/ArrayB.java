package org.smoothbuild.vm.bytecode.expr.value;

import static com.google.common.base.Suppliers.memoize;

import io.vavr.collection.Array;
import java.util.function.Supplier;
import org.smoothbuild.vm.bytecode.expr.BytecodeDb;
import org.smoothbuild.vm.bytecode.expr.MerkleRoot;
import org.smoothbuild.vm.bytecode.expr.exc.DecodeExprWrongNodeTypeException;
import org.smoothbuild.vm.bytecode.type.value.ArrayTB;

/**
 * This class is thread-safe.
 */
public final class ArrayB extends ValueB {
  private final Supplier<Array<ValueB>> elemsSupplier;

  public ArrayB(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    super(merkleRoot, bytecodeDb);
    this.elemsSupplier = memoize(this::instantiateElems);
  }

  @Override
  public ArrayTB evaluationT() {
    return type();
  }

  @Override
  public ArrayTB type() {
    return (ArrayTB) super.category();
  }

  public long size() {
    return readDataSeqSize();
  }

  public <T extends ValueB> Array<T> elems(Class<T> elemTJ) {
    assertIsIterableAs(elemTJ);
    @SuppressWarnings("unchecked")
    Array<T> result = (Array<T>) elemsSupplier.get();
    return result;
  }

  private <T extends ValueB> void assertIsIterableAs(Class<T> clazz) {
    var elemT = type().elem();
    if (!clazz.isAssignableFrom(elemT.typeJ())) {
      throw new IllegalArgumentException(
          category().name() + " cannot be viewed as Iterable of " + clazz.getCanonicalName() + ".");
    }
  }

  private Array<ValueB> instantiateElems() {
    var elems = readElems();
    var expectedElemT = type().elem();
    for (int i = 0; i < elems.size(); i++) {
      var elemT = elems.get(i).type();
      if (!expectedElemT.equals(elemT)) {
        throw new DecodeExprWrongNodeTypeException(
            hash(), category(), DATA_PATH, i, expectedElemT, elemT);
      }
    }
    return elems;
  }

  private Array<ValueB> readElems() {
    return readDataSeqElems(ValueB.class);
  }

  @Override
  public String exprToString() {
    return "[" + exprsToString(readElems()) + ']';
  }
}

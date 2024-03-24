package org.smoothbuild.virtualmachine.bytecode.expr.value;

import static org.smoothbuild.common.function.Function0.memoizer;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.function.Function0;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeExprWrongNodeTypeException;
import org.smoothbuild.virtualmachine.bytecode.type.value.BArrayType;

/**
 * This class is thread-safe.
 */
public final class BArray extends BValue {
  private final Function0<List<BValue>, BytecodeException> elementsMemoizer;

  public BArray(MerkleRoot merkleRoot, BExprDb exprDb) {
    super(merkleRoot, exprDb);
    this.elementsMemoizer = memoizer(this::instantiateElements);
  }

  @Override
  public BArrayType evaluationType() {
    return type();
  }

  @Override
  public BArrayType type() {
    return (BArrayType) super.kind();
  }

  public long size() throws BytecodeException {
    return readDataAsHashChainSize();
  }

  public <T extends BValue> List<T> elements(Class<T> elemTJ) throws BytecodeException {
    assertIsIterableAs(elemTJ);
    @SuppressWarnings("unchecked")
    List<T> result = (List<T>) elementsMemoizer.apply();
    return result;
  }

  private <T extends BValue> void assertIsIterableAs(Class<T> clazz) {
    var elemT = type().elem();
    if (!clazz.isAssignableFrom(elemT.javaType())) {
      throw new IllegalArgumentException(
          kind().name() + " cannot be viewed as Iterable of " + clazz.getCanonicalName() + ".");
    }
  }

  private List<BValue> instantiateElements() throws BytecodeException {
    var elements = readElements();
    var expectedElemT = type().elem();
    for (int i = 0; i < elements.size(); i++) {
      var elemT = elements.get(i).type();
      if (!expectedElemT.equals(elemT)) {
        throw new DecodeExprWrongNodeTypeException(
            hash(), kind(), DATA_PATH, i, expectedElemT, elemT);
      }
    }
    return elements;
  }

  private List<BValue> readElements() throws BytecodeException {
    return readDataAsExprChain(BValue.class);
  }

  @Override
  public String exprToString() throws BytecodeException {
    return "[" + exprsToString(readElements()) + ']';
  }
}

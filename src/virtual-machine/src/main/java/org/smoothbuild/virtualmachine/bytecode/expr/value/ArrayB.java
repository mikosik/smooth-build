package org.smoothbuild.virtualmachine.bytecode.expr.value;

import static org.smoothbuild.common.function.Function0.memoizer;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.function.Function0;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeExprWrongNodeTypeException;
import org.smoothbuild.virtualmachine.bytecode.type.value.ArrayTB;

/**
 * This class is thread-safe.
 */
public final class ArrayB extends ValueB {
  private final Function0<List<ValueB>, BytecodeException> elementsMemoizer;

  public ArrayB(MerkleRoot merkleRoot, ExprDb exprDb) {
    super(merkleRoot, exprDb);
    this.elementsMemoizer = memoizer(this::instantiateElements);
  }

  @Override
  public ArrayTB evaluationType() {
    return type();
  }

  @Override
  public ArrayTB type() {
    return (ArrayTB) super.category();
  }

  public long size() throws BytecodeException {
    return readDataAsHashChainSize();
  }

  public <T extends ValueB> List<T> elements(Class<T> elemTJ) throws BytecodeException {
    assertIsIterableAs(elemTJ);
    @SuppressWarnings("unchecked")
    List<T> result = (List<T>) elementsMemoizer.apply();
    return result;
  }

  private <T extends ValueB> void assertIsIterableAs(Class<T> clazz) {
    var elemT = type().elem();
    if (!clazz.isAssignableFrom(elemT.typeJ())) {
      throw new IllegalArgumentException(
          category().name() + " cannot be viewed as Iterable of " + clazz.getCanonicalName() + ".");
    }
  }

  private List<ValueB> instantiateElements() throws BytecodeException {
    var elements = readElements();
    var expectedElemT = type().elem();
    for (int i = 0; i < elements.size(); i++) {
      var elemT = elements.get(i).type();
      if (!expectedElemT.equals(elemT)) {
        throw new DecodeExprWrongNodeTypeException(
            hash(), category(), DATA_PATH, i, expectedElemT, elemT);
      }
    }
    return elements;
  }

  private List<ValueB> readElements() throws BytecodeException {
    return readDataAsExprChain(ValueB.class);
  }

  @Override
  public String exprToString() throws BytecodeException {
    return "[" + exprsToString(readElements()) + ']';
  }
}
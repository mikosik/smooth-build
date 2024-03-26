package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static java.util.Objects.checkIndex;
import static org.smoothbuild.common.function.Function0.memoizer;
import static org.smoothbuild.virtualmachine.bytecode.type.base.Validator.validateTuple;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.function.Function0;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeExprWrongNodeTypeException;
import org.smoothbuild.virtualmachine.bytecode.type.base.BTupleType;
import org.smoothbuild.virtualmachine.bytecode.type.base.BType;

/**
 * This class is thread-safe.
 */
public final class BTuple extends BValue {
  private final Function0<List<BValue>, BytecodeException> elementsMemoizer;

  public BTuple(MerkleRoot merkleRoot, BExprDb exprDb) {
    super(merkleRoot, exprDb);
    this.elementsMemoizer = memoizer(this::instantiateItems);
  }

  @Override
  public BTupleType evaluationType() {
    return type();
  }

  @Override
  public BTupleType type() {
    return (BTupleType) super.kind();
  }

  public BValue get(int index) throws BytecodeException {
    List<BValue> elements = elements();
    checkIndex(index, elements.size());
    return elements.get(index);
  }

  public List<BValue> elements() throws BytecodeException {
    return elementsMemoizer.apply();
  }

  private List<BValue> instantiateItems() throws BytecodeException {
    var type = type();
    var expectedElementTs = type.elements();
    var elements = readDataAsValueChain(expectedElementTs.size());
    var elementTypes = elements.map(BValue::type);
    validateTuple(
        type,
        elementTypes,
        () -> new DecodeExprWrongNodeTypeException(
            hash(), kind(), DATA_PATH, type, asTupleToString(elementTypes)));
    return elements;
  }

  private static String asTupleToString(List<BType> elementTypes) {
    return elementTypes.toString("`{", ",", "}`");
  }

  @Override
  public String exprToString() throws BytecodeException {
    return "{" + exprsToString(elements()) + '}';
  }
}

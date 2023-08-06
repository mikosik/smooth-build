package org.smoothbuild.virtualmachine.bytecode.expr.value;

import static java.util.Objects.checkIndex;
import static org.smoothbuild.common.function.Function0.memoizer;
import static org.smoothbuild.virtualmachine.bytecode.type.Validator.validateTuple;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.function.Function0;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeExprWrongNodeTypeException;
import org.smoothbuild.virtualmachine.bytecode.type.value.TupleTB;
import org.smoothbuild.virtualmachine.bytecode.type.value.TypeB;

/**
 * This class is thread-safe.
 */
public final class TupleB extends ValueB {
  private final Function0<List<ValueB>, BytecodeException> elementsMemoizer;

  public TupleB(MerkleRoot merkleRoot, ExprDb exprDb) {
    super(merkleRoot, exprDb);
    this.elementsMemoizer = memoizer(this::instantiateItems);
  }

  @Override
  public TupleTB evaluationT() {
    return type();
  }

  @Override
  public TupleTB type() {
    return (TupleTB) super.category();
  }

  public ValueB get(int index) throws BytecodeException {
    List<ValueB> elements = elements();
    checkIndex(index, elements.size());
    return elements.get(index);
  }

  public List<ValueB> elements() throws BytecodeException {
    return elementsMemoizer.apply();
  }

  private List<ValueB> instantiateItems() throws BytecodeException {
    var type = type();
    var expectedElementTs = type.elements();
    var elements = readDataSeqElems(expectedElementTs.size());
    var elementTs = elements.map(ValueB::type);
    validateTuple(
        type,
        elementTs,
        () -> new DecodeExprWrongNodeTypeException(
            hash(), category(), DATA_PATH, type, asTupleToString(elementTs)));
    return elements;
  }

  private static String asTupleToString(List<TypeB> elementTs) {
    return elementTs.toString("`{", ",", "}`");
  }

  @Override
  public String exprToString() throws BytecodeException {
    return "{" + exprsToString(elements()) + '}';
  }
}

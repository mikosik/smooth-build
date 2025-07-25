package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.checkIndex;
import static org.smoothbuild.common.function.Function0.memoizer;

import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.function.Function0;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.MemberHasWrongTypeException;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BTupleType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BType;
import org.smoothbuild.virtualmachine.bytecode.kind.exc.BKindDbException;

/**
 * This class is thread-safe.
 */
public final class BTuple extends BValue {
  private final Function0<List<BValue>, BytecodeException> elementsMemoizer;

  public BTuple(MerkleRoot merkleRoot, BExprDb exprDb) {
    super(merkleRoot, exprDb);
    checkArgument(merkleRoot.kind() instanceof BTupleType);
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
    validateTuple(type, elementTypes);
    return elements;
  }

  private void validateTuple(BTupleType type, List<BType> elementTypes)
      throws BKindDbException, MemberHasWrongTypeException {
    var actualType = kindDb().tuple(elementTypes);
    if (!actualType.equals(type)) {
      throw new MemberHasWrongTypeException(hash(), kind(), "elements", type, actualType);
    }
  }

  @Override
  public String exprToString() throws BytecodeException {
    return new ToStringBuilder(getClass().getSimpleName())
        .addField("hash", hash())
        .addField("type", type())
        .addListField("elements", elements().map(BExpr::exprToString))
        .toString();
  }
}

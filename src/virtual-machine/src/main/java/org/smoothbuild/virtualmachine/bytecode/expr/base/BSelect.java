package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.SelectHasIndexOutOfBoundException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.SelectHasWrongEvaluationTypeException;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BSelectKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BTupleType;

/**
 * This class is thread-safe.
 */
public final class BSelect extends BOperation {
  public static final int SELECTABLE_INDEX = 0;
  public static final int INDEX_INDEX = 1;

  public BSelect(MerkleRoot merkleRoot, BExprDb exprDb) {
    super(merkleRoot, exprDb);
    checkArgument(merkleRoot.kind() instanceof BSelectKind);
  }

  @Override
  public BSelectKind kind() {
    return (BSelectKind) super.kind();
  }

  @Override
  public BSubExprs subExprs() throws BytecodeException {
    var members = members("selectable", "index");
    var selectable = members.get(SELECTABLE_INDEX).asExpr(BTupleType.class);
    var index = members.get(INDEX_INDEX).asInstanceOf(BInt.class);
    int i = index.toJavaBigInteger().intValue();
    var tupleType = (BTupleType) selectable.evaluationType();
    int size = tupleType.elements().size();
    if (i < SELECTABLE_INDEX || size <= i) {
      throw new SelectHasIndexOutOfBoundException(hash(), kind(), i, size);
    }
    var fieldType = tupleType.elements().get(i);
    if (!evaluationType().equals(fieldType)) {
      throw new SelectHasWrongEvaluationTypeException(hash(), kind(), fieldType);
    }
    return new BSubExprs(selectable, index);
  }

  @Override
  public String exprToString() throws BytecodeException {
    var subExprs = subExprs();
    return new ToStringBuilder(getClass().getSimpleName())
        .addField("hash", hash())
        .addField("evaluationType", evaluationType())
        .addField("selectable", subExprs.selectable())
        .addField("index", subExprs.index())
        .toString();
  }

  public static record BSubExprs(BExpr selectable, BInt index) implements BExprs {
    @Override
    public List<BExpr> toList() {
      return list(selectable, index);
    }
  }
}

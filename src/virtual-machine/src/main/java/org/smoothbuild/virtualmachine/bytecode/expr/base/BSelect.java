package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.MemberHasWrongTypeException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.SelectHasIndexOutOfBoundException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.SelectHasWrongEvaluationTypeException;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BSelectKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BTupleType;

/**
 * This class is thread-safe.
 */
public final class BSelect extends BOperation {
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
    var hashes = readDataAsHashChain(2);
    var selectable = readMemberFromHashChain(hashes, 0);
    var index = readAndCastMemberFromHashChain(hashes, 1, "index", BInt.class);
    if (!(selectable.evaluationType() instanceof BTupleType tupleType)) {
      throw new MemberHasWrongTypeException(
          hash(), kind(), "tuple", BTupleType.class, selectable.evaluationType().getClass());
    }
    int i = index.toJavaBigInteger().intValue();
    int size = tupleType.elements().size();
    if (i < 0 || size <= i) {
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

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
  public static final int DATA_SEQ_SIZE = 2;
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
    var hashes = readDataAsHashChain(DATA_SEQ_SIZE);
    var selectable = readMemberFromHashChain(hashes, SELECTABLE_INDEX);
    var index = readAndCastMemberFromHashChain(hashes, INDEX_INDEX, "index", BInt.class);
    if (!(selectable.evaluationType() instanceof BTupleType tupleType)) {
      throw new MemberHasWrongTypeException(
          hash(),
          kind(),
          "selectable",
          BTupleType.class,
          selectable.evaluationType().getClass());
    }
    int i = index.toJavaBigInteger().intValue();
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

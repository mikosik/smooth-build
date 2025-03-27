package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.MemberHasWrongTypeException;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BInvokeKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BTupleType;

/**
 * Invocation of native function.
 * This class is thread-safe.
 */
public final class BInvoke extends BOperation {
  private static final int DATA_SEQ_SIZE = 3;
  public static final int METHOD_INDEX = 0;
  public static final int IS_PURE_IDX = 1;
  public static final int ARGUMENTS_INDEX = 2;

  public BInvoke(MerkleRoot merkleRoot, BExprDb exprDb) {
    super(merkleRoot, exprDb);
    checkArgument(merkleRoot.kind() instanceof BInvokeKind);
  }

  @Override
  public BSubExprs subExprs() throws BytecodeException {
    var hashes = readDataAsHashChain(DATA_SEQ_SIZE);
    var method =
        readMemberFromHashChain(hashes, METHOD_INDEX, "method", kindDb().method());
    var isPure = readMemberFromHashChain(hashes, IS_PURE_IDX, "isPure", kindDb().bool());
    var arguments = readMemberFromHashChain(hashes, ARGUMENTS_INDEX);
    if (!(arguments.evaluationType() instanceof BTupleType)) {
      throw new MemberHasWrongTypeException(
          hash(),
          kind(),
          "arguments",
          BTupleType.class,
          arguments.evaluationType().getClass());
    }
    return new BSubExprs(method, isPure, arguments);
  }

  public BBool isPure() throws BytecodeException {
    return readElementFromDataAsInstanceChain(IS_PURE_IDX, DATA_SEQ_SIZE, BBool.class);
  }

  @Override
  public String exprToString() throws BytecodeException {
    var subExprs = subExprs();
    return new ToStringBuilder(getClass().getSimpleName())
        .addField("hash", hash())
        .addField("evaluationType", evaluationType())
        .addField("method", subExprs.method())
        .addField("isPure", subExprs.isPure())
        .addField("arguments", subExprs.arguments())
        .toString();
  }

  public static record BSubExprs(BExpr method, BExpr isPure, BExpr arguments) implements BExprs {
    @Override
    public List<BExpr> toList() {
      return list(method, isPure, arguments);
    }
  }
}

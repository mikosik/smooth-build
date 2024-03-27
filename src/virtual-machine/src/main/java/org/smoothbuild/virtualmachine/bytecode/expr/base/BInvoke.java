package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeExprWrongMemberTypeException;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BInvokeKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BTupleType;

/**
 * Invocation of native function.
 * This class is thread-safe.
 */
public final class BInvoke extends BOperation {
  private static final int DATA_SEQ_SIZE = 4;
  private static final int JAR_IDX = 0;
  private static final int CLASS_BINARY_NAME_IDX = 1;
  private static final int IS_PURE_IDX = 2;

  public BInvoke(MerkleRoot merkleRoot, BExprDb exprDb) {
    super(merkleRoot, exprDb);
    checkArgument(merkleRoot.kind() instanceof BInvokeKind);
  }

  @Override
  public SubExprsB subExprs() throws BytecodeException {
    var hashes = readDataAsHashChain(4);
    var jar = readMemberFromHashChain(hashes, 0, "jar", kindDb().blob());
    var classBinaryName =
        readMemberFromHashChain(hashes, 1, "classBinaryName", kindDb().string());
    var isPure = readMemberFromHashChain(hashes, 2, "isPure", kindDb().bool());
    var arguments = readMemberFromHashChain(hashes, 3);
    if (!(arguments.evaluationType() instanceof BTupleType)) {
      throw new DecodeExprWrongMemberTypeException(
          hash(),
          kind(),
          "arguments",
          BTupleType.class,
          arguments.evaluationType().getClass());
    }
    return new SubExprsB(jar, classBinaryName, isPure, arguments);
  }

  public BBlob jar() throws BytecodeException {
    return readElementFromDataAsInstanceChain(JAR_IDX, DATA_SEQ_SIZE, BBlob.class);
  }

  public BString classBinaryName() throws BytecodeException {
    return readElementFromDataAsInstanceChain(CLASS_BINARY_NAME_IDX, DATA_SEQ_SIZE, BString.class);
  }

  public BBool isPure() throws BytecodeException {
    return readElementFromDataAsInstanceChain(IS_PURE_IDX, DATA_SEQ_SIZE, BBool.class);
  }

  public static record SubExprsB(BExpr jar, BExpr classBinaryName, BExpr isPure, BExpr arguments)
      implements BExprs {
    @Override
    public List<BExpr> toList() {
      return list(jar, classBinaryName, isPure, arguments);
    }
  }
}

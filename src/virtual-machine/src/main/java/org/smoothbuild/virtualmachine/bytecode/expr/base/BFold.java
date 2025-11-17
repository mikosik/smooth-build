package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BArrayType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BFoldKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BType;

/**
 * 'Fold' function.
 * This class is thread-safe.
 */
public final class BFold extends BOperation {
  private static final int ARRAY_INDEX = 0;
  private static final int INITIAL_INDEX = 1;
  private static final int FOLDER_INDEX = 2;

  public BFold(MerkleRoot merkleRoot, BExprDb exprDb) {
    super(merkleRoot, exprDb);
    checkArgument(merkleRoot.kind() instanceof BFoldKind);
  }

  @Override
  public BFoldKind kind() {
    return (BFoldKind) super.kind();
  }

  @Override
  public BType evaluationType() {
    return kind().evaluationType();
  }

  @Override
  public BSubExprs subExprs() throws BytecodeException {
    var members = members("array", "initial", "folder");
    var array = members.get(ARRAY_INDEX).asExpr(BArrayType.class);
    var arrayType = (BArrayType) array.evaluationType();
    var initial = members.get(INITIAL_INDEX).asExpr();
    var initialEvaluationType = initial.evaluationType();
    var expectedFolderEvaluationType =
        kindDb().lambda(list(initialEvaluationType, arrayType.element()), initialEvaluationType);
    var folder = members.get(FOLDER_INDEX).asExpr(expectedFolderEvaluationType);
    return new BSubExprs(array, initial, folder);
  }

  @Override
  public String exprToString() throws BytecodeException {
    var subExprs = subExprs();
    return new ToStringBuilder(getClass().getSimpleName())
        .addField("hash", hash())
        .addField("evaluationType", evaluationType())
        .addField("array", subExprs.array())
        .addField("initial", subExprs.initial())
        .addField("folder", subExprs.folder())
        .toString();
  }

  public static record BSubExprs(BExpr array, BExpr initial, BExpr folder) implements BExprs {
    @Override
    public List<BExpr> toList() {
      return list(array, initial, folder);
    }
  }
}

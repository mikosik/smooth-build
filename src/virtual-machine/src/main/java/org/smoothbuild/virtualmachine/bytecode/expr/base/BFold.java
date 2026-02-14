package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.function.Function0;
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

  private static final List<String> SUB_EXPR_NAMES = list("array", "initial", "folder");

  private final Function0<SubExprs, BytecodeException> subExprs =
      Function0.memoizer(this::fetchAndValidateSubExprs);

  public BFold(MerkleRoot merkleRoot, BExprDb exprDb) {
    super(merkleRoot, exprDb, SUB_EXPR_NAMES.size());
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

  private SubExprs fetchAndValidateSubExprs() throws BytecodeException {
    var subExprs = createSubExprList(SUB_EXPR_NAMES);
    var array = subExprs.get(ARRAY_INDEX).asExpr(BArrayType.class);
    var arrayType = (BArrayType) array.evaluationType();
    var initial = subExprs.get(INITIAL_INDEX).asExpr();
    var initialEvaluationType = initial.evaluationType();
    var expectedFolderEvaluationType =
        kindDb().lambda(list(initialEvaluationType, arrayType.element()), initialEvaluationType);
    var folder = subExprs.get(FOLDER_INDEX).asExpr(expectedFolderEvaluationType);
    return new SubExprs(array, initial, folder);
  }

  public BExpr array() throws BytecodeException {
    return subExprs().array();
  }

  public BExpr initial() throws BytecodeException {
    return subExprs().initial();
  }

  public BExpr folder() throws BytecodeException {
    return subExprs().folder();
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

  private SubExprs subExprs() throws BytecodeException {
    return subExprs.apply();
  }

  private record SubExprs(BExpr array, BExpr initial, BExpr folder) {}
}

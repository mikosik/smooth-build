package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.MemberHasWrongEvaluationTypeException;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BArrayType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BFoldKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BType;

/**
 * 'Fold' function.
 * This class is thread-safe.
 */
public final class BFold extends BOperation {
  private static final int DATA_SEQ_SIZE = 3;
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
    var hashes = readDataAsHashChain(DATA_SEQ_SIZE);
    var array = readMemberFromHashChain(hashes, ARRAY_INDEX);
    var arrayEvaluationType = array.evaluationType();
    if (!(arrayEvaluationType instanceof BArrayType arrayType)) {
      throw new MemberHasWrongEvaluationTypeException(
          hash(), kind(), "array", BArrayType.class, arrayEvaluationType);
    }
    var initial = readMemberFromHashChain(hashes, INITIAL_INDEX);
    var initialEvaluationType = initial.evaluationType();
    var folder = readMemberFromHashChain(hashes, FOLDER_INDEX);
    var folderEvaluationType = folder.evaluationType();
    var expectedFolderEvaluationType =
        kindDb().lambda(list(initialEvaluationType, arrayType.element()), initialEvaluationType);
    if (!folderEvaluationType.equals(expectedFolderEvaluationType)) {
      throw new MemberHasWrongEvaluationTypeException(
          hash(), kind(), "folder", expectedFolderEvaluationType, folderEvaluationType);
    }
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

package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.function.Function0;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.ChooseHasIndexOutOfBoundException;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BCreateVariantKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BVariantType;

/**
 * This class is thread-safe.
 */
public final class BCreateVariant extends BOperation {
  public static final int INDEX_INDEX = 0;
  public static final int CHOICE_INDEX = 1;

  private static final List<String> SUB_EXPR_NAMES = list("index", "choice");

  private final Function0<SubExprs, BytecodeException> subExprs =
      Function0.memoizer(this::fetchAndValidateSubExprs);

  public BCreateVariant(MerkleRoot merkleRoot, BExprDb exprDb) {
    super(merkleRoot, exprDb, SUB_EXPR_NAMES.size());
    checkArgument(merkleRoot.kind() instanceof BCreateVariantKind);
  }

  @Override
  public BVariantType evaluationType() {
    return (BVariantType) super.evaluationType();
  }

  @Override
  public BCreateVariantKind kind() {
    return (BCreateVariantKind) super.kind();
  }

  private SubExprs fetchAndValidateSubExprs() throws BytecodeException {
    var subExprs = createSubExprList(SUB_EXPR_NAMES);
    var index = subExprs.get(INDEX_INDEX).asInstanceOf(BInt.class);

    int i = index.toJavaBigInteger().intValue();
    var evaluationType = kind().evaluationType();
    var alternatives = evaluationType.alternatives();
    int size = alternatives.size();
    if (i < INDEX_INDEX || size <= i) {
      throw new ChooseHasIndexOutOfBoundException(hash(), evaluationType, i, size);
    }

    var expectedEvaluationType = alternatives.get(i);
    var choice = subExprs.get(CHOICE_INDEX).asExpr(expectedEvaluationType);
    return new SubExprs(index, choice);
  }

  public BInt index() throws BytecodeException {
    return subExprs().index();
  }

  public BExpr choice() throws BytecodeException {
    return subExprs().choice();
  }

  @Override
  public String exprToString() throws BytecodeException {
    var subExprs = subExprs();
    return new ToStringBuilder(getClass().getSimpleName())
        .addField("hash", hash())
        .addField("evaluationType", evaluationType())
        .addField("choice", subExprs.choice())
        .addField("index", subExprs.index())
        .toString();
  }

  private SubExprs subExprs() throws BytecodeException {
    return subExprs.apply();
  }

  private record SubExprs(BInt index, BExpr choice) {}
}

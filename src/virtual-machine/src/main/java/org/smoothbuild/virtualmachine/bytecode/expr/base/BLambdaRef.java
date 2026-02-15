package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.function.Function0;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BLambdaRefKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BType;

/**
 * This class is thread-safe.
 */
public final class BLambdaRef extends BOperation {
  private final Function0<BValue, BytecodeException> referencedName =
      Function0.memoizer(this::fetchReferencedName);

  public BLambdaRef(MerkleRoot merkleRoot, BExprDb exprDb) {
    super(merkleRoot, exprDb, 1);
    checkArgument(merkleRoot.kind() instanceof BLambdaRefKind);
  }

  @Override
  public BLambdaRefKind kind() {
    return (BLambdaRefKind) super.kind();
  }

  @Override
  public BType evaluationType() {
    return kind().evaluationType();
  }

  public BValue referencedName() throws BytecodeException {
    return referencedName.apply();
  }

  private BValue fetchReferencedName() throws BytecodeException {
    return createLoneSubExpr("referencedName").asInstanceOf(BValue.class);
  }

  @Override
  public String exprToString() throws BytecodeException {
    return new ToStringBuilder(getClass().getSimpleName())
        .addField("hash", hash())
        .addField("evaluationType", evaluationType())
        .addField("referencedName", referencedName())
        .toString();
  }
}

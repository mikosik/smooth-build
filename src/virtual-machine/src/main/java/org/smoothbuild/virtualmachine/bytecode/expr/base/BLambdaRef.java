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
  private final Function0<BValue, BytecodeException> tag =
      Function0.memoizer(this::fetchLambdaName);

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

  public BValue lambdaName() throws BytecodeException {
    return tag.apply();
  }

  private BValue fetchLambdaName() throws BytecodeException {
    return createLoneMember("name").asInstanceOf(BValue.class);
  }

  @Override
  public String exprToString() throws BytecodeException {
    return new ToStringBuilder(getClass().getSimpleName())
        .addField("hash", hash())
        .addField("evaluationType", evaluationType())
        .addField("lambdaName", tag.apply())
        .toString();
  }
}

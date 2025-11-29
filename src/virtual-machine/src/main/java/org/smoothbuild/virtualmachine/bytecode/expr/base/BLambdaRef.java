package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BLambdaRefKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BType;

/**
 * This class is thread-safe.
 */
public final class BLambdaRef extends BOperation {
  public BLambdaRef(MerkleRoot merkleRoot, BExprDb exprDb) {
    super(merkleRoot, exprDb);
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
    return loneMember("name").asInstanceOf(BValue.class);
  }

  @Override
  public BSubExprs subExprs() throws BytecodeException {
    return new BSubExprs(lambdaName());
  }

  @Override
  public String exprToString() throws BytecodeException {
    var subExprs = subExprs();
    return new ToStringBuilder(getClass().getSimpleName())
        .addField("hash", hash())
        .addField("evaluationType", evaluationType())
        .addField("lambdaName", subExprs.name())
        .toString();
  }

  public static record BSubExprs(BValue name) implements BExprs {
    @Override
    public List<BExpr> toList() {
      return list(name);
    }
  }
}

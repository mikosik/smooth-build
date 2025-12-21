package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.function.Function0;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BPickKind;

/**
 * This class is thread-safe.
 */
public final class BPick extends BOperation {
  public static final int PICKABLE_INDEX = 0;
  public static final int INDEX_INDEX = 1;

  private final Function0<BSubExprs, BytecodeException> subExprs =
      Function0.memoizer(this::fetchAndValidateSubExprs);

  public BPick(MerkleRoot merkleRoot, BExprDb exprDb) {
    super(merkleRoot, exprDb, 2);
    checkArgument(merkleRoot.kind() instanceof BPickKind);
  }

  @Override
  public BPickKind kind() {
    return (BPickKind) super.kind();
  }

  private BSubExprs fetchAndValidateSubExprs() throws BytecodeException {
    var members = members("pickable", "index");
    var pickable = members.get(PICKABLE_INDEX).asExpr(kindDb().array(evaluationType()));
    var index = members.get(INDEX_INDEX).asExpr(kindDb().int_());
    return new BSubExprs(pickable, index);
  }

  public BExpr pickable() throws BytecodeException {
    return subExprs.apply().pickable();
  }

  public BExpr index() throws BytecodeException {
    return subExprs.apply().index();
  }

  @Override
  public String exprToString() throws BytecodeException {
    var subExprs = this.subExprs.apply();
    return new ToStringBuilder(getClass().getSimpleName())
        .addField("hash", hash())
        .addField("evaluationType", evaluationType())
        .addField("pickable", subExprs.pickable())
        .addField("index", subExprs.index())
        .toString();
  }

  private record BSubExprs(BExpr pickable, BExpr index) {}
}

package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BPickKind;

/**
 * This class is thread-safe.
 */
public final class BPick extends BOperation {
  public BPick(MerkleRoot merkleRoot, BExprDb exprDb) {
    super(merkleRoot, exprDb);
    checkArgument(merkleRoot.kind() instanceof BPickKind);
  }

  @Override
  public BPickKind kind() {
    return (BPickKind) super.kind();
  }

  @Override
  public BSubExprs subExprs() throws BytecodeException {
    var hashes = readDataAsHashChain(2);
    var pickable = readMemberFromHashChain(hashes, 0, "pickable", kindDb().array(evaluationType()));
    var index = readMemberFromHashChain(hashes, 1, "index", kindDb().int_());
    return new BSubExprs(pickable, index);
  }

  @Override
  public String exprToString() throws BytecodeException {
    var subExprs = subExprs();
    return new ToStringBuilder(getClass().getSimpleName())
        .addField("hash", hash())
        .addField("evaluationType", evaluationType())
        .addField("pickable", subExprs.pickable())
        .addField("index", subExprs.index())
        .toString();
  }

  public static record BSubExprs(BExpr pickable, BExpr index) implements BExprs {
    @Override
    public List<BExpr> toList() {
      return list(pickable, index);
    }
  }
}

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
  public static final int DATA_SEQ_SIZE = 2;
  public static final int PICKABLE_INDEX = 0;
  public static final int INDEX_INDEX = 1;

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
    var hashes = readDataAsHashChain(DATA_SEQ_SIZE);
    var pickable = readMemberFromHashChain(
        hashes, PICKABLE_INDEX, "pickable", kindDb().array(evaluationType()));
    var index = readMemberFromHashChain(hashes, INDEX_INDEX, "index", kindDb().int_());
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

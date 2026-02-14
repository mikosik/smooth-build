package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BParamRefKind;

/**
 * References bound value using De Bruijn indexing with zero-based numbering.
 * <a href="https://en.wikipedia.org/wiki/De_Bruijn_index">De Bruijn index article</a> in wikipedia.
 * <p>
 * This class is thread-safe.
 */
public final class BParamRef extends BOperation {
  public BParamRef(MerkleRoot merkleRoot, BExprDb exprDb) {
    super(merkleRoot, exprDb, 1);
    checkArgument(merkleRoot.kind() instanceof BParamRefKind);
  }

  public BInt index() throws BytecodeException {
    return createLoneSubExpr("index").asInstanceOf(BInt.class);
  }

  @Override
  public String exprToString() throws BytecodeException {
    return new ToStringBuilder(getClass().getSimpleName())
        .addField("hash", hash())
        .addField("evaluationType", evaluationType())
        .addField("index", index().toJavaBigInteger())
        .toString();
  }
}

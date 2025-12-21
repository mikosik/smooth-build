package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.function.Function0;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BArrayType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BOrderKind;

/**
 * This class is thread-safe.
 */
public final class BOrder extends BOperation {
  private final Function0<List<BExpr>, BytecodeException> elements =
      Function0.memoizer(this::fetchElements);

  public BOrder(MerkleRoot merkleRoot, BExprDb exprDb) {
    super(merkleRoot, exprDb, -1);
    checkArgument(merkleRoot.kind() instanceof BOrderKind);
  }

  @Override
  public BOrderKind kind() {
    return (BOrderKind) super.kind();
  }

  @Override
  public BArrayType evaluationType() {
    return kind().evaluationType();
  }

  public List<BExpr> elements() throws BytecodeException {
    return elements.apply();
  }

  private List<BExpr> fetchElements() throws BytecodeException {
    var member = loneElementsMember("elements");
    var elements = member.elements();
    member.checkElementTypes(evaluationType().element());
    return elements;
  }

  @Override
  public String exprToString() throws BytecodeException {
    return new ToStringBuilder(getClass().getSimpleName())
        .addField("hash", hash())
        .addField("evaluationType", evaluationType())
        .addListField("elements", elements.apply())
        .toString();
  }
}

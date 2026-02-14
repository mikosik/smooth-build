package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.function.Function0;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.SubExprHasWrongTypeException;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BCombineKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BTupleType;

/**
 * This class is thread-safe.
 */
public final class BCombine extends BOperation {
  private final Function0<List<BExpr>, BytecodeException> items =
      Function0.memoizer(this::itemsValidated);

  public BCombine(MerkleRoot merkleRoot, BExprDb exprDb) {
    super(merkleRoot, exprDb, -1);
    checkArgument(merkleRoot.kind() instanceof BCombineKind);
  }

  @Override
  public BCombineKind kind() {
    return (BCombineKind) super.kind();
  }

  @Override
  public BTupleType evaluationType() {
    return kind().evaluationType();
  }

  public List<BExpr> items() throws BytecodeException {
    return items.apply();
  }

  private List<BExpr> itemsValidated() throws BytecodeException {
    var items = createLoneElements("items").asList();
    var actualType = kindDb().tuple(items.map(BExpr::evaluationType));
    if (!actualType.equals(evaluationType())) {
      throw new SubExprHasWrongTypeException(hash(), kind(), "items", evaluationType(), actualType);
    }
    return items;
  }

  @Override
  public String exprToString() throws BytecodeException {
    return new ToStringBuilder(getClass().getSimpleName())
        .addField("hash", hash())
        .addField("evaluationType", evaluationType())
        .addListField("items", items.apply())
        .toString();
  }
}

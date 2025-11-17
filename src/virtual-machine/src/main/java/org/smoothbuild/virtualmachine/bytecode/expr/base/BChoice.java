package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.ChoiceHasIndexOutOfBoundException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.MemberHasWrongTypeException;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BChoiceType;

/**
 * This class is thread-safe.
 */
public final class BChoice extends BValue {
  private static final int INDEX_INDEX = 0;
  private static final int CHOSEN_INDEX = 1;

  public BChoice(MerkleRoot merkleRoot, BExprDb exprDb) {
    super(merkleRoot, exprDb);
    checkArgument(merkleRoot.kind() instanceof BChoiceType);
  }

  @Override
  public BChoiceType evaluationType() {
    return type();
  }

  @Override
  public BChoiceType type() {
    return (BChoiceType) super.kind();
  }

  public BSubExprs components() throws BytecodeException {
    var members = members("index", "chosen");
    var index = members.get(INDEX_INDEX).asInstanceOf(BInt.class);

    int i = index.toJavaBigInteger().intValue();
    var alternatives = type().alternatives();
    int size = alternatives.size();
    if (i < INDEX_INDEX || size <= i) {
      throw new ChoiceHasIndexOutOfBoundException(hash(), type(), i, size);
    }

    var expectedExprType = alternatives.get(i);
    var value = members.get(CHOSEN_INDEX).asInstanceOf(BValue.class);
    var itemType = value.evaluationType();
    if (!itemType.equals(expectedExprType)) {
      throw new MemberHasWrongTypeException(hash(), kind(), "chosen", expectedExprType, itemType);
    }
    return new BSubExprs(index, value);
  }

  @Override
  public String exprToString() throws BytecodeException {
    return new ToStringBuilder(getClass().getSimpleName())
        .addField("hash", hash())
        .addField("type", type())
        .addListField("members", components().toList().map(BExpr::exprToString))
        .toString();
  }

  public static record BSubExprs(BInt index, BValue chosen) implements BExprs {
    @Override
    public List<BExpr> toList() {
      return list(index, chosen);
    }
  }
}

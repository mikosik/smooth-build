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

  public BSubExprs members() throws BytecodeException {
    var hashes = readDataAsHashChain(2);
    var index = readAndCastMemberFromHashChain(hashes, 0, "index", BInt.class);

    int i = index.toJavaBigInteger().intValue();
    var alternatives = type().alternatives();
    int size = alternatives.size();
    if (i < 0 || size <= i) {
      throw new ChoiceHasIndexOutOfBoundException(hash(), type(), i, size);
    }

    var expectedExprType = alternatives.get(i);
    var value = readAndCastMemberFromHashChain(hashes, 1, "chosen", BValue.class);
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
        .addListField("members", members().toList().map(BExpr::exprToString))
        .toString();
  }

  public static record BSubExprs(BInt index, BValue chosen) implements BExprs {
    @Override
    public List<BExpr> toList() {
      return list(index, chosen);
    }
  }
}

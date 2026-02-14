package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.function.Function0;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.ChoiceHasIndexOutOfBoundException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.SubExprHasWrongTypeException;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BChoiceType;

/**
 * This class is thread-safe.
 */
public final class BChoice extends BValue {
  private static final int INDEX_INDEX = 0;
  private static final int CHOSEN_INDEX = 1;

  private static final List<String> SUB_EXPR_NAMES = list("index", "chosen");

  private final Function0<Components, BytecodeException> components =
      Function0.memoizer(this::fetchAndValidateComponents);

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

  private Components fetchAndValidateComponents() throws BytecodeException {
    var subExprs = createSubExprList(SUB_EXPR_NAMES);
    var index = subExprs.get(INDEX_INDEX).asInstanceOf(BInt.class);

    int i = index.toJavaBigInteger().intValue();
    var alternatives = type().alternatives();
    int size = alternatives.size();
    if (i < INDEX_INDEX || size <= i) {
      throw new ChoiceHasIndexOutOfBoundException(hash(), type(), i, size);
    }

    var expectedExprType = alternatives.get(i);
    var value = subExprs.get(CHOSEN_INDEX).asInstanceOf(BValue.class);
    var itemType = value.evaluationType();
    if (!itemType.equals(expectedExprType)) {
      throw new SubExprHasWrongTypeException(hash(), kind(), "chosen", expectedExprType, itemType);
    }
    return new Components(index, value);
  }

  public BInt index() throws BytecodeException {
    return components.apply().index();
  }

  public BValue chosen() throws BytecodeException {
    return components.apply().chosen();
  }

  @Override
  public String exprToString() throws BytecodeException {
    var components = this.components.apply();
    return new ToStringBuilder(getClass().getSimpleName())
        .addField("hash", hash())
        .addField("type", type())
        .addField("index", components.index())
        .addField("chosen", components.chosen())
        .toString();
  }

  private static record Components(BInt index, BValue chosen) {}
}

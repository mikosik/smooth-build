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
import org.smoothbuild.virtualmachine.bytecode.kind.base.BVariantType;

/**
 * This class is thread-safe.
 */
public final class BVariant extends BValue {
  private static final int INDEX_INDEX = 0;
  private static final int CHOICE_INDEX = 1;

  private static final List<String> SUB_EXPR_NAMES = list("index", "choice");

  private final Function0<Components, BytecodeException> components =
      Function0.memoizer(this::fetchAndValidateComponents);

  public BVariant(MerkleRoot merkleRoot, BExprDb exprDb) {
    super(merkleRoot, exprDb);
    checkArgument(merkleRoot.kind() instanceof BVariantType);
  }

  @Override
  public BVariantType evaluationType() {
    return type();
  }

  @Override
  public BVariantType type() {
    return (BVariantType) super.kind();
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
    var choice = subExprs.get(CHOICE_INDEX).asInstanceOf(BValue.class);
    var itemType = choice.evaluationType();
    if (!itemType.equals(expectedExprType)) {
      throw new SubExprHasWrongTypeException(hash(), kind(), "choice", expectedExprType, itemType);
    }
    return new Components(index, choice);
  }

  public BInt index() throws BytecodeException {
    return components.apply().index();
  }

  public BValue choice() throws BytecodeException {
    return components.apply().choice();
  }

  @Override
  public String exprToString() throws BytecodeException {
    var components = this.components.apply();
    return new ToStringBuilder(getClass().getSimpleName())
        .addField("hash", hash())
        .addField("type", type())
        .addField("index", components.index())
        .addField("choice", components.choice())
        .toString();
  }

  private static record Components(BInt index, BValue choice) {}
}

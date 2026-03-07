package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.function.Function0;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.TupleGetHasIndexOutOfBoundException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.TupleGetHasWrongEvaluationTypeException;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BTupleGetKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BTupleType;

/**
 * This class is thread-safe.
 */
public final class BTupleGet extends BOperation {
  public static final int TUPLE_INDEX = 0;
  public static final int INDEX_INDEX = 1;

  private static final List<String> SUB_EXPR_NAMES = list("tuple", "index");

  private final Function0<SubExprs, BytecodeException> subExprs =
      Function0.memoizer(this::fetchAndValidateSubExprs);

  public BTupleGet(MerkleRoot merkleRoot, BExprDb exprDb) {
    super(merkleRoot, exprDb, SUB_EXPR_NAMES.size());
    checkArgument(merkleRoot.kind() instanceof BTupleGetKind);
  }

  @Override
  public BTupleGetKind kind() {
    return (BTupleGetKind) super.kind();
  }

  private SubExprs fetchAndValidateSubExprs() throws BytecodeException {
    var subExprs = createSubExprList(SUB_EXPR_NAMES);
    var tuple = subExprs.get(TUPLE_INDEX).asExpr(BTupleType.class);
    var index = subExprs.get(INDEX_INDEX).asInstanceOf(BInt.class);
    int i = index.toJavaBigInteger().intValue();
    var tupleType = (BTupleType) tuple.evaluationType();
    int size = tupleType.elements().size();
    if (i < 0 || size <= i) {
      throw new TupleGetHasIndexOutOfBoundException(hash(), kind(), i, size);
    }
    var fieldType = tupleType.elements().get(i);
    if (!evaluationType().equals(fieldType)) {
      throw new TupleGetHasWrongEvaluationTypeException(hash(), kind(), fieldType);
    }
    return new SubExprs(tuple, index);
  }

  public BExpr tuple() throws BytecodeException {
    return subExprs().tuple();
  }

  public BInt index() throws BytecodeException {
    return subExprs().index();
  }

  @Override
  public String exprToString() throws BytecodeException {
    var subExprs = subExprs();
    return new ToStringBuilder(getClass().getSimpleName())
        .addField("hash", hash())
        .addField("evaluationType", evaluationType())
        .addField("tuple", subExprs.tuple())
        .addField("index", subExprs.index())
        .toString();
  }

  private SubExprs subExprs() throws BytecodeException {
    return subExprs.apply();
  }

  private record SubExprs(BExpr tuple, BInt index) {}
}

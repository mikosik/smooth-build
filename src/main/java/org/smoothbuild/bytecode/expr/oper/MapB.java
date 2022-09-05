package org.smoothbuild.bytecode.expr.oper;

import static org.smoothbuild.bytecode.type.ValidateArgs.validateArgs;
import static org.smoothbuild.util.collect.Lists.list;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.ExprB;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.bytecode.expr.exc.DecodeExprWrongNodeClassExc;
import org.smoothbuild.bytecode.expr.exc.DecodeExprWrongNodeTypeExc;
import org.smoothbuild.bytecode.expr.exc.DecodeMapIllegalMappingFuncExc;
import org.smoothbuild.bytecode.type.oper.MapCB;
import org.smoothbuild.bytecode.type.val.ArrayTB;
import org.smoothbuild.bytecode.type.val.CallableTB;
import org.smoothbuild.bytecode.type.val.FuncTB;
import org.smoothbuild.bytecode.type.val.TypeB;

/**
 * Map expression.
 *
 * This class is thread-safe.
 */
public final class MapB extends OperB {
  private static final int DATA_SEQ_SIZE = 2;
  private static final int ARRAY_IDX = 0;
  private static final int FUNC_IDX = 1;

  public MapB(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    super(merkleRoot, bytecodeDb);
  }

  @Override
  public MapCB cat() {
    return (MapCB) super.cat();
  }

  @Override
  public ArrayTB type() {
    return this.cat().evalT();
  }

  public Data data() {
    var array = readArray();
    var func = readFunc();
    var maybeArrayT = array.type();
    if (!(maybeArrayT instanceof ArrayTB arrayT)) {
      throw new DecodeExprWrongNodeClassExc(
          hash(), cat(), DATA_PATH, ARRAY_IDX, ArrayTB.class, maybeArrayT.getClass());
    }
    var maybeFuncT = func.type();
    if (!(maybeFuncT instanceof FuncTB funcT)) {
      throw new DecodeExprWrongNodeClassExc(
          hash(), cat(), DATA_PATH, FUNC_IDX, FuncTB.class, maybeFuncT.getClass());
    }
    if (funcT.params().size() != 1) {
      throw new DecodeMapIllegalMappingFuncExc(hash(), cat(), funcT);
    }
    var elemT = arrayT.elem();
    validateArgs(funcT, list(elemT), () -> illegalArgs(funcT, elemT));
    var expectedElemT = type().elem();
    if (!expectedElemT.equals(funcT.res())) {
      throw new DecodeExprWrongNodeTypeExc(
          hash(), cat(), "func.result", expectedElemT, funcT.res());
    }
    return new Data(array, func);
  }

  private RuntimeException illegalArgs(CallableTB callableTB, TypeB arrayElemT) {
    return new DecodeExprWrongNodeTypeExc(
        hash(), this.cat(), "array element", callableTB.paramsTuple().items().get(0), arrayElemT);
  }

  public record Data(ExprB array, ExprB func) {}

  private ExprB readArray() {
    return readSeqElemExpr(DATA_PATH, dataHash(), ARRAY_IDX, DATA_SEQ_SIZE, ExprB.class);
  }

  private ExprB readFunc() {
    return readSeqElemExpr(DATA_PATH, dataHash(), FUNC_IDX, DATA_SEQ_SIZE, ExprB.class);
  }
}

package org.smoothbuild.bytecode.obj.expr;

import static org.smoothbuild.util.collect.Lists.list;

import org.smoothbuild.bytecode.obj.ObjDbImpl;
import org.smoothbuild.bytecode.obj.base.ExprB;
import org.smoothbuild.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.bytecode.obj.base.ObjB;
import org.smoothbuild.bytecode.obj.exc.DecodeMapIllegalMappingFuncExc;
import org.smoothbuild.bytecode.obj.exc.DecodeObjWrongNodeClassExc;
import org.smoothbuild.bytecode.obj.exc.DecodeObjWrongNodeTypeExc;
import org.smoothbuild.bytecode.type.expr.MapCB;
import org.smoothbuild.bytecode.type.val.ArrayTB;
import org.smoothbuild.bytecode.type.val.CallableTB;
import org.smoothbuild.bytecode.type.val.FuncTB;
import org.smoothbuild.bytecode.type.val.TypeB;

/**
 * Map expression.
 *
 * This class is thread-safe.
 */
public final class MapB extends ExprB {
  private static final int DATA_SEQ_SIZE = 2;
  private static final int ARRAY_IDX = 0;
  private static final int FUNC_IDX = 1;

  public MapB(MerkleRoot merkleRoot, ObjDbImpl objDb) {
    super(merkleRoot, objDb);
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
      throw new DecodeObjWrongNodeClassExc(
          hash(), cat(), DATA_PATH, ARRAY_IDX, ArrayTB.class, maybeArrayT.getClass());
    }
    var maybeFuncT = func.type();
    if (!(maybeFuncT instanceof FuncTB funcT)) {
      throw new DecodeObjWrongNodeClassExc(
          hash(), cat(), DATA_PATH, FUNC_IDX, FuncTB.class, maybeFuncT.getClass());
    }
    if (funcT.params().size() != 1) {
      throw new DecodeMapIllegalMappingFuncExc(hash(), cat(), funcT);
    }
    var elemT = arrayT.elem();
    var funcActualResT =
        typing().inferCallResT(funcT, list(elemT), () -> illegalArgs(funcT, elemT));
    var expectedElemT = type().elem();
    if (!typing().isAssignable(expectedElemT, funcActualResT)) {
      throw new DecodeObjWrongNodeTypeExc(
          hash(), cat(), "func.result", expectedElemT, funcActualResT);
    }
    return new Data(array, func);
  }

  private RuntimeException illegalArgs(CallableTB callableTB, TypeB arrayElemT) {
    return new DecodeObjWrongNodeTypeExc(
        hash(), this.cat(), "array element", callableTB.paramsTuple().items().get(0), arrayElemT);
  }

  public record Data(ObjB array, ObjB func) {}

  private ObjB readArray() {
    return readSeqElemObj(DATA_PATH, dataHash(), ARRAY_IDX, DATA_SEQ_SIZE, ObjB.class);
  }

  private ObjB readFunc() {
    return readSeqElemObj(DATA_PATH, dataHash(), FUNC_IDX, DATA_SEQ_SIZE, ObjB.class);
  }
}

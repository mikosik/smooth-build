package org.smoothbuild.db.object.obj.expr;

import static org.smoothbuild.util.collect.Lists.list;

import org.smoothbuild.db.object.obj.ByteDb;
import org.smoothbuild.db.object.obj.base.ExprB;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.ObjB;
import org.smoothbuild.db.object.obj.exc.DecodeMapIllegalMappingFuncExc;
import org.smoothbuild.db.object.obj.exc.DecodeObjWrongNodeTypeExc;
import org.smoothbuild.db.object.type.base.TypeB;
import org.smoothbuild.db.object.type.expr.MapCB;
import org.smoothbuild.db.object.type.val.ArrayTB;
import org.smoothbuild.db.object.type.val.CallableTB;
import org.smoothbuild.db.object.type.val.FuncTB;

/**
 * Map expression.
 *
 * This class is thread-safe.
 */
public final class MapB extends ExprB {
  private static final int DATA_SEQ_SIZE = 2;
  private static final int ARRAY_INDEX = 0;
  private static final int FUNC_INDEX = 1;

  public MapB(MerkleRoot merkleRoot, ByteDb byteDb) {
    super(merkleRoot, byteDb);
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
      throw new DecodeObjWrongNodeTypeExc(
          hash(), cat(), DATA_PATH, ARRAY_INDEX, ArrayTB.class, maybeArrayT.getClass());
    }
    var maybeFuncT = func.type();
    if (!(maybeFuncT instanceof FuncTB funcT)) {
      throw new DecodeObjWrongNodeTypeExc(
          hash(), cat(), DATA_PATH, FUNC_INDEX, FuncTB.class, maybeFuncT.getClass());
    }
    if (funcT.params().size() != 1) {
      throw new DecodeMapIllegalMappingFuncExc(hash(), cat(), funcT);
    }
    var elemT = arrayT.elem();
    var funcActualResT = byteDb().inferCallResT(
        funcT, list(elemT), () -> illegalArgs(funcT, elemT));
    var expectedElemT = type().elem();
    if (!byteDb().typing().isAssignable(expectedElemT, funcActualResT)) {
      throw new DecodeObjWrongNodeTypeExc(
          hash(), cat(), "func.result", expectedElemT, funcActualResT);
    }
    return new Data(array, func);
  }

  private void illegalArgs(CallableTB callableTB, TypeB arrayElemT) {
    throw new DecodeObjWrongNodeTypeExc(
        hash(), this.cat(), "array element", callableTB.paramsTuple().items().get(0), arrayElemT);
  }

  public record Data(ObjB array, ObjB func) {}

  private ObjB readArray() {
    return readSeqElemObj(DATA_PATH, dataHash(), ARRAY_INDEX, DATA_SEQ_SIZE, ObjB.class);
  }

  private ObjB readFunc() {
    return readSeqElemObj(DATA_PATH, dataHash(), FUNC_INDEX, DATA_SEQ_SIZE, ObjB.class);
  }
}

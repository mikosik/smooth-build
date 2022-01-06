package org.smoothbuild.bytecode.obj.expr;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.bytecode.obj.ObjDbImpl;
import org.smoothbuild.bytecode.obj.base.ExprB;
import org.smoothbuild.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.bytecode.obj.base.ObjB;
import org.smoothbuild.bytecode.obj.exc.DecodeObjWrongNodeTypeExc;
import org.smoothbuild.bytecode.type.expr.PickCB;
import org.smoothbuild.bytecode.type.val.ArrayTB;
import org.smoothbuild.bytecode.type.val.IntTB;

/**
 * This class is thread-safe.
 */
public class PickB extends ExprB {
  private static final int DATA_SEQ_SIZE = 2;
  private static final int ARRAY_IDX = 0;
  private static final int IDX_IDX = 1;

  public PickB(MerkleRoot merkleRoot, ObjDbImpl byteDb) {
    super(merkleRoot, byteDb);
    checkArgument(merkleRoot.cat() instanceof PickCB);
  }

  @Override
  public PickCB cat() {
    return (PickCB) super.cat();
  }

  public Data data() {
    ObjB pickable = readPickable();
    if (!(pickable.type() instanceof ArrayTB arrayT)) {
      throw new DecodeObjWrongNodeTypeExc(
          hash(), cat(), "array", ArrayTB.class, pickable.type().getClass());
    }
    ObjB index = readIndex();
    if (!(index.type() instanceof IntTB)) {
      throw new DecodeObjWrongNodeTypeExc(
          hash(), cat(), "index", IntTB.class, index.type().getClass());
    }
    if (!byteDb().typing().isAssignable(type(), arrayT.elem())) {
      throw new DecodeObjWrongNodeTypeExc(hash(), cat(), "array element type", type(),
          arrayT.elem());
    }
    return new Data(pickable, index);
  }

  public record Data(ObjB pickable, ObjB index) {}

  private ObjB readPickable() {
    return readSeqElemObj(DATA_PATH, dataHash(), ARRAY_IDX, DATA_SEQ_SIZE, ObjB.class);
  }

  private ObjB readIndex() {
    return readSeqElemObj(DATA_PATH, dataHash(), IDX_IDX, DATA_SEQ_SIZE, ObjB.class);
  }
}

package org.smoothbuild.db.object.obj.expr;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Objects;

import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.ExprH;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.ObjH;
import org.smoothbuild.db.object.obj.exc.DecodeExprWrongEvalTypeOfCompExc;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.db.object.type.expr.OrderCH;
import org.smoothbuild.db.object.type.val.ArrayTH;

import com.google.common.collect.ImmutableList;

/**
 * This class is thread-safe.
 */
public class OrderH extends ExprH {
  public OrderH(MerkleRoot merkleRoot, ObjDb objDb) {
    super(merkleRoot, objDb);
    checkArgument(merkleRoot.cat() instanceof OrderCH);
  }

  @Override
  public OrderCH cat() {
    return (OrderCH) super.cat();
  }

  @Override
  public ArrayTH type() {
    return cat().evalType();
  }

  public ImmutableList<ObjH> elems() {
    var elems = readSeqObjs(DATA_PATH, dataHash(), ObjH.class);
    var expectedElementType = cat().evalType().elem();
    for (int i = 0; i < elems.size(); i++) {
      TypeH actualType = elems.get(i).type();
      if (!Objects.equals(expectedElementType, actualType)) {
        throw new DecodeExprWrongEvalTypeOfCompExc(
            hash(), cat(), "elems[" + i + "]", expectedElementType, actualType);
      }
    }
    return elems;
  }
}

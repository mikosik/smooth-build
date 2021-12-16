package org.smoothbuild.db.object.obj.expr;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.ExprH;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.ObjH;
import org.smoothbuild.db.object.obj.exc.DecodeObjWrongNodeTypeExc;
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
    return cat().evalT();
  }

  public ImmutableList<ObjH> elems() {
    var elems = readSeqObjs(DATA_PATH, dataHash(), ObjH.class);
    var expectedElemT = cat().evalT().elem();
    for (int i = 0; i < elems.size(); i++) {
      var actualT = elems.get(i).type();
      if (!objDb().typing().isAssignable(expectedElemT, actualT)) {
        throw new DecodeObjWrongNodeTypeExc(hash(), cat(), "elems", i, expectedElemT, actualT);
      }
    }
    return elems;
  }
}

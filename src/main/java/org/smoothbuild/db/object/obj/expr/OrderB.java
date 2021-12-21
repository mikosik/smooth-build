package org.smoothbuild.db.object.obj.expr;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.db.object.obj.ByteDb;
import org.smoothbuild.db.object.obj.base.ExprB;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.ObjB;
import org.smoothbuild.db.object.obj.exc.DecodeObjWrongNodeTypeExc;
import org.smoothbuild.db.object.type.expr.OrderCB;
import org.smoothbuild.db.object.type.val.ArrayTB;

import com.google.common.collect.ImmutableList;

/**
 * This class is thread-safe.
 */
public class OrderB extends ExprB {
  public OrderB(MerkleRoot merkleRoot, ByteDb byteDb) {
    super(merkleRoot, byteDb);
    checkArgument(merkleRoot.cat() instanceof OrderCB);
  }

  @Override
  public OrderCB cat() {
    return (OrderCB) super.cat();
  }

  @Override
  public ArrayTB type() {
    return cat().evalT();
  }

  public ImmutableList<ObjB> elems() {
    var elems = readSeqObjs(DATA_PATH, dataHash(), ObjB.class);
    var expectedElemT = cat().evalT().elem();
    for (int i = 0; i < elems.size(); i++) {
      var actualT = elems.get(i).type();
      if (!byteDb().typing().isAssignable(expectedElemT, actualT)) {
        throw new DecodeObjWrongNodeTypeExc(hash(), cat(), "elems", i, expectedElemT, actualT);
      }
    }
    return elems;
  }
}
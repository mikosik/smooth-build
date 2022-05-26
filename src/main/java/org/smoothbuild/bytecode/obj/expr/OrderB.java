package org.smoothbuild.bytecode.obj.expr;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.bytecode.obj.ObjDbImpl;
import org.smoothbuild.bytecode.obj.base.ExprB;
import org.smoothbuild.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.bytecode.obj.base.ObjB;
import org.smoothbuild.bytecode.obj.exc.DecodeObjWrongNodeTypeExc;
import org.smoothbuild.bytecode.type.cnst.ArrayTB;
import org.smoothbuild.bytecode.type.expr.OrderCB;

import com.google.common.collect.ImmutableList;

/**
 * This class is thread-safe.
 */
public class OrderB extends ExprB {
  public OrderB(MerkleRoot merkleRoot, ObjDbImpl objDb) {
    super(merkleRoot, objDb);
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
      if (!typing().isAssignable(expectedElemT, actualT)) {
        throw new DecodeObjWrongNodeTypeExc(hash(), cat(), "elems", i, expectedElemT, actualT);
      }
    }
    return elems;
  }
}

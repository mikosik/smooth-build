package org.smoothbuild.db.object.obj.val;

import org.smoothbuild.db.object.obj.ByteDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.ObjB;
import org.smoothbuild.db.object.obj.exc.DecodeObjWrongNodeTypeExc;
import org.smoothbuild.db.object.type.val.FuncTB;

/**
 * Function.
 * This class is thread-safe.
 */
public final class FuncB extends ValB {
  public FuncB(MerkleRoot merkleRoot, ByteDb byteDb) {
    super(merkleRoot, byteDb);
  }

  @Override
  public FuncTB type() {
    return (FuncTB) super.type();
  }

  @Override
  public FuncTB cat() {
    return (FuncTB) super.cat();
  }

  public ObjB body() {
    var body = readObj(DATA_PATH, dataHash(), ObjB.class);
    var resT = cat().res();
    var bodyT = body.type();
    if (!byteDb().typing().isAssignable(resT, bodyT)) {
      throw new DecodeObjWrongNodeTypeExc(hash(), cat(), DATA_PATH, resT, bodyT);
    }
    return body;
  }

  @Override
  public String objToString() {
    return "Func(" + cat().name() + ")";
  }
}

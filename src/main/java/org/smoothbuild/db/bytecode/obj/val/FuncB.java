package org.smoothbuild.db.bytecode.obj.val;

import org.smoothbuild.db.bytecode.obj.ByteDbImpl;
import org.smoothbuild.db.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.db.bytecode.obj.base.ObjB;
import org.smoothbuild.db.bytecode.obj.exc.DecodeObjWrongNodeTypeExc;
import org.smoothbuild.db.bytecode.type.val.FuncTB;

/**
 * Function.
 * This class is thread-safe.
 */
public final class FuncB extends ValB {
  public FuncB(MerkleRoot merkleRoot, ByteDbImpl byteDb) {
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

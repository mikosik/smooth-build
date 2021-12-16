package org.smoothbuild.db.object.obj.val;

import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.ObjH;
import org.smoothbuild.db.object.obj.exc.DecodeObjWrongNodeTypeExc;
import org.smoothbuild.db.object.type.val.FuncTH;

/**
 * Function.
 * This class is thread-safe.
 */
public final class FuncH extends ValH {
  public FuncH(MerkleRoot merkleRoot, ObjDb objDb) {
    super(merkleRoot, objDb);
  }

  @Override
  public FuncTH type() {
    return (FuncTH) super.type();
  }

  @Override
  public FuncTH cat() {
    return (FuncTH) super.cat();
  }

  public ObjH body() {
    var body = readObj(DATA_PATH, dataHash(), ObjH.class);
    var resT = cat().res();
    var bodyT = body.type();
    if (!objDb().typing().isAssignable(resT, bodyT)) {
      throw new DecodeObjWrongNodeTypeExc(hash(), cat(), DATA_PATH, resT, bodyT);
    }
    return body;
  }

  @Override
  public String objToString() {
    return "Func(" + cat().name() + ")";
  }
}

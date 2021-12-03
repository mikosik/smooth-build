package org.smoothbuild.db.object.obj.val;

import static org.smoothbuild.db.object.type.base.CatKindH.DEF_FUNC;

import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.ObjH;
import org.smoothbuild.db.object.obj.exc.DecodeExprWrongEvalTypeOfCompExc;

/**
 * Defined function.
 * This class is thread-safe.
 */
public final class DefFuncH extends FuncH {
  public DefFuncH(MerkleRoot merkleRoot, ObjDb objDb) {
    super(merkleRoot, objDb, DEF_FUNC);
  }

  public ObjH body() {
    var body = readObj(DATA_PATH, dataHash(), ObjH.class);
    var resultType = cat().res();
    var bodyType = body.type();
    if (!objDb().typing().isAssignable(resultType, bodyType)) {
      throw new DecodeExprWrongEvalTypeOfCompExc(
          hash(), cat(), DATA_PATH, resultType, bodyType);
    }
    return body;
  }

  @Override
  public String objToString() {
    return "DefFuncH(" + cat().name() + ")";
  }
}

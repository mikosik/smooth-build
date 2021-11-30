package org.smoothbuild.db.object.obj.val;

import static org.smoothbuild.db.object.type.base.SpecKindH.DEF_FUNC;

import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.ObjectH;
import org.smoothbuild.db.object.obj.exc.DecodeExprWrongEvalTypeOfComponentException;

public class DefFuncH extends FuncH {
  public DefFuncH(MerkleRoot merkleRoot, ObjectHDb objectHDb) {
    super(merkleRoot, objectHDb, DEF_FUNC);
  }

  public ObjectH body() {
    var body = readObj(DATA_PATH, dataHash(), ObjectH.class);
    var resultType = spec().res();
    var bodyType = body.type();
    if (!objectDb().typing().isAssignable(resultType, bodyType)) {
      throw new DecodeExprWrongEvalTypeOfComponentException(
          hash(), spec(), DATA_PATH, resultType, bodyType);
    }
    return body;
  }

  @Override
  public String valToString() {
    return "DefFuncH(" + spec().name() + ")";
  }
}

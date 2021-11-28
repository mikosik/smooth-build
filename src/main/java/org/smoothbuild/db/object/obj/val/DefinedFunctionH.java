package org.smoothbuild.db.object.obj.val;

import static org.smoothbuild.db.object.type.base.TypeKindH.DEFINED_FUNCTION;

import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.ObjectH;
import org.smoothbuild.db.object.obj.exc.DecodeExprWrongEvaluationTypeOfComponentException;

public class DefinedFunctionH extends FunctionH {
  public DefinedFunctionH(MerkleRoot merkleRoot, ObjectHDb objectHDb) {
    super(merkleRoot, objectHDb, DEFINED_FUNCTION);
  }

  public ObjectH body() {
    var body = readObj(DATA_PATH, dataHash(), ObjectH.class);
    var resultType = type().result();
    var bodyType = body.evaluationType();
    if (!objectDb().typing().isAssignable(resultType, bodyType)) {
      throw new DecodeExprWrongEvaluationTypeOfComponentException(
          hash(), type(), DATA_PATH, resultType, bodyType);
    }
    return body;
  }

  @Override
  public String valueToString() {
    return "Lambda(" + this.type().name() + ")";
  }
}
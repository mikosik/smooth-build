package org.smoothbuild.db.object.type.val;

import static org.smoothbuild.db.object.type.base.SpecKindH.DEFINED_FUNCTION;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.val.DefinedFunctionH;
import org.smoothbuild.db.object.type.base.TypeH;

public class DefinedFunctionTypeH extends FunctionTypeH {
  public DefinedFunctionTypeH(Hash hash, TypeH result, TupleTypeH paramsTuple) {
    super(hash, DEFINED_FUNCTION, result, paramsTuple);
  }

  @Override
  public DefinedFunctionH newObj(MerkleRoot merkleRoot, ObjectHDb objectHDb) {
    return (DefinedFunctionH) super.newObj(merkleRoot, objectHDb);
  }
}

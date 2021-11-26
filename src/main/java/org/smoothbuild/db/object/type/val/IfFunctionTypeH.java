package org.smoothbuild.db.object.type.val;

import static org.smoothbuild.db.object.type.base.TypeKindH.IF_FUNCTION;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.val.IfFunctionH;
import org.smoothbuild.db.object.type.base.TypeHV;

public class IfFunctionTypeH extends FunctionTypeH {
  public IfFunctionTypeH(Hash hash, TypeHV result, TupleTypeH paramsTuple) {
    super(hash, IF_FUNCTION, result, paramsTuple);
  }

  @Override
  public IfFunctionH newObj(MerkleRoot merkleRoot, ObjectHDb objectHDb) {
    return (IfFunctionH) super.newObj(merkleRoot, objectHDb);
  }
}

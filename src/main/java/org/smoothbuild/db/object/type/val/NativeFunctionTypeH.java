package org.smoothbuild.db.object.type.val;

import static org.smoothbuild.db.object.type.base.TypeKindH.NATIVE_FUNCTION;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.val.NativeFunctionH;
import org.smoothbuild.db.object.type.base.TypeHV;

public class NativeFunctionTypeH extends FunctionTypeH {
  public NativeFunctionTypeH(Hash hash, TypeHV result, TupleTypeH paramsTuple) {
    super(hash, NATIVE_FUNCTION, result, paramsTuple);
  }

  @Override
  public NativeFunctionH newObj(MerkleRoot merkleRoot, ObjectHDb objectHDb) {
    return (NativeFunctionH) super.newObj(merkleRoot, objectHDb);
  }
}

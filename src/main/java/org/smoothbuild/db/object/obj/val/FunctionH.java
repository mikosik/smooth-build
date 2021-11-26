package org.smoothbuild.db.object.obj.val;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.obj.base.EvaluableH;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.ValueH;
import org.smoothbuild.db.object.type.base.TypeKindH;
import org.smoothbuild.db.object.type.val.FunctionTypeH;

public abstract class FunctionH extends ValueH implements EvaluableH {
  public FunctionH(MerkleRoot merkleRoot, ObjectHDb objectHDb, TypeKindH kind) {
    super(merkleRoot, objectHDb);
    checkType(merkleRoot, kind);
  }

  protected void checkType(MerkleRoot merkleRoot, TypeKindH kind) {
    checkArgument(merkleRoot.type() instanceof FunctionTypeH functionTypeH
            && functionTypeH.kind().equals(kind));
  }

  @Override
  public FunctionTypeH type() {
    return (FunctionTypeH) super.type();
  }
}

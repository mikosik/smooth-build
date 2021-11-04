package org.smoothbuild.db.object.type.val;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.db.object.type.base.ObjKind.BLOB;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.val.Blob;
import org.smoothbuild.db.object.type.base.ValType;
import org.smoothbuild.lang.base.type.api.BlobType;
import org.smoothbuild.lang.base.type.api.TypeNames;

/**
 * This class is immutable.
 */
public class BlobOType extends ValType implements BlobType {
  public BlobOType(Hash hash) {
    super(TypeNames.BLOB, hash, BLOB);
  }

  @Override
  public Blob newObj(MerkleRoot merkleRoot, ObjDb objDb) {
    checkArgument(this.equals(merkleRoot.type()));
    return new Blob(merkleRoot, objDb);
  }
}

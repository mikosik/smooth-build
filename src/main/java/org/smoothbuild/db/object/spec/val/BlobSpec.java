package org.smoothbuild.db.object.spec.val;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.db.object.spec.base.SpecKind.BLOB;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.val.Blob;
import org.smoothbuild.db.object.spec.base.ValSpec;

/**
 * This class is immutable.
 */
public class BlobSpec extends ValSpec {
  public BlobSpec(Hash hash) {
    super(hash, BLOB);
  }

  @Override
  public Blob newObj(MerkleRoot merkleRoot, ObjectDb objectDb) {
    checkArgument(this.equals(merkleRoot.spec()));
    return new Blob(merkleRoot, objectDb);
  }
}

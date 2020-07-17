package org.smoothbuild.record.type;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.record.base.Bool;
import org.smoothbuild.record.base.MerkleRoot;
import org.smoothbuild.record.db.ObjectDb;

/**
 * This class is immutable.
 */
public class BoolType extends BinaryType {
  public BoolType(MerkleRoot merkleRoot, HashedDb hashedDb, ObjectDb objectDb) {
    super(merkleRoot, TypeKind.BOOL, hashedDb, objectDb);
  }

  @Override
  public Bool newJObject(MerkleRoot merkleRoot) {
    checkArgument(this.equals(merkleRoot.type()));
    return new Bool(merkleRoot, hashedDb);
  }
}

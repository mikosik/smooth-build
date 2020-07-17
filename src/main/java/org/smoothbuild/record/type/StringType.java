package org.smoothbuild.record.type;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.record.base.MerkleRoot;
import org.smoothbuild.record.base.SString;
import org.smoothbuild.record.db.ObjectDb;

/**
 * This class is immutable.
 */
public class StringType extends BinaryType {
  public StringType(MerkleRoot merkleRoot, HashedDb hashedDb, ObjectDb objectDb) {
    super(merkleRoot, TypeKind.STRING, hashedDb, objectDb);
  }

  @Override
  public SString newJObject(MerkleRoot merkleRoot) {
    checkArgument(this.equals(merkleRoot.type()));
    return new SString(merkleRoot, hashedDb);
  }
}

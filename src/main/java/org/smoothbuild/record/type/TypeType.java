package org.smoothbuild.record.type;

import static org.smoothbuild.record.type.TypeKind.TYPE;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.record.base.MerkleRoot;
import org.smoothbuild.record.db.ObjectDb;

/**
 * This class is immutable.
 */
public class TypeType extends BinaryType {
  public TypeType(MerkleRoot merkleRoot, HashedDb hashedDb, ObjectDb objectDb) {
    super(merkleRoot, TYPE, hashedDb, objectDb);
  }

  @Override
  public BinaryType type() {
    return this;
  }

  @Override
  public BinaryType newJObject(MerkleRoot merkleRoot) {
    return objectDb.getType(merkleRoot);
  }
}

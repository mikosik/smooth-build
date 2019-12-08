package org.smoothbuild.lang.object.type;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.lang.object.type.TypeNames.BOOL;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.object.base.Bool;
import org.smoothbuild.lang.object.base.MerkleRoot;
import org.smoothbuild.lang.object.db.ObjectDb;

public class BoolType extends ConcreteType {
  public BoolType(MerkleRoot merkleRoot, HashedDb hashedDb, ObjectDb objectDb) {
    super(merkleRoot, null, BOOL, Bool.class, hashedDb, objectDb);
  }

  @Override
  public Bool newObject(MerkleRoot merkleRoot) {
    checkArgument(this.equals(merkleRoot.type()));
    return new Bool(merkleRoot, hashedDb);
  }
}

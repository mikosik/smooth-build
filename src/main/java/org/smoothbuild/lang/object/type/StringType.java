package org.smoothbuild.lang.object.type;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.lang.object.type.TypeNames.STRING;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.object.base.MerkleRoot;
import org.smoothbuild.lang.object.base.SString;
import org.smoothbuild.lang.object.db.ObjectDb;

public class StringType extends ConcreteType {
  public StringType(MerkleRoot merkleRoot, HashedDb hashedDb, ObjectDb objectDb) {
    super(merkleRoot, null, STRING, SString.class, hashedDb, objectDb);
  }

  @Override
  public SString newObject(MerkleRoot merkleRoot) {
    checkArgument(this.equals(merkleRoot.type()));
    return new SString(merkleRoot, hashedDb);
  }
}

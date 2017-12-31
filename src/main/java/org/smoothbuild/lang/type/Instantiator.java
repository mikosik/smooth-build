package org.smoothbuild.lang.type;

import java.util.List;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.values.CorruptedValueException;
import org.smoothbuild.lang.value.Value;

import com.google.common.hash.HashCode;

public class Instantiator {
  private final HashedDb hashedDb;
  private final TypesDb typesDb;

  public Instantiator(HashedDb hashedDb, TypesDb typesDb) {
    this.hashedDb = hashedDb;
    this.typesDb = typesDb;
  }

  public Value instantiate(HashCode hash) {
    List<HashCode> hashes = hashedDb.readHashes(hash);
    switch (hashes.size()) {
      case 1:
        // If Merkle Tree root has only one child then it must
        // be Type("Type") smooth value. Let TypesDb handle and verify it.
        return typesDb.read(hash);
      case 2:
        Type type = typesDb.read(hashes.get(0));
        return type.newValue(hashes.get(1));
      default:
        throw new CorruptedValueException(
            hash, "Its merkle tree root has " + hashes.size() + " children");
    }
  }
}

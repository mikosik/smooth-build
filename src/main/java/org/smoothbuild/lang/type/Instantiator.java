package org.smoothbuild.lang.type;

import static org.smoothbuild.db.values.ValuesDbException.corruptedValueException;
import static org.smoothbuild.db.values.ValuesDbException.ioException;

import java.io.EOFException;
import java.io.IOException;
import java.util.List;

import org.smoothbuild.db.hashed.HashedDb;
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
    List<HashCode> hashes = readHashes(hash);
    switch (hashes.size()) {
      case 1:
        // If Merkle tree root has only one child then it must
        // be Type("Type") smooth value. Let TypesDb handle and verify it.
        return typesDb.read(hash);
      case 2:
        ConcreteType type = typesDb.read(hashes.get(0));
        return type.newValue(hashes.get(1));
      default:
        throw corruptedValueException(
            hash, "Its Merkle tree root has " + hashes.size() + " children");
    }
  }

  private List<HashCode> readHashes(HashCode hash) {
    try {
      return hashedDb.readHashes(hash);
    } catch (EOFException e) {
      throw corruptedValueException(hash,
          "Its Merkle tree root is hash of byte sequence which size is not multiple of hash size.");
    } catch (IOException e) {
      throw ioException(e);
    }
  }
}

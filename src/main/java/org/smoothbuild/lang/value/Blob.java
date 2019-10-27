package org.smoothbuild.lang.value;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.IOException;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.type.ConcreteType;

import okio.BufferedSource;

public class Blob extends AbstractValue {
  public Blob(Hash dataHash, ConcreteType type, HashedDb hashedDb) {
    super(dataHash, type, hashedDb);
    checkArgument(type.name().equals("Blob"));
  }

  public BufferedSource source() throws IOException {
    return hashedDb.source(dataHash());
  }
}

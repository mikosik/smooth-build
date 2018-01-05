package org.smoothbuild.lang.value;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.InputStream;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.type.Type;

import com.google.common.hash.HashCode;

public class Blob extends Value {
  public Blob(HashCode dataHash, Type type, HashedDb hashedDb) {
    super(dataHash, type, hashedDb);
    checkArgument(type.name().equals("Blob"));
  }

  public InputStream openInputStream() {
    return hashedDb.newUnmarshaller(dataHash());
  }
}

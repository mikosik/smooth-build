package org.smoothbuild.db.objects.marshal;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.objects.base.StringObject;
import org.smoothbuild.lang.base.SString;

import com.google.common.hash.HashCode;

public class StringReader implements ObjectReader<SString> {
  private final HashedDb hashedDb;

  public StringReader(HashedDb hashedDb) {
    this.hashedDb = hashedDb;
  }

  @Override
  public SString read(HashCode hash) {
    return new StringObject(hashedDb, hash);
  }
}
package org.smoothbuild.lang.value;

import static org.smoothbuild.lang.type.Types.FILE;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.Marshaller;
import org.smoothbuild.db.hashed.Unmarshaller;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.hash.HashCode;

public class SFile extends Value {
  private final ImmutableMap<String, Value> fields;

  public SFile(HashCode hash, HashedDb hashedDb) {
    super(FILE, hash);
    try (Unmarshaller unmarshaller = hashedDb.newUnmarshaller(hash)) {
      Builder<String, Value> builder = ImmutableMap.builder();
      builder.put("content", new Blob(unmarshaller.readHash(), hashedDb));
      builder.put("path", new SString(unmarshaller.readHash(), hashedDb));
      fields = builder.build();
    }
  }

  public static SFile storeFileInDb(SString path, Blob content, HashedDb hashedDb) {
    Marshaller marshaller = hashedDb.newMarshaller();
    marshaller.writeHash(content.hash());
    marshaller.writeHash(path.hash());
    marshaller.close();
    return new SFile(marshaller.hash(), hashedDb);
  }

  public SString path() {
    return (SString) fields.get("path");
  }

  public Blob content() {
    return (Blob) fields.get("content");
  }

  @Override
  public String toString() {
    return "File(" + path().toString() + " " + content().toString() + ")";
  }
}

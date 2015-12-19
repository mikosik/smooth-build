package org.smoothbuild.lang.value;

import static org.smoothbuild.lang.type.Types.FILE;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.Marshaller;
import org.smoothbuild.db.hashed.Unmarshaller;

import com.google.common.hash.HashCode;

public class SFile extends Value {
  private final SString path;
  private final Blob content;

  public SFile(HashCode hash, HashedDb hashedDb) {
    super(FILE, hash);
    try (Unmarshaller unmarshaller = new Unmarshaller(hashedDb, hash)) {
      this.path = new SString(unmarshaller.readHash(), hashedDb);
      this.content = new Blob(unmarshaller.readHash(), hashedDb);
    }
  }

  public static SFile storeFileInDb(SString path, Blob content, HashedDb hashedDb) {
    Marshaller marshaller = new Marshaller(hashedDb);
    marshaller.write(path.hash());
    marshaller.write(content.hash());
    HashCode hash = marshaller.close();
    return new SFile(hash, hashedDb);
  }

  public SString path() {
    return path;
  }

  public Blob content() {
    return content;
  }

  @Override
  public String toString() {
    return "File(" + path + " " + content.toString() + ")";
  }
}

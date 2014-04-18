package org.smoothbuild.db.objects.marshal;

import static org.smoothbuild.SmoothContants.CHARSET;
import static org.smoothbuild.util.Streams.inputStreamToString;

import java.io.IOException;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.err.ReadingHashedObjectFailedError;
import org.smoothbuild.db.objects.base.StringObject;
import org.smoothbuild.lang.base.SString;

import com.google.common.hash.HashCode;

public class StringMarshaller implements ObjectMarshaller<SString> {
  private final HashedDb hashedDb;

  public StringMarshaller(HashedDb hashedDb) {
    this.hashedDb = hashedDb;
  }

  public SString write(String string) {
    byte[] bytes = string.getBytes(CHARSET);
    HashCode hash = hashedDb.write(bytes);
    return new StringObject(hash, this);
  }

  @Override
  public SString read(HashCode hash) {
    return new StringObject(hash, this);
  }

  public String readValue(HashCode hash) {
    try {
      return inputStreamToString(hashedDb.openInputStream(hash));
    } catch (IOException e) {
      throw new ReadingHashedObjectFailedError(hash, e);
    }
  }
}

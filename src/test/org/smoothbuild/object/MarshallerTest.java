package org.smoothbuild.object;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.smoothbuild.fs.base.Path.path;

import org.junit.Test;
import org.smoothbuild.fs.base.Path;

import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.primitives.Bytes;
import com.google.common.primitives.Ints;

public class MarshallerTest {
  HashedDb hashedDb = mock(HashedDb.class);
  private Marshaller marshaller;

  @Test
  public void storing_single_hash() {
    HashCode hashCode = HashCode.fromInt(33);

    marshaller = new Marshaller(hashedDb);
    marshaller.addHash(hashCode);
    marshaller.store();

    verify(hashedDb).store(hashToBytes(hashCode));
  }

  @Test
  public void storing_single_path() {
    Path path = path("my/path");

    marshaller = new Marshaller(hashedDb);
    marshaller.addPath(path);
    marshaller.store();

    verify(hashedDb).store(pathToBytes(path));
  }

  @Test
  public void storing_int() throws Exception {
    byte[] bytes = new byte[] { 0x12, 0x34, 0x56, 0x78 };

    marshaller = new Marshaller(hashedDb);
    marshaller.addInt(Ints.fromByteArray(bytes));
    marshaller.store();

    verify(hashedDb).store(bytes);
  }

  @Test
  public void storing_int_at_given_hash() throws Exception {
    byte[] bytes = new byte[] { 0x12, 0x34, 0x56, 0x78 };
    HashCode hash = HashCode.fromInt(33);

    marshaller = new Marshaller(hashedDb);
    marshaller.addInt(Ints.fromByteArray(bytes));
    marshaller.store(hash);

    verify(hashedDb).store(hash, bytes);
  }

  private static byte[] pathToBytes(Path path) {
    byte[] sizeBytes = Ints.toByteArray(path.value().length());
    byte[] charBytes = path.value().getBytes(Charsets.UTF_8);
    byte[] bytes = Bytes.concat(sizeBytes, charBytes);
    return bytes;
  }

  private static byte[] hashToBytes(HashCode hashCode) {
    return hashCode.asBytes();
  }
}

package org.smoothbuild.db;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.fs.base.Path.path;

import org.junit.Test;
import org.smoothbuild.fs.base.Path;

import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.primitives.Bytes;
import com.google.common.primitives.Ints;

public class MarshallerTest {
  private Marshaller marshaller;

  @Test
  public void marshalling_single_hash() {
    HashCode hashCode = HashCode.fromInt(33);

    marshaller = new Marshaller();
    marshaller.addHash(hashCode);

    assertThat(marshaller.getBytes()).isEqualTo(hashToBytes(hashCode));
  }

  @Test
  public void marshalling_single_path() {
    Path path = path("my/path");

    marshaller = new Marshaller();
    marshaller.addPath(path);

    assertThat(marshaller.getBytes()).isEqualTo(pathToBytes(path));
  }

  @Test
  public void marshalling_byte() throws Exception {
    marshaller = new Marshaller();
    marshaller.addByte((byte) 123);

    assertThat(marshaller.getBytes()).isEqualTo(new byte[] { 123 });
  }

  @Test
  public void marshalling_int() throws Exception {
    byte[] bytes = new byte[] { 0x12, 0x34, 0x56, 0x78 };

    marshaller = new Marshaller();
    marshaller.addInt(Ints.fromByteArray(bytes));

    assertThat(marshaller.getBytes()).isEqualTo(bytes);
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

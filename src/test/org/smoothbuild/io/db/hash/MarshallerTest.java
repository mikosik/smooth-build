package org.smoothbuild.io.db.hash;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.command.SmoothContants.CHARSET;
import static org.smoothbuild.io.db.hash.HashedDb.FALSE_AS_BYTE;
import static org.smoothbuild.io.db.hash.HashedDb.TRUE_AS_BYTE;
import static org.smoothbuild.io.fs.base.Path.path;

import org.junit.Test;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.function.value.Hashed;
import org.smoothbuild.testing.lang.function.value.FakeHashed;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.HashCode;
import com.google.common.primitives.Bytes;
import com.google.common.primitives.Ints;

public class MarshallerTest {
  private Marshaller marshaller;

  @Test
  public void marshalling_list_of_hashed_objects() throws Exception {
    byte[] hash1 = new byte[] { 1, 2, 3 };
    Hashed hashed1 = new FakeHashed(hash1);
    byte[] hash2 = new byte[] { 5, 6, 7 };
    Hashed hashed2 = new FakeHashed(hash2);

    marshaller = new Marshaller();
    marshaller.write(ImmutableList.of(hashed1, hashed2));
    assertThat(marshaller.getBytes()).isEqualTo(Bytes.concat(Ints.toByteArray(2), hash1, hash2));
  }

  @Test
  public void marshalling_single_path() {
    Path path = path("my/path");

    marshaller = new Marshaller();
    marshaller.write(path);

    assertThat(marshaller.getBytes()).isEqualTo(pathToBytes(path));
  }

  @Test
  public void marshalling_single_string() {
    String string = "some string";

    marshaller = new Marshaller();
    marshaller.write(string);

    assertThat(marshaller.getBytes()).isEqualTo(stringToBytes(string));
  }

  @Test
  public void marshalling_single_hash() {
    HashCode hashCode = HashCode.fromInt(33);

    marshaller = new Marshaller();
    marshaller.write(hashCode);

    assertThat(marshaller.getBytes()).isEqualTo(hashToBytes(hashCode));
  }

  @Test
  public void marshalling_byte() throws Exception {
    marshaller = new Marshaller();
    marshaller.write((byte) 123);

    assertThat(marshaller.getBytes()).isEqualTo(new byte[] { 123 });
  }

  @Test
  public void marshalling_true_boolean() throws Exception {
    marshaller = new Marshaller();
    marshaller.write(true);

    assertThat(marshaller.getBytes()).isEqualTo(new byte[] { TRUE_AS_BYTE });
  }

  @Test
  public void marshalling_false_boolean() throws Exception {
    marshaller = new Marshaller();
    marshaller.write(false);

    assertThat(marshaller.getBytes()).isEqualTo(new byte[] { FALSE_AS_BYTE });
  }

  @Test
  public void marshalling_int() throws Exception {
    byte[] bytes = new byte[] { 0x12, 0x34, 0x56, 0x78 };

    marshaller = new Marshaller();
    marshaller.write(Ints.fromByteArray(bytes));

    assertThat(marshaller.getBytes()).isEqualTo(bytes);
  }

  private static byte[] pathToBytes(Path path) {
    return stringToBytes(path.value());
  }

  private static byte[] stringToBytes(String string) {
    byte[] sizeBytes = Ints.toByteArray(string.length());
    byte[] charBytes = string.getBytes(CHARSET);
    return Bytes.concat(sizeBytes, charBytes);
  }

  private static byte[] hashToBytes(HashCode hashCode) {
    return hashCode.asBytes();
  }
}

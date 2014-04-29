package org.smoothbuild.db.hashed;

import static com.google.common.primitives.Ints.toByteArray;
import static org.smoothbuild.SmoothContants.CHARSET;
import static org.smoothbuild.db.hashed.Constants.FALSE_AS_BYTE;
import static org.smoothbuild.db.hashed.Constants.TRUE_AS_BYTE;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.base.Hashed;
import org.smoothbuild.testing.db.objects.FakeObjectsDb;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.HashCode;
import com.google.common.primitives.Bytes;
import com.google.common.primitives.Ints;

public class MarshallerTest {
  private final FakeObjectsDb objectsDb = new FakeObjectsDb();
  private Marshaller marshaller;
  private Hashed hashed1;
  private Hashed hashed2;
  private final Path path = path("my/path");
  private String string;
  private HashCode hashCode;

  @Test
  public void marshalling_list_of_hashed_objects() throws Exception {
    given(hashed1 = objectsDb.string("abc"));
    given(hashed2 = objectsDb.string("def"));
    given(marshaller = new Marshaller());
    given(marshaller).write(ImmutableList.of(hashed1, hashed2));
    when(marshaller).getBytes();
    thenReturned(Bytes.concat(toByteArray(2), hashed1.hash().asBytes(), hashed2.hash().asBytes()));
  }

  @Test
  public void marshalling_single_path() {
    given(marshaller = new Marshaller());
    given(marshaller).write(path);
    when(marshaller).getBytes();
    thenReturned(pathToBytes(path));
  }

  @Test
  public void marshalling_single_string() {
    given(marshaller = new Marshaller());
    given(string = "some string");
    given(marshaller).write(string);
    when(marshaller).getBytes();
    thenReturned(stringToBytes(string));
  }

  @Test
  public void marshalling_single_hash() {
    given(hashCode = HashCode.fromInt(33));
    given(marshaller = new Marshaller());
    given(marshaller).write(hashCode);
    when(marshaller).getBytes();
    thenReturned(hashCode.asBytes());
  }

  @Test
  public void marshalling_byte() throws Exception {
    given(marshaller = new Marshaller());
    given(marshaller).write((byte) 123);
    when(marshaller).getBytes();
    thenReturned(new byte[] { 123 });
  }

  @Test
  public void marshalling_true_boolean() throws Exception {
    given(marshaller = new Marshaller());
    given(marshaller).write(true);
    when(marshaller).getBytes();
    thenReturned(new byte[] { TRUE_AS_BYTE });
  }

  @Test
  public void marshalling_false_boolean() throws Exception {
    given(marshaller = new Marshaller());
    given(marshaller).write(false);
    when(marshaller).getBytes();
    thenReturned(new byte[] { FALSE_AS_BYTE });
  }

  @Test
  public void marshalling_ints() throws Exception {
    given(marshaller = new Marshaller());
    given(marshaller).write(0x12345678);
    when(marshaller).getBytes();
    thenReturned(Ints.toByteArray(0x12345678));
  }

  private static byte[] pathToBytes(Path path) {
    return stringToBytes(path.value());
  }

  private static byte[] stringToBytes(String string) {
    byte[] sizeBytes = Ints.toByteArray(string.length());
    byte[] charBytes = string.getBytes(CHARSET);
    return Bytes.concat(sizeBytes, charBytes);
  }
}

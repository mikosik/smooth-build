package org.smoothbuild.db.hashed;

import static com.google.common.primitives.Ints.toByteArray;
import static java.util.Arrays.asList;
import static org.smoothbuild.SmoothConstants.CHARSET;
import static org.smoothbuild.db.hashed.Constants.FALSE_AS_BYTE;
import static org.smoothbuild.db.hashed.Constants.TRUE_AS_BYTE;
import static org.smoothbuild.db.values.ValuesDb.memoryValuesDb;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.value.Value;

import com.google.common.hash.HashCode;
import com.google.common.primitives.Bytes;
import com.google.common.primitives.Ints;

public class MarshallerTest {
  private final ValuesDb valuesDb = memoryValuesDb();
  private Marshaller marshaller;
  private Value value1;
  private Value value2;
  private String string;
  private HashCode hashCode;

  @Test
  public void marshalling_list_of_hashed_objects() throws Exception {
    given(value1 = valuesDb.string("abc"));
    given(value2 = valuesDb.string("def"));
    given(marshaller = new Marshaller());
    given(marshaller).write(asList(value1, value2));
    when(marshaller).getBytes();
    thenReturned(Bytes.concat(toByteArray(2), value1.hash().asBytes(), value2.hash().asBytes()));
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

  private static byte[] stringToBytes(String string) {
    byte[] sizeBytes = Ints.toByteArray(string.length());
    byte[] charBytes = string.getBytes(CHARSET);
    return Bytes.concat(sizeBytes, charBytes);
  }
}

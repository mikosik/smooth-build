package org.smoothbuild.db.hashed;

import static org.smoothbuild.db.hashed.Constants.FALSE_AS_BYTE;
import static org.smoothbuild.db.hashed.Constants.TRUE_AS_BYTE;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;

import com.google.common.hash.HashCode;
import com.google.common.primitives.Ints;

public class MarshallerTest {
  private Marshaller marshaller;
  private HashCode hashCode;

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
}

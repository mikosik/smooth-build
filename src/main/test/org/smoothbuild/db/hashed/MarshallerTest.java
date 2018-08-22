package org.smoothbuild.db.hashed;

import static okio.Okio.sink;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.io.ByteArrayOutputStream;

import org.junit.Test;

import com.google.common.base.Supplier;

public class MarshallerTest {
  private Marshaller marshaller;

  @Test
  public void getting_hash_when_sink_is_not_closed_causes_exception() throws Exception {
    given(marshaller = new Marshaller(sink(new ByteArrayOutputStream()), mock(Supplier.class)));
    when(() -> marshaller.hash());
    thenThrown(IllegalStateException.class);
  }
}

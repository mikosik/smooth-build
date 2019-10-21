package org.smoothbuild.lang.value;

import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.db.values.TestingValuesDb;
import org.smoothbuild.lang.runtime.TestingRuntimeTypes;
import org.smoothbuild.lang.type.TestingTypesDb;

import okio.ByteString;

public class ValueFactoryTest {
  private final ValueFactory valueFactory = new ValueFactory(
      new TestingRuntimeTypes(new TestingTypesDb()), new TestingValuesDb());

  private final ByteString bytes = ByteString.encodeUtf8("aaa");

  @Test
  public void blob_data_can_be_read_back() throws Exception {
    when(valueFactory.blob(sink -> sink.write(bytes)).source().readByteString());
    thenReturned(bytes);
  }
}

package org.smoothbuild.lang.plugin;

import static org.mockito.Mockito.mock;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.smoothbuild.io.cache.value.CachedBlob;
import org.smoothbuild.io.cache.value.ValueDb;

public class BlobBuilderTest {
  ValueDb valueDb = mock(ValueDb.class);
  BlobBuilder blobBuilder = new BlobBuilder(valueDb);
  byte[] bytes = new byte[] { 1, 2, 3 };
  CachedBlob blob = Mockito.mock(CachedBlob.class);

  @Test
  public void opening_output_stream_twice_fails() throws Exception {
    given(blobBuilder).openOutputStream();
    when(blobBuilder).openOutputStream();
    thenThrown(IllegalStateException.class);
  }

  @Test
  public void build_fails_when_no_content_was_provided() {
    when(blobBuilder).build();
    thenThrown(IllegalStateException.class);
  }

  @Test
  public void build_returns_blob_stored_in_object_db_with_empty_content() throws Exception {
    BDDMockito.given(valueDb.writeBlob(new byte[] {})).willReturn(blob);
    given(blobBuilder).openOutputStream();
    when(blobBuilder).build();
    thenReturned(blob);
  }

  @Test
  public void build_returns_blob_stored_in_object_db() throws Exception {
    BDDMockito.given(valueDb.writeBlob(bytes)).willReturn(blob);
    given(blobBuilder.openOutputStream()).write(bytes);
    when(blobBuilder).build();
    thenReturned(blob);
  }
}

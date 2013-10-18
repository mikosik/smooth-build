package org.smoothbuild.object;

import static org.mockito.Mockito.mock;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import org.mockito.BDDMockito;
import org.testory.common.Closure;

import com.google.common.hash.HashCode;

public class BlobObjectTest {
  String content = "content";
  InputStream inputStream = mock(InputStream.class);
  HashedDb hashedDb = mock(HashedDb.class);
  HashCode hash = HashCode.fromInt(1);

  BlobObject blobObject;

  @Test
  public void null_hash_is_forbidden() throws Exception {
    when(blobObject(hashedDb, null));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void null_file_system_is_forbidden() throws Exception {
    when(blobObject(null, hash));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void hash() {
    given(blobObject = new BlobObject(hashedDb, hash));
    when(blobObject.hash());
    thenReturned(hash);
  }

  @Test
  public void open_input_stream_reads_from_file_on_file_system_with_name_equal_to_blob_hash()
      throws IOException {
    BDDMockito.given(hashedDb.openInputStream(hash)).willReturn(inputStream);
    given(blobObject = new BlobObject(hashedDb, hash));
    when(blobObject.openInputStream());
    thenReturned(inputStream);
  }

  private static Closure blobObject(final HashedDb hashedDb, final HashCode hash) {
    return new Closure() {
      @Override
      public Object invoke() throws Throwable {
        return new BlobObject(hashedDb, hash);
      }
    };
  }
}

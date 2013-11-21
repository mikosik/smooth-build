package org.smoothbuild.io.cache.value;

import static org.mockito.Mockito.mock;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import org.mockito.BDDMockito;
import org.smoothbuild.io.cache.hash.HashedDb;
import org.smoothbuild.lang.type.Type;
import org.testory.common.Closure;

import com.google.common.hash.HashCode;

public class CachedBlobTest {
  String content = "content";
  InputStream inputStream = mock(InputStream.class);
  HashedDb hashedDb = mock(HashedDb.class);
  HashCode hash = HashCode.fromInt(1);

  CachedBlob cachedBlob;

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
  public void type() throws Exception {
    given(cachedBlob = new CachedBlob(hashedDb, hash));
    when(cachedBlob.type());
    thenReturned(Type.BLOB);
  }

  @Test
  public void hash() {
    given(cachedBlob = new CachedBlob(hashedDb, hash));
    when(cachedBlob.hash());
    thenReturned(hash);
  }

  @Test
  public void open_input_stream_calls_hashed_db_open_input_stream() throws IOException {
    BDDMockito.given(hashedDb.openInputStream(hash)).willReturn(inputStream);
    given(cachedBlob = new CachedBlob(hashedDb, hash));
    when(cachedBlob.openInputStream());
    thenReturned(inputStream);
  }

  private static Closure blobObject(final HashedDb hashedDb, final HashCode hash) {
    return new Closure() {
      @Override
      public Object invoke() throws Throwable {
        return new CachedBlob(hashedDb, hash);
      }
    };
  }
}

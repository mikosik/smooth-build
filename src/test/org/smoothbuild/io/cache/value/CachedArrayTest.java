package org.smoothbuild.io.cache.value;

import static org.mockito.Mockito.mock;
import static org.smoothbuild.lang.type.Type.FILE;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.lang.type.SBlob;
import org.smoothbuild.lang.type.SFile;
import org.smoothbuild.testing.io.cache.value.FakeValueDb;
import org.testory.common.Closure;

import com.google.common.hash.HashCode;

public class CachedArrayTest {
  ValueDb valueDb = new FakeValueDb();
  HashCode hash = HashCode.fromInt(33);
  SBlob blob = mock(SBlob.class);

  CachedArray<SFile> cachedFileArray;

  @Test
  public void null_value_db_is_forbidden() {
    when(newCachedArray(null, hash));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void null_hash_is_forbidden() {
    when(newCachedArray(valueDb, null));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void type() throws Exception {
    given(cachedFileArray = cachedArray(valueDb, hash));
    when(cachedFileArray.type());
    thenReturned(FILE);
  }

  @Test
  public void hash_passed_to_constructor_is_returned_from_hash_method() throws Exception {
    given(cachedFileArray = cachedArray(valueDb, hash));
    when(cachedFileArray.hash());
    thenReturned(hash);
  }

  private static Closure newCachedArray(final ValueDb valueDb, final HashCode hash) {
    return new Closure() {
      @Override
      public Object invoke() throws Throwable {
        return cachedArray(valueDb, hash);
      }
    };
  }

  private static CachedArray<SFile> cachedArray(ValueDb valueDb, HashCode hash) {
    return new CachedArray<SFile>(valueDb, hash, FILE, valueDb.fileReader());
  }
}

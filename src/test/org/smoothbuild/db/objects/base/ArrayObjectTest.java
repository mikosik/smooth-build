package org.smoothbuild.db.objects.base;

import static org.smoothbuild.lang.base.STypes.STRING;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.objects.marshal.ObjectReader;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.testing.io.fs.base.FakeFileSystem;
import org.testory.Closure;

import com.google.common.hash.HashCode;

public class ArrayObjectTest {
  private final HashedDb hashedDb = new HashedDb(new FakeFileSystem());
  private final HashCode hash = HashCode.fromInt(33);
  private ArrayObject<SString> arrayObject;

  @Test
  public void null_value_db_is_forbidden() {
    when(newCachedArray(null, hash));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void null_hash_is_forbidden() {
    when(newCachedArray(hashedDb, null));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void type() throws Exception {
    given(arrayObject = arrayObject(hashedDb, hash));
    when(arrayObject.type());
    thenReturned(STRING);
  }

  @Test
  public void hash_passed_to_constructor_is_returned_from_hash_method() throws Exception {
    given(arrayObject = arrayObject(hashedDb, hash));
    when(arrayObject.hash());
    thenReturned(hash);
  }

  private Closure newCachedArray(final HashedDb hashedDb, final HashCode hash) {
    return new Closure() {
      @Override
      public Object invoke() throws Throwable {
        return arrayObject(hashedDb, hash);
      }
    };
  }

  private static ArrayObject<SString> arrayObject(HashedDb hashedDb, HashCode hash) {
    @SuppressWarnings("unchecked")
    ObjectReader<SString> elementReader = mock(ObjectReader.class);
    return new ArrayObject<SString>(hashedDb, hash, STRING, elementReader);
  }
}

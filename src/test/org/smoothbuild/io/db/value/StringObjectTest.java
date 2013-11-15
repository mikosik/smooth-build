package org.smoothbuild.io.db.value;

import static org.mockito.Mockito.mock;
import static org.smoothbuild.command.SmoothContants.CHARSET;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import org.mockito.BDDMockito;
import org.smoothbuild.io.db.hash.HashedDb;
import org.smoothbuild.lang.function.base.Type;
import org.testory.common.Closure;

import com.google.common.hash.HashCode;

public class StringObjectTest {
  String content = "content";
  InputStream inputStream = mock(InputStream.class);
  HashedDb hashedDb = mock(HashedDb.class);
  HashCode hash = HashCode.fromInt(1);

  StringObject stringObject;

  @Test
  public void null_hash_is_forbidden() throws Exception {
    when(stringObject(hashedDb, null));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void null_file_system_is_forbidden() throws Exception {
    when(stringObject(null, hash));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void type() throws Exception {
    given(stringObject = new StringObject(hashedDb, hash));
    when(stringObject.type());
    thenReturned(Type.STRING);
  }

  @Test
  public void hash() {
    given(stringObject = new StringObject(hashedDb, hash));
    when(stringObject.hash());
    thenReturned(hash);
  }

  @Test
  public void open_input_stream_calls_hashed_db_open_input_stream() throws IOException {
    BDDMockito.given(hashedDb.openInputStream(hash)).willReturn(
        new ByteArrayInputStream(content.getBytes(CHARSET)));
    given(stringObject = new StringObject(hashedDb, hash));
    when(stringObject.value());
    thenReturned(content);
  }

  private static Closure stringObject(final HashedDb hashedDb, final HashCode hash) {
    return new Closure() {
      @Override
      public Object invoke() throws Throwable {
        return new StringObject(hashedDb, hash);
      }
    };
  }
}

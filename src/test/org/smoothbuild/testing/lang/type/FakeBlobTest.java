package org.smoothbuild.testing.lang.type;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.smoothbuild.lang.type.STypes.BLOB;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.testing.common.StreamTester;
import org.testory.common.Closure;

public class FakeBlobTest {
  byte[] data = new byte[] { 1, 2, 3 };
  byte[] data2 = new byte[] { 1, 2, 3, 4 };
  FakeBlob blob;

  @Test
  public void null_data_is_forbidden() throws Exception {
    when($fakeBlob(null));
    thenThrown(NullPointerException.class);
  }

  private Closure $fakeBlob(final byte[] data) {
    return new Closure() {
      @Override
      public Object invoke() throws Throwable {
        return new FakeBlob(data);
      }
    };
  }

  @Test
  public void type() throws Exception {
    given(blob = new FakeBlob(data));
    when(blob.type());
    thenReturned(BLOB);
  }

  @Test
  public void open_output_stream_returns_data_passed_to_constructor() throws Exception {
    given(blob = new FakeBlob(data));
    when(StreamTester.inputStreamToBytes(blob.openInputStream()));
    thenReturned(data);
  }

  @Test
  public void hash_of_blobs_with_data_content_is_different() throws Exception {
    given(blob = new FakeBlob(data));
    when(blob.hash());
    thenReturned(not(equalTo(new FakeBlob(data2).hash())));
  }
}

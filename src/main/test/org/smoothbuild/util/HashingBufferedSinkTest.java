package org.smoothbuild.util;

import static org.smoothbuild.SmoothConstants.CHARSET;
import static org.smoothbuild.testing.common.ExceptionMatcher.exception;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.db.hashed.Hash;

import okio.Buffer;

public class HashingBufferedSinkTest {
  private HashingBufferedSink sink;
  private Buffer buffer;

  @Before
  public void before() {
    given(buffer = new Buffer());
    given(sink = new HashingBufferedSink(buffer));
  }

  @Test
  public void all_data_is_written_to_source() {
    given(() -> sink.writeString("abcde", CHARSET));
    given(() -> sink.flush());
    when(() -> buffer.readString(CHARSET));
    thenReturned("abcde");
  }

  @Test
  public void hash_is_calculated() {
    given(() -> sink.writeString("abcde", CHARSET));
    given(() -> sink.close());
    when(() -> sink.hash());
    thenReturned(Hash.string("abcde"));
  }

  @Test
  public void getting_hash_when_sink_is_not_closed_causes_exception() {
    when(() -> sink.hash());
    thenThrown(exception(new IllegalStateException("HashingBufferedSink is not closed.")));
  }
}
package org.smoothbuild.exec.compute;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.exec.compute.ComputationCacheException.corruptedValueException;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.base.Array;
import org.smoothbuild.db.object.base.Blob;
import org.smoothbuild.db.object.base.Bool;
import org.smoothbuild.db.object.base.Obj;
import org.smoothbuild.db.object.base.Str;
import org.smoothbuild.db.object.base.Tuple;
import org.smoothbuild.db.object.spec.ArraySpec;
import org.smoothbuild.exec.base.Output;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.testing.TestingContext;

import okio.ByteString;

public class ComputationCacheTest extends TestingContext {
  private final Hash hash = Hash.of("abc");
  private final ByteString bytes = ByteString.encodeUtf8("abc");
  private Path path = path("file/path");

  private Array array;
  private Tuple file;
  private Blob blob;
  private Bool boolValue;
  private Str stringValue;
  private final String string = "some string";

  @Test
  public void cache_does_not_contain_not_written_result() throws Exception {
    assertThat(outputDb().contains(hash))
        .isFalse();
  }

  @Test
  public void cache_contains_written_result() throws Exception {
    outputDb().write(hash, new Output(string("result"), emptyMessageArray()));
    assertThat(outputDb().contains(hash))
        .isTrue();
  }

  @Test
  public void cache_is_corrupted_when_task_hash_points_to_directory() throws Exception {
    path = ComputationCache.toPath(hash);
    outputDbFileSystem().sink(path.appendPart("file"));

    assertCall(() -> outputDb().contains(hash))
        .throwsException(corruptedValueException(hash, path + " is directory not a file."));
  }

  @Test
  public void reading_not_written_value_fails() {
    assertCall(() -> outputDb().read(hash, stringSpec()))
        .throwsException(ComputationCacheException.class);
  }

  @Test
  public void written_messages_can_be_read_back() throws Exception {
    stringValue = string("abc");
    Obj message = errorMessage("error message");
    Array messages = array(message);
    outputDb().write(hash, new Output(stringValue, messages));

    assertThat(outputDb().read(hash, stringSpec()).messages())
        .isEqualTo(messages);
  }

  @Test
  public void written_file_array_can_be_read_back() throws Exception {
    file = file(path, bytes);
    array = arrayBuilder(objectFactory().fileSpec()).add(file).build();
    outputDb().write(hash, new Output(array, emptyMessageArray()));
    ArraySpec arraySpec = arraySpec(objectFactory().fileSpec());

    assertThat(((Array) outputDb().read(hash, arraySpec).value()).asIterable(Tuple.class))
        .containsExactly(file);
  }

  @Test
  public void written_blob_array_can_be_read_back() throws Exception {
    blob = blob(bytes);
    array = arrayBuilder(blobSpec()).add(blob).build();
    outputDb().write(hash, new Output(array, emptyMessageArray()));
    ArraySpec arraySpec = arraySpec(blobSpec());

    assertThat(((Array) outputDb().read(hash, arraySpec).value()).asIterable(Blob.class))
        .containsExactly(blob);
  }

  @Test
  public void written_bool_array_can_be_read_back() throws Exception {
    boolValue = bool(true);
    array = arrayBuilder(boolSpec()).add(boolValue).build();
    outputDb().write(hash, new Output(array, emptyMessageArray()));
    ArraySpec arraySpec = arraySpec(boolSpec());

    assertThat(((Array) outputDb().read(hash, arraySpec).value()).asIterable(Bool.class))
        .containsExactly(boolValue);
  }

  @Test
  public void written_string_array_can_be_read_back() throws Exception {
    stringValue = string(string);
    array = arrayBuilder(stringSpec()).add(stringValue).build();
    outputDb().write(hash, new Output(array, emptyMessageArray()));
    ArraySpec arraySpec = arraySpec(stringSpec());

    assertThat(((Array) outputDb().read(hash, arraySpec).value()).asIterable(Str.class))
        .containsExactly(stringValue);
  }

  @Test
  public void written_file_can_be_read_back() throws Exception {
    file = file(path, bytes);
    outputDb().write(hash, new Output(file, emptyMessageArray()));

    assertThat(outputDb().read(hash, objectFactory().fileSpec()).value())
        .isEqualTo(file);
  }

  @Test
  public void written_blob_can_be_read_back() throws Exception {
    blob = blob(bytes);
    outputDb().write(hash, new Output(blob, emptyMessageArray()));

    assertThat(outputDb().read(hash, blobSpec()).value())
        .isEqualTo(blob);
  }

  @Test
  public void written_bool_can_be_read_back() throws Exception {
    boolValue = bool(true);
    outputDb().write(hash, new Output(boolValue, emptyMessageArray()));

    assertThat(((Bool) outputDb().read(hash, boolSpec()).value()).jValue())
        .isTrue();
  }

  @Test
  public void written_string_can_be_read_back() throws Exception {
    stringValue = string(string);
    outputDb().write(hash, new Output(stringValue, emptyMessageArray()));
    assertThat(((Str) outputDb().read(hash, stringSpec()).value()).jValue())
        .isEqualTo(string);
  }
}

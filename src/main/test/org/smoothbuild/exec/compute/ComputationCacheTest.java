package org.smoothbuild.exec.compute;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.exec.compute.ComputationCacheException.corruptedValueException;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.val.Array;
import org.smoothbuild.db.object.obj.val.Blob;
import org.smoothbuild.db.object.obj.val.Bool;
import org.smoothbuild.db.object.obj.val.Int;
import org.smoothbuild.db.object.obj.val.Str;
import org.smoothbuild.db.object.obj.val.Struc_;
import org.smoothbuild.exec.base.Output;
import org.smoothbuild.testing.TestingContextImpl;

import okio.ByteString;

public class ComputationCacheTest extends TestingContextImpl {
  private final Hash hash = Hash.of("abc");
  private final ByteString bytes = ByteString.encodeUtf8("abc");

  @Test
  public void cache_does_not_contain_not_written_result() throws Exception {
    assertThat(computationCache().contains(hash))
        .isFalse();
  }

  @Test
  public void cache_contains_written_result() throws Exception {
    computationCache().write(hash, new Output(string("result"), emptyMessageArray()));
    assertThat(computationCache().contains(hash))
        .isTrue();
  }

  @Test
  public void cache_is_corrupted_when_task_hash_points_to_directory() throws Exception {
    var path = ComputationCache.toPath(hash);
    computationCacheFileSystem().sink(path.appendPart("file"));

    assertCall(() -> computationCache().contains(hash))
        .throwsException(corruptedValueException(hash, path + " is directory not a file."));
  }

  @Test
  public void reading_not_written_value_fails() {
    assertCall(() -> computationCache().read(hash, stringSpec()))
        .throwsException(ComputationCacheException.class);
  }

  @Test
  public void written_messages_can_be_read_back() throws Exception {
    var strV = string("abc");
    var message = errorMessage("error message");
    var messages = array(message);
    computationCache().write(hash, new Output(strV, messages));

    assertThat(computationCache().read(hash, stringSpec()).messages())
        .isEqualTo(messages);
  }

  @Test
  public void written_file_array_can_be_read_back() throws Exception {
    var file = file(path("file/path"), bytes);
    computationCache().write(hash, new Output(array(file), emptyMessageArray()));
    var arrayType = arraySpec(objectFactory().fileType());

    assertThat(((Array) computationCache().read(hash, arrayType).value()).elements(Struc_.class))
        .containsExactly(file);
  }

  @Test
  public void written_blob_array_can_be_read_back() throws Exception {
    var blob = blob(bytes);
    computationCache().write(hash, new Output(array(blob), emptyMessageArray()));
    var arrayType = arraySpec(blobSpec());

    assertThat(((Array) computationCache().read(hash, arrayType).value()).elements(Blob.class))
        .containsExactly(blob);
  }

  @Test
  public void written_bool_array_can_be_read_back() throws Exception {
    var boolV = bool(true);
    computationCache().write(hash, new Output(array(boolV), emptyMessageArray()));
    var arrayType = arraySpec(boolSpec());

    assertThat(((Array) computationCache().read(hash, arrayType).value()).elements(Bool.class))
        .containsExactly(boolV);
  }

  @Test
  public void written_int_array_can_be_read_back() throws Exception {
    var intV = int_(123);
    computationCache().write(hash, new Output(array(intV), emptyMessageArray()));
    var arrayType = arraySpec(intSpec());

    assertThat(((Array) computationCache().read(hash, arrayType).value()).elements(Int.class))
        .containsExactly(intV);
  }

  @Test
  public void written_string_array_can_be_read_back() throws Exception {
    var strV = string("some string");
    var array = array(strV);
    computationCache().write(hash, new Output(array, emptyMessageArray()));
    var arrayType = arraySpec(stringSpec());

    assertThat(((Array) computationCache().read(hash, arrayType).value()).elements(Str.class))
        .containsExactly(strV);
  }

  @Test
  public void written_file_can_be_read_back() throws Exception {
    var file = file(path("file/path"), bytes);
    computationCache().write(hash, new Output(file, emptyMessageArray()));

    assertThat(computationCache().read(hash, objectFactory().fileType()).value())
        .isEqualTo(file);
  }

  @Test
  public void written_blob_can_be_read_back() throws Exception {
    var blob = blob(bytes);
    computationCache().write(hash, new Output(blob, emptyMessageArray()));

    assertThat(computationCache().read(hash, blobSpec()).value())
        .isEqualTo(blob);
  }

  @Test
  public void written_bool_can_be_read_back() throws Exception {
    var boolV = bool(true);
    computationCache().write(hash, new Output(boolV, emptyMessageArray()));

    assertThat(((Bool) computationCache().read(hash, boolSpec()).value()).jValue())
        .isTrue();
  }

  @Test
  public void written_int_can_be_read_back() throws Exception {
    var intV = int_(123);
    computationCache().write(hash, new Output(intV, emptyMessageArray()));

    assertThat(((Int) computationCache().read(hash, intSpec()).value()).jValue())
        .isEqualTo(BigInteger.valueOf(123));
  }

  @Test
  public void written_string_can_be_read_back() throws Exception {
    var string = "some string";
    var strV = string(string);
    computationCache().write(hash, new Output(strV, emptyMessageArray()));
    assertThat(((Str) computationCache().read(hash, stringSpec()).value()).jValue())
        .isEqualTo(string);
  }
}

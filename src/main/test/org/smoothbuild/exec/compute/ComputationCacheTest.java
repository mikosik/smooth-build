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
import org.smoothbuild.testing.TestingContext;

import okio.ByteString;

public class ComputationCacheTest extends TestingContext {
  private final Hash hash = Hash.of("abc");
  private final ByteString bytes = ByteString.encodeUtf8("abc");

  @Test
  public void cache_does_not_contain_not_written_result() throws Exception {
    assertThat(computationCache().contains(hash))
        .isFalse();
  }

  @Test
  public void cache_contains_written_result() throws Exception {
    computationCache().write(hash, new Output(string("result"), messageArrayEmtpy()));
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
    assertCall(() -> computationCache().read(hash, stringOT()))
        .throwsException(ComputationCacheException.class);
  }

  @Test
  public void written_messages_can_be_read_back() throws Exception {
    var strV = string("abc");
    var message = errorMessage("error message");
    var messages = array(message);
    computationCache().write(hash, new Output(strV, messages));

    assertThat(computationCache().read(hash, stringOT()).messages())
        .isEqualTo(messages);
  }

  @Test
  public void written_file_array_can_be_read_back() throws Exception {
    var file = file(path("file/path"), bytes);
    computationCache().write(hash, new Output(array(file), messageArrayEmtpy()));
    var arrayType = arrayOT(objectFactory().fileType());

    assertThat(((Array) computationCache().read(hash, arrayType).value()).elements(Struc_.class))
        .containsExactly(file);
  }

  @Test
  public void written_blob_array_can_be_read_back() throws Exception {
    var blob = blob(bytes);
    computationCache().write(hash, new Output(array(blob), messageArrayEmtpy()));
    var arrayType = arrayOT(blobOT());

    assertThat(((Array) computationCache().read(hash, arrayType).value()).elements(Blob.class))
        .containsExactly(blob);
  }

  @Test
  public void written_bool_array_can_be_read_back() throws Exception {
    var boolV = bool(true);
    computationCache().write(hash, new Output(array(boolV), messageArrayEmtpy()));
    var arrayType = arrayOT(boolOT());

    assertThat(((Array) computationCache().read(hash, arrayType).value()).elements(Bool.class))
        .containsExactly(boolV);
  }

  @Test
  public void written_int_array_can_be_read_back() throws Exception {
    var intV = int_(123);
    computationCache().write(hash, new Output(array(intV), messageArrayEmtpy()));
    var arrayType = arrayOT(intOT());

    assertThat(((Array) computationCache().read(hash, arrayType).value()).elements(Int.class))
        .containsExactly(intV);
  }

  @Test
  public void written_string_array_can_be_read_back() throws Exception {
    var strV = string("some string");
    var array = array(strV);
    computationCache().write(hash, new Output(array, messageArrayEmtpy()));
    var arrayType = arrayOT(stringOT());

    assertThat(((Array) computationCache().read(hash, arrayType).value()).elements(Str.class))
        .containsExactly(strV);
  }

  @Test
  public void written_file_can_be_read_back() throws Exception {
    var file = file(path("file/path"), bytes);
    computationCache().write(hash, new Output(file, messageArrayEmtpy()));

    assertThat(computationCache().read(hash, objectFactory().fileType()).value())
        .isEqualTo(file);
  }

  @Test
  public void written_blob_can_be_read_back() throws Exception {
    var blob = blob(bytes);
    computationCache().write(hash, new Output(blob, messageArrayEmtpy()));

    assertThat(computationCache().read(hash, blobOT()).value())
        .isEqualTo(blob);
  }

  @Test
  public void written_bool_can_be_read_back() throws Exception {
    var boolV = bool(true);
    computationCache().write(hash, new Output(boolV, messageArrayEmtpy()));

    assertThat(((Bool) computationCache().read(hash, boolOT()).value()).jValue())
        .isTrue();
  }

  @Test
  public void written_int_can_be_read_back() throws Exception {
    var intV = int_(123);
    computationCache().write(hash, new Output(intV, messageArrayEmtpy()));

    assertThat(((Int) computationCache().read(hash, intOT()).value()).jValue())
        .isEqualTo(BigInteger.valueOf(123));
  }

  @Test
  public void written_string_can_be_read_back() throws Exception {
    var string = "some string";
    var strV = string(string);
    computationCache().write(hash, new Output(strV, messageArrayEmtpy()));
    assertThat(((Str) computationCache().read(hash, stringOT()).value()).jValue())
        .isEqualTo(string);
  }
}

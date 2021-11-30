package org.smoothbuild.exec.compute;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.exec.compute.ComputationCacheExc.corruptedValueException;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.val.ArrayH;
import org.smoothbuild.db.object.obj.val.BlobH;
import org.smoothbuild.db.object.obj.val.BoolH;
import org.smoothbuild.db.object.obj.val.IntH;
import org.smoothbuild.db.object.obj.val.StringH;
import org.smoothbuild.db.object.obj.val.TupleH;
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
    computationCache().write(hash, new Output(stringH("result"), messageArrayEmtpy()));
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
    assertCall(() -> computationCache().read(hash, stringHT()))
        .throwsException(ComputationCacheExc.class);
  }

  @Test
  public void written_messages_can_be_read_back() throws Exception {
    var strV = stringH("abc");
    var message = errorMessage("error message");
    var messages = arrayH(message);
    computationCache().write(hash, new Output(strV, messages));

    assertThat(computationCache().read(hash, stringHT()).messages())
        .isEqualTo(messages);
  }

  @Test
  public void written_file_array_can_be_read_back() throws Exception {
    var file = fileH(path("file/path"), bytes);
    computationCache().write(hash, new Output(arrayH(file), messageArrayEmtpy()));
    var arrayType = arrayHT(objFactory().fileT());

    assertThat(((ArrayH) computationCache().read(hash, arrayType).value()).elems(TupleH.class))
        .containsExactly(file);
  }

  @Test
  public void written_blob_array_can_be_read_back() throws Exception {
    var blob = blobH(bytes);
    computationCache().write(hash, new Output(arrayH(blob), messageArrayEmtpy()));
    var arrayType = arrayHT(blobHT());

    assertThat(((ArrayH) computationCache().read(hash, arrayType).value()).elems(BlobH.class))
        .containsExactly(blob);
  }

  @Test
  public void written_bool_array_can_be_read_back() throws Exception {
    var boolV = boolH(true);
    computationCache().write(hash, new Output(arrayH(boolV), messageArrayEmtpy()));
    var arrayType = arrayHT(boolHT());

    assertThat(((ArrayH) computationCache().read(hash, arrayType).value()).elems(BoolH.class))
        .containsExactly(boolV);
  }

  @Test
  public void written_int_array_can_be_read_back() throws Exception {
    var intV = intH(123);
    computationCache().write(hash, new Output(arrayH(intV), messageArrayEmtpy()));
    var arrayType = arrayHT(intHT());

    assertThat(((ArrayH) computationCache().read(hash, arrayType).value()).elems(IntH.class))
        .containsExactly(intV);
  }

  @Test
  public void written_string_array_can_be_read_back() throws Exception {
    var strV = stringH("some string");
    var array = arrayH(strV);
    computationCache().write(hash, new Output(array, messageArrayEmtpy()));
    var arrayType = arrayHT(stringHT());

    assertThat(((ArrayH) computationCache().read(hash, arrayType).value()).elems(StringH.class))
        .containsExactly(strV);
  }

  @Test
  public void written_file_can_be_read_back() throws Exception {
    var file = fileH(path("file/path"), bytes);
    computationCache().write(hash, new Output(file, messageArrayEmtpy()));

    assertThat(computationCache().read(hash, objFactory().fileT()).value())
        .isEqualTo(file);
  }

  @Test
  public void written_blob_can_be_read_back() throws Exception {
    var blob = blobH(bytes);
    computationCache().write(hash, new Output(blob, messageArrayEmtpy()));

    assertThat(computationCache().read(hash, blobHT()).value())
        .isEqualTo(blob);
  }

  @Test
  public void written_bool_can_be_read_back() throws Exception {
    var boolV = boolH(true);
    computationCache().write(hash, new Output(boolV, messageArrayEmtpy()));

    assertThat(((BoolH) computationCache().read(hash, boolHT()).value()).toJ())
        .isTrue();
  }

  @Test
  public void written_int_can_be_read_back() throws Exception {
    var intV = intH(123);
    computationCache().write(hash, new Output(intV, messageArrayEmtpy()));

    assertThat(((IntH) computationCache().read(hash, intHT()).value()).toJ())
        .isEqualTo(BigInteger.valueOf(123));
  }

  @Test
  public void written_string_can_be_read_back() throws Exception {
    var string = "some string";
    var strV = stringH(string);
    computationCache().write(hash, new Output(strV, messageArrayEmtpy()));
    assertThat(((StringH) computationCache().read(hash, stringHT()).value()).toJ())
        .isEqualTo(string);
  }
}

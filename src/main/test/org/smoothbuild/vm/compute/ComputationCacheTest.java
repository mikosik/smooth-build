package org.smoothbuild.vm.compute;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.fs.base.PathS.path;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.expr.val.ArrayB;
import org.smoothbuild.bytecode.expr.val.BlobB;
import org.smoothbuild.bytecode.expr.val.BoolB;
import org.smoothbuild.bytecode.expr.val.IntB;
import org.smoothbuild.bytecode.expr.val.StringB;
import org.smoothbuild.bytecode.expr.val.TupleB;
import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.vm.algorithm.Output;

import okio.ByteString;

public class ComputationCacheTest extends TestContext {
  private final Hash hash = Hash.of("abc");
  private final ByteString bytes = ByteString.encodeUtf8("abc");

  @Test
  public void cache_does_not_contain_not_written_result() throws Exception {
    assertThat(computationCache().contains(hash))
        .isFalse();
  }

  @Test
  public void cache_contains_written_result() throws Exception {
    computationCache().write(hash, new Output(stringB("result"), messageArrayEmtpy()));
    assertThat(computationCache().contains(hash))
        .isTrue();
  }

  @Test
  public void cache_is_corrupted_when_task_hash_points_to_directory() throws Exception {
    var path = ComputationCache.toPath(hash);
    computationCacheFileSystem().sink(path.appendPart("file"));

    assertCall(() -> computationCache().contains(hash))
        .throwsException(ComputationCacheExc.corruptedValueException(hash, path + " is directory not a file."));
  }

  @Test
  public void reading_not_written_value_fails() {
    assertCall(() -> computationCache().read(hash, stringTB()))
        .throwsException(ComputationCacheExc.class);
  }

  @Test
  public void written_messages_can_be_read_back() throws Exception {
    var strV = stringB("abc");
    var message = errorMessage("error message");
    var messages = arrayB(message);
    computationCache().write(hash, new Output(strV, messages));

    assertThat(computationCache().read(hash, stringTB()).messages())
        .isEqualTo(messages);
  }

  @Test
  public void written_file_array_can_be_read_back() throws Exception {
    var file = fileB(path("file/path"), bytes);
    computationCache().write(hash, new Output(arrayB(file), messageArrayEmtpy()));
    var arrayT = arrayTB(bytecodeF().fileT());

    assertThat(((ArrayB) computationCache().read(hash, arrayT).valB()).elems(TupleB.class))
        .containsExactly(file);
  }

  @Test
  public void written_blob_array_can_be_read_back() throws Exception {
    var blob = blobB(bytes);
    computationCache().write(hash, new Output(arrayB(blob), messageArrayEmtpy()));
    var arrayT = arrayTB(blobTB());

    assertThat(((ArrayB) computationCache().read(hash, arrayT).valB()).elems(BlobB.class))
        .containsExactly(blob);
  }

  @Test
  public void written_bool_array_can_be_read_back() throws Exception {
    var boolV = boolB(true);
    computationCache().write(hash, new Output(arrayB(boolV), messageArrayEmtpy()));
    var arrayT = arrayTB(boolTB());

    assertThat(((ArrayB) computationCache().read(hash, arrayT).valB()).elems(BoolB.class))
        .containsExactly(boolV);
  }

  @Test
  public void written_int_array_can_be_read_back() throws Exception {
    var intV = intB(123);
    computationCache().write(hash, new Output(arrayB(intV), messageArrayEmtpy()));
    var arrayT = arrayTB(intTB());

    assertThat(((ArrayB) computationCache().read(hash, arrayT).valB()).elems(IntB.class))
        .containsExactly(intV);
  }

  @Test
  public void written_string_array_can_be_read_back() throws Exception {
    var string = stringB("some string");
    var array = arrayB(string);
    computationCache().write(hash, new Output(array, messageArrayEmtpy()));
    var arrayT = arrayTB(stringTB());

    assertThat(((ArrayB) computationCache().read(hash, arrayT).valB()).elems(StringB.class))
        .containsExactly(string);
  }

  @Test
  public void written_file_can_be_read_back() throws Exception {
    var file = fileB(path("file/path"), bytes);
    computationCache().write(hash, new Output(file, messageArrayEmtpy()));

    assertThat(computationCache().read(hash, bytecodeF().fileT()).valB())
        .isEqualTo(file);
  }

  @Test
  public void written_blob_can_be_read_back() throws Exception {
    var blob = blobB(bytes);
    computationCache().write(hash, new Output(blob, messageArrayEmtpy()));

    assertThat(computationCache().read(hash, blobTB()).valB())
        .isEqualTo(blob);
  }

  @Test
  public void written_bool_can_be_read_back() throws Exception {
    var boolV = boolB(true);
    computationCache().write(hash, new Output(boolV, messageArrayEmtpy()));

    assertThat(((BoolB) computationCache().read(hash, boolTB()).valB()).toJ())
        .isTrue();
  }

  @Test
  public void written_int_can_be_read_back() throws Exception {
    var intV = intB(123);
    computationCache().write(hash, new Output(intV, messageArrayEmtpy()));

    assertThat(((IntB) computationCache().read(hash, intTB()).valB()).toJ())
        .isEqualTo(BigInteger.valueOf(123));
  }

  @Test
  public void written_string_can_be_read_back() throws Exception {
    var string = "some string";
    var strV = stringB(string);
    computationCache().write(hash, new Output(strV, messageArrayEmtpy()));
    assertThat(((StringB) computationCache().read(hash, stringTB()).valB()).toJ())
        .isEqualTo(string);
  }
}

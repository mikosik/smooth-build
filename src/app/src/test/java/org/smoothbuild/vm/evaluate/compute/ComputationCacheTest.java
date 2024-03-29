package org.smoothbuild.vm.evaluate.compute;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.fs.base.PathS.path;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.vm.bytecode.expr.value.ArrayB;
import org.smoothbuild.vm.bytecode.expr.value.BlobB;
import org.smoothbuild.vm.bytecode.expr.value.BoolB;
import org.smoothbuild.vm.bytecode.expr.value.IntB;
import org.smoothbuild.vm.bytecode.expr.value.StringB;
import org.smoothbuild.vm.bytecode.expr.value.TupleB;
import org.smoothbuild.vm.bytecode.hashed.Hash;
import org.smoothbuild.vm.evaluate.task.Output;

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
    var computationCache = computationCache();
    computationCache.write(hash, new Output(stringB("result"), messageArrayEmpty()));
    assertThat(computationCache.contains(hash))
        .isTrue();
  }

  @Test
  public void cache_is_corrupted_when_task_hash_points_to_directory() throws Exception {
    var path = ComputationCache.toPath(hash);
    var fileSystem = computationCacheFileSystem();
    fileSystem.sink(path.appendPart("file"));
    var computationCache = new ComputationCache(fileSystem, bytecodeDb(), bytecodeF());
    assertCall(() -> computationCache.contains(hash))
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
    var computationCache = computationCache();
    computationCache.write(hash, new Output(strV, messages));

    assertThat(computationCache.read(hash, stringTB()).messages())
        .isEqualTo(messages);
  }

  @Test
  public void written_file_array_can_be_read_back() throws Exception {
    var file = fileB(path("file/path"), bytes);
    var computationCache = computationCache();
    computationCache.write(hash, new Output(arrayB(file), messageArrayEmpty()));
    var arrayT = arrayTB(bytecodeF().fileT());

    assertThat(((ArrayB) computationCache.read(hash, arrayT).valueB()).elems(TupleB.class))
        .containsExactly(file);
  }

  @Test
  public void written_blob_array_can_be_read_back() throws Exception {
    var blob = blobB(bytes);
    var computationCache = computationCache();
    computationCache.write(hash, new Output(arrayB(blob), messageArrayEmpty()));
    var arrayT = arrayTB(blobTB());

    assertThat(((ArrayB) computationCache.read(hash, arrayT).valueB()).elems(BlobB.class))
        .containsExactly(blob);
  }

  @Test
  public void written_bool_array_can_be_read_back() throws Exception {
    var boolV = boolB(true);
    var computationCache = computationCache();
    computationCache.write(hash, new Output(arrayB(boolV), messageArrayEmpty()));
    var arrayT = arrayTB(boolTB());

    assertThat(((ArrayB) computationCache.read(hash, arrayT).valueB()).elems(BoolB.class))
        .containsExactly(boolV);
  }

  @Test
  public void written_int_array_can_be_read_back() throws Exception {
    var intV = intB(123);
    var computationCache = computationCache();
    computationCache.write(hash, new Output(arrayB(intV), messageArrayEmpty()));
    var arrayT = arrayTB(intTB());

    assertThat(((ArrayB) computationCache.read(hash, arrayT).valueB()).elems(IntB.class))
        .containsExactly(intV);
  }

  @Test
  public void written_string_array_can_be_read_back() throws Exception {
    var string = stringB("some string");
    var array = arrayB(string);
    var computationCache = computationCache();
    computationCache.write(hash, new Output(array, messageArrayEmpty()));
    var arrayT = arrayTB(stringTB());

    assertThat(((ArrayB) computationCache.read(hash, arrayT).valueB()).elems(StringB.class))
        .containsExactly(string);
  }

  @Test
  public void written_file_can_be_read_back() throws Exception {
    var file = fileB(path("file/path"), bytes);
    var computationCache = computationCache();
    computationCache.write(hash, new Output(file, messageArrayEmpty()));

    assertThat(computationCache.read(hash, bytecodeF().fileT()).valueB())
        .isEqualTo(file);
  }

  @Test
  public void written_blob_can_be_read_back() throws Exception {
    var blob = blobB(bytes);
    var computationCache = computationCache();
    computationCache.write(hash, new Output(blob, messageArrayEmpty()));

    assertThat(computationCache.read(hash, blobTB()).valueB())
        .isEqualTo(blob);
  }

  @Test
  public void written_bool_can_be_read_back() throws Exception {
    var boolV = boolB(true);
    var computationCache = computationCache();
    computationCache.write(hash, new Output(boolV, messageArrayEmpty()));

    assertThat(((BoolB) computationCache.read(hash, boolTB()).valueB()).toJ())
        .isTrue();
  }

  @Test
  public void written_int_can_be_read_back() throws Exception {
    var intV = intB(123);
    var computationCache = computationCache();
    computationCache.write(hash, new Output(intV, messageArrayEmpty()));

    assertThat(((IntB) computationCache.read(hash, intTB()).valueB()).toJ())
        .isEqualTo(BigInteger.valueOf(123));
  }

  @Test
  public void written_string_can_be_read_back() throws Exception {
    var string = "some string";
    var strV = stringB(string);
    var computationCache = computationCache();
    computationCache.write(hash, new Output(strV, messageArrayEmpty()));
    assertThat(((StringB) computationCache.read(hash, stringTB()).valueB()).toJ())
        .isEqualTo(string);
  }
}

package org.smoothbuild.virtualmachine.evaluate.compute;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.bucket.base.Path.path;
import static org.smoothbuild.commontesting.AssertCall.assertCall;
import static org.smoothbuild.virtualmachine.evaluate.compute.ComputeException.corruptedValueException;

import java.math.BigInteger;
import okio.ByteString;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ArrayB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BlobB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BoolB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.IntB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.StringB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.TupleB;
import org.smoothbuild.virtualmachine.evaluate.task.Output;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class ComputationCacheTest extends TestingVirtualMachine {
  private final Hash hash = Hash.of("abc");
  private final ByteString bytes = ByteString.encodeUtf8("abc");

  @Test
  public void cache_does_not_contain_not_written_result() throws Exception {
    assertThat(computationCache().contains(hash)).isFalse();
  }

  @Test
  public void cache_contains_written_result() throws Exception {
    var computationCache = computationCache();
    computationCache.write(hash, new Output(stringB("result"), logArrayEmpty()));
    assertThat(computationCache.contains(hash)).isTrue();
  }

  @Test
  public void cache_is_corrupted_when_task_hash_points_to_directory() throws Exception {
    var path = computationCache().toPath(hash);
    computationCacheBucket().sink(path.appendPart("file"));
    var computationCache = computationCache();
    assertCall(() -> computationCache.contains(hash))
        .throwsException(corruptedValueException(hash, path + " is directory not a file."));
  }

  @Test
  public void reading_not_written_value_fails() {
    assertCall(() -> computationCache().read(hash, stringTB()))
        .throwsException(ComputeException.class);
  }

  @Test
  public void written_messages_can_be_read_back() throws Exception {
    var strV = stringB("abc");
    var message = errorLog("error message");
    var messages = arrayB(message);
    var computationCache = computationCache();
    computationCache.write(hash, new Output(strV, messages));

    assertThat(computationCache.read(hash, stringTB()).storedLogs()).isEqualTo(messages);
  }

  @Test
  public void written_file_array_can_be_read_back() throws Exception {
    var file = fileB(path("file/path"), bytes);
    var computationCache = computationCache();
    computationCache.write(hash, new Output(arrayB(file), logArrayEmpty()));
    var arrayT = arrayTB(bytecodeF().fileType());

    assertThat(((ArrayB) computationCache.read(hash, arrayT).valueB()).elements(TupleB.class))
        .containsExactly(file);
  }

  @Test
  public void written_blob_array_can_be_read_back() throws Exception {
    var blob = blobB(bytes);
    var computationCache = computationCache();
    computationCache.write(hash, new Output(arrayB(blob), logArrayEmpty()));
    var arrayT = arrayTB(blobTB());

    assertThat(((ArrayB) computationCache.read(hash, arrayT).valueB()).elements(BlobB.class))
        .containsExactly(blob);
  }

  @Test
  public void written_bool_array_can_be_read_back() throws Exception {
    var boolV = boolB(true);
    var computationCache = computationCache();
    computationCache.write(hash, new Output(arrayB(boolV), logArrayEmpty()));
    var arrayT = arrayTB(boolTB());

    assertThat(((ArrayB) computationCache.read(hash, arrayT).valueB()).elements(BoolB.class))
        .containsExactly(boolV);
  }

  @Test
  public void written_int_array_can_be_read_back() throws Exception {
    var intV = intB(123);
    var computationCache = computationCache();
    computationCache.write(hash, new Output(arrayB(intV), logArrayEmpty()));
    var arrayT = arrayTB(intTB());

    assertThat(((ArrayB) computationCache.read(hash, arrayT).valueB()).elements(IntB.class))
        .containsExactly(intV);
  }

  @Test
  public void written_string_array_can_be_read_back() throws Exception {
    var string = stringB("some string");
    var array = arrayB(string);
    var computationCache = computationCache();
    computationCache.write(hash, new Output(array, logArrayEmpty()));
    var arrayT = arrayTB(stringTB());

    assertThat(((ArrayB) computationCache.read(hash, arrayT).valueB()).elements(StringB.class))
        .containsExactly(string);
  }

  @Test
  public void written_file_can_be_read_back() throws Exception {
    var file = fileB(path("file/path"), bytes);
    var computationCache = computationCache();
    computationCache.write(hash, new Output(file, logArrayEmpty()));

    assertThat(computationCache.read(hash, bytecodeF().fileType()).valueB()).isEqualTo(file);
  }

  @Test
  public void written_blob_can_be_read_back() throws Exception {
    var blob = blobB(bytes);
    var computationCache = computationCache();
    computationCache.write(hash, new Output(blob, logArrayEmpty()));

    assertThat(computationCache.read(hash, blobTB()).valueB()).isEqualTo(blob);
  }

  @Test
  public void written_bool_can_be_read_back() throws Exception {
    var boolV = boolB(true);
    var computationCache = computationCache();
    computationCache.write(hash, new Output(boolV, logArrayEmpty()));

    assertThat(((BoolB) computationCache.read(hash, boolTB()).valueB()).toJavaBoolean())
        .isTrue();
  }

  @Test
  public void written_int_can_be_read_back() throws Exception {
    var intV = intB(123);
    var computationCache = computationCache();
    computationCache.write(hash, new Output(intV, logArrayEmpty()));

    assertThat(((IntB) computationCache.read(hash, intTB()).valueB()).toJavaBigInteger())
        .isEqualTo(BigInteger.valueOf(123));
  }

  @Test
  public void written_string_can_be_read_back() throws Exception {
    var string = "some string";
    var strV = stringB(string);
    var computationCache = computationCache();
    computationCache.write(hash, new Output(strV, logArrayEmpty()));
    assertThat(((StringB) computationCache.read(hash, stringTB()).valueB()).toJavaString())
        .isEqualTo(string);
  }
}

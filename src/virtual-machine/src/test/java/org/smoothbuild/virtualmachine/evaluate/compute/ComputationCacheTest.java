package org.smoothbuild.virtualmachine.evaluate.compute;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.bucket.base.Path.path;
import static org.smoothbuild.commontesting.AssertCall.assertCall;
import static org.smoothbuild.virtualmachine.evaluate.compute.ComputeException.corruptedValueException;

import java.math.BigInteger;
import okio.ByteString;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BArray;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BBlob;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BBool;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BInt;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BString;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BTuple;
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
    var arrayType = arrayTB(bytecodeF().fileType());

    assertThat(((BArray) computationCache.read(hash, arrayType).value()).elements(BTuple.class))
        .containsExactly(file);
  }

  @Test
  public void written_blob_array_can_be_read_back() throws Exception {
    var blob = blobB(bytes);
    var computationCache = computationCache();
    computationCache.write(hash, new Output(arrayB(blob), logArrayEmpty()));
    var arrayType = arrayTB(blobTB());

    assertThat(((BArray) computationCache.read(hash, arrayType).value()).elements(BBlob.class))
        .containsExactly(blob);
  }

  @Test
  public void written_bool_array_can_be_read_back() throws Exception {
    var bool = boolB(true);
    var computationCache = computationCache();
    computationCache.write(hash, new Output(arrayB(bool), logArrayEmpty()));
    var arrayType = arrayTB(boolTB());

    assertThat(((BArray) computationCache.read(hash, arrayType).value()).elements(BBool.class))
        .containsExactly(bool);
  }

  @Test
  public void written_int_array_can_be_read_back() throws Exception {
    var int_ = intB(123);
    var computationCache = computationCache();
    computationCache.write(hash, new Output(arrayB(int_), logArrayEmpty()));
    var arrayType = arrayTB(intTB());

    assertThat(((BArray) computationCache.read(hash, arrayType).value()).elements(BInt.class))
        .containsExactly(int_);
  }

  @Test
  public void written_string_array_can_be_read_back() throws Exception {
    var string = stringB("some string");
    var array = arrayB(string);
    var computationCache = computationCache();
    computationCache.write(hash, new Output(array, logArrayEmpty()));
    var arrayType = arrayTB(stringTB());

    assertThat(((BArray) computationCache.read(hash, arrayType).value()).elements(BString.class))
        .containsExactly(string);
  }

  @Test
  public void written_file_can_be_read_back() throws Exception {
    var file = fileB(path("file/path"), bytes);
    var computationCache = computationCache();
    computationCache.write(hash, new Output(file, logArrayEmpty()));

    assertThat(computationCache.read(hash, bytecodeF().fileType()).value()).isEqualTo(file);
  }

  @Test
  public void written_blob_can_be_read_back() throws Exception {
    var blob = blobB(bytes);
    var computationCache = computationCache();
    computationCache.write(hash, new Output(blob, logArrayEmpty()));

    assertThat(computationCache.read(hash, blobTB()).value()).isEqualTo(blob);
  }

  @Test
  public void written_bool_can_be_read_back() throws Exception {
    var bool = boolB(true);
    var computationCache = computationCache();
    computationCache.write(hash, new Output(bool, logArrayEmpty()));

    assertThat(((BBool) computationCache.read(hash, boolTB()).value()).toJavaBoolean())
        .isTrue();
  }

  @Test
  public void written_int_can_be_read_back() throws Exception {
    var int_ = intB(123);
    var computationCache = computationCache();
    computationCache.write(hash, new Output(int_, logArrayEmpty()));

    assertThat(((BInt) computationCache.read(hash, intTB()).value()).toJavaBigInteger())
        .isEqualTo(BigInteger.valueOf(123));
  }

  @Test
  public void written_string_can_be_read_back() throws Exception {
    var string = "some string";
    var bString = stringB(string);
    var computationCache = computationCache();
    computationCache.write(hash, new Output(bString, logArrayEmpty()));
    assertThat(((BString) computationCache.read(hash, stringTB()).value()).toJavaString())
        .isEqualTo(string);
  }
}

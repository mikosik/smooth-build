package org.smoothbuild.virtualmachine.evaluate.compute;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.bucket.base.Path.path;
import static org.smoothbuild.commontesting.AssertCall.assertCall;
import static org.smoothbuild.virtualmachine.evaluate.compute.ComputeCacheException.corruptedValueException;

import java.math.BigInteger;
import okio.ByteString;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BArray;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BBlob;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BBool;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BInt;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BString;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.evaluate.step.BOutput;
import org.smoothbuild.virtualmachine.testing.VmTestContext;

public class ComputationCacheTest extends VmTestContext {
  private final Hash hash = Hash.of("abc");
  private final ByteString bytes = ByteString.encodeUtf8("abc");

  @Test
  void cache_does_not_contain_not_written_result() throws Exception {
    assertThat(computationCache().contains(hash)).isFalse();
  }

  @Test
  void cache_contains_written_result() throws Exception {
    var computationCache = computationCache();
    computationCache.write(hash, new BOutput(bString("result"), bLogArrayEmpty()));
    assertThat(computationCache.contains(hash)).isTrue();
  }

  @Test
  void cache_is_corrupted_when_task_hash_points_to_directory() throws Exception {
    var path = computationCache().toPath(hash);
    computationCacheBucket().createDir(path);
    var computationCache = computationCache();
    assertCall(() -> computationCache.contains(hash))
        .throwsException(corruptedValueException(hash, path + " is directory not a file."));
  }

  @Test
  void reading_not_written_value_fails() {
    assertCall(() -> computationCache().read(hash, bStringType()))
        .throwsException(ComputeCacheException.class);
  }

  @Test
  void written_messages_can_be_read_back() throws Exception {
    var strV = bString("abc");
    var message = bErrorLog("error message");
    var messages = bArray(message);
    var computationCache = computationCache();
    computationCache.write(hash, new BOutput(strV, messages));

    assertThat(computationCache.read(hash, bStringType()).storedLogs()).isEqualTo(messages);
  }

  @Test
  void written_file_array_can_be_read_back() throws Exception {
    var file = bFile(path("file/path"), bytes);
    var computationCache = computationCache();
    computationCache.write(hash, new BOutput(bArray(file), bLogArrayEmpty()));
    var arrayType = bArrayType(bytecodeF().fileType());

    assertThat(((BArray) computationCache.read(hash, arrayType).value()).elements(BTuple.class))
        .containsExactly(file);
  }

  @Test
  void written_blob_array_can_be_read_back() throws Exception {
    var blob = bBlob(bytes);
    var computationCache = computationCache();
    computationCache.write(hash, new BOutput(bArray(blob), bLogArrayEmpty()));
    var arrayType = bArrayType(bBlobType());

    assertThat(((BArray) computationCache.read(hash, arrayType).value()).elements(BBlob.class))
        .containsExactly(blob);
  }

  @Test
  void written_bool_array_can_be_read_back() throws Exception {
    var bool = bBool(true);
    var computationCache = computationCache();
    computationCache.write(hash, new BOutput(bArray(bool), bLogArrayEmpty()));
    var arrayType = bArrayType(bBoolType());

    assertThat(((BArray) computationCache.read(hash, arrayType).value()).elements(BBool.class))
        .containsExactly(bool);
  }

  @Test
  void written_int_array_can_be_read_back() throws Exception {
    var int_ = bInt(123);
    var computationCache = computationCache();
    computationCache.write(hash, new BOutput(bArray(int_), bLogArrayEmpty()));
    var arrayType = bArrayType(bIntType());

    assertThat(((BArray) computationCache.read(hash, arrayType).value()).elements(BInt.class))
        .containsExactly(int_);
  }

  @Test
  void written_string_array_can_be_read_back() throws Exception {
    var string = bString("some string");
    var array = bArray(string);
    var computationCache = computationCache();
    computationCache.write(hash, new BOutput(array, bLogArrayEmpty()));
    var arrayType = bArrayType(bStringType());

    assertThat(((BArray) computationCache.read(hash, arrayType).value()).elements(BString.class))
        .containsExactly(string);
  }

  @Test
  void written_file_can_be_read_back() throws Exception {
    var file = bFile(path("file/path"), bytes);
    var computationCache = computationCache();
    computationCache.write(hash, new BOutput(file, bLogArrayEmpty()));

    assertThat(computationCache.read(hash, bytecodeF().fileType()).value()).isEqualTo(file);
  }

  @Test
  void written_blob_can_be_read_back() throws Exception {
    var blob = bBlob(bytes);
    var computationCache = computationCache();
    computationCache.write(hash, new BOutput(blob, bLogArrayEmpty()));

    assertThat(computationCache.read(hash, bBlobType()).value()).isEqualTo(blob);
  }

  @Test
  void written_bool_can_be_read_back() throws Exception {
    var bool = bBool(true);
    var computationCache = computationCache();
    computationCache.write(hash, new BOutput(bool, bLogArrayEmpty()));

    assertThat(((BBool) computationCache.read(hash, bBoolType()).value()).toJavaBoolean())
        .isTrue();
  }

  @Test
  void written_int_can_be_read_back() throws Exception {
    var int_ = bInt(123);
    var computationCache = computationCache();
    computationCache.write(hash, new BOutput(int_, bLogArrayEmpty()));

    assertThat(((BInt) computationCache.read(hash, bIntType()).value()).toJavaBigInteger())
        .isEqualTo(BigInteger.valueOf(123));
  }

  @Test
  void written_string_can_be_read_back() throws Exception {
    var string = "some string";
    var bString = bString(string);
    var computationCache = computationCache();
    computationCache.write(hash, new BOutput(bString, bLogArrayEmpty()));
    assertThat(((BString) computationCache.read(hash, bStringType()).value()).toJavaString())
        .isEqualTo(string);
  }
}

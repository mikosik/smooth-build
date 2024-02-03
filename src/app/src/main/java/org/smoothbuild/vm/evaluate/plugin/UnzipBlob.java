package org.smoothbuild.vm.evaluate.plugin;

import static okio.Okio.source;
import static org.smoothbuild.common.collect.Either.left;
import static org.smoothbuild.common.collect.Either.right;
import static org.smoothbuild.common.io.Unzip.unzip;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Predicate;
import org.smoothbuild.common.collect.Either;
import org.smoothbuild.vm.bytecode.BytecodeF;
import org.smoothbuild.vm.bytecode.expr.exc.IoBytecodeException;
import org.smoothbuild.vm.bytecode.expr.value.ArrayB;
import org.smoothbuild.vm.bytecode.expr.value.BlobB;
import org.smoothbuild.vm.bytecode.expr.value.StringB;
import org.smoothbuild.vm.bytecode.expr.value.TupleB;

public class UnzipBlob {
  public static Either<String, ArrayB> unzipBlob(
      BytecodeF bytecodeF, BlobB blob, Predicate<String> includePredicate) {
    var arrayBuilder = bytecodeF.arrayBuilderWithElems(bytecodeF.fileT());
    try (var source = blob.source()) {
      var errors =
          unzip(source, includePredicate, (f, is) -> arrayBuilder.add(fileB(bytecodeF, f, is)));
      if (errors.isSome()) {
        return left(errors.get());
      }
    } catch (IOException e) {
      throw new IoBytecodeException(e);
    }
    return right(arrayBuilder.build());
  }

  private static TupleB fileB(BytecodeF bytecodeF, String fileName, InputStream inputStream) {
    StringB path = bytecodeF.string(fileName);
    BlobB content = bytecodeF.blob(sink -> sink.writeAll(source(inputStream)));
    return bytecodeF.file(content, path);
  }
}

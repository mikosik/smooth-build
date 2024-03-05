package org.smoothbuild.virtualmachine.evaluate.plugin;

import static okio.Okio.source;
import static org.smoothbuild.common.collect.Either.left;
import static org.smoothbuild.common.collect.Either.right;
import static org.smoothbuild.common.io.Unzip.unzip;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Predicate;
import org.smoothbuild.common.collect.Either;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.BytecodeFactory;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.IoBytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ArrayB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BlobB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.StringB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.TupleB;

public class UnzipBlob {
  public static Either<String, ArrayB> unzipBlob(
      BytecodeFactory bytecodeFactory, BlobB blob, Predicate<String> includePredicate)
      throws BytecodeException {
    var arrayBuilder = bytecodeFactory.arrayBuilderWithElements(bytecodeFactory.fileType());
    try (var source = blob.source()) {
      var errors = unzip(
          source, includePredicate, (f, is) -> arrayBuilder.add(fileB(bytecodeFactory, f, is)));
      if (errors.isSome()) {
        return left(errors.get());
      }
    } catch (IOException e) {
      throw new IoBytecodeException(e);
    }
    return right(arrayBuilder.build());
  }

  private static TupleB fileB(
      BytecodeFactory bytecodeFactory, String fileName, InputStream inputStream)
      throws BytecodeException {
    StringB path = bytecodeFactory.string(fileName);
    BlobB content = bytecodeFactory.blob(sink -> sink.writeAll(source(inputStream)));
    return bytecodeFactory.file(content, path);
  }
}

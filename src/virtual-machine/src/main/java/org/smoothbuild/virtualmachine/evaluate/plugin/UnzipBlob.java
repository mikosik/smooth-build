package org.smoothbuild.virtualmachine.evaluate.plugin;

import static okio.Okio.buffer;
import static okio.Okio.source;
import static org.smoothbuild.common.collect.Result.err;
import static org.smoothbuild.common.collect.Result.ok;
import static org.smoothbuild.common.io.Unzip.unzip;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Predicate;
import org.smoothbuild.common.collect.Result;
import org.smoothbuild.virtualmachine.bytecode.BytecodeFactory;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BArray;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BBlob;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BString;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;

public class UnzipBlob {
  public static Result<BArray> unzipBlob(
      BytecodeFactory bytecodeFactory, BBlob blob, Predicate<String> includePredicate)
      throws IOException {
    var arrayBuilder = bytecodeFactory.arrayBuilderWithElements(bytecodeFactory.fileType());
    try (var source = buffer(blob.source())) {
      var errors = unzip(
          source, includePredicate, (f, is) -> arrayBuilder.add(fileB(bytecodeFactory, f, is)));
      if (errors.isSome()) {
        return err(errors.get());
      }
    }
    return ok(arrayBuilder.build());
  }

  private static BTuple fileB(
      BytecodeFactory bytecodeFactory, String fileName, InputStream inputStream)
      throws IOException {
    BString path = bytecodeFactory.string(fileName);
    BBlob content = bytecodeFactory.blob(sink -> sink.writeAll(source(inputStream)));
    return bytecodeFactory.file(content, path);
  }
}

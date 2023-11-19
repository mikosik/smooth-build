package org.smoothbuild.vm.evaluate.plugin;

import static okio.Okio.source;
import static org.smoothbuild.common.io.Unzip.unzip;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Predicate;
import net.lingala.zip4j.exception.ZipException;
import org.smoothbuild.common.io.DuplicateFileNameException;
import org.smoothbuild.common.io.IllegalZipEntryFileNameException;
import org.smoothbuild.vm.bytecode.BytecodeF;
import org.smoothbuild.vm.bytecode.expr.value.ArrayB;
import org.smoothbuild.vm.bytecode.expr.value.BlobB;
import org.smoothbuild.vm.bytecode.expr.value.StringB;
import org.smoothbuild.vm.bytecode.expr.value.TupleB;

public class UnzipBlob {
  public static ArrayB unzipBlob(
      BytecodeF bytecodeF, BlobB blob, Predicate<String> includePredicate)
      throws IOException, ZipException, DuplicateFileNameException,
          IllegalZipEntryFileNameException {
    var arrayBuilder = bytecodeF.arrayBuilderWithElems(bytecodeF.fileT());
    unzip(blob, includePredicate, (f, is) -> arrayBuilder.add(fileB(bytecodeF, f, is)));
    return arrayBuilder.build();
  }

  private static TupleB fileB(BytecodeF bytecodeF, String fileName, InputStream inputStream) {
    StringB path = bytecodeF.string(fileName);
    BlobB content = bytecodeF.blob(sink -> sink.writeAll(source(inputStream)));
    return bytecodeF.file(content, path);
  }
}

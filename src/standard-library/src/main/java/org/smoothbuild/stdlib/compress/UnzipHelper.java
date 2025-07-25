package org.smoothbuild.stdlib.compress;

import static org.smoothbuild.virtualmachine.evaluate.plugin.UnzipBlob.unzipBlob;

import java.io.IOException;
import java.util.function.Predicate;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BArray;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BBlob;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

public class UnzipHelper {
  public static BArray unzipToArrayB(
      NativeApi nativeApi, BBlob blob, Predicate<String> includePredicate) throws IOException {
    return unzipBlob(nativeApi.factory(), blob, includePredicate)
        .ifErr(error -> nativeApi.log().error("Error reading archive: " + error))
        .okOr(null);
  }
}

package org.smoothbuild.stdlib.java.javac;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import javax.tools.SimpleJavaFileObject;
import okio.ForwardingSink;
import okio.Okio;
import org.smoothbuild.common.filesystem.base.PathS;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ArrayBBuilder;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BlobBBuilder;
import org.smoothbuild.virtualmachine.bytecode.expr.value.StringB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.TupleB;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

public class OutputClassFile extends SimpleJavaFileObject {
  private final ArrayBBuilder fileArrayBuilder;
  private final PathS path;
  private final BlobBBuilder contentBuilder;
  private final NativeApi nativeApi;

  public OutputClassFile(ArrayBBuilder fileArrayBuilder, PathS path, NativeApi nativeApi)
      throws BytecodeException {
    super(URI.create("class:///" + path.toString()), Kind.CLASS);
    this.fileArrayBuilder = fileArrayBuilder;
    this.path = path;
    this.nativeApi = nativeApi;
    this.contentBuilder = nativeApi.factory().blobBuilder();
  }

  @Override
  public OutputStream openOutputStream() {
    return Okio.buffer(new ForwardingSink(contentBuilder) {
          @Override
          public void close() throws IOException {
            super.close();
            try {
              StringB pathString = nativeApi.factory().string(path.toString());
              TupleB file = nativeApi.factory().file(contentBuilder.build(), pathString);
              fileArrayBuilder.add(file);
            } catch (BytecodeException e) {
              throw e.toIOException();
            }
          }
        })
        .outputStream();
  }
}

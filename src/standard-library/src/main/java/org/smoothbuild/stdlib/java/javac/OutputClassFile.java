package org.smoothbuild.stdlib.java.javac;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import javax.tools.SimpleJavaFileObject;
import okio.ForwardingSink;
import okio.Okio;
import org.smoothbuild.common.bucket.base.Path;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BArrayBuilder;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BBlobBuilder;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BString;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

public class OutputClassFile extends SimpleJavaFileObject {
  private final BArrayBuilder fileArrayBuilder;
  private final Path path;
  private final BBlobBuilder contentBuilder;
  private final NativeApi nativeApi;

  public OutputClassFile(BArrayBuilder fileArrayBuilder, Path path, NativeApi nativeApi)
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
              BString pathString = nativeApi.factory().string(path.toString());
              BTuple file = nativeApi.factory().file(contentBuilder.build(), pathString);
              fileArrayBuilder.add(file);
            } catch (BytecodeException e) {
              throw e.toIOException();
            }
          }
        })
        .outputStream();
  }
}

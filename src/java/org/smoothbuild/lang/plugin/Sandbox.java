package org.smoothbuild.lang.plugin;

import org.smoothbuild.lang.type.ArrayType;
import org.smoothbuild.lang.type.SString;
import org.smoothbuild.lang.type.Value;
import org.smoothbuild.message.base.Message;

public interface Sandbox {
  public void report(Message message);

  public <T extends Value> ArrayBuilder<T> arrayBuilder(ArrayType<T> arrayType);

  public FileBuilder fileBuilder();

  public BlobBuilder blobBuilder();

  public SString string(String string);
}

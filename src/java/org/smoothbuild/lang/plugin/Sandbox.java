package org.smoothbuild.lang.plugin;

import org.smoothbuild.lang.function.value.StringValue;
import org.smoothbuild.message.base.Message;

public interface Sandbox {
  public void report(Message message);

  public FileSetBuilder fileSetBuilder();

  public BlobSetBuilder blobSetBuilder();

  public StringSetBuilder stringSetBuilder();

  public FileBuilder fileBuilder();

  public BlobBuilder blobBuilder();

  public StringValue string(String string);
}

package org.smoothbuild.lang.plugin;

import org.smoothbuild.lang.type.Blob;
import org.smoothbuild.lang.type.File;
import org.smoothbuild.lang.type.StringValue;
import org.smoothbuild.message.base.Message;

public interface Sandbox {
  public void report(Message message);

  public ArrayBuilder<File> fileArrayBuilder();

  public ArrayBuilder<Blob> blobArrayBuilder();

  public ArrayBuilder<StringValue> stringArrayBuilder();

  public FileBuilder fileBuilder();

  public BlobBuilder blobBuilder();

  public StringValue string(String string);
}

package org.smoothbuild.lang.plugin;

import org.smoothbuild.lang.type.SBlob;
import org.smoothbuild.lang.type.SFile;
import org.smoothbuild.lang.type.SString;
import org.smoothbuild.message.base.Message;

public interface Sandbox {
  public void report(Message message);

  public ArrayBuilder<SFile> fileArrayBuilder();

  public ArrayBuilder<SBlob> blobArrayBuilder();

  public ArrayBuilder<SString> stringArrayBuilder();

  public FileBuilder fileBuilder();

  public BlobBuilder blobBuilder();

  public SString string(String string);
}

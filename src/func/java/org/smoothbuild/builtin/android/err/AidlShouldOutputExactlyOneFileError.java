package org.smoothbuild.builtin.android.err;

import static org.smoothbuild.builtin.android.AndroidSdk.AIDL_BINARY;
import static org.smoothbuild.message.base.MessageType.ERROR;

import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.message.base.Message;

public class AidlShouldOutputExactlyOneFileError extends Message {
  public AidlShouldOutputExactlyOneFileError(Array<SFile> outputFiles) {
    super(ERROR, createMessage(outputFiles));
  }

  private static String createMessage(Array<SFile> outputFiles) {
    String aidl = AIDL_BINARY;
    if (!outputFiles.iterator().hasNext()) {
      return aidl + " binary should return exactly one file but returned zero.";
    } else {
      StringBuilder builder = new StringBuilder();
      builder.append(aidl);
      builder.append("binary should return exactly one file but it returned following files:\n");
      for (SFile file : outputFiles) {
        builder.append(file.path().value());
        builder.append("\n");
      }
      return builder.toString();
    }
  }
}

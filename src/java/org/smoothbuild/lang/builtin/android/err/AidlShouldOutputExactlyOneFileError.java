package org.smoothbuild.lang.builtin.android.err;

import static org.smoothbuild.lang.builtin.android.AndroidSdk.AIDL_BINARY;
import static org.smoothbuild.message.base.MessageType.ERROR;

import org.smoothbuild.lang.type.SArray;
import org.smoothbuild.lang.type.SFile;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.util.LineBuilder;

import com.google.common.collect.Iterables;

@SuppressWarnings("serial")
public class AidlShouldOutputExactlyOneFileError extends Message {
  public AidlShouldOutputExactlyOneFileError(SArray<SFile> outputFiles) {
    super(ERROR, createMessage(outputFiles));
  }

  private static String createMessage(SArray<SFile> outputFiles) {
    String aidl = AIDL_BINARY;
    if (Iterables.size(outputFiles) == 0) {
      return aidl + " binary should return exactly one file but returned zero.";
    } else {
      LineBuilder b = new LineBuilder();
      b.addLine(aidl + "binary should return exactly one file but it returned following files:");
      for (SFile file : outputFiles) {
        b.addLine(file.path().value());
      }
      return b.build();
    }
  }
}

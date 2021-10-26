package org.smoothbuild.exec.base;

import static org.smoothbuild.util.collect.Lists.list;

import org.smoothbuild.db.object.obj.val.Blob;
import org.smoothbuild.db.object.obj.val.Str;
import org.smoothbuild.db.object.obj.val.Struc_;

import com.google.common.collect.ImmutableList;

public class FileStruct {
  public static final String NAME = "File";
  private static final int CONTENT_INDEX = 0;
  private static final int PATH_INDEX = 1;
  public static final ImmutableList<String> FIELD_NAMES = list("content", "path");

  public static Str filePath(Struc_ file) {
    return (Str) file.get(PATH_INDEX);
  }

  public static Blob fileContent(Struc_ file) {
    return (Blob) file.get(CONTENT_INDEX);
  }
}

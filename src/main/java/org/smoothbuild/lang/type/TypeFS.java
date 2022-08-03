package org.smoothbuild.lang.type;

import com.google.common.collect.ImmutableList;

public class TypeFS {
  public static final BlobTS BLOB = new BlobTS();
  public static final BoolTS BOOL = new BoolTS();
  public static final IntTS INT = new IntTS();
  public static final StringTS STRING = new StringTS();

  /**
   * Base types that are legal in smooth language.
   */
  public static ImmutableList<BaseTS> baseTs() {
    return ImmutableList.of(
        BLOB,
        BOOL,
        INT,
        STRING
    );
  }
}

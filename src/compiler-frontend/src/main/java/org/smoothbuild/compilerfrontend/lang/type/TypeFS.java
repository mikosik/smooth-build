package org.smoothbuild.compilerfrontend.lang.type;

import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.collect.List;

public class TypeFS {
  public static final BlobTS BLOB = new BlobTS();
  public static final BoolTS BOOL = new BoolTS();
  public static final IntTS INT = new IntTS();
  public static final StringTS STRING = new StringTS();

  /**
   * Base types that are legal in smooth language.
   */
  public static List<TypeS> baseTs() {
    return list(BLOB, BOOL, INT, STRING);
  }
}

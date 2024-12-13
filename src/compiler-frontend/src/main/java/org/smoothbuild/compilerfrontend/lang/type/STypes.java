package org.smoothbuild.compilerfrontend.lang.type;

import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.collect.List;

public class STypes {
  public static final SBlobType BLOB = new SBlobType();
  public static final SBoolType BOOL = new SBoolType();
  public static final SIntType INT = new SIntType();
  public static final SStringType STRING = new SStringType();

  /**
   * Base types that are legal in smooth language.
   */
  public static List<SBaseType> baseTypes() {
    return list(BLOB, BOOL, INT, STRING);
  }
}

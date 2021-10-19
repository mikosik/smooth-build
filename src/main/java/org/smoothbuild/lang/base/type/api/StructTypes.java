package org.smoothbuild.lang.base.type.api;

public class StructTypes {
  public static boolean containsField(StructType type, String name) {
    return type.nameToIndex().containsKey(name);
  }

  public static Type fieldGet(StructType type, String name) {
    return type.fields().get(type.nameToIndex().get(name));
  }
}

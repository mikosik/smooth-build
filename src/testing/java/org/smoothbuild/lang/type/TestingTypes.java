package org.smoothbuild.lang.type;

public class TestingTypes {
  public static final TypesDb typesDb = new TestingTypesDb();
  public static final Type type = typesDb.type();
  public static final Type string = typesDb.string();
  public static final Type blob = typesDb.blob();
  public static final Type a = typesDb.generic("a");
  public static final Type b = typesDb.generic("b");

  public static ArrayType array2(Type elemType) {
    return array(array(elemType));
  }

  public static ArrayType array(Type elemType) {
    return typesDb.array(elemType);
  }
}

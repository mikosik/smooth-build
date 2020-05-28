package org.smoothbuild.acceptance.lang.assign.spec;

import static org.smoothbuild.util.Lists.list;

import org.smoothbuild.lang.object.type.TypeNames;

public class TestedType {
  public static final TestedType BLOB = new TestedType(
      TypeNames.BLOB,
      "toBlob('xyz')",
      "xyz"
  );
  public static final TestedType BOOL = new TestedType(
      TypeNames.BOOL,
      "true()",
      new String(new byte[] {1})
  );
  public static final TestedType NOTHING = new TestedType(
      TypeNames.NOTHING,
      "reportError('e')",
      null,
      "Nothing reportError(String message);");
  public static final TestedType STRING = new TestedType(
      TypeNames.STRING,
      "'abc'",
      "abc"
  );
  public static final TestedType STRUCT_WITH_BOOL = new TestedType(
      "Flag",
      "flag(true())",
      null,
      "Flag{ Bool value }");
  public static final TestedType STRUCT_WITH_STRING = new TestedType(
      "Person",
      "person('John')",
      null,
      "Person{ String name }");

  public static final TestedType BLOB_ARRAY = array(BLOB);
  public static final TestedType BOOL_ARRAY = array(BOOL);
  public static final TestedType NOTHING_ARRAY = new TestedType(
      "[" + TypeNames.NOTHING + "]",
      "[]",
      list()
  );
  public static final TestedType STRING_ARRAY = array(STRING);
  public static final TestedType STRUCT_WITH_BOOL_ARRAY = array(STRUCT_WITH_BOOL);
  public static final TestedType STRUCT_WITH_STRING_ARRAY = array(STRUCT_WITH_STRING);

  public static final TestedType BLOB_ARRAY2 = array(BLOB_ARRAY);
  public static final TestedType BOOL_ARRAY2 = array(BOOL_ARRAY);
  public static final TestedType NOTHING_ARRAY2 = array(NOTHING_ARRAY);
  public static final TestedType STRING_ARRAY2 = array(STRING_ARRAY);
  public static final TestedType STRUCT_WITH_BOOL_ARRAY2 = array(STRUCT_WITH_BOOL_ARRAY);
  public static final TestedType STRUCT_WITH_STRING_ARRAY2 = array(STRUCT_WITH_STRING_ARRAY);

  public final String name;
  public final String literal;
  public final Object value;
  public final String declarations;

  private static TestedType array(TestedType type) {
    Object value = type.value == null ? null : list(type.value);
    return array(type, value);
  }

  private static TestedType array(TestedType type, Object value) {
    return new TestedType(
        "[" + type.name + "]",
        "[" + type.literal + "]",
        value,
        type.declarations
    );
  }

  TestedType(String name, String literal, Object value) {
    this(name, literal, value, "");
  }

  TestedType(String name, String literal, Object value, String declarations) {
    this.name = name;
    this.literal = literal;
    this.value = value;
    this.declarations = declarations;
  }
}

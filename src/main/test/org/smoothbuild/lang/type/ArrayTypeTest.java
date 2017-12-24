package org.smoothbuild.lang.type;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class ArrayTypeTest {
  private final TypeSystem typeSystem = new TypeSystem();
  private final Type type = typeSystem.type();
  private final Type string = typeSystem.string();
  private final Type blob = typeSystem.blob();
  private final Type file = typeSystem.file();
  private final Type nothing = typeSystem.nothing();

  @Test
  public void array_elem_types() {
    assertEquals(type, typeSystem.array(type).elemType());
    assertEquals(string, typeSystem.array(string).elemType());
    assertEquals(blob, typeSystem.array(blob).elemType());
    assertEquals(personType(), typeSystem.array(personType()).elemType());
    assertEquals(nothing, typeSystem.array(nothing).elemType());

    assertEquals(typeSystem.array(type), typeSystem.array(typeSystem.array(type)).elemType());
    assertEquals(typeSystem.array(string), typeSystem.array(typeSystem.array(string)).elemType());
    assertEquals(typeSystem.array(blob), typeSystem.array(typeSystem.array(blob)).elemType());
    assertEquals(typeSystem.array(personType()), typeSystem.array(typeSystem.array(personType()))
        .elemType());
    assertEquals(typeSystem.array(nothing), typeSystem.array(typeSystem.array(nothing))
        .elemType());
  }

  @Test
  public void direct_convertible_to() throws Exception {
    assertEquals(typeSystem.array(blob), typeSystem.array(file).directConvertibleTo());
    assertEquals(typeSystem.array(string), typeSystem.array(personType())
        .directConvertibleTo());
    assertEquals(null, typeSystem.array(string).directConvertibleTo());
    assertEquals(null, typeSystem.array(nothing).directConvertibleTo());
  }

  private StructType personType() {
    return typeSystem.struct(
        "Person", ImmutableMap.of("firstName", string, "lastName", string));
  }
}

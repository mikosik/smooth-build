package org.smoothbuild.lang.type;

import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.hash.HashCode;

public class TypeHashTest {
  @Test
  public void hash_of_string_type_is_stable() throws Exception {
    TypeSystem typeSystem = new TypeSystem();
    assertHash(typeSystem.type(), "9774f308a7b1110d0958c8c9120db3d6745b3e53");
    assertHash(typeSystem.string(), "8c842ebb68ca09f033419a6336859e8ae372a227");
    assertHash(typeSystem.blob(), "56f5f4cdcad173aaae82a89df1a7cda54f22fad9");
    assertHash(typeSystem.nothing(), "2aa542ce2fd5ad73acd4c33b26d91132ecc71e73");
    assertHash(typeSystem.array(typeSystem.type()), "15f42011e71f81d36c9f4e4ba86f6ddc10bc7b63");
    assertHash(typeSystem.array(typeSystem.string()), "d3a0aa3689e91679c81f6474fefe902e0be785e8");
    assertHash(typeSystem.array(typeSystem.blob()), "86a1fa106e1f0c89d05f24bb0da1a709dd965b2a");
    assertHash(typeSystem.array(typeSystem.nothing()), "84e4384bcf54b9dfcf841db1e3df4d9b91356e72");
    assertHash(structType(typeSystem), "4921efcb53d2545cca3d32b4372205d372ea2581");
  }

  private StructType structType(TypeSystem typeSystem) {
    return typeSystem.struct("NewType",
        ImmutableMap.of("name", typeSystem.string(), "data", typeSystem.blob()));
  }

  private void assertHash(Type type, String hash) {
    when(() -> type.hash());
    thenReturned(HashCode.fromString(hash));
  }
}

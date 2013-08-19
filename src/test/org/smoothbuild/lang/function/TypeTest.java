package org.smoothbuild.lang.function;

import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Assert;
import org.junit.Test;
import org.smoothbuild.lang.type.File;
import org.smoothbuild.lang.type.Files;

public class TypeTest {

  @Test
  public void javaType() {
    assertThat((Object) Type.STRING.javaType()).isEqualTo(String.class);
    assertThat((Object) Type.FILE.javaType()).isEqualTo(File.class);
    assertThat((Object) Type.FILES.javaType()).isEqualTo(Files.class);
  }

  @Test
  public void toType() {
    assertThat(Type.toType(String.class)).isEqualTo(Type.STRING);
    assertThat(Type.toType(File.class)).isEqualTo(Type.FILE);
    assertThat(Type.toType(Files.class)).isEqualTo(Type.FILES);
  }

  @Test
  public void javaToSmoothContainsAllTypes() throws Exception {
    Type[] array = new Type[] {};
    assertThat(Type.JAVA_TO_SMOOTH.values()).containsOnly(Type.ALL_TYPES.toArray(array));
  }

  @Test
  public void javaTypesContainAllTypes() throws Exception {
    assertThat(Type.allTypes().size()).isEqualTo(Type.allJavaTypes().size());
    for (Type type : Type.allTypes()) {
      if (!Type.ALL_JAVA_TYPES.contains(type.javaType())) {
        Assert.fail("JAVA_TYPES should containt " + type.javaType());
      }
    }
  }
}

package org.smoothbuild.function;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.smoothbuild.plugin.File;
import org.smoothbuild.plugin.Files;

public class TypeTest {

  @Test
  public void javaType() {
    assertThat((Object) Type.STRING.javaType()).isEqualTo(String.class);
    assertThat((Object) Type.FILE.javaType()).isEqualTo(File.class);
    assertThat((Object) Type.FILES.javaType()).isEqualTo(Files.class);
  }

  @Test
  public void isAssignableFrom() throws Exception {
    assertThat(Type.STRING.isAssignableFrom(Type.STRING)).isTrue();
    assertThat(Type.STRING.isAssignableFrom(Type.FILE)).isFalse();
    assertThat(Type.STRING.isAssignableFrom(Type.FILES)).isFalse();

    assertThat(Type.FILE.isAssignableFrom(Type.STRING)).isFalse();
    assertThat(Type.FILE.isAssignableFrom(Type.FILE)).isTrue();
    assertThat(Type.FILE.isAssignableFrom(Type.FILES)).isFalse();

    assertThat(Type.FILES.isAssignableFrom(Type.STRING)).isFalse();
    assertThat(Type.FILES.isAssignableFrom(Type.FILE)).isFalse();
    assertThat(Type.FILES.isAssignableFrom(Type.FILES)).isTrue();
  }

  @Test
  public void equalsAndHashCode() throws Exception {
    // TODO uncomment once bug
    // https://code.google.com/p/equalsverifier/issues/detail?id=83 is fixed
    // and remove test below
    // EqualsVerifier.forClass(Type.class).suppress(NULL_FIELDS).verify();
  }

  @Test
  public void equalsAndHashCodeWorkaround() throws Exception {
    assertThat(Type.STRING).isEqualTo(Type.STRING);
    assertThat(Type.STRING).isNotEqualTo(Type.FILE);
    assertThat(Type.STRING).isNotEqualTo(Type.FILES);

    assertThat(Type.FILE).isNotEqualTo(Type.STRING);
    assertThat(Type.FILE).isEqualTo(Type.FILE);
    assertThat(Type.FILE).isNotEqualTo(Type.FILES);

    assertThat(Type.FILES).isNotEqualTo(Type.STRING);
    assertThat(Type.FILES).isNotEqualTo(Type.FILE);
    assertThat(Type.FILES).isEqualTo(Type.FILES);

    assertThat(Type.STRING.hashCode()).isNotEqualTo(Type.FILE.hashCode());
    assertThat(Type.FILE.hashCode()).isNotEqualTo(Type.FILES.hashCode());
    assertThat(Type.FILES.hashCode()).isNotEqualTo(Type.STRING.hashCode());
  }

  @Test
  public void javaParamTypetoType() {
    assertThat(Type.javaParamTypetoType(String.class)).isEqualTo(Type.STRING);
    assertThat(Type.javaParamTypetoType(File.class)).isEqualTo(Type.FILE);
    assertThat(Type.javaParamTypetoType(Files.class)).isEqualTo(Type.FILES);
  }

  @Test
  public void voidIsNotValidParamType() throws Exception {
    assertThat(Type.javaParamTypetoType(Void.class)).isNull();
  }

  @Test
  public void javaResultTypetoType() {
    assertThat(Type.javaResultTypetoType(String.class)).isEqualTo(Type.STRING);
    assertThat(Type.javaResultTypetoType(File.class)).isEqualTo(Type.FILE);
    assertThat(Type.javaResultTypetoType(Files.class)).isEqualTo(Type.FILES);
    assertThat(Type.javaResultTypetoType(Void.TYPE)).isEqualTo(Type.VOID);
  }

  @Test
  public void javaResultToSmoothContainsAllResultTypes() throws Exception {
    Type[] array = new Type[] {};
    assertThat(Type.JAVA_RESULT_TO_SMOOTH.values()).containsOnly(Type.RESULT_TYPES.toArray(array));
  }

  @Test
  public void javaParamToSmoothContainsAllResultTypes() throws Exception {
    Type[] array = new Type[] {};
    assertThat(Type.JAVA_PARAM_TO_SMOOTH.values()).containsOnly(Type.PARAM_TYPES.toArray(array));
  }
}

package org.smoothbuild.function.base;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.smoothbuild.plugin.File;
import org.smoothbuild.plugin.FileList;

public class TypeTest {

  @Test
  public void javaType() {
    assertThat((Object) Type.STRING.javaType()).isEqualTo(String.class);
    assertThat((Object) Type.FILE.javaType()).isEqualTo(File.class);
    assertThat((Object) Type.FILE_LIST.javaType()).isEqualTo(FileList.class);
  }

  @Test
  public void isAssignableFrom() throws Exception {
    assertThat(Type.STRING.isAssignableFrom(Type.STRING)).isTrue();
    assertThat(Type.STRING.isAssignableFrom(Type.FILE)).isFalse();
    assertThat(Type.STRING.isAssignableFrom(Type.FILE_LIST)).isFalse();

    assertThat(Type.FILE.isAssignableFrom(Type.STRING)).isFalse();
    assertThat(Type.FILE.isAssignableFrom(Type.FILE)).isTrue();
    assertThat(Type.FILE.isAssignableFrom(Type.FILE_LIST)).isFalse();

    assertThat(Type.FILE_LIST.isAssignableFrom(Type.STRING)).isFalse();
    assertThat(Type.FILE_LIST.isAssignableFrom(Type.FILE)).isFalse();
    assertThat(Type.FILE_LIST.isAssignableFrom(Type.FILE_LIST)).isTrue();
  }

  @Test
  public void equalsAndHashCodeWorkaround() throws Exception {
    assertThat(Type.STRING).isEqualTo(Type.STRING);
    assertThat(Type.STRING).isNotEqualTo(Type.FILE);
    assertThat(Type.STRING).isNotEqualTo(Type.FILE_LIST);

    assertThat(Type.FILE).isNotEqualTo(Type.STRING);
    assertThat(Type.FILE).isEqualTo(Type.FILE);
    assertThat(Type.FILE).isNotEqualTo(Type.FILE_LIST);

    assertThat(Type.FILE_LIST).isNotEqualTo(Type.STRING);
    assertThat(Type.FILE_LIST).isNotEqualTo(Type.FILE);
    assertThat(Type.FILE_LIST).isEqualTo(Type.FILE_LIST);

    assertThat(Type.STRING.hashCode()).isNotEqualTo(Type.FILE.hashCode());
    assertThat(Type.FILE.hashCode()).isNotEqualTo(Type.FILE_LIST.hashCode());
    assertThat(Type.FILE_LIST.hashCode()).isNotEqualTo(Type.STRING.hashCode());
  }

  @Test
  public void javaParamTypetoType() {
    assertThat(Type.javaParamTypetoType(String.class)).isEqualTo(Type.STRING);
    assertThat(Type.javaParamTypetoType(File.class)).isEqualTo(Type.FILE);
    assertThat(Type.javaParamTypetoType(FileList.class)).isEqualTo(Type.FILE_LIST);
  }

  @Test
  public void voidIsNotValidParamType() throws Exception {
    assertThat(Type.javaParamTypetoType(Void.class)).isNull();
  }

  @Test
  public void javaResultTypetoType() {
    assertThat(Type.javaResultTypetoType(String.class)).isEqualTo(Type.STRING);
    assertThat(Type.javaResultTypetoType(File.class)).isEqualTo(Type.FILE);
    assertThat(Type.javaResultTypetoType(FileList.class)).isEqualTo(Type.FILE_LIST);
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

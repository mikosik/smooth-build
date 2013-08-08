package org.smoothbuild.registry.instantiate;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.smoothbuild.lang.type.Path.path;
import static org.smoothbuild.registry.instantiate.FunctionInstanceId.BUILD_ROOT;

import org.junit.Test;

public class FunctionInstanceIdFactoryTest {

  FunctionInstanceIdFactory idFactory = new FunctionInstanceIdFactory();

  @Test
  public void test() {
    assertThat(idFactory.createId("abc").resultDir()).isEqualTo(BUILD_ROOT.append(path("0abc")));
    assertThat(idFactory.createId("def").resultDir()).isEqualTo(BUILD_ROOT.append(path("1def")));
    assertThat(idFactory.createId("ghi").resultDir()).isEqualTo(BUILD_ROOT.append(path("2ghi")));
  }
}

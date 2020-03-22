package org.smoothbuild.exec;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.exec.SandboxHashProvider.javaPlatformHash;

import java.util.Properties;

import org.junit.jupiter.api.Test;

public class SandboxHashProviderTest {
  private Properties properties;

  @Test
  public void java_platforms_with_same_properties_have_same_hash() {
    assertThat(javaPlatformHash(properties()))
        .isEqualTo(javaPlatformHash(properties()));
  }

  @Test
  public void java_platforms_with_different_java_vendor_have_different_hashes() {
    properties = properties();
    properties.setProperty("java.vendor", "different");

    assertThat(javaPlatformHash(properties))
        .isNotEqualTo(javaPlatformHash(properties()));
  }

  @Test
  public void java_platforms_with_different_java_version_have_different_hashes() {
    properties = properties();
    properties.setProperty("java.version", "different");

    assertThat(javaPlatformHash(properties))
        .isNotEqualTo(javaPlatformHash(properties()));
  }

  @Test
  public void java_platforms_with_different_java_runtime_name_have_different_hashes() {
    properties = properties();
    properties.setProperty("java.runtime.name", "different");

    assertThat(javaPlatformHash(properties))
        .isNotEqualTo(javaPlatformHash(properties()));
  }

  @Test
  public void java_platforms_with_different_java_runtime_version_have_different_hashes() {
    properties = properties();
    properties.setProperty("java.runtime.version", "different");

    assertThat(javaPlatformHash(properties))
        .isNotEqualTo(javaPlatformHash(properties()));
  }

  @Test
  public void java_platforms_with_different_java_vm_name_have_different_hashes() {
    properties = properties();
    properties.setProperty("java.vm.name", "different");

    assertThat(javaPlatformHash(properties))
        .isNotEqualTo(javaPlatformHash(properties()));
  }

  @Test
  public void java_platforms_with_different_java_vm_version_have_different_hashes() {
    properties = properties();
    properties.setProperty("java.vm.version", "different");

    assertThat(javaPlatformHash(properties))
        .isNotEqualTo(javaPlatformHash(properties()));
  }

  @Test
  public void regression_collisions_are_not_possible() {
    properties = properties();
    properties.setProperty("java.vendor", "A");
    properties.setProperty("java.version", "");
    Properties properties2 = properties();
    properties2.setProperty("java.vendor", "");
    properties2.setProperty("java.version", "A");

    assertThat(javaPlatformHash(properties))
        .isNotEqualTo(javaPlatformHash(properties2));
  }

  private static Properties properties() {
    Properties properties = new Properties();
    properties.setProperty("java.vendor", "1");
    properties.setProperty("java.version", "2");
    properties.setProperty("java.runtime.name", "3");
    properties.setProperty("java.runtime.version", "4");
    properties.setProperty("java.vm.name", "5");
    properties.setProperty("java.vm.version", "6");
    return properties;
  }
}

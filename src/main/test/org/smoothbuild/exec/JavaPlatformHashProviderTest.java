package org.smoothbuild.exec;

import static org.hamcrest.Matchers.not;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.util.Properties;

import org.junit.Test;

public class JavaPlatformHashProviderTest {
  private Properties properties;
  private Properties properties2;

  @Test
  public void java_platforms_with_same_properties_have_same_hash() throws Exception {
    when(() -> new JavaPlatformHashProvider(properties()).get());
    thenReturned(new JavaPlatformHashProvider(properties()).get());
  }

  @Test
  public void java_platforms_with_different_java_vendor_have_different_hashes() throws Exception {
    given(properties = properties());
    given(properties).setProperty("java.vendor", "different");
    when(() -> new JavaPlatformHashProvider(properties).get());
    thenReturned(not(new JavaPlatformHashProvider(properties()).get()));
  }

  @Test
  public void java_platforms_with_different_java_version_have_different_hashes() throws Exception {
    given(properties = properties());
    given(properties).setProperty("java.version", "different");
    when(() -> new JavaPlatformHashProvider(properties).get());
    thenReturned(not(new JavaPlatformHashProvider(properties()).get()));
  }

  @Test
  public void java_platforms_with_different_java_runtime_name_have_different_hashes()
      throws Exception {
    given(properties = properties());
    given(properties).setProperty("java.runtime.name", "different");
    when(() -> new JavaPlatformHashProvider(properties).get());
    thenReturned(not(new JavaPlatformHashProvider(properties()).get()));
  }

  @Test
  public void java_platforms_with_different_java_runtime_version_have_different_hashes()
      throws Exception {
    given(properties = properties());
    given(properties).setProperty("java.runtime.version", "different");
    when(() -> new JavaPlatformHashProvider(properties).get());
    thenReturned(not(new JavaPlatformHashProvider(properties()).get()));
  }

  @Test
  public void java_platforms_with_different_java_vm_name_have_different_hashes() throws Exception {
    given(properties = properties());
    given(properties).setProperty("java.vm.name", "different");
    when(() -> new JavaPlatformHashProvider(properties).get());
    thenReturned(not(new JavaPlatformHashProvider(properties()).get()));
  }

  @Test
  public void java_platforms_with_different_java_vm_version_have_different_hashes()
      throws Exception {
    given(properties = properties());
    given(properties).setProperty("java.vm.version", "different");
    when(() -> new JavaPlatformHashProvider(properties).get());
    thenReturned(not(new JavaPlatformHashProvider(properties()).get()));
  }

  @Test
  public void regression_collisions_are_not_possible()
      throws Exception {
    given(properties = properties());
    given(properties).setProperty("java.vendor", "A");
    given(properties).setProperty("java.version", "");
    given(properties2 = properties());
    given(properties2).setProperty("java.vendor", "");
    given(properties2).setProperty("java.version", "A");
    when(() -> new JavaPlatformHashProvider(properties).get());
    thenReturned(not(new JavaPlatformHashProvider(properties2).get()));
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

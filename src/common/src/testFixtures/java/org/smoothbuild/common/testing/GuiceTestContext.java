package org.smoothbuild.common.testing;

import static com.google.common.base.Suppliers.memoize;
import static com.google.inject.Guice.createInjector;

import com.google.common.base.Supplier;
import com.google.inject.Injector;
import com.google.inject.Module;

public abstract class GuiceTestContext {
  private final Supplier<Injector> injectorSupplier = memoize(() -> createInjector(module()));

  public Injector injector() {
    return injectorSupplier.get();
  }

  protected abstract Module module();
}

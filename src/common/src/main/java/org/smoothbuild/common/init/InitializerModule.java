package org.smoothbuild.common.init;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ElementsIntoSet;
import java.util.Set;

@Module
public interface InitializerModule {
  // Provides an empty set of initializers so Initializer can be requested inside a component
  // which does not `@Binds @IntoSet` any Initializer.
  @Provides
  @ElementsIntoSet
  static Set<Initializable> provideInitializers() {
    return Set.of();
  }
}

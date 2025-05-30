package org.smoothbuild.cli.dagger;

import org.smoothbuild.compilerfrontend.dagger.FrontendCompilerTestApi;

public interface CliTestApi extends FrontendCompilerTestApi {
  @Override
  CliTestComponent provide();
}

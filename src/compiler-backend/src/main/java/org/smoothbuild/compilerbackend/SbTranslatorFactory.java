package org.smoothbuild.compilerbackend;

import dagger.assisted.AssistedFactory;
import org.smoothbuild.compilerfrontend.lang.define.SPolyEvaluable;
import org.smoothbuild.compilerfrontend.lang.name.Bindings;

@AssistedFactory
public interface SbTranslatorFactory {
  public SbTranslator create(Bindings<SPolyEvaluable> evaluables);
}

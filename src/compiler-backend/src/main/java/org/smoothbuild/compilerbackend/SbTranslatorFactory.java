package org.smoothbuild.compilerbackend;

import com.google.inject.assistedinject.Assisted;
import org.smoothbuild.compilerfrontend.lang.bindings.ImmutableBindings;
import org.smoothbuild.compilerfrontend.lang.define.SNamedEvaluable;

public interface SbTranslatorFactory {
  public SbTranslator create(@Assisted ImmutableBindings<SNamedEvaluable> evaluables);
}

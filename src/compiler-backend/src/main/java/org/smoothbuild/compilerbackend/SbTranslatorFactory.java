package org.smoothbuild.compilerbackend;

import com.google.inject.assistedinject.Assisted;
import org.smoothbuild.compilerfrontend.lang.define.SNamedEvaluable;
import org.smoothbuild.compilerfrontend.lang.name.Bindings;

public interface SbTranslatorFactory {
  public SbTranslator create(@Assisted Bindings<SNamedEvaluable> evaluables);
}

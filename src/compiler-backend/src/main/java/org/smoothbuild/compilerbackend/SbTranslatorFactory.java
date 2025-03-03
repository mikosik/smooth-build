package org.smoothbuild.compilerbackend;

import com.google.inject.assistedinject.Assisted;
import org.smoothbuild.compilerfrontend.lang.define.SPolyEvaluable;
import org.smoothbuild.compilerfrontend.lang.name.Bindings;

public interface SbTranslatorFactory {
  public SbTranslator create(@Assisted Bindings<SPolyEvaluable> evaluables);
}

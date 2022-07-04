package org.smoothbuild.parse.ast;

import java.util.Optional;

/**
 * Referencable.
 */
public sealed interface RefableP extends Parsed, NamedP
    permits MonoRefableP, GenericRefableP, TopRefableP {

  public Optional<AnnP> ann();

  public Optional<TypeP> evalT();

  public Optional<ObjP> body();
}

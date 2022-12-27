package org.smoothbuild.compile.ps.infer;

import static org.smoothbuild.compile.lang.base.TypeNamesS.isVarName;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.Optionals.mapPair;
import static org.smoothbuild.util.collect.Optionals.pullUp;

import java.util.Optional;

import org.smoothbuild.compile.lang.base.Tanal;
import org.smoothbuild.compile.lang.define.TypeDefinitionS;
import org.smoothbuild.compile.lang.type.ArrayTS;
import org.smoothbuild.compile.lang.type.FuncTS;
import org.smoothbuild.compile.lang.type.TypeS;
import org.smoothbuild.compile.lang.type.VarS;
import org.smoothbuild.compile.ps.ast.type.ArrayTP;
import org.smoothbuild.compile.ps.ast.type.FuncTP;
import org.smoothbuild.compile.ps.ast.type.TypeP;
import org.smoothbuild.util.bindings.OptionalBindings;

public class TypePsTranslator {
  private final OptionalBindings<TypeDefinitionS> types;

  public TypePsTranslator(OptionalBindings<TypeDefinitionS> types) {
    this.types = types;
  }

  public Optional<TypeS> translate(TypeP type) {
    if (isVarName(type.name())) {
      return Optional.of(new VarS(type.name()));
    }
    return switch (type) {
      case ArrayTP array -> translate(array.elemT()).map(ArrayTS::new);
      case FuncTP func -> {
        var resultOpt = translate(func.resT());
        var paramsOpt = pullUp(map(func.paramTs(), this::translate));
        yield mapPair(resultOpt, paramsOpt, (r, p) -> new FuncTS(p, r));
      }
      default -> types.get(type.name()).map(Tanal::type);
    };
  }
}

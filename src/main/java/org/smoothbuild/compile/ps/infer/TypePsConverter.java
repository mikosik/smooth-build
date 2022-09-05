package org.smoothbuild.compile.ps.infer;

import static org.smoothbuild.compile.lang.type.TNamesS.isVarName;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.Optionals.mapPair;
import static org.smoothbuild.util.collect.Optionals.pullUp;

import java.util.Optional;

import org.smoothbuild.compile.lang.base.Tapanal;
import org.smoothbuild.compile.lang.define.TDefS;
import org.smoothbuild.compile.lang.type.ArrayTS;
import org.smoothbuild.compile.lang.type.FuncTS;
import org.smoothbuild.compile.lang.type.TypeS;
import org.smoothbuild.compile.lang.type.VarS;
import org.smoothbuild.compile.ps.ast.type.ArrayTP;
import org.smoothbuild.compile.ps.ast.type.FuncTP;
import org.smoothbuild.compile.ps.ast.type.TypeP;
import org.smoothbuild.util.bindings.Bindings;

public class TypePsConverter {
  private final Bindings<Optional<TDefS>> types;

  public TypePsConverter(Bindings<Optional<TDefS>> types) {
    this.types = types;
  }

  public Optional<TypeS> convert(TypeP type) {
    if (isVarName(type.name())) {
      return Optional.of(new VarS(type.name()));
    }
    return switch (type) {
      case ArrayTP array -> convert(array.elemT()).map(ArrayTS::new);
      case FuncTP func -> {
        var resultOpt = convert(func.resT());
        var paramsOpt = pullUp(map(func.paramTs(), this::convert));
        yield mapPair(resultOpt, paramsOpt, (r, p) -> new FuncTS(r, p));
      }
      default -> types.get(type.name()).map(Tapanal::type);
    };
  }
}

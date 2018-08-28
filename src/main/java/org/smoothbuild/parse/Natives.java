package org.smoothbuild.parse;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.smoothbuild.lang.base.Native;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.parse.ast.Ast;
import org.smoothbuild.parse.ast.FuncNode;
import org.smoothbuild.parse.ast.ParamNode;

public class Natives {
  private final Map<String, Native> map;

  public Natives(Map<String, Native> map) {
    this.map = map;
  }

  public List<ParseError> assignNatives(Ast ast) {
    List<ParseError> errors = new ArrayList<>();
    new AstVisitor() {
      @Override
      public void visitFunc(FuncNode func) {
        super.visitFunc(func);
        if (map.containsKey(func.name())) {
          Native nativ = map.get(func.name());
          if (func.isNative()) {
            assign(func, nativ);
          } else {
            errors.add(new ParseError(func, "Function '" + func.name()
                + "' has both definition and native implementation in " + nativ.jarFile() + "."));
          }
        } else {
          if (func.isNative()) {
            errors.add(new ParseError(func, "Function '" + func.name()
                + "' is native but does not have native implementation."));
          }
        }
      }

      private void assign(FuncNode func, Native nativ) {
        Method method = nativ.method();
        Type resultType = func.get(Type.class);
        Class<?> resultJType = method.getReturnType();
        if (!resultType.jType().equals(resultJType)) {
          errors.add(new ParseError(func, "Function '" + func.name() + "' has result type "
              + resultType.q() + " so its native implementation result type must be "
              + resultType.jType().getCanonicalName() + " but it is "
              + resultJType.getCanonicalName() + "."));
          return;
        }
        Parameter[] nativeParams = method.getParameters();
        List<ParamNode> params = func.params();
        if (params.size() != nativeParams.length - 1) {
          errors.add(new ParseError(func, "Function '" + func.name() + "' has "
              + params.size() + " parameter(s) but its native implementation has "
              + (nativeParams.length - 1) + " parameter(s)."));
          return;
        }
        for (int i = 0; i < params.size(); i++) {
          String declaredName = params.get(i).name().toString();
          Parameter nativeParam = nativeParams[i + 1];
          String nativeName = nativeParam.isNamePresent() ? nativeParam.getName() : "";
          if (!declaredName.equals(nativeName)) {
            errors.add(new ParseError(func, "Function '" + func.name()
                + "' has parameter named '" + declaredName
                + "' but its native implementation has parameter named '" + nativeName
                + "' at this position."));
            return;
          }
          Type paramType = params.get(i).type().get(Type.class);
          Class<?> paramJType = nativeParam.getType();
          if (!paramType.jType().equals(paramJType)) {
            errors.add(new ParseError(func, "Function '" + func.name()
                + "' parameter '" + declaredName + "' has type "
                + paramType.name() + " so its native implementation type must be "
                + paramType.jType().getCanonicalName() + " but it is "
                + paramJType.getCanonicalName() + "."));
            return;
          }
        }
        func.set(Native.class, nativ);
      }
    }.visitAst(ast);
    return errors;
  }
}

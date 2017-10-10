package org.smoothbuild.parse;

import static org.smoothbuild.util.Maybe.maybe;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.function.nativ.Native;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.parse.ast.Ast;
import org.smoothbuild.parse.ast.FuncNode;
import org.smoothbuild.parse.ast.ParamNode;
import org.smoothbuild.util.Maybe;

import com.google.inject.TypeLiteral;

public class AssignNatives {
  public static Maybe<Ast> assignNatives(Ast ast, Map<Name, Native> natives) {
    List<ParseError> errors = new ArrayList<>();
    new AstVisitor() {
      public void visitFunction(FuncNode func) {
        super.visitFunction(func);
        if (natives.containsKey(func.name())) {
          Native nativ = natives.get(func.name());
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
        TypeLiteral<?> resultJType = TypeLiteral.get(method.getGenericReturnType());
        if (!resultType.jType().equals(resultJType)) {
          errors.add(new ParseError(func, "Function '" + func.name() + "' has result type "
              + resultType + " so its native implementation result type must be "
              + resultType.jType() + " but it is " + resultJType + "."));
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
          TypeLiteral<?> paramJType = TypeLiteral.get(nativeParam.getParameterizedType());
          if (!paramType.jType().equals(paramJType)) {
            errors.add(new ParseError(func, "Function '" + func.name()
                + "' parameter '" + declaredName + "' has type " + paramType
                + " so its native implementation type must be " + paramType.jType()
                + " but it is " + paramJType + "."));
            return;
          }
        }
        func.set(Native.class, nativ);
      }
    }.visitAst(ast);
    return maybe(ast, errors);
  }
}

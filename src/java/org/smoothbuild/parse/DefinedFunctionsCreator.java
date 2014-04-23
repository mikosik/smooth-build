package org.smoothbuild.parse;

import static org.smoothbuild.lang.base.STypes.BLOB;
import static org.smoothbuild.lang.base.STypes.FILE;
import static org.smoothbuild.lang.base.STypes.NIL;
import static org.smoothbuild.lang.base.STypes.NOTHING;
import static org.smoothbuild.lang.base.STypes.STRING;
import static org.smoothbuild.lang.base.STypes.basicTypes;
import static org.smoothbuild.lang.function.base.Name.name;
import static org.smoothbuild.lang.function.def.args.Arg.namedArg;
import static org.smoothbuild.lang.function.def.args.Arg.namelessArg;
import static org.smoothbuild.lang.function.def.args.Arg.pipedArg;
import static org.smoothbuild.message.base.MessageType.ERROR;
import static org.smoothbuild.message.base.MessageType.FATAL;
import static org.smoothbuild.parse.LocationHelpers.locationOf;
import static org.smoothbuild.util.StringUnescaper.unescaped;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.antlr.v4.runtime.tree.TerminalNode;
import org.smoothbuild.antlr.SmoothParser.ArgContext;
import org.smoothbuild.antlr.SmoothParser.ArgListContext;
import org.smoothbuild.antlr.SmoothParser.ArrayContext;
import org.smoothbuild.antlr.SmoothParser.ArrayElemContext;
import org.smoothbuild.antlr.SmoothParser.CallContext;
import org.smoothbuild.antlr.SmoothParser.ExpressionContext;
import org.smoothbuild.antlr.SmoothParser.FunctionContext;
import org.smoothbuild.antlr.SmoothParser.ParamNameContext;
import org.smoothbuild.antlr.SmoothParser.PipeContext;
import org.smoothbuild.db.objects.ObjectsDb;
import org.smoothbuild.lang.base.SArray;
import org.smoothbuild.lang.base.SArrayType;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.base.SType;
import org.smoothbuild.lang.base.STypes;
import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.lang.expr.ArrayExpr;
import org.smoothbuild.lang.expr.CallExpr;
import org.smoothbuild.lang.expr.Expr;
import org.smoothbuild.lang.expr.InvalidExpr;
import org.smoothbuild.lang.expr.StringExpr;
import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Module;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.function.base.Param;
import org.smoothbuild.lang.function.base.Signature;
import org.smoothbuild.lang.function.def.DefinedFunction;
import org.smoothbuild.lang.function.def.args.Arg;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.message.base.CodeMessage;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.message.listen.LoggedMessages;
import org.smoothbuild.parse.err.ForbiddenArrayElemError;
import org.smoothbuild.parse.err.IncompatibleArrayElemsError;
import org.smoothbuild.util.UnescapingFailedException;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class DefinedFunctionsCreator {
  private final ObjectsDb objectsDb;
  private final ArgExprsCreator argExprsCreator;

  @Inject
  public DefinedFunctionsCreator(ObjectsDb objectsDb, ArgExprsCreator argExprsCreator) {
    this.objectsDb = objectsDb;
    this.argExprsCreator = argExprsCreator;
  }

  public Map<Name, Function<?>> createDefinedFunctions(LoggedMessages messages,
      Module builtinModule, Map<Name, FunctionContext> functionContexts, List<Name> sorted) {
    Worker worker =
        new Worker(messages, builtinModule, functionContexts, sorted, objectsDb, argExprsCreator);
    Map<Name, Function<?>> result = worker.run();
    messages.failIfContainsProblems();
    return result;
  }

  private static class Worker {
    private final LoggedMessages messages;
    private final Module builtinModule;
    private final Map<Name, FunctionContext> functionContexts;
    private final List<Name> sorted;
    private final ObjectsDb objectsDb;
    private final ArgExprsCreator argExprsCreator;

    private final Map<Name, Function<?>> functions = Maps.newHashMap();

    public Worker(LoggedMessages messages, Module builtinModule,
        Map<Name, FunctionContext> functionContexts, List<Name> sorted, ObjectsDb objectsDb,
        ArgExprsCreator argExprsCreator) {
      this.messages = messages;
      this.builtinModule = builtinModule;
      this.functionContexts = functionContexts;
      this.sorted = sorted;
      this.objectsDb = objectsDb;
      this.argExprsCreator = argExprsCreator;
    }

    public Map<Name, Function<?>> run() {
      for (Name name : sorted) {
        DefinedFunction<?> definedFunction = build(functionContexts.get(name));
        functions.put(name, definedFunction);
      }
      return functions;
    }

    public DefinedFunction<?> build(FunctionContext function) {
      Expr<?> expr = build(function.pipe());
      return buildDefinedFunction(function, expr);
    }

    private <T extends SValue> DefinedFunction<T> buildDefinedFunction(FunctionContext function,
        Expr<T> expr) {
      Name name = name(function.functionName().getText());
      Signature<T> signature = new Signature<>(expr.type(), name, ImmutableList.<Param> of());
      return new DefinedFunction<>(signature, expr);
    }

    private Expr<?> build(PipeContext pipe) {
      Expr<?> result = build(pipe.expression());
      List<CallContext> elements = pipe.call();
      for (int i = 0; i < elements.size(); i++) {
        CallContext call = elements.get(i);
        List<Arg> args = build(call.argList());
        // nameless piped argument's location is set to the pipe character '|'
        CodeLocation codeLocation = locationOf(pipe.p.get(i));
        args.add(pipedArg(result, codeLocation));
        result = build(call, args);
      }
      return result;
    }

    private Expr<?> build(ExpressionContext expression) {
      if (expression.array() != null) {
        return build(expression.array());
      }
      if (expression.call() != null) {
        return build(expression.call());
      }
      if (expression.STRING() != null) {
        return buildStringExpr(expression.STRING());
      }
      throw new Message(FATAL, "Bug in smooth binary: Illegal parse tree: "
          + ExpressionContext.class.getSimpleName() + " without children.");
    }

    private Expr<?> build(ArrayContext list) {
      List<ArrayElemContext> elems = list.arrayElem();
      ImmutableList<Expr<?>> elemExprs = build(elems);

      CodeLocation location = locationOf(list);
      SType<?> elemType = commonSuperType(elems, elemExprs, location);

      if (elemType != null) {
        return buildArray(elemType, elemExprs, location);
      } else {
        return new InvalidExpr<>(NIL, location);
      }
    }

    private <T extends SValue> Expr<SArray<T>> buildArray(SType<T> elemType,
        ImmutableList<Expr<?>> elemExpr, CodeLocation location) {
      SArrayType<T> arrayType = STypes.arrayTypeContaining(elemType);
      ImmutableList<Expr<T>> convertedExpr = Convert.ifNeeded(elemType, elemExpr);
      return new ArrayExpr<>(arrayType, convertedExpr, location);
    }

    private ImmutableList<Expr<?>> build(List<ArrayElemContext> elems) {
      Builder<Expr<?>> builder = ImmutableList.builder();
      for (ArrayElemContext elem : elems) {
        Expr<?> expr = build(elem);
        if (!basicTypes().contains(expr.type())) {
          CodeLocation location = locationOf(elem);
          messages.log(new ForbiddenArrayElemError(location, expr.type()));
          builder.add(new InvalidExpr<>(NOTHING, location));
        } else {
          builder.add(expr);
        }
      }
      return builder.build();
    }

    private Expr<?> build(ArrayElemContext elem) {
      if (elem.STRING() != null) {
        return buildStringExpr(elem.STRING());
      }
      if (elem.call() != null) {
        return build(elem.call());
      }

      throw new Message(FATAL, "Bug in smooth binary: Illegal parse tree: "
          + ArrayElemContext.class.getSimpleName() + " without children.");
    }

    private SType<?> commonSuperType(List<ArrayElemContext> elems,
        ImmutableList<Expr<?>> elemExprs, CodeLocation location) {
      if (elems.size() == 0) {
        return NOTHING;
      }
      SType<?> firstType = elemExprs.get(0).type();
      SType<?> commonSuperType = firstType;

      for (int i = 1; i < elemExprs.size(); i++) {
        SType<?> currentType = elemExprs.get(i).type();
        commonSuperType = commonSuperType(commonSuperType, currentType);

        if (commonSuperType == null) {
          messages.log(new IncompatibleArrayElemsError(location, firstType, i, currentType));
          return null;
        }
      }
      return commonSuperType;
    }

    private static SType<?> commonSuperType(SType<?> type1, SType<?> type2) {
      if (type1 == STRING) {
        if (type2 == STRING) {
          return STRING;
        } else {
          return null;
        }
      } else if (type1 == BLOB) {
        if (type2 == BLOB || type2 == FILE) {
          return BLOB;
        } else {
          return null;
        }
      } else if (type1 == FILE) {
        if (type2 == FILE) {
          return FILE;
        } else if (type2 == BLOB) {
          return BLOB;
        } else {
          return null;
        }
      }
      return null;
    }

    private Expr<?> build(CallContext call) {
      List<Arg> args = build(call.argList());
      return build(call, args);
    }

    private Expr<?> build(CallContext call, List<Arg> args) {
      String functionName = call.functionName().getText();

      Function<?> function = getFunction(functionName);

      CodeLocation codeLocation = locationOf(call.functionName());
      ImmutableMap<String, ? extends Expr<?>> namedArgs =
          argExprsCreator.createArgExprs(codeLocation, messages, function, args);

      if (namedArgs == null) {
        return new InvalidExpr<>(function.type(), locationOf(call.functionName()));
      } else {
        return new CallExpr<>(function, codeLocation, namedArgs);
      }
    }

    private Function<?> getFunction(String functionName) {
      // UndefinedFunctionDetector has been run already so we can be sure at
      // this point that function with given name exists either among imported
      // functions or among already handled defined functions.

      Name name = Name.name(functionName);
      Function<?> function = builtinModule.getFunction(name);
      if (function == null) {
        return functions.get(name);
      } else {
        return function;
      }
    }

    private List<Arg> build(ArgListContext argList) {
      List<Arg> result = Lists.newArrayList();
      if (argList != null) {
        List<ArgContext> argContextList = argList.arg();
        for (int i = 0; i < argContextList.size(); i++) {
          result.add(build(i, argContextList.get(i)));
        }
      }
      return result;
    }

    private Arg build(int index, ArgContext arg) {
      Expr<?> expr = build(arg.expression());

      CodeLocation location = locationOf(arg);
      ParamNameContext paramName = arg.paramName();
      if (paramName == null) {
        return namelessArg(index + 1, expr, location);
      } else {
        return namedArg(index + 1, paramName.getText(), expr, location);
      }
    }

    private Expr<?> buildStringExpr(TerminalNode stringToken) {
      String quotedString = stringToken.getText();
      String string = quotedString.substring(1, quotedString.length() - 1);
      try {
        SString stringValue = objectsDb.string(unescaped(string));
        return new StringExpr(stringValue, locationOf(stringToken.getSymbol()));
      } catch (UnescapingFailedException e) {
        CodeLocation location = locationOf(stringToken.getSymbol());
        messages.log(new CodeMessage(ERROR, location, e.getMessage()));
        return new InvalidExpr<>(STRING, locationOf(stringToken.getSymbol()));
      }
    }
  }
}

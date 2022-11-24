package org.smoothbuild.compile.ps.ast;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static org.smoothbuild.compile.lang.base.Loc.loc;
import static org.smoothbuild.compile.ps.CompileError.compileError;
import static org.smoothbuild.out.log.Maybe.maybe;
import static org.smoothbuild.util.Throwables.unexpectedCaseExc;
import static org.smoothbuild.util.collect.Lists.concat;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.Lists.sane;
import static org.smoothbuild.util.collect.NList.nlistWithNonUniqueNames;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.smoothbuild.antlr.lang.SmoothBaseVisitor;
import org.smoothbuild.antlr.lang.SmoothParser.AnnContext;
import org.smoothbuild.antlr.lang.SmoothParser.ArgContext;
import org.smoothbuild.antlr.lang.SmoothParser.ArgListContext;
import org.smoothbuild.antlr.lang.SmoothParser.ArrayTContext;
import org.smoothbuild.antlr.lang.SmoothParser.ChainContext;
import org.smoothbuild.antlr.lang.SmoothParser.ChainHeadContext;
import org.smoothbuild.antlr.lang.SmoothParser.ChainPartContext;
import org.smoothbuild.antlr.lang.SmoothParser.ExprContext;
import org.smoothbuild.antlr.lang.SmoothParser.FuncTContext;
import org.smoothbuild.antlr.lang.SmoothParser.ItemContext;
import org.smoothbuild.antlr.lang.SmoothParser.ItemListContext;
import org.smoothbuild.antlr.lang.SmoothParser.ModContext;
import org.smoothbuild.antlr.lang.SmoothParser.NamedFuncContext;
import org.smoothbuild.antlr.lang.SmoothParser.NamedValueContext;
import org.smoothbuild.antlr.lang.SmoothParser.PipeContext;
import org.smoothbuild.antlr.lang.SmoothParser.SelectContext;
import org.smoothbuild.antlr.lang.SmoothParser.StructContext;
import org.smoothbuild.antlr.lang.SmoothParser.TypeContext;
import org.smoothbuild.antlr.lang.SmoothParser.TypeNameContext;
import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.ps.ast.expr.BlobP;
import org.smoothbuild.compile.ps.ast.expr.CallP;
import org.smoothbuild.compile.ps.ast.expr.ExprP;
import org.smoothbuild.compile.ps.ast.expr.IntP;
import org.smoothbuild.compile.ps.ast.expr.NamedArgP;
import org.smoothbuild.compile.ps.ast.expr.OrderP;
import org.smoothbuild.compile.ps.ast.expr.RefP;
import org.smoothbuild.compile.ps.ast.expr.SelectP;
import org.smoothbuild.compile.ps.ast.expr.StringP;
import org.smoothbuild.compile.ps.ast.refable.ItemP;
import org.smoothbuild.compile.ps.ast.refable.NamedEvaluableP;
import org.smoothbuild.compile.ps.ast.refable.NamedFuncP;
import org.smoothbuild.compile.ps.ast.refable.NamedValueP;
import org.smoothbuild.compile.ps.ast.type.ArrayTP;
import org.smoothbuild.compile.ps.ast.type.FuncTP;
import org.smoothbuild.compile.ps.ast.type.TypeP;
import org.smoothbuild.fs.space.FilePath;
import org.smoothbuild.out.log.Level;
import org.smoothbuild.out.log.LogBuffer;
import org.smoothbuild.out.log.Maybe;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;

public class AstCreator {
  public static Maybe<Ast> fromParseTree(FilePath filePath, ModContext module) {
    var logs = new LogBuffer();
    List<StructP> structs = new ArrayList<>();
    List<NamedEvaluableP> evaluables = new ArrayList<>();
    new SmoothBaseVisitor<Void>() {
      @Override
      public Void visitStruct(StructContext struct) {
        var name = struct.NAME().getText();
        var loc = locOf(filePath, struct.NAME().getSymbol());
        var fields = createItems(struct.itemList());
        structs.add(new StructP(name, fields, loc));
        return null;
      }

      @Override
      public Void visitNamedFunc(NamedFuncContext namedFunc) {
        TerminalNode nameNode = namedFunc.NAME();
        visitChildren(namedFunc);
        Optional<TypeP> type = createTypeSane(namedFunc.type());
        String name = nameNode.getText();
        Optional<ExprP> expr = createPipeSane(namedFunc.pipe());
        Optional<AnnP> annotation = createNativeSane(namedFunc.ann());
        var loc = locOf(filePath, nameNode);
        var params = createItems(namedFunc.itemList());
        evaluables.add(new NamedFuncP(type, name, params, expr, annotation, loc));
        return null;
      }

      @Override
      public Void visitNamedValue(NamedValueContext namedValue) {
        TerminalNode nameNode = namedValue.NAME();
        visitChildren(namedValue);
        Optional<TypeP> type = createTypeSane(namedValue.type());
        String name = nameNode.getText();
        Optional<ExprP> expr = createPipeSane(namedValue.pipe());
        Optional<AnnP> annotation = createNativeSane(namedValue.ann());
        Loc loc = locOf(filePath, nameNode);
        evaluables.add(new NamedValueP(type, name, expr, annotation, loc));
        return null;
      }

      private Optional<AnnP> createNativeSane(AnnContext annotation) {
        if (annotation == null) {
          return Optional.empty();
        } else {
          var name = annotation.NAME().getText();
          return Optional.of(new AnnP(
              name,
              createStringNode(annotation, annotation.STRING()),
              locOf(filePath, annotation)));
        }
      }

      private NList<ItemP> createItems(ItemListContext itemList) {
        return nlistWithNonUniqueNames(createItemsList(itemList));
      }

      private ImmutableList<ItemP> createItemsList(ItemListContext itemList) {
        if (itemList != null) {
          return sane(itemList.item())
              .stream()
              .map(this::createItem)
              .collect(toImmutableList());
        }
        return ImmutableList.of();
      }

      private ItemP createItem(ItemContext item) {
        var type = createT(item.type());
        var nameNode = item.NAME();
        var name = nameNode.getText();
        var defaultValue = createExprSane(item.expr());
        var loc = locOf(filePath, nameNode);
        return new ItemP(type, name, defaultValue, loc);
      }

      private Optional<ExprP> createPipeSane(PipeContext pipe) {
        return Optional.ofNullable(pipe).map(this::createPipe);
      }

      private ExprP createPipe(PipeContext pipe) {
        return createPipe(new AtomicReference<>(), pipe);
      }

      private ExprP createPipe(AtomicReference<ExprP> outerPiped, PipeContext pipe) {
        var exprs = pipe.expr();
        var firstExpr = createExpr(outerPiped, exprs.get(0));
        var innerPiped = new AtomicReference<>(firstExpr);
        for (int i = 1; i < exprs.size(); i++) {
          var exprContext = exprs.get(i);
          var expr = createExpr(innerPiped, exprContext);
          if (innerPiped.get() != null) {
            logPipedValueNotConsumedError(exprContext);
          }
          innerPiped.set(expr);
        }
        return innerPiped.get();
      }

      private void logPipedValueNotConsumedError(ExprContext parserRuleContext) {
        var loc = locOf(filePath, parserRuleContext);
        logs.log(compileError(loc, "Piped value is not consumed."));
      }

      private Optional<ExprP> createExprSane(ExprContext expr) {
        return Optional.ofNullable(expr).map(this::createExpr);
      }

      private ExprP createExpr(ExprContext expr) {
        return createExpr(new AtomicReference<>(), expr);
      }

      private ExprP createExpr(AtomicReference<ExprP> piped, ExprContext expr) {
        return switch (expr) {
          case ChainContext chain -> createChain(piped, chain);
          default -> throw new RuntimeException("shouldn't happen");
        };
      }

      private ExprP createChain(AtomicReference<ExprP> piped, ChainContext chain) {
        var chainHead = createChainHead(piped, chain.chainHead());
        return createChain(piped, chainHead, chain.chainPart());
      }

      private ExprP createChainHead(AtomicReference<ExprP> pipedArg, ChainHeadContext chainHead) {
        var loc = locOf(filePath, chainHead);
        if (chainHead.NAME() != null) {
          return new RefP(chainHead.NAME().getText(), loc);
        }
        if (chainHead.array() != null) {
          var elems = map(chainHead.array().expr(), this::createExpr);
          if (pipedArg.get() != null) {
            elems = concat(pipedArg.get(), elems);
            pipedArg.set(null);
          }
          return new OrderP(elems, loc);
        }
        if (chainHead.parens() != null) {
           return createPipe(pipedArg, chainHead.parens().pipe());
        }
        if (chainHead.BLOB() != null) {
          return new BlobP(chainHead.BLOB().getText().substring(2), loc);
        }
        if (chainHead.INT() != null) {
          return new IntP(chainHead.INT().getText(), loc);
        }
        if (chainHead.STRING() != null) {
          return createStringNode(chainHead, chainHead.STRING());
        }
        throw newRuntimeException(ChainHeadContext.class);
      }

      private ExprP createChain(AtomicReference<ExprP> pipedArg, ExprP chainHead,
          List<ChainPartContext> chainParts) {
        var result = chainHead;
        for (var chainPart : chainParts) {
          var argList = chainPart.argList();
          if (argList != null) {
            var args = createArgList(argList);
            if (pipedArg.get() != null) {
              args = concat(pipedArg.get(), args);
              pipedArg.set(null);
            }
            result = createCall(result, args, argList);
          } else if (chainPart.select() != null) {
            result = createSelect(result, chainPart.select());
          } else {
            throw newRuntimeException(ChainPartContext.class);
          }
        }
        return result;
      }

      private StringP createStringNode(ParserRuleContext expr, TerminalNode quotedString) {
        String unquoted = unquote(quotedString.getText());
        Loc loc = locOf(filePath, expr);
        return new StringP(unquoted, loc);
      }

      private SelectP createSelect(ExprP selectable, SelectContext fieldRead) {
        String name = fieldRead.NAME().getText();
        Loc loc = locOf(filePath, fieldRead);
        return new SelectP(selectable, name, loc);
      }

      private List<ExprP> createArgList(ArgListContext argList) {
        List<ExprP> result = new ArrayList<>();
        for (ArgContext arg : argList.arg()) {
          ExprContext expr = arg.expr();
          TerminalNode nameNode = arg.NAME();
          ExprP exprP = createExpr(expr);
          if (nameNode == null) {
            result.add(exprP);
          } else {
            result.add(new NamedArgP(nameNode.getText(), exprP, locOf(filePath, arg)));
          }
        }
        return result;
      }

      private ExprP createCall(ExprP callable, List<ExprP> args, ArgListContext argListContext) {
        Loc loc = locOf(filePath, argListContext);
        return new CallP(callable, args, loc);
      }

      private Optional<TypeP> createTypeSane(TypeContext type) {
        return type == null ? Optional.empty() : Optional.of(createT(type));
      }

      private TypeP createT(TypeContext type) {
        return switch (type) {
          case TypeNameContext name -> createT(name);
          case ArrayTContext arrayT -> createArrayT(arrayT);
          case FuncTContext funcT -> createFuncT(funcT);
          default -> throw unexpectedCaseExc(type);
        };
      }

      private TypeP createT(TypeNameContext type) {
        return new TypeP(type.getText(), locOf(filePath, type.NAME()));
      }

      private TypeP createArrayT(ArrayTContext arrayT) {
        TypeP elemType = createT(arrayT.type());
        return new ArrayTP(elemType, locOf(filePath, arrayT));
      }

      private TypeP createFuncT(FuncTContext funcT) {
        var types = map(funcT.type(), this::createT);
        var resT = types.get(types.size() - 1);
        var paramTs = types.subList(0, types.size() - 1);
        return new FuncTP(resT, paramTs, locOf(filePath, funcT));
      }

      private RuntimeException newRuntimeException(Class<?> clazz) {
        return new RuntimeException("Illegal parse tree: " + clazz.getSimpleName()
            + " without children.");
      }
    }.visit(module);
    var ast = new Ast(structs, evaluables);
    return maybe(logs.containsAtLeast(Level.ERROR) ? null : ast, logs);
  }

  private static String unquote(String quotedString) {
    return quotedString.substring(1, quotedString.length() - 1);
  }

  private static Loc locOf(FilePath filePath, ParserRuleContext parserRuleContext) {
    return locOf(filePath, parserRuleContext.getStart());
  }

  private static Loc locOf(FilePath filePath, TerminalNode node) {
    return locOf(filePath, node.getSymbol());
  }

  private static Loc locOf(FilePath filePath, Token token) {
    return loc(filePath, token.getLine());
  }
}

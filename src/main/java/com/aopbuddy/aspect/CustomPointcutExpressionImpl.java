package com.aopbuddy.aspect;

import org.aspectj.util.FuzzyBoolean;
import org.aspectj.weaver.ResolvedMember;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.Shadow;
import org.aspectj.weaver.World;
import org.aspectj.weaver.ast.Literal;
import org.aspectj.weaver.ast.Test;
import org.aspectj.weaver.patterns.*;
import org.aspectj.weaver.reflect.ReflectionFastMatchInfo;
import org.aspectj.weaver.reflect.StandardShadow;
import org.aspectj.weaver.reflect.StandardShadowMatchImpl;
import org.aspectj.weaver.tools.*;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lipan
 * @since 2025-01-22
 */
public class CustomPointcutExpressionImpl implements CustomPointcutExpression {
    private World world;

    private Pointcut pointcut;

    private String expression;

    private PointcutParameter[] parameters;

    private MatchingContext matchContext = new DefaultMatchingContext();

    public CustomPointcutExpressionImpl(Pointcut pointcut, String expression, PointcutParameter[] params,
                                        World inWorld) {
        this.pointcut = pointcut;
        this.expression = expression;
        this.world = inWorld;
        this.parameters = params;
        if (this.parameters == null) {
            this.parameters = new PointcutParameter[0];
        }
    }

    public Pointcut getUnderlyingPointcut() {
        return this.pointcut;
    }

    @Override
    public boolean fastMatchType(String name) {
        ResolvedType resolve = this.world.resolve(name);
        if (resolve.isMissing() || !doCouldMatchJoinPointsInType(resolve)) {
            return false;
        }
        List<ResolvedMember> resolvedMembers = getResolvedMembers(resolve);
        for (ResolvedMember resolvedMember : resolvedMembers) {
            ShadowMatch shadowMatch = matchesExecution(resolvedMember);
            if (shadowMatch.maybeMatches()) {
                return true;
            }
        }
        return false;
    }

    private List<ResolvedMember> getResolvedMembers(ResolvedType owner) {
        List<ResolvedMember> resolvedMembers = new ArrayList<>();
        for (ResolvedMember declaredMethod : owner.getDeclaredMethods()) {
            if (skip(declaredMethod)) {
                resolvedMembers.add(declaredMethod);
            }
        }
        return resolvedMembers;
    }

    @Override
    public boolean couldMatchJoinPointsInType(String name) {
        ResolvedType matchType = this.world.resolve(name);
        return doCouldMatchJoinPointsInType(matchType);
    }

    protected boolean doCouldMatchJoinPointsInType(ResolvedType resolvedType) {
        ReflectionFastMatchInfo info = new ReflectionFastMatchInfo(resolvedType, null, this.matchContext, this.world);
        if (info.getType().isAspect()) {
            return false;
        }
        if (pointcut instanceof KindedPointcut) {
            KindedPointcut pointcut = (KindedPointcut) this.pointcut;
            if (pointcut.getSignature().getDeclaringType() instanceof WildTypePattern) {
                WildTypePattern pattern = (WildTypePattern) pointcut.getSignature().getDeclaringType();
                ResolvedType type = info.getType();
                return pattern.matches(type, TypePattern.STATIC).alwaysTrue();
            }
        }
        return pointcut.fastMatch(info).maybeTrue();
    }

    public boolean mayNeedDynamicTest() {
        HasPossibleDynamicContentVisitor visitor = new HasPossibleDynamicContentVisitor();
        pointcut.traverse(visitor, null);
        return visitor.hasDynamicContent();
    }

    private ExposedState getExposedState() {
        return new ExposedState(parameters.length);
    }

    @Override
    public boolean matchesMethodExecution(String owner, String methodName, String methodDesc) {
        final ResolvedType resolve = world.resolve(owner);
        if (resolve.isMissing()) {
            return false;
        }
        ResolvedMember resolvedMember = getResolvedMember(resolve, methodName, methodDesc);
        if (resolvedMember == null) {
            return false;
        }
        ShadowMatch shadowMatch = matchesExecution(resolvedMember);
        return shadowMatch.maybeMatches() && shadowMatch.alwaysMatches();
    }

    private ResolvedMember getResolvedMember(ResolvedType owner, String method, String methodDesc) {
        for (ResolvedMember declaredMethod : owner.getDeclaredMethods()) {
            if (skip(declaredMethod) && declaredMethod.getName().equals(method) && declaredMethod.getSignature().equals(methodDesc)) {
                return declaredMethod;
            }
        }
        return null;
    }

    private boolean skip(ResolvedMember member) {
        return !member.isAjSynthetic() && member.getAssociatedShadowMunger() == null;
    }

    private ShadowMatch matchesExecution(ResolvedMember aMember) {
        Shadow s = StandardShadow.makeExecutionShadow(world, aMember, this.matchContext);
        StandardShadowMatchImpl sm = getShadowMatch(s);
        sm.setSubject(aMember);
        sm.setWithinCode(null);
        sm.setWithinType((ResolvedType) aMember.getDeclaringType());
        return sm;
    }

    private StandardShadowMatchImpl getShadowMatch(Shadow forShadow) {
        FuzzyBoolean match = pointcut.match(forShadow);
        Test residueTest = Literal.TRUE;
        ExposedState state = getExposedState();
        if (match.maybeTrue()) {
            residueTest = pointcut.findResidue(forShadow, state);
        }
        StandardShadowMatchImpl sm = new StandardShadowMatchImpl(match, residueTest, state, parameters);
        sm.setMatchingContext(this.matchContext);
        return sm;
    }

    @Override
    public String getPointcutExpression() {
        return expression;
    }

    private static class HasPossibleDynamicContentVisitor extends AbstractPatternNodeVisitor {
        private boolean hasDynamicContent = false;

        public boolean hasDynamicContent() {
            return hasDynamicContent;
        }

        @Override
        public Object visit(WithinAnnotationPointcut node, Object data) {
            hasDynamicContent = true;
            return null;
        }

        @Override
        public Object visit(WithinCodeAnnotationPointcut node, Object data) {
            hasDynamicContent = true;
            return null;
        }

        @Override
        public Object visit(AnnotationPointcut node, Object data) {
            hasDynamicContent = true;
            return null;
        }

        @Override
        public Object visit(ArgsAnnotationPointcut node, Object data) {
            hasDynamicContent = true;
            return null;
        }

        @Override
        public Object visit(ArgsPointcut node, Object data) {
            hasDynamicContent = true;
            return null;
        }

        @Override
        public Object visit(CflowPointcut node, Object data) {
            hasDynamicContent = true;
            return null;
        }

        @Override
        public Object visit(IfPointcut node, Object data) {
            hasDynamicContent = true;
            return null;
        }

        @Override
        public Object visit(NotAnnotationTypePattern node, Object data) {
            return node.getNegatedPattern().accept(this, data);
        }

        @Override
        public Object visit(NotPointcut node, Object data) {
            return node.getNegatedPointcut().accept(this, data);
        }

        @Override
        public Object visit(ThisOrTargetAnnotationPointcut node, Object data) {
            hasDynamicContent = true;
            return null;
        }

        @Override
        public Object visit(ThisOrTargetPointcut node, Object data) {
            hasDynamicContent = true;
            return null;
        }

    }

    public static class Handler implements Member {

        private Class<?> decClass;

        private Class<?> exType;

        public Handler(Class decClass, Class exType) {
            this.decClass = decClass;
            this.exType = exType;
        }

        public int getModifiers() {
            return 0;
        }

        public Class getDeclaringClass() {
            return decClass;
        }

        public String getName() {
            return null;
        }

        public Class getHandledExceptionType() {
            return exType;
        }

        public boolean isSynthetic() {
            return false;
        }
    }

    private static class NoShadowMatch implements ShadowMatch {
        static ShadowMatch NO_SHADOW_MATCH = new NoShadowMatch();

        @Override
        public boolean alwaysMatches() {
            return false;
        }

        @Override
        public boolean maybeMatches() {
            return false;
        }

        @Override
        public boolean neverMatches() {
            return true;
        }

        @Override
        public JoinPointMatch matchesJoinPoint(Object thisObject, Object targetObject, Object[] args) {
            return null;
        }

        @Override
        public void setMatchingContext(MatchingContext aMatchContext) {

        }
    }
}

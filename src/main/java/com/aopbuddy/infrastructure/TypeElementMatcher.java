package com.aopbuddy.infrastructure;

import com.aopbuddy.aspect.ClassObject;
import com.aopbuddy.retransform.Advisor;
import com.aopbuddy.retransform.Context;
import lombok.SneakyThrows;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

public class TypeElementMatcher implements ElementMatcher<TypeDescription> {

    @SneakyThrows
    @Override
    public boolean matches(TypeDescription typeDescription) {
        for (Advisor advisor : Context.ADVISORS) {
            if (advisor.getPointcut().matches(new ClassObject.ForUnloaded(typeDescription.getName()))) {
                return true;
            }
        }
        return false;
    }
}

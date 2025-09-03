package com.aopbuddy.retransform;

import com.aopbuddy.infrastructure.TypeElementMatcher;
import lombok.Data;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.instrument.ClassFileTransformer;

import static com.aopbuddy.retransform.Context.getDefaultIgnore;


public class TransformerController {

    public static ClassFileTransformer buddyTransformer;
}

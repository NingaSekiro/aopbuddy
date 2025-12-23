package com.aopbuddy.retransform;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.aopbuddy.agent.MethodListener;
import com.aopbuddy.aspect.MethodPointcut;
import com.aopbuddytest.TargetService;
import com.aopbuddytest.TestHelper;
import java.lang.instrument.Instrumentation;
import lombok.SneakyThrows;
import net.bytebuddy.agent.ByteBuddyAgent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestMain {

  @SneakyThrows
  @BeforeEach
  public void init() {
    Instrumentation inst = ByteBuddyAgent.install();
    TestHelper.appendSpyJar(inst);
    Context.init(inst);
  }

  @AfterEach
  public void cleanup() {
    Context.ADVISORS.clear();
  }


  @Test
  public void addMethodPointcut() {
    Context.init(null);
    TargetService svc = new TargetService();
    MethodPointcut pointcut = MethodPointcut.of(
        "com.aopbuddytest.TargetService", "greetString", "(..)");
    Listener listener = new MethodListener();
    Context.registerAdvisor(pointcut, listener);
    String again = svc.greetString("again");
  }

  @Test
  public void deleteMethodPointcut() {
    Context.init(null);
    TargetService svc = new TargetService();
    MethodPointcut pointcut = MethodPointcut.of(
        "com.aopbuddytest.TargetService", "greetString", "(..)");
    Listener listener = new MethodListener();
    Context.registerAdvisor(pointcut, listener);
    String again = svc.greetString("again");
    assertEquals("mocked", again);
    Context.unregisterAdvisor(pointcut, MethodListener.class.getName());
    again = svc.greetString("again");
    assertEquals("Num 1", again);
  }

}

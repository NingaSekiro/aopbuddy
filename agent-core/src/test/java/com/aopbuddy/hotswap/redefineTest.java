package com.aopbuddy.hotswap;

import com.alibaba.bytekit.utils.AgentUtils;
import com.aopbuddy.record.CallRecord;
import com.aopbuddy.record.CallRecordDo;
import java.io.IOException;
import java.lang.instrument.UnmodifiableClassException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

public class redefineTest {

  @Test
  public void redefine() throws IOException, UnmodifiableClassException, ClassNotFoundException {
    Path classPath = Paths.get(
        "D:\\Code\\aopbuddy\\agent-core\\src\\test\\java\\com\\test\\CallRecordDo.class");
    byte[] bytes = Files.readAllBytes(classPath);
    AgentUtils.reTransform(CallRecordDo.class, bytes);
    CallRecordDo.toCallRecordDo(new CallRecord());
  }

  @Test
  public void origin() throws IOException, UnmodifiableClassException, ClassNotFoundException {
    CallRecordDo.toCallRecordDo(new CallRecord());
  }
}

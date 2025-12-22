package com.aopbuddy.record;

import lombok.Data;

@Data
public class ClassFilePath {

  private String className;
  private String fullPath;
  private String payload;
}
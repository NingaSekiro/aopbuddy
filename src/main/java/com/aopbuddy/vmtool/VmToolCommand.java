package com.aopbuddy.vmtool;

import arthas.VmTool;
import com.taobao.arthas.common.IOUtils;
import com.taobao.arthas.common.VmToolUtils;
import lombok.extern.slf4j.Slf4j;


import java.io.*;
import java.lang.instrument.Instrumentation;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.security.CodeSource;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


@Slf4j
public class VmToolCommand {

    /**
     * default value 10
     */
    private int limit;

    private static VmTool vmTool = null;

    public static VmTool vmToolInstance() {
        if (vmTool != null) {
            return vmTool;
        } else {
            try {
                Path tempFile = Files.createTempFile("ArthasJniLibrary", null, (FileAttribute<?>[]) new FileAttribute[0]);
                Files.copy(Objects.<InputStream>requireNonNull(VmToolCommand.class.getResourceAsStream("/lib/" + VmToolUtils.detectLibName())), tempFile, new CopyOption[]{StandardCopyOption.REPLACE_EXISTING});
                tempFile.toFile().deleteOnExit();
                vmTool = VmTool.getInstance(tempFile.toAbsolutePath().toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return vmTool;
    }


}

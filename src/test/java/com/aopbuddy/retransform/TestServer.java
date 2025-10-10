package com.aopbuddy.retransform;

import cn.hutool.http.HttpUtil;
import com.aopbuddy.servlet.EvalServlet;

public class TestServer {
    public static void main(String[] args) {
        HttpUtil.createServer(8888)
                .addAction("/eval", new EvalServlet())
                .start();
    }
}

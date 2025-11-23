package com.aopbuddy.bytekit;


import com.aopbuddy.infrastructure.StringUtils;
import com.aopbuddy.retransform.Context;
import com.aopbuddy.retransform.Listener;

import java.util.List;

/**
 * <pre>
 * 怎么从 className|methodDesc 到 id 对应起来？？
 * 当id少时，可以id自己来判断是否符合？
 *
 * 如果是每个 className|methodDesc 为 key ，是否
 * </pre>
 *
 * @author hengyunabc 2020-04-24
 */
public class SpyImpl extends SpyAPI.AbstractSpy {

    @Override
    public void atEnter(Class<?> clazz, String methodInfo, Object target, Object[] args) {
        String[] info = StringUtils.splitMethodInfo(methodInfo);
        String methodName = info[1];
        List<Listener> listeners = Context.getCache(Context.key(clazz.getName(), methodName));
        if (listeners != null) {
            for (Listener Listener : listeners) {
                Listener.before(target, clazz, MethodInfo.builder().className(clazz.getName()).methodName(methodName).methodAccess(info[0]).methodDesc(info[2]).build(), args);
            }
        }

    }

    @Override
    public void atExit(Class<?> clazz, String methodInfo, Object target, Object[] args, Object returnObject) {

        String[] info = StringUtils.splitMethodInfo(methodInfo);
        String methodName = info[1];
        List<Listener> listeners = Context.getCache(Context.key(clazz.getName(), methodName));

        if (listeners != null) {
            for (Listener Listener : listeners) {
                Listener.after(target, clazz, MethodInfo.builder().className(clazz.getName()).methodName(methodName).methodAccess(info[0]).methodDesc(info[2]).build(), args, returnObject);
            }
        }
    }

    @Override
    public void atExceptionExit(Class<?> clazz, String methodInfo, Object target, Object[] args, Throwable throwable) {
        String[] info = StringUtils.splitMethodInfo(methodInfo);
        String methodName = info[1];
        List<Listener> listeners = Context.getCache(Context.key(clazz.getName(), methodName));
        if (listeners != null) {
            for (Listener Listener : listeners) {
                Listener.onException(target, clazz, MethodInfo.builder().className(clazz.getName()).methodName(methodName).methodAccess(info[0]).methodDesc(info[2]).build(), args, throwable);
            }
        }
    }

    @Override
    public void atBeforeInvoke(Class<?> clazz, String invokeInfo, Object target) {

    }

    @Override
    public void atAfterInvoke(Class<?> clazz, String invokeInfo, Object target) {

    }

    @Override
    public void atInvokeException(Class<?> clazz, String invokeInfo, Object target, Throwable throwable) {

    }

}
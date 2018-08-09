package com.huya.marksman.ipc.server;

import android.os.Bundle;
import android.text.TextUtils;

import com.huya.marksman.ipc.IpcConstant;

import java.lang.reflect.Method;

/**
 * Created by charles on 2018/8/9.
 */

public class Dispatcher {
    private IpcServer ipcServer;

    public Dispatcher(IpcServer ipcServer) {
        this.ipcServer = ipcServer;
    }

    public String dispatch(String ipcMethod, Bundle ipcExtras) {
        Method targetMethod = null;
        Method[] methods = ipcServer.getClass().getDeclaredMethods();

        if (TextUtils.isEmpty(ipcMethod) || (methods == null)) {
            return null;
        }

        for (Method method : methods) {
            if (ipcMethod.equals(method.getName())) {
                targetMethod = method;
                break;
            }
        }

        String ret = null;
        try {
            int argCount = ipcExtras.getInt(IpcConstant.ARG_COUNT);
            Object[] args = new Object[argCount];
            Class[] paramTypes = targetMethod.getParameterTypes();
            for (int i = 0; i < argCount; i++) {
                if (paramTypes[i] == String.class) {
                    args[i] = ipcExtras.getString(IpcConstant.ARG_KEYS[i]);
                }
                else if (paramTypes[i] == Boolean.class || paramTypes[i] == boolean.class) {
                    args[i] = ipcExtras.getBoolean(IpcConstant.ARG_KEYS[i]);
                }
                else if (paramTypes[i] == Integer.class || paramTypes[i] == int.class) {
                    args[i] = ipcExtras.getInt(IpcConstant.ARG_KEYS[i]);
                }
                else if (paramTypes[i] == Long.class || paramTypes[i] == long.class) {
                    args[i] = ipcExtras.getLong(IpcConstant.ARG_KEYS[i]);
                }
                else {
                    args[i] = null;
                }
            }

            Object invokeRet = targetMethod.invoke(ipcServer, (Object[]) args);
            if (invokeRet != null) {
                ret = invokeRet.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }
}

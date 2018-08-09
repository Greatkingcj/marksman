package com.huya.marksman.ipc.client;

import com.huya.marksman.ipc.proxy.IpcServerStubProxy;
import com.huya.marksman.ipc.server.stub.RemoteIpcServerStub;

/**
 * Created by charles on 2018/8/9.
 */

public enum RemoteIpcClient {
    /**
     *
     */
    SINGLETON;

    RemoteIpcServerStub stub;
    RemoteIpcClient() {
        stub = IpcServerStubProxy.create(RemoteIpcServerStub.class);
    }

    public static RemoteIpcServerStub getInstance() {
        return SINGLETON.stub;
    }
}

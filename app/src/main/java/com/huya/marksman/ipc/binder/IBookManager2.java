package com.huya.marksman.ipc.binder;

import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

import com.huya.marksman.ipc.aidl.Book;

import java.util.List;

/**
 * Created by charles on 2018/8/9.
 */

public interface IBookManager2 extends IInterface{
    static final String DESCRIPTOR = "com.huya.marksman.ipc.binder.IBookManager2";
    static final int TRANSACTION_getBookList = IBinder.FIRST_CALL_TRANSACTION + 0;
    static final int TRANSACTION_addBook = IBinder.FIRST_CALL_TRANSACTION + 1;

    public List<Book> getBookList() throws RemoteException;

    public void addBook(Book book) throws RemoteException;
}

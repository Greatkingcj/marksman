package com.huya.marksman.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.huya.marksman.ipc.aidl.Book;
import com.huya.marksman.ipc.aidl.IBookManager;

import java.util.ArrayList;
import java.util.List;

public class BookManagerService extends Service {

    private List<Book> books = new ArrayList<>();

    public BookManagerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("BookManagerService", "BookManagerService: onCreate");
        books.add(new Book(1, "book1"));
        books.add(new Book(2, "book2"));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("BookManagerService", "BookManagerService: onDestroy");
    }

    private Binder mBinder = new IBookManager.Stub() {
        @Override
        public List<Book> getBookList() throws RemoteException {
            return books;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            books.add(book);
        }
    };
}

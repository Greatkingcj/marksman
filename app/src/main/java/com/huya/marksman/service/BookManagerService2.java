package com.huya.marksman.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.huya.marksman.ipc.aidl.Book;
import com.huya.marksman.ipc.binder.BookManagerImpl2;

import java.util.ArrayList;
import java.util.List;

public class BookManagerService2 extends Service {

    private List<Book> books = new ArrayList<>();

    public BookManagerService2() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("BookManagerService2", "BookManagerService2: onCreate");
        books.add(new Book(1, "bookB1"));
        books.add(new Book(2, "bookB2"));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("BookManagerService2", "BookManagerService2: onCreate");
    }

    private Binder mBinder = new BookManagerImpl2() {
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

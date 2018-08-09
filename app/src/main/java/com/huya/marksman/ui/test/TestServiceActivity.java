package com.huya.marksman.ui.test;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.huya.marksman.R;
import com.huya.marksman.ipc.aidl.Book;
import com.huya.marksman.ipc.aidl.IBookManager;
import com.huya.marksman.ipc.binder.BookManagerImpl2;
import com.huya.marksman.ipc.binder.IBookManager2;
import com.huya.marksman.ipc.client.RemoteIpcClient;
import com.huya.marksman.service.BookManagerService;
import com.huya.marksman.service.BookManagerService2;
import com.huya.marksman.service.MyService;

import java.util.List;

public class TestServiceActivity extends AppCompatActivity {
    TextView textView;
    Intent intentProvider;
    Intent intentAIDL;
    Intent intentBinder;
    private ServiceConnection connectionAIDL;
    private ServiceConnection connectionBinder;
    StringBuilder stringBuilder = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_service);
        initView();
    }

    private void initView() {
        textView = findViewById(R.id.tv_result_from_service);
        intentProvider = new Intent(this, MyService.class);
        intentAIDL = new Intent(this, BookManagerService.class);
        intentBinder = new Intent(this, BookManagerService2.class);
    }

    public void testContentProvider(View view) {
        startService(intentProvider);
        String result1 = RemoteIpcClient.getInstance().switchVideo();
        String result2 = RemoteIpcClient.getInstance().updateSetting();
        textView.setText("返回的值： " + result1 + result2);
    }

    public void testAIDL(View view) {
        if (connectionAIDL == null) {
            connectionAIDL = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    IBookManager bookManager = IBookManager.Stub.asInterface(service);
                    try {
                        bookManager.addBook(new Book(3, "book3"));
                        List<Book> books = bookManager.getBookList();
                        for (Book book : books) {
                            stringBuilder.append(book.bookName + book.bookId);
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                    if (stringBuilder != null) {
                        textView.setText(stringBuilder.toString());
                    }
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {

                }
            };
        }

        bindService(intentAIDL, connectionAIDL, BIND_AUTO_CREATE);
    }

    public void testBinder(View view) {
        if (connectionBinder == null) {
            connectionBinder = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    IBookManager2 bookManager2 = BookManagerImpl2.asInterface(service);
                    try {
                        bookManager2.addBook(new Book(3, "bookB3"));
                        List<Book> books = bookManager2.getBookList();
                        for (Book book : books) {
                            stringBuilder.append(book.bookName + book.bookId);
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                    if (stringBuilder != null) {
                        textView.setText(stringBuilder.toString());
                    }
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {

                }
            };
        }

        bindService(intentBinder, connectionBinder, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(intentProvider);
        unbindService(connectionAIDL);
        unbindService(connectionBinder);
    }
}

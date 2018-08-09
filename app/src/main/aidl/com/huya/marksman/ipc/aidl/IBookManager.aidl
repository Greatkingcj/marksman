// IBookManager.aidl
package com.huya.marksman.ipc.aidl;

// Declare any non-default types here with import statements
import com.huya.marksman.ipc.aidl.Book;
interface IBookManager {
    List<Book> getBookList();
    void addBook(in Book book);
}

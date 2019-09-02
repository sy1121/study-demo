// BookController.aidl
package com.example.yishe.aidlstudyserver;
import com.example.yishe.aidlstudyserver.Book;

// Declare any non-default types here with import statements

interface BookController {

    List<Book> getBookList();

    void addBookInOut(inout Book book);

}
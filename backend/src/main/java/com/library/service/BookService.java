package com.library.service;

import com.library.entity.Book;
import com.library.util.DateUtil;
import com.library.util.IdGenerator;
import com.library.util.JsonFileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {
    
    private static final String BOOK_FILE = "books.json";
    
    @Autowired
    private JsonFileUtil jsonFileUtil;
    
    @PostConstruct
    public void init() {
        List<Book> books = getAllBooks();
        if (!books.isEmpty()) {
            Long maxId = books.stream()
                    .map(Book::getId)
                    .max(Long::compareTo)
                    .orElse(0L);
            IdGenerator.initBookId(maxId);
        }
    }
    
    public List<Book> getAllBooks() {
        return jsonFileUtil.readList(BOOK_FILE, Book.class);
    }
    
    public Book getBookById(Long id) {
        List<Book> books = getAllBooks();
        return books.stream()
                .filter(book -> book.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    public List<Book> searchBooks(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return getAllBooks();
        }
        List<Book> books = getAllBooks();
        String lowerKeyword = keyword.toLowerCase();
        return books.stream()
                .filter(book -> 
                    (book.getName() != null && book.getName().toLowerCase().contains(lowerKeyword)) ||
                    (book.getAuthor() != null && book.getAuthor().toLowerCase().contains(lowerKeyword)) ||
                    (book.getIsbn() != null && book.getIsbn().toLowerCase().contains(lowerKeyword)) ||
                    (book.getCategory() != null && book.getCategory().toLowerCase().contains(lowerKeyword))
                )
                .collect(Collectors.toList());
    }
    
    public Book addBook(Book book) {
        List<Book> books = getAllBooks();
        book.setId(IdGenerator.nextBookId());
        book.setCreateTime(DateUtil.getCurrentDateTime());
        book.setUpdateTime(DateUtil.getCurrentDateTime());
        if (book.getAvailableStock() == null) {
            book.setAvailableStock(book.getTotalStock());
        }
        books.add(book);
        jsonFileUtil.writeList(BOOK_FILE, books);
        return book;
    }
    
    public Book updateBook(Book book) {
        Book existingBook = getBookById(book.getId());
        if (existingBook == null) {
            return null;
        }
        List<Book> books = getAllBooks();
        books.removeIf(b -> b.getId().equals(book.getId()));
        book.setCreateTime(existingBook.getCreateTime());
        book.setUpdateTime(DateUtil.getCurrentDateTime());
        books.add(book);
        jsonFileUtil.writeList(BOOK_FILE, books);
        return book;
    }
    
    public boolean deleteBook(Long id) {
        Book book = getBookById(id);
        if (book == null) {
            return false;
        }
        List<Book> books = getAllBooks();
        books.removeIf(b -> b.getId().equals(id));
        jsonFileUtil.writeList(BOOK_FILE, books);
        return true;
    }
    
    public boolean decreaseAvailableStock(Long bookId) {
        Book book = getBookById(bookId);
        if (book == null || book.getAvailableStock() <= 0) {
            return false;
        }
        book.setAvailableStock(book.getAvailableStock() - 1);
        book.setUpdateTime(DateUtil.getCurrentDateTime());
        updateBook(book);
        return true;
    }
    
    public boolean increaseAvailableStock(Long bookId) {
        Book book = getBookById(bookId);
        if (book == null || book.getAvailableStock() >= book.getTotalStock()) {
            return false;
        }
        book.setAvailableStock(book.getAvailableStock() + 1);
        book.setUpdateTime(DateUtil.getCurrentDateTime());
        updateBook(book);
        return true;
    }
}

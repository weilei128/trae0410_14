package com.library.controller;

import com.library.common.Result;
import com.library.entity.Book;
import com.library.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {
    
    @Autowired
    private BookService bookService;
    
    @GetMapping
    public Result<List<Book>> list(@RequestParam(required = false) String keyword) {
        List<Book> books = bookService.searchBooks(keyword);
        return Result.success(books);
    }
    
    @GetMapping("/{id}")
    public Result<Book> getById(@PathVariable Long id) {
        Book book = bookService.getBookById(id);
        if (book == null) {
            return Result.error("图书不存在");
        }
        return Result.success(book);
    }
    
    @PostMapping
    public Result<Book> add(@RequestBody Book book) {
        if (book.getName() == null || book.getName().trim().isEmpty()) {
            return Result.error("图书名称不能为空");
        }
        if (book.getTotalStock() == null || book.getTotalStock() < 0) {
            return Result.error("库存数量无效");
        }
        Book savedBook = bookService.addBook(book);
        return Result.success("添加成功", savedBook);
    }
    
    @PutMapping("/{id}")
    public Result<Book> update(@PathVariable Long id, @RequestBody Book book) {
        Book existingBook = bookService.getBookById(id);
        if (existingBook == null) {
            return Result.error("图书不存在");
        }
        book.setId(id);
        Book updatedBook = bookService.updateBook(book);
        return Result.success("更新成功", updatedBook);
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        boolean success = bookService.deleteBook(id);
        if (!success) {
            return Result.error("删除失败，图书不存在");
        }
        return Result.success("删除成功", null);
    }
}

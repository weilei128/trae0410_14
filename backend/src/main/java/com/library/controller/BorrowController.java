package com.library.controller;

import com.library.common.Result;
import com.library.entity.BorrowRecord;
import com.library.service.BorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/borrows")
public class BorrowController {
    
    @Autowired
    private BorrowService borrowService;
    
    @GetMapping
    public Result<List<BorrowRecord>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        List<BorrowRecord> records = borrowService.searchRecords(keyword, status);
        return Result.success(records);
    }
    
    @GetMapping("/{id}")
    public Result<BorrowRecord> getById(@PathVariable Long id) {
        BorrowRecord record = borrowService.getRecordById(id);
        if (record == null) {
            return Result.error("借阅记录不存在");
        }
        return Result.success(record);
    }
    
    @GetMapping("/overdue")
    public Result<List<BorrowRecord>> getOverdue() {
        List<BorrowRecord> records = borrowService.getOverdueRecords();
        return Result.success(records);
    }
    
    @PostMapping("/borrow")
    public Result<BorrowRecord> borrow(@RequestBody Map<String, Object> params) {
        try {
            Long bookId = Long.valueOf(params.get("bookId").toString());
            String borrowerName = (String) params.get("borrowerName");
            String borrowerPhone = (String) params.get("borrowerPhone");
            Integer days = params.get("days") != null ? 
                    Integer.valueOf(params.get("days").toString()) : null;
            
            if (borrowerName == null || borrowerName.trim().isEmpty()) {
                return Result.error("借阅人姓名不能为空");
            }
            
            BorrowRecord record = borrowService.borrowBook(bookId, borrowerName, borrowerPhone, days);
            return Result.success("借阅成功", record);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("借阅失败：" + e.getMessage());
        }
    }
    
    @PostMapping("/return/{id}")
    public Result<BorrowRecord> returnBook(@PathVariable Long id) {
        try {
            BorrowRecord record = borrowService.returnBook(id);
            return Result.success("归还成功", record);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("归还失败：" + e.getMessage());
        }
    }
}

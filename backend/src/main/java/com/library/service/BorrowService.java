package com.library.service;

import com.library.entity.Book;
import com.library.entity.BorrowRecord;
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
public class BorrowService {
    
    private static final String BORROW_FILE = "borrows.json";
    private static final int DEFAULT_BORROW_DAYS = 30;
    
    @Autowired
    private JsonFileUtil jsonFileUtil;
    
    @Autowired
    private BookService bookService;
    
    @PostConstruct
    public void init() {
        List<BorrowRecord> records = getAllRecords();
        if (!records.isEmpty()) {
            Long maxId = records.stream()
                    .map(BorrowRecord::getId)
                    .max(Long::compareTo)
                    .orElse(0L);
            IdGenerator.initBorrowId(maxId);
        }
    }
    
    public List<BorrowRecord> getAllRecords() {
        return jsonFileUtil.readList(BORROW_FILE, BorrowRecord.class);
    }
    
    public BorrowRecord getRecordById(Long id) {
        List<BorrowRecord> records = getAllRecords();
        return records.stream()
                .filter(record -> record.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    public List<BorrowRecord> searchRecords(String keyword, Integer status) {
        List<BorrowRecord> records = getAllRecords();
        
        if (StringUtils.hasText(keyword)) {
            String lowerKeyword = keyword.toLowerCase();
            records = records.stream()
                    .filter(record -> 
                        (record.getBookName() != null && record.getBookName().toLowerCase().contains(lowerKeyword)) ||
                        (record.getBorrowerName() != null && record.getBorrowerName().toLowerCase().contains(lowerKeyword)) ||
                        (record.getBorrowerPhone() != null && record.getBorrowerPhone().contains(keyword))
                    )
                    .collect(Collectors.toList());
        }
        
        if (status != null) {
            records = records.stream()
                    .filter(record -> record.getStatus().equals(status))
                    .collect(Collectors.toList());
        }
        
        return records;
    }
    
    public List<BorrowRecord> getRecordsByBorrower(String borrowerName) {
        List<BorrowRecord> records = getAllRecords();
        return records.stream()
                .filter(record -> borrowerName.equals(record.getBorrowerName()))
                .collect(Collectors.toList());
    }
    
    public BorrowRecord borrowBook(Long bookId, String borrowerName, String borrowerPhone, Integer days) {
        Book book = bookService.getBookById(bookId);
        if (book == null) {
            throw new RuntimeException("图书不存在");
        }
        
        if (book.getAvailableStock() <= 0) {
            throw new RuntimeException("库存不足，无法借阅");
        }
        
        List<BorrowRecord> records = getAllRecords();
        boolean hasUnreturned = records.stream()
                .anyMatch(r -> r.getBookId().equals(bookId) && 
                              r.getBorrowerName().equals(borrowerName) && 
                              r.getStatus() == BorrowRecord.STATUS_BORROWED);
        if (hasUnreturned) {
            throw new RuntimeException("您已借阅此书且未归还，不能重复借阅");
        }
        
        if (!bookService.decreaseAvailableStock(bookId)) {
            throw new RuntimeException("库存更新失败");
        }
        
        BorrowRecord record = new BorrowRecord();
        record.setId(IdGenerator.nextBorrowId());
        record.setBookId(bookId);
        record.setBookName(book.getName());
        record.setBorrowerName(borrowerName);
        record.setBorrowerPhone(borrowerPhone);
        record.setBorrowDate(DateUtil.getCurrentDate());
        
        int borrowDays = (days != null && days > 0) ? days : DEFAULT_BORROW_DAYS;
        record.setDueDate(DateUtil.addDays(record.getBorrowDate(), borrowDays));
        record.setStatus(BorrowRecord.STATUS_BORROWED);
        record.setOverdue(false);
        
        records.add(record);
        jsonFileUtil.writeList(BORROW_FILE, records);
        
        return record;
    }
    
    public BorrowRecord returnBook(Long recordId) {
        BorrowRecord record = getRecordById(recordId);
        if (record == null) {
            throw new RuntimeException("借阅记录不存在");
        }
        
        if (record.getStatus() == BorrowRecord.STATUS_RETURNED) {
            throw new RuntimeException("该图书已归还，请勿重复操作");
        }
        
        if (!bookService.increaseAvailableStock(record.getBookId())) {
            throw new RuntimeException("库存更新失败");
        }
        
        List<BorrowRecord> records = getAllRecords();
        records.removeIf(r -> r.getId().equals(recordId));
        
        record.setStatus(BorrowRecord.STATUS_RETURNED);
        record.setReturnDate(DateUtil.getCurrentDate());
        record.setOverdue(DateUtil.isOverdue(record.getDueDate()));
        
        records.add(record);
        jsonFileUtil.writeList(BORROW_FILE, records);
        
        return record;
    }
    
    public void updateOverdueStatus() {
        List<BorrowRecord> records = getAllRecords();
        boolean updated = false;
        
        for (BorrowRecord record : records) {
            if (record.getStatus() == BorrowRecord.STATUS_BORROWED) {
                boolean isOverdue = DateUtil.isOverdue(record.getDueDate());
                if (isOverdue != record.getOverdue()) {
                    record.setOverdue(isOverdue);
                    if (isOverdue) {
                        record.setStatus(BorrowRecord.STATUS_OVERDUE);
                    }
                    updated = true;
                }
            }
        }
        
        if (updated) {
            jsonFileUtil.writeList(BORROW_FILE, records);
        }
    }
    
    public List<BorrowRecord> getOverdueRecords() {
        updateOverdueStatus();
        List<BorrowRecord> records = getAllRecords();
        return records.stream()
                .filter(record -> Boolean.TRUE.equals(record.getOverdue()))
                .collect(Collectors.toList());
    }
}

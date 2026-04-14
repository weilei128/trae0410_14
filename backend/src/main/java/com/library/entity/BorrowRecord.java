package com.library.entity;

import lombok.Data;
import java.io.Serializable;

@Data
public class BorrowRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private Long bookId;
    private String bookName;
    private String borrowerName;
    private String borrowerPhone;
    private String borrowDate;
    private String dueDate;
    private String returnDate;
    private Integer status;
    private Boolean overdue;
    
    public static final int STATUS_BORROWED = 0;
    public static final int STATUS_RETURNED = 1;
    public static final int STATUS_OVERDUE = 2;
}

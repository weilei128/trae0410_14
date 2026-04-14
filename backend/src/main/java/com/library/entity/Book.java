package com.library.entity;

import lombok.Data;
import java.io.Serializable;

@Data
public class Book implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String name;
    private String author;
    private String isbn;
    private String category;
    private Integer totalStock;
    private Integer availableStock;
    private String createTime;
    private String updateTime;
}

package com.tzp.es.service.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {
    private Long id;
    private String name;
    private String sex;
    private Integer age;
}

package com.tui.proof;

import lombok.*;

/**
 * @author akazmierczak
 * @create 07.08.2022
 */
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Violation {
    @Getter
    private String fieldName;
    @Getter
    private String message;
}

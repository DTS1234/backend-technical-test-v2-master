package com.tui.proof.web;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Paging {

    private Integer pageNumber;
    private Integer pageSize;
    private String sort;
    private Order order;
    private Integer limitTo;

}

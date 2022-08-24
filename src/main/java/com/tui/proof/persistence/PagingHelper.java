package com.tui.proof.persistence;

import com.tui.proof.web.Order;
import com.tui.proof.web.Paging;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Optional;

public class PagingHelper {

    public static final int DEFAUL_PAGE_NUM = 0;
    public static final int DEFAUL_PAGE_SIZE = 10;
    public static final Order DEFAULT_ORDER = Order.DESC;
    public static final String DEFAUL_SORT = "email";

    public static Pageable getPageable(Paging paging) {
        int pageNumber = Optional.ofNullable(paging.getPageNumber()).orElse(DEFAUL_PAGE_NUM);
        int pageSize = Optional.ofNullable(paging.getPageSize()).orElse(DEFAUL_PAGE_SIZE);
        Order order = Optional.ofNullable(paging.getOrder()).orElse(DEFAULT_ORDER);
        String sort = Optional.ofNullable(paging.getSort()).orElse(DEFAUL_SORT);

        return PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.valueOf(order.toValue()), sort));
    }

}

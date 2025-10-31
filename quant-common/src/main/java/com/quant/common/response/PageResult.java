package com.quant.common.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * Page Result
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PageResult<T> extends Result<List<T>> {

    private static final long serialVersionUID = 1L;

    /**
     * Current page
     */
    private Integer page;

    /**
     * Page size
     */
    private Integer size;

    /**
     * Total records
     */
    private Long total;

    /**
     * Total pages
     */
    private Integer totalPages;

    public static <T> PageResult<T> success(List<T> data, Integer page, Integer size, Long total) {
        PageResult<T> result = new PageResult<>();
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMessage(ResultCode.SUCCESS.getMessage());
        result.setData(data);
        result.setPage(page);
        result.setSize(size);
        result.setTotal(total);
        result.setTotalPages((int) Math.ceil((double) total / size));
        return result;
    }
}

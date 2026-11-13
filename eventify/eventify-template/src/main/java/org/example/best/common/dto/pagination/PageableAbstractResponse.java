package org.example.best.common.dto.pagination;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PageableAbstractResponse {
    private PageableObject pageable;
    private Boolean last;
    private Integer totalElements;
    private Integer totalPages;
    private Integer size;
    private Integer number;
    private SortObject sort;
    private Boolean first;
    private Integer numberOfElements;
    private Boolean empty;
}

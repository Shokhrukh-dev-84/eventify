package org.example.best.common.dto.pagination;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PageableObject {

    private Integer offset;
    private Boolean paged;
    private Integer pageNumber;
    private Integer pageSize;
    private Boolean unpaged;

}

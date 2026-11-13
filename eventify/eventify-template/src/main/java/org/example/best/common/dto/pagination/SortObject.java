package org.example.best.common.dto.pagination;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class SortObject {
    private Boolean empty;
    private Boolean sorted;
    private Boolean unsorted;

}

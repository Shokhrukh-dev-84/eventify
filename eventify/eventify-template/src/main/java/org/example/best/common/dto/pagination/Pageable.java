package org.example.best.common.dto.pagination;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Pageable {
    @Min(0)
    private Integer page;

    @Min(1)
    private Integer size;

    List<String> sort;

}

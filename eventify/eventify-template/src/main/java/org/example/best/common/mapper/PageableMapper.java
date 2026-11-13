package org.example.best.common.mapper;

import org.example.best.common.dto.pagination.Pageable;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface PageableMapper {

    default org.springframework.data.domain.Pageable toNativePageable(Pageable customPageable) {

        if (customPageable == null) {
            return org.springframework.data.domain.PageRequest.of(0, 10);
        }

        int page = customPageable.getPage() != null ? customPageable.getPage() : 0;
        int size = customPageable.getSize() != null ? customPageable.getSize() : 10;

        List<Sort.Order> orders = new ArrayList<>();
        if (customPageable.getSort() != null) {
            for (String sortRule : customPageable.getSort()) {
                if (sortRule == null || sortRule.isBlank()) continue;

                String[] parts = sortRule.split(",");
                if (parts.length == 2) {
                    orders.add(new org.springframework.data.domain.Sort.Order(
                            org.springframework.data.domain.Sort.Direction.fromString(parts[1].trim().toUpperCase()),
                            parts[0].trim()
                    ));
                } else if (parts.length == 1) {
                    orders.add(org.springframework.data.domain.Sort.Order.asc(parts[0].trim()));
                }
            }
        }

        return orders.isEmpty()
                ? org.springframework.data.domain.PageRequest.of(page, size)
                : org.springframework.data.domain.PageRequest.of(page, size, org.springframework.data.domain.Sort.by(orders));
    }
}

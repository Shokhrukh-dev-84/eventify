package org.example.best.config;

import lombok.RequiredArgsConstructor;
import org.example.best.booking.repository.BookingRepository;
import org.springframework.stereotype.Component;

@Component("resourceSecurity")
@RequiredArgsConstructor
public class ResourceSecurityExpression {

    private final BookingRepository bookingRepository;

    public boolean isBookingOwner(Long id) {
        String currentUserName = SecurityUtils.getUserName();
        String userRole = String.valueOf(SecurityUtils.getUsersRole());

        if("ROLE_ADMIN".equals(userRole)) {
            return true;
        }

       return bookingRepository.findById(id)
               .map(booking -> booking.getUser().getUsername().equals(currentUserName))
               .orElse(false);
    }

}

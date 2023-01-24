package com.korit.library.service;

import com.korit.library.entity.RentalDtl;
import com.korit.library.entity.RentalMst;
import com.korit.library.exception.CustomRentalException;
import com.korit.library.repository.RentalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RentalService {

    @Autowired
    private RentalRepository rentalRepository;

    public void rentalOne(int userId, int bookId) {
        availability(userId);
        availabilityLoan(bookId);

        RentalMst rentalMst = RentalMst.builder()
                .userId(userId)
                .build();

        rentalRepository.saveRentalMst(rentalMst);

        // AI로 증가된 값을 RentalId에 set, bookId는 매개변수로 지정받은 것
        List<RentalDtl> rentalDtlList = new ArrayList<>();
        RentalDtl rentalDtl = RentalDtl.builder()
                .rentalId(rentalMst.getRentalId())
                .bookId(bookId)
                .build();

        rentalDtlList.add(rentalDtl);
        rentalRepository.saveRentalDtl(rentalDtlList);
    }

    private void availability(int userId) {
        int rentalCount = rentalRepository.rentalAvailability(userId);
        if(rentalCount > 2) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("rentalCountError", "대여 횟수를 초과하였습니다.");
            throw new CustomRentalException(errorMap);
        }
    }

    private void availabilityLoan(int bookId){
        int loanCount = rentalRepository.loanRental(bookId);
        if(loanCount > 0) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("loanError", "현재 대여 중인 도서입니다.");
            throw new CustomRentalException(errorMap);
        }
    }
}


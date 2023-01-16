package com.korit.library.service.admin;

import com.korit.library.exception.CustomValidationException;
import com.korit.library.repository.BookRepository;
import com.korit.library.web.dto.*;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class BookService {

    @Value("${file.path}") // yml에 있는 경로를 등록하는 거다.
    private String filePath;

    @Autowired
    private BookRepository bookRepository;

    public List<BookMstDto> searchBook(SearchReqDto searchReqDto) {
        searchReqDto.setIndex();
        return bookRepository.searchBook(searchReqDto);
    }

    public List<CategoryDto> getCategories() {
        return bookRepository.findAllCategory();
    }

    public void registerBook(BookReqDto bookReqDto) {
        duplicateBookCode(bookReqDto.getBookCode());
        bookRepository.saveBook(bookReqDto);
    }

    private void duplicateBookCode(String bookCode) {
        BookMstDto bookMstDto = bookRepository.findBookByBookCode(bookCode);
        if(bookMstDto != null) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("bookCode", "이미 존재하는 도서코드입니다.");

            throw new CustomValidationException(errorMap);
        }
    }

    public void modifyBook(BookReqDto bookReqDto) {
        bookRepository.updateBookByBookCode(bookReqDto);
    }

    public void maintainModifyBook(BookReqDto bookReqDto) {
        bookRepository.maintainUpdateBookByBookCode(bookReqDto);
    }

    public void deleteBook(String bookCode) {
        bookRepository.deleteBookByBookCode(bookCode);
    }

    public void registerBookImages(String bookCode, List<MultipartFile> files) {
        if (files.size() < 1) {
            Map<String, String> errorMap = new HashMap<String, String>();
            errorMap.put("files", "이미지를 선택하세요.");

            throw new CustomValidationException(errorMap);
        }

        List<BookImageDto> bookImageDtos = new ArrayList<BookImageDto>();

        files.forEach(file -> {
            String originFileName = file.getOriginalFilename();
            String extension = originFileName.substring(originFileName.lastIndexOf(".")); // 확장자명 가져오기 (ex) .png)
            String tempFileName = UUID.randomUUID().toString().replaceAll("-", "") + extension;

            Path uploadPath = Paths.get(filePath + "/book/" + tempFileName); // 경로만 지정

            File f = new File(filePath + "/book/"); // 경로를 객체로 저장
            if (!f.exists()) { // !(not)을 붙여 경로가 없으면 True
                f.mkdirs(); // 모든 경로를 다 생성해라
            }

            try {
                Files.write(uploadPath, file.getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            BookImageDto bookImageDto = BookImageDto.builder()
                    .bookCode(bookCode)
                    .saveName(tempFileName)
                    .originName(originFileName)
                    .build();

            bookImageDtos.add(bookImageDto);
        });
        bookRepository.registerBookImages(bookImageDtos);

    }
}

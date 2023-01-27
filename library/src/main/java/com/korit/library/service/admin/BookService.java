package com.korit.library.service.admin;

import com.korit.library.entity.BookImage;
import com.korit.library.entity.BookMst;
import com.korit.library.entity.CategoryView;
import com.korit.library.exception.CustomValidationException;
import com.korit.library.repository.BookRepository;
import com.korit.library.web.dto.*;
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

    public int getBookTotalCount(SearchNumberListReqDto searchNumberListReqDto) {
        return bookRepository.getBookTotalCount(searchNumberListReqDto);
    }

    public List<BookMst> searchBook(SearchReqDto searchReqDto) {
        searchReqDto.setIndex();
        return bookRepository.searchBook(searchReqDto);
    }

    public List<CategoryView> getCategories() {
        return bookRepository.findAllCategory();
    }

    public void registerBook(BookReqDto bookReqDto) {
        duplicateBookCode(bookReqDto.getBookCode());
        bookRepository.saveBook(bookReqDto);
    }

    private void duplicateBookCode(String bookCode) {
        BookMst bookMst = bookRepository.findBookByBookCode(bookCode);
        if(bookMst != null) {
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

    public void removeBooks(DeleteBooksReqDto deleteBooksReqDto) {
        bookRepository.deleteBooks(deleteBooksReqDto.getUserIds());
    }

    public void registerBookImages(String bookCode, List<MultipartFile> files) {
        if (files.size() < 1) {
            Map<String, String> errorMap = new HashMap<String, String>();
            errorMap.put("files", "이미지를 선택하세요.");

            throw new CustomValidationException(errorMap);
        }

        List<BookImage> bookImages = new ArrayList<BookImage>();

        files.forEach(file -> {
            String originFileName = file.getOriginalFilename();
            String extension = originFileName.substring(originFileName.lastIndexOf(".")); // 확장자명 가져오기 (ex) .png)
            String tempFileName = UUID.randomUUID().toString().replaceAll("-", "") + extension; // UUID로 바꿔주고 -을 공백으로 바꾸는 작업

            Path uploadPath = Paths.get(filePath + "book/" + tempFileName); // 경로만 지정

            // 경로가 없을 때 경로를 만드는 것(이 작업이 없으면 오류~)
            File f = new File(filePath + "book/"); // 경로를 객체로 저장
            if (!f.exists()) { // !(not)을 붙여 경로가 없으면 True
                f.mkdirs(); // 모든 경로를 다 생성해라
            }

            try {
                Files.write(uploadPath, file.getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            BookImage bookImage = BookImage.builder()
                    .bookCode(bookCode)
                    .saveName(tempFileName)
                    .originName(originFileName)
                    .build();

            bookImages.add(bookImage);
        });
        bookRepository.registerBookImages(bookImages);
    }

    public List<BookImage> getBooks(String bookCode) {
        return bookRepository.findBookImageAll(bookCode);
    }

    public void removeBookImage(int imageId) {
        BookImage bookImage = bookRepository.findBookImageByImageId(imageId);

        if (bookImage == null){
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("error", "존재하지 않는 이미지 ID입니다.");

            throw new CustomValidationException(errorMap);
        }

        if (bookRepository.deleteBookImage(imageId) > 0) {
            File file = new File(filePath + "book/" + bookImage.getSaveName());
            if(file.exists()) { // 경로가 존재한다면
                file.delete();
            }
        }
    }
}

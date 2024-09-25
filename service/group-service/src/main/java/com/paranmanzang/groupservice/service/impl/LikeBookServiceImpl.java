package com.paranmanzang.groupservice.service.impl;

import com.paranmanzang.groupservice.model.domain.LikeBookModel;
import com.paranmanzang.groupservice.model.entity.LikeBooks;
import com.paranmanzang.groupservice.model.repository.BookRepository;
import com.paranmanzang.groupservice.model.repository.LikeBooksRepository;
import com.paranmanzang.groupservice.service.LikeBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;

@Service
@RequiredArgsConstructor
public class LikeBookServiceImpl implements LikeBookService {
    private final LikeBooksRepository likeBooksRepository;
    private final BookRepository bookRepository;

    @Override
    public Object add(LikeBookModel likeBookModel) throws BindException {
        return bookRepository.findById(likeBookModel.getBookId())
                .map(book -> {
                    if (likeBooksRepository.existsByNicknameAndBook(likeBookModel.getNickname(), book)) {
                        return false;
                    }
                    likeBooksRepository.save(LikeBooks.builder()
                            .nickname(likeBookModel.getNickname())
                            .book(book)
                            .build());
                    return true;
                })
                .orElseThrow(() -> {
                    var bindException = new BindException(likeBookModel, "likeBookModel");
                    bindException.addError(new FieldError("LikeBookModel", "id", "해당 책을 찾을 수 없습니다."));
                    return bindException;
                });
    }

    @Override
    public Boolean remove(LikeBookModel likeBookModel) throws BindException {
        return likeBooksRepository.findById(likeBookModel.getId())
                .map(likeBooks -> {
                    likeBooksRepository.deleteById(likeBookModel.getId());
                    return true;
                })
                .orElseThrow(() -> {
                    var bindException = new BindException(likeBookModel, "likeBookModel");
                    bindException.addError(new FieldError("LikeBookModel", "id", "해당 책을 찾을 수 없습니다."));
                    return bindException;
                });
    }

    @Override
    public Page<LikeBookModel> findAllByNickname(String nickname, Pageable pageable) {
        return likeBooksRepository.findLikeBooksByNickname(nickname,pageable);
    }
}
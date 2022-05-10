package com.example.library.Repo;

import com.example.library.models.Books;
import com.example.library.models.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BooksRepository extends CrudRepository<Books, Long> {
    List<Books> findByTitle(String title);
    List<Books> findAllByCreator(User user);
}

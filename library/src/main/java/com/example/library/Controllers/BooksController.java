package com.example.library.Controllers;

import com.example.library.Repo.BooksRepository;
import com.example.library.models.Books;
import com.example.library.models.Role;
import com.example.library.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Optional;

@Controller
public class BooksController {
    @Autowired
    private BooksRepository booksRepository;

    @GetMapping("/books")
    public String books(Model model){
        model.addAttribute("namePage", "Книги");
        Iterable<Books> books = booksRepository.findAll();
        model.addAttribute("books", books);
        return "books";
    }
    @PostMapping("/books")
    public String searchBook(@RequestParam String filter, Model model){
        Iterable<Books> books;
        if (filter != null && !filter.isEmpty()){
            books = booksRepository.findByTitle(filter);
        }
        else {
            books = booksRepository.findAll();
            model.addAttribute("error", "По запросу ничено не найдено.");
        }

        model.addAttribute("books", books);
        return "books";
    }
    @PreAuthorize("hasAuthority('ADMIN') || hasAnyAuthority('MODER')")
    @GetMapping("books/new")
    public String bookNew(Model model){
        model.addAttribute("namePage", "Добавление книги");
        return "bookNew";
    }
    @PreAuthorize("hasAuthority('ADMIN') || hasAnyAuthority('MODER')")
    @PostMapping("books/new")
    public String bookAdd(
            @AuthenticationPrincipal User user,
            @RequestParam String title,@RequestParam String img,
            @RequestParam String download,@RequestParam String str, Model model){
        Books book = new Books(title,img,download, str, user);
        booksRepository.save(book);
        return "redirect:/books";
    }

    @GetMapping("/books/{id}")
    public String bookId(@AuthenticationPrincipal User user,
            @PathVariable(value = "id") long id, Model model){
        if (!booksRepository.existsById(id)){
            model.addAttribute("namePage", "404");
            return "404";
        }
        model.addAttribute("namePage", booksRepository.findById(id).orElseThrow().getTitle());
        Optional<Books> book = booksRepository.findById(id);
        ArrayList<Books> res = new ArrayList<>();
        book.ifPresent(res::add);
        model.addAttribute("book", res);
        if (user == null){
            return "book";
        }
        else if (user.getRoles().contains(Role.ADMIN) || user.getRoles().contains(Role.MODER)){
            return "bookAdm";
        }
        return "book";
    }
    @PreAuthorize("hasAuthority('ADMIN') || hasAnyAuthority('MODER')")
    @GetMapping("/books/{id}/edit")
    public String bookIdEdit(
            @PathVariable(value = "id") long id, Model model){
        if (!booksRepository.existsById(id)){
            model.addAttribute("namePage", "404");
            return "404";
        }
        model.addAttribute("namePage", booksRepository.findById(id).orElseThrow().getTitle());
        Optional<Books> book = booksRepository.findById(id);
        ArrayList<Books> res = new ArrayList<>();
        book.ifPresent(res::add);
        model.addAttribute("book", res);
        return "bookEdit";
    }
    @PreAuthorize("hasAuthority('ADMIN') || hasAnyAuthority('MODER')")
    @PostMapping("books/{id}/edit")
    public String bookIdPostEdit(
            @PathVariable(value = "id") long id,
            @RequestParam String title,@RequestParam String img,
            @RequestParam String download,@RequestParam String str, Model model){
        Books book = booksRepository.findById(id).orElseThrow();
        book.setTitle(title);
        book.setImg(img);
        book.setDownload(download);
        book.setStr(str);
        booksRepository.save(book);
        return "redirect:/books/" + id;
    }
    @PreAuthorize("hasAuthority('ADMIN') || hasAnyAuthority('MODER')")
    @GetMapping("/books/{id}/del")
    public String bookDelId(
            @AuthenticationPrincipal User user,
            @PathVariable(value = "id") long id, Model model){
        if (!booksRepository.existsById(id)){
            model.addAttribute("error", "404");
            return "404";
        }
        model.addAttribute("namePage", "Книга удалена");
        Books book = booksRepository.findById(id).orElseThrow();
        if (user == book.getCreator() || !user.getRoles().equals(Role.ADMIN)){
            booksRepository.deleteById(id);
            return "redirect:/books";
        }
        else {
            model.addAttribute("error", "403");
            return "404";
        }

    }

}

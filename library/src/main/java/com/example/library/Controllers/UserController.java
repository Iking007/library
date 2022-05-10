package com.example.library.Controllers;

import com.example.library.Repo.UsersRepository;
import com.example.library.models.Books;
import com.example.library.models.Role;
import com.example.library.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
@PreAuthorize("hasAuthority('ADMIN')")
public class UserController {
    @Autowired
    private UsersRepository usersRepository;

    @GetMapping
    public String userList(Model model){
        List<User> users = usersRepository.findAll();
        model.addAttribute("users", users);
        return "userList";
    }
    @GetMapping("{id}")
    public String bookId(@PathVariable(value = "id") long id, Model model){
        if (!usersRepository.existsById(id)){
            model.addAttribute("namePage", "404");
            return "404";
        }
        Optional<User> user = usersRepository.findById(id);
        ArrayList<User> res = new ArrayList<>();
        user.ifPresent(res::add);
        model.addAttribute("namePage", usersRepository.findById(id).orElseThrow().getUsername());
        model.addAttribute("roles", Role.values());
        model.addAttribute("user", res);
        return "user";
    }
    @PostMapping("{id}")
    public String bookIdEdit(@PathVariable(value = "id") long id, @RequestParam String username, @RequestParam Map<String, String> form, Model model){
        if (!usersRepository.existsById(id)){
            model.addAttribute("namePage", "404");
            return "404";
        }
        User user = usersRepository.findById(id).orElseThrow();
        user.setUsername(username);
        Set<String> roles = Arrays.stream(Role.values()).map(Role::name).collect(Collectors.toSet());
        user.getRoles().clear();
        boolean one = false;
        for (String key : form.keySet()){
            if (roles.contains(key)){
                user.getRoles().add(Role.valueOf(key));
                one = true;
            }
        }
        if (!one){
            user.getRoles().add(Role.valueOf(Role.USER.name()));
        }
        usersRepository.save(user);
        return "redirect:/user";
    }
}

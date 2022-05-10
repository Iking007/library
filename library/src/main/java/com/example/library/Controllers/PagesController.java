package com.example.library.Controllers;


import com.example.library.models.Role;
import com.example.library.models.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


import java.io.*;
import java.util.ArrayList;

@Controller
public class PagesController {
    private String phrase() throws IOException {
        File file = new File("phrases.txt");
        //создаем объект FileReader для объекта File
        FileReader fr = new FileReader(file);
        //создаем BufferedReader с существующего FileReader для построчного считывания
        BufferedReader reader = new BufferedReader(fr);
        // считаем сначала первую строку
        String line = reader.readLine();
        ArrayList<String> str = new ArrayList<String>();
        Integer num = 0;
        while (line != null ) {
            str.add(line);
            num += 1;
            // считываем остальные строки в цикле
            line = reader.readLine();
        }
        return str.get((int) (Math.random() * num));
    }

    @GetMapping("/")
    public String index(Model model) throws IOException {
        model.addAttribute("namePage", "Главная");
        model.addAttribute("phrase", phrase());
        return "index";
    }
    @GetMapping("/prof")
    public String prof(@AuthenticationPrincipal User user, Model model){
        model.addAttribute("namePage", "Профиль");
        model.addAttribute("username", user.getUsername());
        boolean one = false;
        if(user.getRoles().contains(Role.ADMIN)){
            return "profAdm";
        }
        else if (user.getRoles().contains(Role.MODER)){
            return "profMod";
        }
        model.addAttribute("new", one);
        model.addAttribute("user", one);
        return "prof";
    }
}

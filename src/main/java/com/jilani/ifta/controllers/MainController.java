package com.jilani.ifta.controllers;

import com.jilani.ifta.fatwa.*;
import com.jilani.ifta.users.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.Optional;

@Controller
public class MainController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FatwaRepository fatwaRepository;

    @Autowired
    private FatwaService fatwaService;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private TagRepository tagRepository;

    @GetMapping("/")
    public String rootContext(Model model) {
        return homePage(model);
    }

    @GetMapping("/index")
    public String index(Model model) {
        return homePage(model);
    }

    @GetMapping("/home")
    public String home(Model model) {
        return homePage(model);
    }

    public String homePage(Model model) {

        model.addAttribute("specialFatwaList",fatwaRepository.findTop30ByTag("Special"));
        model.addAttribute("modernFatwaList",fatwaRepository.findTop30ByTag("Modern"));
        model.addAttribute("recentFatwaList",fatwaRepository.findTop30Latest());

        return "home";
    }

    @GetMapping("/topics/{id}")
    public String topicPage(@PathVariable long id, Model model){
        Optional<Topic> optionalTopic = topicRepository.findById(id);
        if(optionalTopic.isEmpty()) {
            return "not-found";
        }
        model.addAttribute("fatwaList",fatwaRepository.fatawaByTopic(optionalTopic.get()));
        return "topic-page";
    }


}

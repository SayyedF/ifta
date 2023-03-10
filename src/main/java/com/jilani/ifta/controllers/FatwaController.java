package com.jilani.ifta.controllers;

import com.jilani.ifta.notifications.NotificationService;
import com.jilani.ifta.fatwa.*;
import com.jilani.ifta.ui.Modal;
import com.jilani.ifta.users.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
public class FatwaController {

    @Autowired
    FatwaRepository fatwaRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    FatwaService fatwaService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/fatwa/{id}")
    public String showFatwa(@PathVariable Long id, Model model) {
        Optional<Fatwa> optionalFatwa = fatwaRepository.findById(id);
        if(optionalFatwa.isEmpty())
            return "not-found";

        Fatwa fatwa = optionalFatwa.get();
        fatwa.incrementViewsBy(1);
        fatwaRepository.save(fatwa);
        model.addAttribute("fatwa", fatwa);
        return "show-fatwa";
    }

    @GetMapping("/search")
    public String search(@RequestParam String query, Model model) {

        List<Fatwa> fatwaList = fatwaRepository.search(query);
        String heading = fatwaList.size() + " Result(s) found for: \"" + query + "\"";
        model.addAttribute("sectionHeading", heading);
        model.addAttribute("fatwaList",fatwaList);
        return "search-result";
    }

    @GetMapping("/ask")
    public String ask(Model model) {
        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);
        return "ask-page";
    }

    @PostMapping("/ask/submit")
    public ModelAndView submitQuestion(
            @RequestParam long topic,
            @RequestParam String title,
            @RequestParam String question,
            ModelMap model
    ){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principal == null) {
            model.addAttribute("message", "User is not authenticated!");
        } else {
            User asker =  ((CustomUserDetails) principal).getUser();
            fatwaService.raiseQuestion(title, question, asker, topic);
            model.addAttribute("message", "Question submitted!");
        }
        return new ModelAndView("redirect:/", model);
    }

    @GetMapping("/asked/unanswered")
    public String getUnanswered(Model model) {
        User currentUser = fatwaService.getCurrentUser();
        List<Fatwa> fatwaList;
        if(fatwaService.hasRole("MAINMUFTI")) {
            fatwaList = fatwaRepository.getAllUnanswered();
            model.addAttribute("userId", currentUser.getId());
            model.addAttribute("fatwaList", fatwaList);
        }
        else if(fatwaService.hasRole("MUFTI")) {
            fatwaList = fatwaRepository.getAllUnansweredByMufti(currentUser);
            model.addAttribute("userId", currentUser.getId());
            model.addAttribute("fatwaList", fatwaList);
        }
        return "fatwa-list";
    }

    @GetMapping("/asked/answered")
    public String getAnswered(Model model) {
        User currentUser = fatwaService.getCurrentUser();
        List<Fatwa> fatwaList;
        if(fatwaService.hasRole("MAINMUFTI")) {
            fatwaList = fatwaRepository.getAllAnswered();
            model.addAttribute("userId", currentUser.getId());
            model.addAttribute("fatwaList", fatwaList);
        }
        else if(fatwaService.hasRole("MUFTI")) {
            fatwaList = fatwaRepository.getAllAnsweredByMufti(currentUser);
            model.addAttribute("userId", currentUser.getId());
            model.addAttribute("fatwaList", fatwaList);
        }
        return "fatwa-list";
    }

    @GetMapping("/asked/{fatwaId}/edit")
    public ModelAndView edit(@PathVariable long fatwaId,
                               ModelMap model){
        Fatwa fatwa = fatwaRepository.getById(fatwaId);
        User currentUser = fatwaService.getCurrentUser();
        if(fatwaService.canWriteAnswer(fatwa, currentUser)) {
            fatwa.setMufti(currentUser);
            List<Category> categories = categoryRepository.findAll();
            model.addAttribute("categories", categories);
            model.addAttribute("fatwa", fatwa);
            fatwaRepository.save(fatwa); //************************************ conflict alert
        }
        else model.addAttribute("message", "You're not authorized to edit.");

        return new ModelAndView("edit-fatwa", model);
    }

    @GetMapping("/asked/{fatwaId}/answer")
    public ModelAndView answer(@PathVariable long fatwaId,
                               ModelMap model, Principal principal){
        Fatwa fatwa = fatwaRepository.getById(fatwaId);
        User currentUser = fatwaService.getCurrentUser();
        if(fatwaService.canWriteAnswer(fatwa, currentUser)) {
            fatwa.setMufti(currentUser);
            List<Category> categories = categoryRepository.findAll();
            model.addAttribute("categories", categories);
            model.addAttribute("fatwa", fatwa);
            fatwaRepository.save(fatwa); //************************************ conflict alert
            //notificationService.unPublish(fatwaId, currentUser);
            notificationService.unPublish(fatwaId);
        }
        else model.addAttribute("message", "Other Mufti will write the answer.");

        return new ModelAndView("answer-fatwa", model);
    }

    @GetMapping("/asked/{fatwaId}/deselect")
    public ModelAndView deselect(@PathVariable long fatwaId){

        Fatwa fatwa = fatwaRepository.getById(fatwaId);
        User currentUser = fatwaService.getCurrentUser();
        if(fatwaService.canWriteAnswer(fatwa, currentUser)) {
            User mufti = fatwa.getMufti();
            if(mufti != null && mufti.getUsername().equals(currentUser.getUsername()))
            fatwa.setMufti(null);
            fatwaRepository.save(fatwa);
            notificationService.alertQuestionRaised(fatwa.getTitle(), fatwaId);
        }
        return new ModelAndView("redirect:/asked/unanswered");
    }

    @PostMapping("/asked/{fatwaId}/answer/save")
    public ModelAndView saveAnswer(@PathVariable long fatwaId,
                                   ModelMap model, @ModelAttribute Fatwa fatwa){
        User currentUser = fatwaService.getCurrentUser();
        if(fatwaService.canWriteAnswer(fatwa, currentUser)) {
            fatwa.setMufti(currentUser);
            Fatwa unansweredFatwa = fatwaRepository.getById(fatwaId);
            fatwaService.answerFatwa(unansweredFatwa, fatwa, currentUser);
            model.addAttribute("fatwa", unansweredFatwa);
        }
        else model.addAttribute("message", "Other Mufti will write the answer.");

        return new ModelAndView("redirect:/fatwa/"+fatwaId, model);
    }

    @PostMapping("/asked/{fatwaId}/edit/save")
    public ModelAndView updateAnswer(@PathVariable long fatwaId,
                                   ModelMap model, @ModelAttribute Fatwa fatwa){
        User currentUser = fatwaService.getCurrentUser();
        if(fatwaService.canWriteAnswer(fatwa, currentUser)) {
            Fatwa unansweredFatwa = fatwaRepository.getById(fatwaId);
            unansweredFatwa.setAnswer(fatwa.getAnswer());

            fatwaService.updateTopicCounters(unansweredFatwa.getTopic(),fatwa.getTopic());

            unansweredFatwa.setTopic(fatwa.getTopic());
            fatwaRepository.save(unansweredFatwa);
            model.addAttribute("fatwa", unansweredFatwa);
        }
        else model.addAttribute("message", "Other Mufti will write the answer.");

        return new ModelAndView("redirect:/fatwa/"+fatwaId, model);
    }

    @GetMapping("/modal/show/{fatwaId}/{muftiId}")
    public String getModal(@PathVariable long fatwaId,
                           @PathVariable long muftiId,
                           Model model) {
        Modal modal = fatwaService.getModal(fatwaId, muftiId);
        model.addAttribute("modal", modal);
        return "show-fatwa-modal";
    }

    @GetMapping("/asked/{fatwaId}/approve")
    public String approve(@PathVariable long fatwaId,
                          ModelMap model, HttpServletRequest request) {
        model.addAttribute("message", fatwaService.approve(fatwaId));
        String referer = request.getHeader("Referer");
        if(referer != null && !referer.isEmpty())
            return "redirect:"+referer;
        return "show-fatwa";
    }
}

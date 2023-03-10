package com.jilani.ifta.controllers;

import com.jilani.ifta.fatwa.FatwaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class FatwaApi {

    @Autowired
    FatwaService fatwaService;

    @GetMapping("/sample/{fatwaId}/assign/{muftiId}")
    public String assign(@PathVariable long fatwaId,
                               @PathVariable long muftiId,
                               ModelMap model){
        fatwaService.assignToMufti(fatwaId, muftiId);
        model.addAttribute("message", "Fatwa Assigned to Mufti sb.");
        return "Assigned Successfully!";
    }

    @CrossOrigin
    @RequestMapping(value = "/sample", produces = "application/json", method = RequestMethod.POST)
    public String sample() {
        System.out.println("Post Request received!");
        return "You are done. Post";
    }

    @RequestMapping(value = "/sample2", produces = "application/json", method = RequestMethod.GET)
    public String sample2() {
        System.out.println("Get Request received!");
        return "You are done. Get";
    }
}

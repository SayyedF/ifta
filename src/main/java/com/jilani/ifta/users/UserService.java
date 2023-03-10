package com.jilani.ifta.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    public List<User> getAllMuftis(){
        Role mufti = roleRepository.getRoleByName("MUFTI");
        return userRepository.findAllByRolesContaining(mufti);
    }

    public User getCurrentUser() {
        SecurityContext context = SecurityContextHolder.getContext();
        if(context != null) {
            Authentication auth = context.getAuthentication();
            if(auth.isAuthenticated()) {
                Object principal = auth.getPrincipal();
                if(principal instanceof UserDetails) {
                    User user = ((CustomUserDetails) principal).getUser();
                    return user;
                }
            }
        }
        return null;
    }

    public boolean isMufti() {
        User user = getCurrentUser();
        if(user != null) {
            return user.getRoles()
                    .stream()
                    .anyMatch(role -> role.getName().equalsIgnoreCase("MUFTI"));
        }
        return false;
    }

    public String getMuftiRole() {
        User user = getCurrentUser();
        if(user != null) {
            if (user.getRoles()
                    .stream()
                    .anyMatch(role -> role.getName().equalsIgnoreCase("MUFTI"))) {
                return "yes";
            }
        }
        return "no";
    }
}

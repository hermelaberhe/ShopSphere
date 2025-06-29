package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ProfileDao;
import org.yearup.data.UserDao;
import org.yearup.models.Profile;
import org.yearup.models.User;

import java.security.Principal;

@RestController
@RequestMapping("/profile")
@PreAuthorize("isAuthenticated()")
@CrossOrigin
public class ProfileController
{
    private final ProfileDao profileDao;
    private final UserDao userDao;

    @Autowired
    public ProfileController(ProfileDao profileDao, UserDao userDao)
    {
        this.profileDao = profileDao;
        this.userDao = userDao;
    }

    // GET /profile
    @GetMapping
    public ResponseEntity<?> getProfile(Principal principal)
    {
        String username = principal.getName();
        User user = userDao.getByUserName(username);

        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("User not found.");

        try
        {
            Profile profile = profileDao.getByUserId(user.getId());
            if (profile == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Profile not found.");
            }
            return ResponseEntity.ok(profile);
        }
        catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving profile.");
        }
    }

    // PUT /profile
    @PutMapping
    public ResponseEntity<?> updateProfile(@RequestBody Profile profile, Principal principal)
    {
        String username = principal.getName();
        User user = userDao.getByUserName(username);

        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("User not found.");

        if (profile.getUserId() != user.getId())
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You can only update your own profile.");

        try
        {
            profileDao.update(profile);
            return ResponseEntity.ok("Profile updated successfully.");
        }
        catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Could not update profile.");
        }
    }
}
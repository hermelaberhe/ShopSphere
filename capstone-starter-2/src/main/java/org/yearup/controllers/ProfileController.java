package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public Profile getProfile(Principal principal)
    {
        String username = principal.getName();
        User user = userDao.getByUserName(username);

        if (user == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found.");

        try
        {
            return profileDao.getByUserId(user.getId());
        }
        catch (Exception e)
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found.");
        }
    }

    // PUT /profile
    @PutMapping
    public void updateProfile(@RequestBody Profile profile, Principal principal)
    {
        String username = principal.getName();
        User user = userDao.getByUserName(username);

        if (user == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found.");

        if (profile.getUserId() != user.getId())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only update your own profile.");

        try
        {
            profileDao.update(profile);
        }
        catch (Exception e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not update profile.");
        }
    }
}
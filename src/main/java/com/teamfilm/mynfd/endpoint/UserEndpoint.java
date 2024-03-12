package com.teamfilm.mynfd.endpoint;

import com.teamfilm.mynfd.response.ApiResponse;
import com.teamfilm.mynfd.service.user.UserModel;
import com.teamfilm.mynfd.service.user.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/users")
public class UserEndpoint {

    private final UserService userService;

    public UserEndpoint(UserService userService) {
        this.userService = userService;
    }


    @PostMapping("/create")
    public ResponseEntity<UserModel> createUser(@Valid @RequestBody UserModel user) {
        UserModel model = userService.createUser(user);
        return new ResponseEntity<>(model, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public String authenticate(@RequestHeader("Authorization") String token){
        boolean isAuthorized = userService.authenticate(token);
        if(isAuthorized) {
            return "authenticated";
        } else {
            return "couldn't authenticate";
        }
    }

    @GetMapping("/read")
    public ResponseEntity<List<UserModel>> readAllFilms() {
        List<UserModel> users = userService.readAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }


    @GetMapping("/read/{id}")
    public ResponseEntity<UserModel> readFilm(@PathVariable("id") String username) {
        UserModel model = userService.readUser(username);
        return new ResponseEntity<>(model, HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<UserModel> updateUser(@Valid @RequestBody UserModel user, @PathVariable("id") String username) {
        UserModel updated = userService.updateUser(user, username);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse deleteUser(@PathVariable("id") String username) {
        userService.deleteUser(username);
        return new ApiResponse("User deleted", true);
    }
}

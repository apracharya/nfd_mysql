package com.teamfilm.mynfd.service.user;

import com.teamfilm.mynfd.persistence.user.UserEntity;
import com.teamfilm.mynfd.persistence.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Component
public class UserServiceImplementation implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImplementation(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserModel createUser(UserModel user) {
        UserEntity entity = new UserEntity(
                user.id(),
                user.firstName(),
                user.lastName(),
                user.username(),
                this.passwordEncoder.encode(user.password())
        );
        UserEntity created = userRepository.save(entity);
        return UserModel.fromEntity(created);
    }

    @Override
    public Optional<UserModel> readUser(int id) {
        return userRepository.findById(id)
                .map(UserModel::fromEntity);
    }

    @Override
    public List<UserModel> readAllUsers(){
        return userRepository.findAll()
                .stream()
                .map(UserModel::fromEntity)
                .toList();
    }

    @Override
    public UserModel updateUser(UserModel userModel, int userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow();
        user.setFirstName(userModel.firstName());
        user.setLastName(userModel.lastName());
        user.setPassword(this.passwordEncoder.encode(userModel.password()));
        user.setUsername(userModel.username());
        UserEntity updated = userRepository.save(user);
        return UserModel.fromEntity(updated);
    }

    @Override
    public void deleteUser(int id) {
        userRepository.deleteById(id);
    }

    @Override
    public boolean authenticate(String token) {
        String[] split = token.split(" ");
        if(split[0].equals("Basic")) {
            byte[] bytePwd = Base64.getDecoder().decode(split[1]);
            String[] userPass = new String(bytePwd).split(":", 2);
            String username = userPass[0];
            String password = userPass[1];
            UserEntity entity = userRepository.findByUsername(username);
            if(entity != null) {
                String encodedPassword = entity.getPassword();
                boolean isPwdRight = passwordEncoder.matches(password, encodedPassword);
                if(isPwdRight) {
                    Optional<UserEntity> user = userRepository.findByUsernameAndPassword(username, encodedPassword);
                    return user.isPresent();
                }
            }
        }
        return false;
    }
}
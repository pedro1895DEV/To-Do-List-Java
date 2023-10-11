package br.com.pedromartins.todolist.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private IUserRepository userRepository;
    @PostMapping("/")
    public ResponseEntity create(@RequestBody UserModel userModel){
        var user = this.userRepository.findByUserName(userModel.getUserName());

        if(user != null){
            System.err.println("Usuário já existe na base de dados");
            return ResponseEntity.status(400).body("Usuário já existe na base de dados.");
        }

        var userCreated = this.userRepository.save(userModel);
        return ResponseEntity.status(200).body("Usuário criado.");
    }
}

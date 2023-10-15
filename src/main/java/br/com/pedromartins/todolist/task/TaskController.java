package br.com.pedromartins.todolist.task;

import br.com.pedromartins.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {
        @Autowired
        private ITaskRepository taskRepository;

        @PostMapping("/")
            public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request){
            var idUser = request.getAttribute("idUser");
            taskModel.setIdUser((UUID) idUser);

            var currentDate = LocalDateTime.now();
            if(currentDate.isAfter(taskModel.getStartsAt()) || currentDate.isAfter(taskModel.getEndsAt())){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Data de adição de tarefa é impossível");
            }

            if(taskModel.getStartsAt().isAfter(taskModel.getEndsAt())){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Data de adição de tarefa é impossível");
            }
            var task = this.taskRepository.save(taskModel);
            return ResponseEntity.status(HttpStatus.OK).body(task);
        }

        @GetMapping("/")
        public Object List(HttpServletRequest request){
            var idUser = request.getAttribute("idUser");
            var tasks = this.taskRepository.findByIdUser((UUID) idUser);
            return tasks;
        }

        @PutMapping("/{id}")
        public TaskModel update(@RequestBody TaskModel taskModel, HttpServletRequest request, @PathVariable UUID id){
            var idUser = request.getAttribute("idUser");

            var task = this.taskRepository.findById(id).orElse(null);

            Utils.copyNonNullProperties(taskModel, task);

            taskModel.setIdUser((UUID) idUser);
            taskModel.setId(id);
            return this.taskRepository.save(task);
        }
}


package pe.edu.vallegrande.vg_ms_teacher.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.vg_ms_teacher.model.entity.Teacher;
import pe.edu.vallegrande.vg_ms_teacher.service.ITeacherService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin("*")
public class TeacherController {

    @Autowired
    private ITeacherService teacherService;

    @GetMapping("/teachers")
    public Flux<Teacher> getAll() {
        return teacherService.listarTodos();
    }

    @GetMapping("/teachers/active")
    public Flux<Teacher> getActive() {
        return teacherService.listarActivos();
    }

    @GetMapping("/teachers/inactive")
    public Flux<Teacher> getInactive() {
        return teacherService.listarInactivos();
    }

    @GetMapping("/teacher/{id}")
    public Mono<Teacher> getForId(@PathVariable String id) {
        return teacherService.listarPorId(id);
    }

    @PostMapping("/teacher/create")
    public Mono<Teacher> createTeacher(@RequestBody Teacher teacher){
        return teacherService.createTeacher(teacher);
    }

    @PutMapping("/teacher/{id}")
    public Mono<Teacher> updateTeacher(@PathVariable String id, @RequestBody Teacher teacher) {
        return teacherService.updateTeacher(id, teacher);
    }

    @DeleteMapping("/teacher/removed/{id}")
    public Mono<Teacher> removed(@PathVariable String id) {
        return teacherService.deleteTeacher(id);
    }

    @DeleteMapping("/teacher/restore/{id}")
    public Mono<Teacher> restore(@PathVariable String id) {
        return teacherService.reactivateTeacher(id);
    }

    @DeleteMapping("/teacher/{id}")
    public Mono<Void> delete(@PathVariable String id) {
        return teacherService.eliminar(id);
    }

    @PatchMapping("/updatePassword/{id}")
    public Mono<ResponseEntity<Teacher>> updatePassword(@PathVariable String id, @RequestBody String newPassword) {
        return teacherService.updatePassword(id, newPassword)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

}

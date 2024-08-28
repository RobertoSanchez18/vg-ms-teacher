package pe.edu.vallegrande.vg_ms_teacher.service;

import pe.edu.vallegrande.vg_ms_teacher.model.entity.Teacher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ITeacherService {

    Flux<Teacher> listarTodos();
    Flux<Teacher> listarActivos();
    Flux<Teacher> listarInactivos();
    Mono<Teacher> listarPorId(String id);
    Mono<Teacher> createTeacher(Teacher teacher);
    Mono<Teacher> updateTeacher(String id, Teacher teacher);
    Mono<Teacher> deleteTeacher(String id);
    Mono<Teacher> reactivateTeacher(String id);
    Mono<Void> eliminar(String id);
    Mono<Teacher> updatePassword(String id, String newPassword);

}

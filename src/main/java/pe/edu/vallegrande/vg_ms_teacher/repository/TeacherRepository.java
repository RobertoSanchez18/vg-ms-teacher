package pe.edu.vallegrande.vg_ms_teacher.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.vg_ms_teacher.model.entity.Teacher;
import reactor.core.publisher.Flux;

@Repository
public interface TeacherRepository extends ReactiveMongoRepository<Teacher, String> {

    Flux<Teacher> findByStatus(String status);

}

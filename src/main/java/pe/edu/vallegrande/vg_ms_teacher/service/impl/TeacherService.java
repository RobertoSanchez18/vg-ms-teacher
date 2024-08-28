package pe.edu.vallegrande.vg_ms_teacher.service.impl;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vg_ms_teacher.model.entity.Teacher;
import pe.edu.vallegrande.vg_ms_teacher.repository.TeacherRepository;
import pe.edu.vallegrande.vg_ms_teacher.service.ITeacherService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class TeacherService implements ITeacherService {

    private static final Logger log = LoggerFactory.getLogger(TeacherService.class);

    @Autowired
    private TeacherRepository teacherRepository;
    private final ModelMapper modelMapper =new ModelMapper();

    @Override
    public Flux<Teacher> listarTodos() {
        return teacherRepository.findAll();
    }

    @Override
    public Flux<Teacher> listarActivos() {
        return teacherRepository.findByStatus("A");
    }

    @Override
    public Flux<Teacher> listarInactivos() {
        return teacherRepository.findByStatus("I");
    }

    @Override
    public Mono<Teacher> listarPorId(String id) {
        return teacherRepository.findById(id);

    }

    @Override
    public Mono<Teacher> createTeacher(Teacher teacher) {
        Teacher newTeacher = modelMapper.map(teacher, Teacher.class);
        newTeacher.setRole("PROFESOR");
        newTeacher.setStatus("A");
        newTeacher.setCreatedAt(LocalDateTime.now());
        newTeacher.setUpdatedAt(LocalDateTime.now());

        // Crear el usuario en Firebase Authentication
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(teacher.getEmail())
                .setPassword(teacher.getDocumentNumber()) // La contraseña se establece aquí
                .setDisplayName(teacher.getFirstName() + " " + teacher.getLastName()); // Concatenar nombre y apellido

        return Mono.fromCallable(() -> FirebaseAuth.getInstance().createUser(request))
                .flatMap(userRecord -> {
                    // Asignar el UID al nuevo usuario
                    newTeacher.setUid(userRecord.getUid());
                    newTeacher.setPassword(teacher.getDocumentNumber()); // Almacenar la contraseña en el campo

                    // Asignar claims personalizados al usuario
                    try {
                        Map<String, Object> claims = new HashMap<>();
                        claims.put("role", "PROFESOR");

                        // Asignar los claims de forma sincrónica
                        FirebaseAuth.getInstance().setCustomUserClaims(userRecord.getUid(), claims);
                    } catch (Exception e) {
                        System.err.println("Error setting custom claims: " + e.getMessage());
                        return Mono.error(e);
                    }

                    return teacherRepository.save(newTeacher);
                })
                .onErrorResume(e -> {
                    // Manejar errores (por ejemplo, si falla la creación del usuario en Firebase)
                    System.err.println("Error creating user in Firebase: " + e.getMessage());
                    return Mono.error(e);
                });
    }

    @Override
    public Mono<Teacher> updateTeacher(String id, Teacher teacher) {
        return teacherRepository.findById(id)
                .flatMap(existingTeacher -> {
                    existingTeacher.setFirstName(teacher.getFirstName());
                    existingTeacher.setLastName(teacher.getLastName());
                    existingTeacher.setDocumentType(teacher.getDocumentType());
                    existingTeacher.setDocumentNumber(teacher.getDocumentNumber());
                    existingTeacher.setDateBirth(teacher.getDateBirth());
                    existingTeacher.setEmail(teacher.getEmail());
                    existingTeacher.setCellPhone(teacher.getCellPhone());
                    existingTeacher.setGender(teacher.getGender());
                    existingTeacher.setUpdatedAt(LocalDateTime.now());
                    return teacherRepository.save(existingTeacher);
                });
    }

    @Override
    public Mono<Teacher> updatePassword(String id, String newPassword) {
        return teacherRepository.findById(id)
                .flatMap(existingTeacher -> {
                    // Actualizar la contraseña en Firebase Authentication
                    return Mono.fromCallable(() -> FirebaseAuth.getInstance().updateUser(
                            new UserRecord.UpdateRequest(existingTeacher.getUid())
                                    .setPassword(newPassword)
                    )).then(Mono.defer(() -> {
                        // Actualizar la contraseña en la base de datos
                        existingTeacher.setPassword(newPassword);
                        existingTeacher.setUpdatedAt(LocalDateTime.now());
                        return teacherRepository.save(existingTeacher);
                    }));
                })
                .onErrorResume(e -> {
                    // Manejar errores (por ejemplo, si falla la actualización de la contraseña en Firebase)
                    System.err.println("Error updating password in Firebase: " + e.getMessage());
                    return Mono.error(e);
                });
    }

    @Override
    public Mono<Teacher> deleteTeacher(String id) {
        return teacherRepository.findById(id)
                .flatMap(existingTeacher -> {
                    // Desactivar cuenta en Firebase
                    return Mono.fromCallable(() -> FirebaseAuth.getInstance().updateUser(
                            new UserRecord.UpdateRequest(existingTeacher.getUid())
                                    .setDisabled(true)
                    )).then(Mono.defer(() -> {
                        existingTeacher.setStatus("I");
                        return teacherRepository.save(existingTeacher);
                    }));
                });
    }

    @Override
    public Mono<Teacher> reactivateTeacher(String id) {
        return teacherRepository.findById(id)
                .flatMap(existingTeacher -> {
                    // Reactivar cuenta en Firebase
                    return Mono.fromCallable(() -> FirebaseAuth.getInstance().updateUser(
                            new UserRecord.UpdateRequest(existingTeacher.getUid())
                                    .setDisabled(false)
                    )).then(Mono.defer(() -> {
                        existingTeacher.setStatus("A");
                        return teacherRepository.save(existingTeacher);
                    }));
                });
    }

    @Override
    public Mono<Void> eliminar(String id) {
        return teacherRepository.deleteById(id);
    }

}

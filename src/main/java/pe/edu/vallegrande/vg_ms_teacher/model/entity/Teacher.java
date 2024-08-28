package pe.edu.vallegrande.vg_ms_teacher.model.entity;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "teacher")
public class Teacher {

    @MongoId
    @Field(name = "_id")
    private String id;
    @Field(name = "uid")
    private String uid;
    @Field(name = "first_name")
    private String firstName;
    @Field(name = "last_name")
    private String lastName;
    @Field(name = "document_type")
    private String documentType;
    @Field(name = "document_number")
    private String documentNumber;
    @Field(name = "date_birth")
    private LocalDate dateBirth;
    @Field(name = "role")
    private String role;
    @Field(name = "email")
    private String email;
    @Field(name = "password")
    private String password;
    @Field(name = "cell_phone")
    private String cellPhone;
    @Field(name = "gender")
    private String gender;
    @Field(name = "date_hire")
    private LocalDate dateHire;
    @Field(name = "status")
    private String Status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
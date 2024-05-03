package sever.application.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    private String id;

    @Column(name = "user_name")
    private String templateName;

    @Column(name = "count_generate")
    private int countGenerate;
}

package sever.application.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "replace_word_mapping")
public class ReplaceWordMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "replace_word_key")
    private String key;

    @Column(name = "replace_word_value")
    private List<String> value;

    // Добавляем связь с сущностью Template
    @ManyToOne
    @JoinColumn(name = "template_id")
    private Template template;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<String> getValue() {
        return value;
    }

    public void setValue(List<String> value) {
        this.value = value;
    }

    public Template getTemplate() {
        return template;
    }

    public void setTemplate(Template template) {
        this.template = template;
    }
}

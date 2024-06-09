package sever.application.model;

import jakarta.persistence.*;

import java.util.List;
import java.util.Map;

@Entity
@Table(name = "templates")
public class Template {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "template_name")
    private String templateName;

    @Lob
    @Column(name = "template_data")
    private byte[] templateData;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "template_id")
    private List<ReplaceWordMapping> replaceWordMappings;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;


    @ManyToOne(fetch = FetchType.LAZY) // Множество шаблонов принадлежит одному пользователю
    @JoinColumn(name = "user_id") // Связь по полю user_id
    private User user; // Ссылка на пользователя

    //--------------- Геттеры, сеттеры и конструкторы ---------------------

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public byte[] getTemplateData() {
        return templateData;
    }

    public void setTemplateData(byte[] templateData) {
        this.templateData = templateData;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ReplaceWordMapping> getReplaceWordMappings() {
        return replaceWordMappings;
    }

    public void setReplaceWordMappings(List<ReplaceWordMapping> replaceWordMappings) {
        this.replaceWordMappings = replaceWordMappings;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategories(Category category) {
        this.category = category;
    }
}

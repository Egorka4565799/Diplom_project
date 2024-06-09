package sever.application.service;


import sever.application.model.Category;
import sever.application.model.ReplaceWordMapping;
import sever.application.model.Template;
import sever.application.model.User;
import sever.application.repository.CategoryRepostory;
import sever.application.repository.TemplateRepository;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class TemplateService {

    private static final Logger logger = LoggerFactory.getLogger(TemplateService.class);
    @Autowired
    private TemplateRepository templateRepository;

    @Autowired
    private CategoryRepostory categoryRepostory;

    //----------- Загрузка, обновление и удаление ----------------------------------

    @Transactional
    public Long uploadTemplate(MultipartFile file) throws IOException {
        logger.info("Start uploading: {}",file.getOriginalFilename());

        try {
            Template template = new Template();

            // Сохраняем имя файла в сущности template
            template.setTemplateName(file.getOriginalFilename());
            // Сохраняем файл в виде Large Object (LOB) в базе данных
            template.setTemplateData(file.getBytes());

            //-------
            // Парсинг шаблона для получения списка переменных
            List<String> variables = parsingTemplate(file);

            // Создание списка ReplaceWordMapping с пустыми значениями для переменных
            List<ReplaceWordMapping> mappings = new ArrayList<>();
            for (String variable : variables) {
                ReplaceWordMapping mapping = new ReplaceWordMapping();
                mapping.setKey(variable);
                mapping.setValue(new ArrayList<>());  // Пустой список для значений
                mapping.setTemplate(template);
                mappings.add(mapping);
            }
            // Сохранаяем список переменных в бд
            template.setReplaceWordMappings(mappings);
            //-------
            templateRepository.save(template);
            logger.info("Uploading successfully: {}",template.getTemplateName());

            // Возвращаем ID созданного шаблона
            return template.getId();
        }
        catch(Exception e){
            logger.error("Error upload! :{}", e.getMessage());
        }
        return null;
    }

//    public void addReplaceWordsToTemplate2(String fileName, List<ReplaceWordMapping> replaceWords, Long categoryId) {
//        // Находим шаблон по имени файла
//        Template template = templateRepository.findByTemplateName(fileName);
//        if (template == null) {
//            // Если шаблон не найден, можно выбросить исключение или выполнить другие действия
//            throw new IllegalArgumentException("Template with filename " + fileName + " not found");
//        }
//        Optional<Category> category = categoryRepostory.findById(categoryId);
//
//
//        template.setCategories(category.get());
//        // Добавляем слова для замены к найденному шаблону
//        List<ReplaceWordMapping> existingReplaceWords = template.getReplaceWordMappings();
//        existingReplaceWords.addAll(replaceWords);
//        template.setReplaceWordMappings(existingReplaceWords);
//        // Сохраняем обновленный шаблон в базу данных
//        templateRepository.save(template);
//    }

    public void addReplaceWordsToTemplate(Long fileId, List<ReplaceWordMapping> newReplaceWords) {
        // Находим шаблон по имени файла
        Template template = templateRepository.findById(fileId).orElse(null);

        if (template == null) {
            // Если шаблон не найден, можно выбросить исключение или выполнить другие действия
            throw new IllegalArgumentException("Template with id " + fileId + " not found");
        }

        // Получаем существующие слова для замены
        List<ReplaceWordMapping> existingReplaceWords = template.getReplaceWordMappings();
        Map<String, ReplaceWordMapping> existingReplaceWordsMap = existingReplaceWords.stream()
                .collect(Collectors.toMap(ReplaceWordMapping::getKey, Function.identity()));

        // Обновляем существующие слова или добавляем новые
        for (ReplaceWordMapping newReplaceWord : newReplaceWords) {
            ReplaceWordMapping existingReplaceWord = existingReplaceWordsMap.get(newReplaceWord.getKey());
            if (existingReplaceWord != null) {
                // Обновляем значение существующего слова для замены
                existingReplaceWord.setValue(newReplaceWord.getValue());
            } else {
                // Добавляем новое слово для замены
                newReplaceWord.setTemplate(template);
                existingReplaceWords.add(newReplaceWord);
            }
        }

//        // Добавляем слова для замены к найденному шаблону
//        List<ReplaceWordMapping> existingReplaceWords = template.getReplaceWordMappings();
//        existingReplaceWords.addAll(replaceWords);

        // Добавляем слова для замены к найденному шаблону
        //template.setReplaceWordMappings(existingReplaceWords);

        // Сохраняем обновленный шаблон в базу данных
        templateRepository.save(template);

        //Template template2 = templateRepository.findById(fileId).orElse(null);
        // Получаем существующие слова для замены
        //List<ReplaceWordMapping> existingReplaceWords2 = template2.getReplaceWordMappings();
        //System.out.println("existingReplaceWords=   "+existingReplaceWords2);
    }

    public void updateCategoryToTemplate(Long fileId, Long categoryId) {
        // Находим шаблон по имени файла
        Template template = templateRepository.findById(fileId).orElse(null);

        if (template == null) {
            // Если шаблон не найден, можно выбросить исключение или выполнить другие действия
            throw new IllegalArgumentException("Template with id " + fileId + " not found");
        }

        Category category = categoryRepostory.findById(categoryId).orElse(null);

        if (category == null) {
            // Если шаблон не найден, можно выбросить исключение или выполнить другие действия
            throw new IllegalArgumentException("Category with id " + categoryId + " not found");
        }

        //обавляем категорию
        template.setCategories(category);

        // Сохраняем обновленный шаблон в базу данных
        templateRepository.save(template);
    }

    public void deleteTemplate(Long templateId) {
        Optional<Template> optionalTemplate = templateRepository.findById(templateId);
        if (optionalTemplate.isPresent()) {
//            Template template = optionalTemplate.get();
//            // Получите пользователя, связанного с этим шаблоном
//            User user = template.getUser();
//            user.removeTemplate(template);

            // Затем удалите сам шаблон
            templateRepository.deleteById(templateId);
        }

    }

    public Map<Long,String> getAllTemplates(){

        List<Template> templates = templateRepository.findAll();

        return templates.stream()
                .collect(Collectors.toMap(Template::getId, Template::getTemplateName));
    }

    public List<ReplaceWordMapping> getReplaceWordsTemplate(Long templateId){
        Optional<Template> optionalTemplate = templateRepository.findById(templateId);
        if (optionalTemplate.isPresent()) {
            Template template = optionalTemplate.get();

            //return new ArrayList<>();
            return template.getReplaceWordMappings();
        }
        return null;
    }

    public List<String> parsingTemplate(MultipartFile file) throws IOException {
        List<String> foundWords = new ArrayList<>();

        try (InputStream is = file.getInputStream()) {
            XWPFDocument doc = new XWPFDocument(is);

            // Регулярное выражение для поиска слов вида {@text}
            Pattern pattern = Pattern.compile("\\{@(.*?)\\}");

            for (XWPFParagraph paragraph : doc.getParagraphs()) {
                String paragraphText = paragraph.getText();

                // Применяем регулярное выражение к тексту абзаца
                Matcher matcher = pattern.matcher(paragraphText);

                while (matcher.find()) {
                    // Добавляем найденное слово в список
                    foundWords.add("{@" + matcher.group(1) + "}");
                }
            }
        }

        return foundWords;
    }

}


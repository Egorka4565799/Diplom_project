package sever.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sever.application.model.Document;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

}

package uz.pdp.fileuploaddownload.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.fileuploaddownload.entity.AttachmentContent;

import java.util.Optional;

public interface AttachmentContentRepository extends JpaRepository<AttachmentContent,Long> {
    Optional<AttachmentContent> findByAttachmentId(Long attachment_id);
}

package uz.pdp.fileuploaddownload.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.pdp.fileuploaddownload.entity.Attachment;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment,Long> {

}

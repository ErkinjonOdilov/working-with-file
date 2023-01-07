package uz.pdp.fileuploaddownload.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import uz.pdp.fileuploaddownload.entity.Attachment;
import uz.pdp.fileuploaddownload.entity.AttachmentContent;
import uz.pdp.fileuploaddownload.repository.AttachmentContentRepository;
import uz.pdp.fileuploaddownload.repository.AttachmentRepository;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/attachment")
public class AttachmentController {

    private final AttachmentRepository attachmentRepository;
    private final AttachmentContentRepository attachmentContentRepository;

    public AttachmentController(AttachmentRepository attachmentRepository,
                                AttachmentContentRepository attachmentContentRepository) {
        this.attachmentRepository = attachmentRepository;
        this.attachmentContentRepository = attachmentContentRepository;
    }

    @PostMapping("/uploadDb")
    public String uploadFileToDb(MultipartHttpServletRequest request) throws IOException {
        Iterator<String> fileNames = request.getFileNames();
        MultipartFile file = request.getFile(fileNames.next());
        if(file!=null){
            String originalFilename = file.getOriginalFilename();
            long size = file.getSize();
            String contentType = file.getContentType();
            Attachment attachment=new Attachment();
            attachment.setFileOriginalName(originalFilename);
            attachment.setSize(size);
            attachment.setContentType(contentType);
            Attachment savedAttachment = attachmentRepository.save(attachment);

            AttachmentContent attachmentContent=new AttachmentContent();
            attachmentContent.setAsosiyContent(file.getBytes());
            attachmentContent.setAttachment(savedAttachment);
            attachmentContentRepository.save(attachmentContent);
            return "File Saqlandi. Id si:"+savedAttachment.getId();

        }
        return "Xatolik";


    }

    private static final String uploadDirectory="filesUploadedDownloade";
    @PostMapping("/uploadSystem")
    public String uploadFileToFileSystem(MultipartHttpServletRequest request) throws IOException {
        Iterator<String> fileNames = request.getFileNames();
        MultipartFile file = request.getFile(fileNames.next());
        if(file !=null){
            String originalFilename = file.getOriginalFilename();
            Attachment attachment=new Attachment();
            attachment.setFileOriginalName(originalFilename);
            attachment.setSize(file.getSize());
            attachment.setContentType(file.getContentType());

            //uyga borish.jpg
            String[] split = originalFilename.split("\\.");
            //asdasdas5352asd.jpg
            String name = UUID.randomUUID().toString()+"."+split[split.length-1];

            attachment.setName(name);
            attachmentRepository.save(attachment);
            Path path= Paths.get(uploadDirectory+"/"+name);
            Files.copy(file.getInputStream(),path);
            return "Fayl saqlandi. Fayl id: "+attachment.getId();
        }
        return "Saqlanmadi";
    }

   @GetMapping("/getFile/{id}")
    public void getFile(@PathVariable Long id, HttpServletResponse response) throws IOException {
       Optional<Attachment> optionalAttachment = attachmentRepository.findById(id);
       if(optionalAttachment.isPresent()){
           Attachment attachment=optionalAttachment.get();
           Optional<AttachmentContent> contentOptional = attachmentContentRepository.findByAttachmentId(id);
           if(contentOptional.isPresent()){
               AttachmentContent attachmentContent = contentOptional.get();
               response.setHeader("Content-Disposition","attachment; filename=\""+attachment.getFileOriginalName()+"\"");
               response.setContentType(attachment.getContentType());
               FileCopyUtils.copy(attachmentContent.getAsosiyContent(),response.getOutputStream());
           }
       }
   }

   @GetMapping("getFileFromSystem/{id}")
    public void getFileFromSystem(@PathVariable Long id, HttpServletResponse response) throws IOException {
       Optional<Attachment> optionalAttachment = attachmentRepository.findById(id);
       if(optionalAttachment.isPresent()){
           Attachment attachment=optionalAttachment.get();
           response.setHeader("Content-Disposition","attachment; filename=\""+attachment.getFileOriginalName()+"\"");
           response.setContentType(attachment.getContentType());

           FileInputStream fileInputStream=new FileInputStream(uploadDirectory+"/"+attachment.getName());
           FileCopyUtils.copy(fileInputStream,response.getOutputStream());
       }
   }



}


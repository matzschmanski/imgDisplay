package imgDisplay.repository;

import imgDisplay.dao.Image;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;

public interface ImageRepository extends Repository<Image, Long> {

	Page<Image> findAll(Pageable pageable);

	Image findByNameIgnoringCase(String name);
	
	Image findById(Long id);

	Page<Image> findByNameIgnoringCase(String name, Pageable pageable);

	Image findByUploadTime(Date uploadTime);

	Image save(Image image);

}
